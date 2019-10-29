package org.pm4knime.node.performance;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;

import org.knime.core.data.DataCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.pm4knime.settingsmodel.SMPerformanceParameter;
import org.pm4knime.util.XLogUtil;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.manifestanalysis.visualization.performance.PerfCounter;
import org.processmining.plugins.manifestanalysis.visualization.performance.TimeFormatter;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;
import org.processmining.plugins.petrinet.manifestreplayresult.ManifestEvClassPattern;

/**
 * this class aims to assist the class PerformanceCheckerNodeModel on information provide. 
 * 
 * @author kefang-pads
 *
 */
public class PerfCheckerInfoAssistant {
	PerfCounter infoProvider;
	SMPerformanceParameter m_parameter;
	Manifest  mResult;
	// init it with the counter information
	public PerfCheckerInfoAssistant(SMPerformanceParameter parameter, Manifest  manifest, PerfCounter counter) {
		m_parameter = parameter;
		mResult = manifest;
		
		infoProvider = counter;
		buildInfoParameter();
	}
	
	private void buildInfoParameter() {

		String timeAttr = m_parameter.getMTimeStamp().getStringValue();
        
		// create caseFilter
		boolean[] caseFilter = new boolean[mResult.getLog().size()];
		Arrays.fill(caseFilter, true);

		if (!m_parameter.isMWithUnreliableResult().getBooleanValue()) {
			for (int i = 0; i < caseFilter.length; i++) {
				caseFilter[i] = mResult.isCaseReliable(i);
			}
		}
		
		infoProvider.init((ManifestEvClassPattern) mResult, timeAttr, XLogUtil.getAttrClass(mResult.getLog(), timeAttr), caseFilter);
	}
	
	public void fillGlobalData(BufferedDataContainer gBuf) {
		// TODO according to the replay result, assign the statistic info table
//		DataCell[] currentRow = new DataCell[2];
//		currentRow[0] = new StringCell("Test Property");
//		currentRow[1] = new DoubleCell(0.5);
//		gBuf.addRowToTable(new DefaultRow(0 +"", currentRow));
		
		// format 
		NumberFormat nfDouble = NumberFormat.getInstance();
		nfDouble.setMinimumFractionDigits(2);
		nfDouble.setMaximumFractionDigits(2);
		
		
		int propCounter = 0;
		DataCell[] currentRow ;
		// info[propCounter++] = new Object[] { "#Cases", infoProvider.getCaseTotalFreq() > 0 ? infoProvider.getCaseTotalFreq() : "-"};
		currentRow = new DataCell[2];
		currentRow[0] = new StringCell("#Cases");
		currentRow[1] = new StringCell((infoProvider.getCaseTotalFreq() > 0 ? infoProvider.getCaseTotalFreq() : "-") + "");
		gBuf.addRowToTable(new DefaultRow(propCounter +"", currentRow));
		
		// info[propCounter++] = new Object[] { "#Perfectly-fitting cases", infoProvider.getCaseTotalFreq() > 0 ? infoProvider.getCaseTotalFreq() - infoProvider.getCaseNonFittingFreq() : "-"};
		currentRow = new DataCell[2];
		currentRow[0] = new StringCell("#Perfectly-fitting cases");
		currentRow[1] = new StringCell((infoProvider.getCaseTotalFreq() > 0 ? infoProvider.getCaseTotalFreq() - infoProvider.getCaseNonFittingFreq() : "-") + "");
		gBuf.addRowToTable(new DefaultRow(propCounter +"", currentRow));
		propCounter++;
		
		//info[propCounter++] = new Object[] { "#Non-fitting cases", infoProvider.getCaseTotalFreq() > 0 ? infoProvider.getCaseNonFittingFreq() : "-"};
		currentRow = new DataCell[2];
		currentRow[0] = new StringCell("#Non-fitting cases");
		currentRow[1] = new StringCell((infoProvider.getCaseTotalFreq() > 0 ? infoProvider.getCaseNonFittingFreq() : "-") + "");
		gBuf.addRowToTable(new DefaultRow(propCounter +"", currentRow));
		propCounter++;
		
		// info[propCounter++] = new Object[] { "#Properly started cases", infoProvider.getCaseTotalFreq() > 0 ? infoProvider.getCaseProperlyStartedFreq() : "-"};
		currentRow = new DataCell[2];
		currentRow[0] = new StringCell("#Properly started cases");
		currentRow[1] = new StringCell((infoProvider.getCaseTotalFreq() > 0 ? infoProvider.getCaseProperlyStartedFreq() : "-") + "");
		gBuf.addRowToTable(new DefaultRow(propCounter +"", currentRow));
		propCounter++;
		
		//info[propCounter++] = new Object[] { "Case Throughput time (avg)", infoProvider.getCaseTotalFreq() > 0 ? TimeFormatter.formatTime(infoProvider.getCaseThroughputAvg(), nfDouble) : "-"};
		currentRow = new DataCell[2];
		currentRow[0] = new StringCell("#Case Throughput time (avg)");
		currentRow[1] = new StringCell((infoProvider.getCaseTotalFreq() > 0 ? TimeFormatter.formatTime(infoProvider.getCaseThroughputAvg(), nfDouble) : "-") + "");
		gBuf.addRowToTable(new DefaultRow(propCounter +"", currentRow));
		propCounter++;
		
		//info[propCounter++] = new Object[] { "Case Throughput time (min)", infoProvider.getCaseTotalFreq() > 0 ? TimeFormatter.formatTime(infoProvider.getCaseThroughputMin(), nfDouble) : "-"};
		currentRow = new DataCell[2];
		currentRow[0] = new StringCell("#Case Throughput time (min)");
		currentRow[1] = new StringCell((infoProvider.getCaseTotalFreq() > 0 ? TimeFormatter.formatTime(infoProvider.getCaseThroughputMin(), nfDouble) : "-") + "");
		gBuf.addRowToTable(new DefaultRow(propCounter +"", currentRow));
		propCounter++;
		
		// info[propCounter++] = new Object[] { "Case Throughput time (max)", infoProvider.getCaseTotalFreq() > 0 ? TimeFormatter.formatTime(infoProvider.getCaseThroughputMax(), nfDouble) : "-" };
		currentRow = new DataCell[2];
		currentRow[0] = new StringCell("#Case Throughput time (max)");
		currentRow[1] = new StringCell((infoProvider.getCaseTotalFreq() > 0 ? TimeFormatter.formatTime(infoProvider.getCaseThroughputMax(), nfDouble) : "-") + "");
		gBuf.addRowToTable(new DefaultRow(propCounter +"", currentRow));
		propCounter++;
		
		
		// info[propCounter++] = new Object[] { "Case Throughput time (std. dev)", infoProvider.getCaseTotalFreq() > 0 ? TimeFormatter.formatTime(infoProvider.getCaseThroughputStdDev(), nfDouble) : "-" };
		currentRow = new DataCell[2];
		currentRow[0] = new StringCell("#Case Throughput time (std. dev)");
		currentRow[1] = new StringCell((infoProvider.getCaseTotalFreq() > 0 ? TimeFormatter.formatTime(infoProvider.getCaseThroughputStdDev(), nfDouble) : "-") + "");
		gBuf.addRowToTable(new DefaultRow(propCounter +"", currentRow));
		propCounter++;
		
		
		// info[propCounter++] = new Object[] { "Observation period", infoProvider.getCaseTotalFreq() > 0 ? TimeFormatter.formatTime(infoProvider.getCasePeriod(), nfDouble) : "-" };
		currentRow = new DataCell[2];
		currentRow[0] = new StringCell("#Observation period");
		currentRow[1] = new StringCell((infoProvider.getCaseTotalFreq() > 0 ? TimeFormatter.formatTime(infoProvider.getCasePeriod(), nfDouble) : "-") + "");
		gBuf.addRowToTable(new DefaultRow(propCounter +"", currentRow));
		// propCounter++;
		
	}
	
	
	public void fillPlaceData(BufferedDataContainer pBuf, Manifest mResult, Collection<Place> places) {
		// TODO Auto-generated method stub
		DataCell[] currentRow = new DataCell[pBuf.getTableSpec().getNumColumns()];
		currentRow[0] = new StringCell("Test Place");
		currentRow[1] = new StringCell("Test Place Pro");
		for(int i=2; i< pBuf.getTableSpec().getNumColumns() ; i++)
			currentRow[i] = new DoubleCell(0.5);
		pBuf.addRowToTable(new DefaultRow(0 +"", currentRow));
	}

	public void fillTransitionData(BufferedDataContainer tBuf, Manifest mResult, Collection<Transition> transitions) {
		// TODO Auto-generated method stub
		DataCell[] currentRow = new DataCell[tBuf.getTableSpec().getNumColumns()];
		currentRow[0] = new StringCell("Test Transition");
		currentRow[1] = new StringCell("Test Transition Pro");
		for(int i=2; i< tBuf.getTableSpec().getNumColumns() ; i++)
			currentRow[i] = new DoubleCell(0.5);
		tBuf.addRowToTable(new DefaultRow(0 +"", currentRow));
	}

	
}
