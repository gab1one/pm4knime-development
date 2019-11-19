package org.pm4knime.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerNoILPRestrictedMoveModel;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.multietc.automaton.Automaton;
import org.processmining.plugins.multietc.automaton.AutomatonFactory;
import org.processmining.plugins.multietc.automaton.AutomatonNode;
import org.processmining.plugins.multietc.reflected.ReflectedLog;
import org.processmining.plugins.multietc.reflected.ReflectedTrace;
import org.processmining.plugins.multietc.res.MultiETCResult;
import org.processmining.plugins.multietc.sett.MultiETCSettings;
import org.processmining.plugins.petrinet.manifestreplayer.EvClassPattern;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClass;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClasses;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.matchinstances.InfoObjectConst;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResultImpl;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * this class provides utility methods for replayer result
 * After the conversion, we can have the more attributes there and get the alignment information. 
 * but now, we need to know, how to calculate the fitness, precision from this part!!
 * We can choose all kind of parameters from the replayer. The result is Ok. 
 * After that, we conver the result to that part. 
 * @author kefang-pads
 *
 */
public class ReplayerUtil {
	// here to list all the algirithm strategies
	public final static String[] strategyList = {"ILP Replayer","Non-ILP Replayer", "A*-ILP Based Manifest Replayer"};
	
	public static IPNReplayAlgorithm getReplayer(String algName) {
		// TODO Auto-generated method stub
		IPNReplayAlgorithm replayEngine = null;
    	if(algName.equals(strategyList[0])) {
    		replayEngine = new PetrinetReplayerWithILP();
    	}else if(algName.equals(strategyList[1])) {
    		replayEngine = new PetrinetReplayerWithoutILP();
    	}else if(algName.equals(strategyList[2])){
    		replayEngine = new PetrinetReplayerNoILPRestrictedMoveModel();
    	}
    	
		return replayEngine;
	}
	
    
	public static Map<TransClass, Integer> buildTMCostMap(TransClasses tc, int cost) {
		// TODO Auto-generated method stub
    	Map<TransClass, Integer> map= new HashMap<TransClass, Integer>();
		
		for (TransClass c : tc.getTransClasses()) {
			map.put(c, cost);
		}
		return map;
	}


	public static Map<XEventClass, Integer> buildLMCostMap(Collection<XEventClass> eventClasses, int cost) {
		// TODO Auto-generated method stub
		Map<XEventClass, Integer> map= new HashMap<XEventClass, Integer>();
		for (XEventClass c : eventClasses) {
			map.put(c, cost);
		}
		
		// TODO : set dummy event class here and assign it value
		return map;
	}

	public static Map<TransClass, Set<EvClassPattern>> buildPattern(TransClasses tc, Collection<XEventClass> eventClasses) {
    	
    	Map<TransClass, Set<EvClassPattern>> pattern = new HashMap<TransClass, Set<EvClassPattern>>();
    	
    	for (TransClass t : tc.getTransClasses()) {
			Set<EvClassPattern> p = new HashSet<EvClassPattern>();
			line: for (XEventClass clazz : eventClasses)
				// look for exact matches on the id
				if (clazz.getId().equals(t.getId())) {
					EvClassPattern pat = new EvClassPattern();
					pat.add(clazz);
					p.add(pat);
					pattern.put(t, p);
					break line;
				}

		}
    	return pattern;
    }
	
	/**
	 * to convert PNResult to PNMatchInstancesRepResult;
	 */
	public static PNMatchInstancesRepResult convert2MatchInstances(PNRepResult repResult) {
		//Convert to n-alignments object
		Collection<AllSyncReplayResult> col = new ArrayList<AllSyncReplayResult>();
		for (SyncReplayResult rep : repResult) {
			List<List<Object>> nodes = new ArrayList<List<Object>>();
			nodes.add(rep.getNodeInstance());

			List<List<StepTypes>> types = new ArrayList<List<StepTypes>>();
			types.add(rep.getStepTypes());

			SortedSet<Integer> traces = rep.getTraceIndex();
			boolean rel = rep.isReliable();

			//Create a n-alignment result with this attributes
			AllSyncReplayResult allRep = new AllSyncReplayResult(nodes, types, -1, rel);
			allRep.setTraceIndex(traces);//The creator not allow add the set directly
			col.add(allRep);
		}
		PNMatchInstancesRepResult repInstances = new PNMatchInstancesRepResult(col);
		for(String key : repResult.getInfo().keySet()) {
			repInstances.addInfo(key, repResult.getInfo().get(key).toString());
		}
		
		return repInstances;
	}
	
	/**
	 * how to conver repInstances to PNRepResult??
	 * @param repInstances
	 * @return
	 */
	public static PNRepResult convert2RepResult(PNMatchInstancesRepResult repInstances) {
		
		List<SyncReplayResult> synResList = new ArrayList();
		
		for(AllSyncReplayResult allSynRes : repInstances) {
			int i = 0;
			
			List<List<Object>> nodes = allSynRes.getNodeInstanceLst();
			List<Object> nodeInstance = nodes.get(i);
			
			
			List<List<StepTypes>> types = allSynRes.getStepTypesLst();
			List<StepTypes> stepInstance = types.get(i);
			
			for(int tIdx : allSynRes.getTraceIndex()) {
				SyncReplayResult synRes = new SyncReplayResult(nodeInstance, stepInstance, tIdx);
				synRes.setReliable(allSynRes.isReliable());
				synResList.add(synRes);
			}
			
		}
		
		PNRepResult repResult = new PNRepResultImpl(synResList);
		for(String key : repInstances.getInfo().keySet()) {
			repResult.addInfo(key, repInstances.getInfo().get(key));
		}
		
		return repResult;
	}
	
	/**
	 * this method rewrites the method checkMultiETC(PluginContext, ReflectedLog, Petrinet, MultiETCSettings) in MultiETCPlugin.
	 * It calculates the precision and automation result as forwards and backwards. 
	 * @param refLog
	 * @param anet
	 * @param sett
	 * @return
	 */
	public static Object[] checkMultiETC(ReflectedLog refLog, AcceptingPetriNet anet, MultiETCSettings sett) {
		// forward check precision
		sett.setWindow(MultiETCSettings.Window.BACKWARDS);
		Object[] forwards = checkETCPrecision(refLog, anet.getNet(), anet.getInitialMarking() , null, sett);
		MultiETCResult resFor = (MultiETCResult) forwards[0];
		//Automaton autoFor = (Automaton) forwards[1];
		
		// backwards check precision
		sett.setWindow(MultiETCSettings.Window.FORWARDS);
		Object[] backwards = checkETCPrecision(refLog, anet.getNet(), null, anet.getFinalMarkings().iterator().next(), sett);
		MultiETCResult resBack = (MultiETCResult) backwards[0];
		//Automaton autoBack = (Automaton) backwards[1];
		
		//Merge the results of the backwards conformance checking with the forwards ones
		mergeForwardsBackwardsResults(resFor, resBack);
		
		return new Object[] {resFor};
	} 
	
	// this methods rewrites checkMultiETC (PluginContext context, ReflectedLog log, Petrinet net, Marking iniM, Marking endM, MultiETCSettings etcSett)
	// accept intial Marking and backwards Marking to get the result.
	public static Object[] checkETCPrecision(ReflectedLog refLog, Petrinet net, Marking iniM, Marking endM, MultiETCSettings etcSett) {

		AutomatonFactory factory = new AutomatonFactory(etcSett);
		Automaton a = factory.createAutomaton();
		MultiETCResult res = new MultiETCResult();
		a.checkConformance(refLog, net, iniM, endM, res, etcSett);
		
		// setSettingsInfoInResult(etcSett,res);
		res.putAttribute(MultiETCResult.ETC_SETT, etcSett);	
		setAutomatonInfoInResult(a,res);
		
		return new Object[] {res, a};
	}
	
	// set the attributes values for MultiETCresult 
	private static void mergeForwardsBackwardsResults(MultiETCResult resFor, MultiETCResult resBack) {
		
		resFor.putAttribute(MultiETCResult.AUTO_STATES_BACK, resBack.getAttribute(MultiETCResult.AUTO_STATES));
		resFor.putAttribute(MultiETCResult.AUTO_STATES_IN_BACK, resBack.getAttribute(MultiETCResult.AUTO_STATES_IN));
		resFor.putAttribute(MultiETCResult.AUTO_STATES_OUT_BACK, resBack.getAttribute(MultiETCResult.AUTO_STATES_OUT));
		
		resFor.putAttribute(MultiETCResult.BACK_PRECISION, resBack.getAttribute(MultiETCResult.PRECISION));
		
		double balanced = ( (Double) resFor.getAttribute(MultiETCResult.PRECISION) + (Double) resBack.getAttribute(MultiETCResult.PRECISION)) / 2;
		resFor.putAttribute(MultiETCResult.BALANCED_PRECISION, balanced);		
	}
	
	// set the attributes values for MultiETCresult 
	private static void setAutomatonInfoInResult(Automaton a, MultiETCResult res) {
		int states = 0;
		int in = 0;
		int out = 0;
		for(AutomatonNode n: a.getJUNG().getVertices()){
			states++;
			if (n.getMarking() == null) out++;
			else in++;
		}
		res.putAttribute(MultiETCResult.AUTO_STATES, states);
		res.putAttribute(MultiETCResult.AUTO_STATES_IN, in);
		res.putAttribute(MultiETCResult.AUTO_STATES_OUT, out);
		
	}
	
	// convert the reflected log with the same transitions in anet to compare
	public static ReflectedLog convertRefLog(ReflectedLog log, AcceptingPetriNet anet) {
		// to save the time, we can convert it while generating it..
		ReflectedLog refLog = new ReflectedLog();
		
		return refLog;
	}
	
	
	// extract reflected log from PNMatchInstancesRepResult
	public static ReflectedLog extractRefLog(PNMatchInstancesRepResult matchResult, AcceptingPetriNet anet) {
		ReflectedLog refLog = new ReflectedLog();
		
		// create a map between the transitions in matchResult and the one in anet!! 
		Map<Transition, Transition> tMap = new HashMap();
		
		for(AllSyncReplayResult caseAlignments : matchResult){
			
			//Check if Sample or Not
			List<Integer> sampleReps = null;
			if(caseAlignments.getInfoObject() != null){
				if (caseAlignments.getInfoObject().get(InfoObjectConst. NUMREPRESENTEDALIGNMENT) != null){
					sampleReps = (List<Integer>) caseAlignments.getInfoObject().get(InfoObjectConst. NUMREPRESENTEDALIGNMENT);
				}
			}
			
			//Compute the number of alignments (if sampling, the ones represented by the samples)
			int nAlign = 0;
			for(int i = 0; i<caseAlignments.getNodeInstanceLst().size(); i++){
				//Sample
				if(sampleReps != null){
					nAlign +=  sampleReps.get(i);
				}
				//Not Sample
				else{
					nAlign ++;
				}
			}
			
			//Compute the increment of weight assigned to each alignment
			int nCases = caseAlignments.getTraceIndex().size();
			double weightPerAlign = (double) nCases / (double) nAlign;
			
			//For each alignment in this set
			for(int i = 0; i<caseAlignments.getNodeInstanceLst().size(); i++){
				ReflectedTrace t = new ReflectedTrace();
				
				//Check the Alignments that are not Movements on the Log only
				Iterator<Object> itTask = caseAlignments.getNodeInstanceLst().get(i).iterator();
				Iterator<StepTypes> itType = caseAlignments.getStepTypesLst().get(i).iterator();
				while(itTask.hasNext()){
					StepTypes type = itType.next();
					
					//If it is a log move, just skip
					if(type == StepTypes.L){
						itTask.next();//Skip the task
					}
					
					else{ //It is a PetriNet Transition
						Transition trans = ((Transition) itTask.next());
						// tMap not includes this transition, then we add it here
						if(!tMap.containsKey(trans)) {
							Transition tInNet = PetriNetUtil.findTransition(trans.getLabel(), anet.getNet().getTransitions());
							tMap.put(trans, tInNet);
						}
							
						t.add(tMap.get(trans));
					}
				}
				
				//Avoid adding empty traces
				if(!t.isEmpty()){
					//SetWeight: num of Cases / num Alignments found for those cases
					int represented  = (sampleReps != null) ? sampleReps.get(i) : 1;
					t.putWeight(represented * weightPerAlign);
					//Add trace
					refLog.add(t);
				}	
			}
		}
		
		return refLog;
	}


	public static void adjustRepResult(PNRepResult repResult, AcceptingPetriNet anet) {
		// TODO adjust the result in repResult to make it correpsond to net transitions values
		Map<Transition, Transition> tMap = new HashMap();
		
		for (SyncReplayResult rep : repResult) {
			// nodes store the transitions
			List<Object> nodes = rep.getNodeInstance();
			List<StepTypes> types = rep.getStepTypes();
			
			for(int i = 0; i< types.size(); i++) {
				StepTypes type = types.get(i);
				if(type == StepTypes.L){
					continue;
				}
				
				// syn step and transition step
				Transition trans = (Transition) nodes.get(i);
				if(!tMap.containsKey(trans)) {
					Transition tInNet = PetriNetUtil.findTransition(trans.getLabel(), anet.getNet().getTransitions());
					tMap.put(trans, tInNet);
				}
					
				nodes.set(i, tMap.get(trans));
			}	
		}
		
	}
}
