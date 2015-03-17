package org.pathvisio.atlasplugin.plugin;

/**
* Stock the necessary information of the Sparql query.
* @author Jonathan Melius
* @see SparqlQuery
*/

public class SparqlResults {
	private String exp;
	private String prop;
	private String pv;
	private String ts;
	private String prob;
	
	public SparqlResults (String exp, String prop, String pV, String tS, String prob){
		this.exp=exp;
		this.prop=prop;
		this.pv=pV;
		this.ts=tS;
		this.prob=prob;
	}	

	public String getExp() {
		return exp;
	}

	public String getProp() {
		return prop;
	}

	public String getPv() {
		return pv;
	}

	public String getTs() {
		return ts;
	}
	
	public String getProb(){
		return prob;
	}
	
	public String toString(){
		return (exp+" "+prop+" "+pv+" "+ts);
		
	}

}
