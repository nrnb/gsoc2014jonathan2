package org.pathvisio.atlasplugin.utils;

import java.util.ArrayList;
import org.pathvisio.atlasplugin.plugin.AbstractQuery;
import org.pathvisio.gexplugin.ImportInformation;

/**
* Interface to notify the view about the importation's change.
* @author Jonathan Melius
* @see AbstractQuery
*/
public interface ObservableAtlas {
	public void addObserver(ObserverAtlas obs);
	public void notifyObservers(ArrayList<String> idExperiment);
	public void notifyObservers(ImportInformation importInformation);
	public void notifyObservers(int progress);
	public void delOneObserver(ObserverAtlas obs);
	public void delAllObservers();
}
