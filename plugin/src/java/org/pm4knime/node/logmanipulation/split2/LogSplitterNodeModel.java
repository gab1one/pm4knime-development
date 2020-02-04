package org.pm4knime.node.logmanipulation.split2;

import java.io.File;
import java.io.IOException;

import org.deckfour.xes.model.XLog;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.pm4knime.portobject.XLogPortObject;
import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.util.XLogSpecUtil;
import org.pm4knime.util.XLogUtil;

/**
 * This is the model implementation of LogSplitter.
 * This log splitter is independent on the event log... we just give the choices , in benefit to use flowVariables
 * How to make it work?? After the RandomClassifier, we should have one option in RandomClassifier to 
 * define the new added attributes in the model.. Else, we need to choose them, but 
 * currently, we don't know which one to choose.. so we just split the positive one!!! 
 * we don't even need one dialog to use... 
 * @author Kefang
 */
public class LogSplitterNodeModel extends NodeModel {
	private static final NodeLogger logger = NodeLogger
            .getLogger(LogSplitterNodeModel.class);
	private XLogPortObjectSpec[] m_outSpecs = new XLogPortObjectSpec[getNrOutPorts()];
	// one for the string, one for the value.. 
	public static final String CFG_ATTRIBUTE_KEY = "Attribute key";
	public static final String CFG_ATTRIBUTE_VALUE = "Attribute value";
	
	SettingsModelString m_attributeKey = LogSplitterNodeModel.createSettingsModelAttributeKey();
	SettingsModelString m_attributeValue = LogSplitterNodeModel.createSettingsModelAttributeValue();
	
	
	static SettingsModelString createSettingsModelAttributeKey() {
	    return new SettingsModelString(CFG_ATTRIBUTE_KEY, "");
	} 
	
	static SettingsModelString createSettingsModelAttributeValue() {
	    return new SettingsModelString(CFG_ATTRIBUTE_VALUE, "");
	}
	
    /**
     * Constructor for the node model.
     */
    protected LogSplitterNodeModel() {
    	super(new PortType[] { XLogPortObject.TYPE },
				new PortType[] { XLogPortObject.TYPE, XLogPortObject.TYPE });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inData,
            final ExecutionContext exec) throws Exception {
        // TODO: Return a BufferedDataTable for each output port 
    	logger.info("Begin to Split the Event Log");
    	// assign the log
    	XLogPortObject logPortObject = (XLogPortObject) inData[0];
		XLog log = logPortObject.getLog();
		
		// change the trace attribute or event attribute
		XLog[] logs = new XLog[2];
		if(m_attributeKey.getStringValue().contains(XLogSpecUtil.TRACE_ATTRIBUTE_PREFIX)) {
			String[] options = m_attributeKey.getStringValue().split(XLogSpecUtil.TRACE_ATTRIBUTE_PREFIX);
			logs = XLogUtil.splitLogByTraceAttr(log, options[1], 
					m_attributeValue.getStringValue());
		}else if(m_attributeKey.getStringValue().contains(XLogSpecUtil.EVENT_ATTRIBUTE_PREFIX)) {
			String[] options = m_attributeKey.getStringValue().split(XLogSpecUtil.EVENT_ATTRIBUTE_PREFIX);
			logs = XLogUtil.splitLogByEventAttr(log, options[1], 
					m_attributeValue.getStringValue());
		}
		
		
		XLogPortObject lp2keep = new XLogPortObject();
    	lp2keep.setLog(logs[0]);
    	
    	XLogPortObject lp2dispose = new XLogPortObject();
    	lp2dispose.setLog(logs[1]);
    	
    	logger.info("End Node Split the Event Log");
        return new PortObject[]{lp2keep, lp2dispose };
 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
    	if(!inSpecs[0].getClass().equals(XLogPortObjectSpec.class)) 
    		throw new InvalidSettingsException("Input is not a valid Event Log!");
    	
    	m_outSpecs[0] = new XLogPortObjectSpec();
		m_outSpecs[1] = new XLogPortObjectSpec();
		return new PortObjectSpec[] { m_outSpecs[0], m_outSpecs[1]};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
         // TODO: generated method stub\
    	m_attributeKey.saveSettingsTo(settings);
    	m_attributeValue.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    	m_attributeKey.loadSettingsFrom(settings);
    	m_attributeValue.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    	m_attributeKey.validateSettings(settings);
    	m_attributeValue.validateSettings(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

}

