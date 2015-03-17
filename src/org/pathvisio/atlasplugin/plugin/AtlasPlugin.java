package org.pathvisio.atlasplugin.plugin;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;

import org.pathvisio.atlasplugin.gui.AtlasWizard;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;

/**
* Initialize the Atlas plugin in the PathVisio's GUI.
* This plugin import datasets from expression Atlas. Currently only one menu item
* to import and visualize the data expression.
* @author Jonathan Melius
*/
public class AtlasPlugin implements Plugin{
	private PvDesktop desktop;
	@Override
	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		AtlasAction action = new AtlasAction();
		desktop.registerMenuAction ("Plugins", action);
	}

	@Override
	public void done() {
		// TODO Auto-generated method stub

	}
	private class AtlasAction extends AbstractAction
	{
		AbstractQuery atlasQuery;
		AtlasControler atlasControler;
		AtlasWizard wizard;

		public AtlasAction()
		{	
			putValue (NAME, "Atlas Plugin");
			putValue(SHORT_DESCRIPTION, "Test Atlas plugin");
		}

		public void actionPerformed(ActionEvent arg0)
		{	
			atlasQuery = new AtlasQuery();
			atlasControler = new AtlasControler(atlasQuery);
			wizard = new AtlasWizard(desktop,atlasControler);
			atlasQuery.addObserver(wizard);
			wizard.initialization();
			wizard.showModalDialog(desktop.getSwingEngine().getFrame());
		}
	}
}