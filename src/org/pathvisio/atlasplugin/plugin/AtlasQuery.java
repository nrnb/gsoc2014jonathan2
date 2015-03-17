package org.pathvisio.atlasplugin.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.bridgedb.DataSource;
import org.pathvisio.core.util.FileUtils;
import org.pathvisio.core.util.ProgressKeeper;

import arq.update;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

/**
* Query Expression Atlas database by RDF using the Sparql endpoint.
* Retrieve the list of experiment id and the dataset.
* @see AbstractQuery
* @author Jonathan Melius
*/

public class AtlasQuery extends AbstractQuery {
	
	public AtlasQuery(){
		super();
	}
	public void queryID() {
		System.out.println("just once upon the time");
		String sparqlEndpoint = "http://www.ebi.ac.uk/rdf/services/atlas/sparql";
		String sparqlQuery =
				"PREFIX dcterms: <http://purl.org/dc/terms/>"+
				"PREFIX atlasterms: <http://rdf.ebi.ac.uk/terms/atlas/>"+
				"PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>"+
				"SELECT DISTINCT ?id  WHERE {"+
				"?experiment a atlasterms:Experiment ."+
				"?experiment dcterms:identifier ?id."+   
				"?experiment atlasterms:hasAnalysis ?analysis ."+
				"}";
	
		Query query = QueryFactory.create(sparqlQuery, Syntax.syntaxARQ) ;
	
		ParameterizedSparqlString parameterizedSparqlString = new ParameterizedSparqlString(query.toString());
	
		QueryEngineHTTP httpQuery = new QueryEngineHTTP(sparqlEndpoint,parameterizedSparqlString.asQuery());
		// execute a Select query
		ResultSet results = httpQuery.execSelect();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			// get the value of the variables in the select clause
			String id = solution.get("id").asLiteral().getLexicalForm();
			idExperiment.add(id);
		}
		notifyObservers(idExperiment); 
		//System.out.println("ss"+idExperiment.size());
	}
	public void queryExperiment(String txtOutput, String experiment,ProgressKeeper pk) {
		
		String outputFileName = null;
		File outputFile = new File(txtOutput);
		File tmpFile = null;		
		PrintWriter fileWriter = null;
	
		queryList = new ArrayList<String>(Arrays.asList(experiment.split(" ")));
		data = new HashMap<String,Map<String,LinkedList<SparqlResults>>>();
	
		String prop = "propertyValue";
		String pValue = "pValue";
		String tStat = "tStat";
		String probe = "probe";
		String header = "Gene_ID";
	
		try {
			outputFile=FileUtils.replaceExtension(outputFile, "pgex");
			outputFileName = outputFile.getCanonicalPath();
	
			String tDir = System.getProperty("java.io.tmpdir");
			tmpFile = File.createTempFile(tDir+"AtlasQuery", ".tmp");
			tmpFile.deleteOnExit();
			
			fileWriter = new PrintWriter(new FileWriter(tmpFile));
			for (String query : queryList){
				header += "\t"+query+"-"+probe+"\t"+query+"-"+prop+"\t"+query+"-"+pValue+"\t"+query+"-"+tStat;
			}
			fileWriter.println(header);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		int progress = 0 ;
		//pk.setProgress(0);
		for (String query : queryList){
			System.out.println(query);
			progress += 100/queryList.size()/2;
			notifyObservers(progress);
//			pk.setProgress(progress);
			new SparqlQuery (query,data);
			progress += 100/queryList.size()/2;
//			pk.setProgress(progress);
			//progress += 100/queryList.size();
			//System.out.println(progress);
			//progressBar.setValue(progress);
			notifyObservers(progress);
		}
	
		for(Entry<String,Map<String,LinkedList<SparqlResults>>> entry : data.entrySet()) {
			String key = entry.getKey();
			Map<String,LinkedList<SparqlResults>> value = entry.getValue();
			ArrayList<LinkedList<SparqlResults>> resultList = new ArrayList<LinkedList<SparqlResults>>();
			for(Entry<String,LinkedList<SparqlResults>> expEntry : value.entrySet()) {
				resultList.add(expEntry.getValue());
			}				
			while (listIsEmpty(resultList)){
				String line = key;
				String[] tab = new String[queryList.size()];
				ArrayList<String> resultsArray = new ArrayList<String>(Arrays.asList(tab));
				for (LinkedList<SparqlResults> lili : resultList) {
					SparqlResults sparql = lili.pollFirst();
					if (!(sparql==null)){
						int index = queryList.indexOf(sparql.getExp());
						String tmp =  "\t"+sparql.getProb()+
								"\t"+sparql.getProp()+
								"\t"+sparql.getPv()+
								"\t"+sparql.getTs();
						resultsArray.set(index, tmp);
					}
				}
				for (String ss : resultsArray){
					if (ss==null){
						line +="\t"+""+"\t"+""+"\t"+""+"\t"+""; 
					}
					else line += ss;
				}
				fileWriter.println(line);
			}
		}
		fileWriter.close();
	
		try {
			importInformation.setTxtFile(tmpFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		importInformation.setGexName(outputFileName);
		importInformation.setFirstDataRow(1);
		importInformation.setFirstHeaderRow(0);
		importInformation.guessSettings();
		importInformation.setSyscodeFixed(true);
		importInformation.setDataSource(DataSource.getBySystemCode("En"));
		importInformation.setDelimiter("\t");
	
		notifyObservers(importInformation);
	}

	public boolean listIsEmpty(ArrayList<LinkedList<SparqlResults>> arrayList) {
		int i=0;
		for (LinkedList<SparqlResults> linked : arrayList){
			if (linked.isEmpty()){
				i++;
			}			
		}
		if (i==arrayList.size()){
			return false;
		}		
		return true;		
	}	
}
