package org.pathvisio.atlasplugin.plugin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import org.pathvisio.atlasplugin.utils.ObservableAtlas;
import org.pathvisio.atlasplugin.utils.ObserverAtlas;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.gexplugin.ImportInformation;

/**
* Abstract class to query Expression Atlas database.
* Retrieve the list of experiment id and the dataset.
* @author Jonathan Melius
* @see ObservableAtlas
*/

public abstract class AbstractQuery implements ObservableAtlas{

	protected ImportInformation importInformation;
	protected ArrayList<String> queryList;
	protected Map<String, Map<String, LinkedList<SparqlResults>>> data;
	protected ArrayList<String> idExperiment;
	protected ArrayList<ObserverAtlas> observers;

	public AbstractQuery() {
		importInformation = new ImportInformation();
		observers = new ArrayList<ObserverAtlas>();
		idExperiment = new ArrayList<String>();
	}

	public abstract void queryID();

	public abstract void queryExperiment(String txtOutput, String experiment,ProgressKeeper pk);

	@Override
	public void addObserver(ObserverAtlas obs) {
		// TODO Auto-generated method stub
		observers.add(obs);
	}

	@Override
	public void notifyObservers(ArrayList<String> idExperiment) {
		// TODO Auto-generated method stub
		for (ObserverAtlas obs : observers){
			obs.update(idExperiment);
		}
	}

	@Override
	public void notifyObservers(ImportInformation importInformation) {
		// TODO Auto-generated method stub
		for (ObserverAtlas obs : observers){
			obs.update(importInformation);
		}
	}
	
	@Override
	public void notifyObservers(int progress) {
		// TODO Auto-generated method stub
		for (ObserverAtlas obs : observers){
			obs.update(progress);
		}
	}

	@Override
	public void delOneObserver(ObserverAtlas obs) {
		// TODO Auto-generated method stub
		observers.remove(obs);
	}

	@Override
	public void delAllObservers() {
		// TODO Auto-generated method stub
	
	}

}