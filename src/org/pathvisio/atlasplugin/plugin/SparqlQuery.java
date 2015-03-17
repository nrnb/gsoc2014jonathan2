package org.pathvisio.atlasplugin.plugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

/**
* Using the Jena API (http://jena.apache.org)
* to querying the Expression Atlas SPARQL endpoint 
* @author Jonathan Melius
* @see AtlasQuery
*/

public class SparqlQuery {

	public SparqlQuery (String experiment, 
			Map<String,Map<String,LinkedList<SparqlResults>>> data){
		String sparqlEndpoint = "http://www.ebi.ac.uk/rdf/services/atlas/sparql";
		String sparqlQuery = ""+
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
				"PREFIX dcterms: <http://purl.org/dc/terms/>"+
				"PREFIX atlasterms: <http://rdf.ebi.ac.uk/terms/atlas/>"+
				"PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>"+				
				"SELECT DISTINCT ?exp ?type ?pb ?propertyValue ?id ?pvalue ?tStat WHERE {"+
				"?experiment a atlasterms:Experiment ."+
				"?experiment dcterms:identifier \""+experiment+"\"^^xsd:string ."+   
				"?experiment dcterms:identifier ?exp ."+
				"?experiment atlasterms:hasAnalysis ?analysis ."+
				"?analysis atlasterms:hasExpressionValue ?value ."+
				"?value atlasterms:hasFactorValue ?factor ."+
				"?factor atlasterms:propertyType ?propertyType ."+    
				"?factor atlasterms:propertyValue ?propertyValue ."+  
				"?value atlasterms:pValue ?pvalue ."+
				"?value atlasterms:tStatistic ?tStat ."+
				"?value atlasterms:isMeasurementOf ?probe ."+
				"?probe dcterms:identifier ?pb ."+
				"?probe atlasterms:dbXref ?dbXref ."+
				"?dbXref rdf:type ?type ."+
				//"?dbXref rdf:type atlasterms:EnsemblDatabaseReference ."+
				//"filter ( ?type = atlasterms:EnsemblDatabaseReference)"+
				"?dbXref dcterms:identifier ?id ."+
				"}";

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();	

		// create the Jena query using the ARQ syntax (has additional support for SPARQL federated queries)
		Query query = QueryFactory.create(sparqlQuery, Syntax.syntaxARQ) ;

		ParameterizedSparqlString parameterizedSparqlString = new ParameterizedSparqlString(query.toString());

		QueryEngineHTTP httpQuery = new QueryEngineHTTP(sparqlEndpoint,parameterizedSparqlString.asQuery());
		// execute a Select query
		ResultSet results = httpQuery.execSelect();

		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		date = new Date();


		int i=0;
		int k=0;
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			String type = solution.getResource("type").getLocalName();
			if (type.equals("EnsemblDatabaseReference")){					
				k=traitement (data, solution,k);
				i++;
			}
		}

		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		date = new Date();

	}
	public int traitement (Map<String,Map<String,LinkedList<SparqlResults>>> data,
			QuerySolution solution,int k){
		String gene_id = solution.get("id").asLiteral().getLexicalForm();
		String pValue = solution.get("pvalue").asLiteral().getLexicalForm();
		String tStat = solution.get("tStat").asLiteral().getLexicalForm();
		String exp_id = solution.get("exp").asLiteral().getLexicalForm();
		String propertyValue = solution.get("propertyValue").asLiteral().getLexicalForm();
		String probe = solution.get("pb").asLiteral().getLexicalForm();
		

		if (!data.containsKey(gene_id) ) {
			HashMap<String,LinkedList<SparqlResults>> expMap =
					new HashMap<String,LinkedList<SparqlResults>>();
			data.put(gene_id, expMap);
			if (!data.get(gene_id).containsKey(exp_id)  )  {
				LinkedList<SparqlResults> array = new LinkedList<SparqlResults>();
				array.add(new SparqlResults(exp_id, propertyValue, pValue, tStat, probe));
				expMap.put(exp_id, array);
				k++;
			}	
		}
		else if (!data.get(gene_id).containsKey(exp_id)  )  {
			HashMap<String,LinkedList<SparqlResults>> expMap =
					new HashMap<String,LinkedList<SparqlResults>>();
			LinkedList<SparqlResults> array = new LinkedList<SparqlResults>();
			array.add(new SparqlResults(exp_id, propertyValue, pValue, tStat, probe));
			data.get(gene_id).put(exp_id, array);
			k++;
		}
		else{
			LinkedList<SparqlResults> array = data.get(gene_id).get(exp_id);
			array.add(new SparqlResults(exp_id, propertyValue, pValue, tStat, probe));
			k++;
		}
		return k;
	}
}
