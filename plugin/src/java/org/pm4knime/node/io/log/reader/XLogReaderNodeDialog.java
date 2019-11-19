package org.pm4knime.node.io.log.reader;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.pm4knime.settingsmodel.SMXLogReaderParameter;

public class XLogReaderNodeDialog extends DefaultNodeSettingsPane {
	
	private final SMXLogReaderParameter sm = new SMXLogReaderParameter();

	public XLogReaderNodeDialog() {
		super();
		addDialogComponent(new DialogComponentFileChooser(sm.getFilePathSettingsModel(), "", new String[]{".xes", ".xes.gz"}));
	}

}