package org.pathvisio.atlasplugin.utils;

import java.util.ArrayList;
import org.pathvisio.atlasplugin.gui.AtlasWizard;
import org.pathvisio.gexplugin.ImportInformation;

/**
* Interface to update the view about the change of the importation
* @author Jonathan Melius
* @see AtlasWizard 
*/

public interface ObserverAtlas {
	public void update(ArrayList<String> idExperiment);
	public void update(ImportInformation importInformation);
	public void update(int progress);
}
