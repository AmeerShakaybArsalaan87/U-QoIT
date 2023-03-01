package DTNRouting;

import java.io.IOException;
import java.util.*;;

public class WriteResults extends dtnrouting{
	private static final long serialVersionUID = 1L;
	Random rand = new Random();

	public WriteResults() { }

	// Calculating Source Vs Network Metric Relationship Weights
	public double[][] sourceVsNetworkMetricRelationshipWeights(double[][] qualityMetricValues, double[][] goodnessValue_wrtNetworkMetrics, double[] relationshipWeights, int noofsources, int no_networkCriteria){
		// 0 = HC, 1 = IU, 2 = BW, 3 = PI
		for (int i = 0; i < noofsources; i++){	
			for (int j = 0; j < no_networkCriteria; j++){
				if (j == 0)		qualityMetricValues[i][j] = ((goodnessValue_wrtNetworkMetrics[i][3] * relationshipWeights[(j*2)]) + (goodnessValue_wrtNetworkMetrics[i][1] * relationshipWeights[((j*2)+1)]));  // A
				else if (j == 1)qualityMetricValues[i][j] = ((goodnessValue_wrtNetworkMetrics[i][2] * relationshipWeights[(j*2)]) + (goodnessValue_wrtNetworkMetrics[i][1] * relationshipWeights[((j*2)+1)]));  // C
				else if (j == 2)qualityMetricValues[i][j] = ((goodnessValue_wrtNetworkMetrics[i][2] * relationshipWeights[(j*2)]) + (goodnessValue_wrtNetworkMetrics[i][0] * relationshipWeights[((j*2)+1)]));  // T
				else if (j == 3)qualityMetricValues[i][j] = ((goodnessValue_wrtNetworkMetrics[i][3] * relationshipWeights[(j*2)]) + (goodnessValue_wrtNetworkMetrics[i][0] * relationshipWeights[((j*2)+1)]));  // R
			}
		}
		return qualityMetricValues;
	}

	// Calculating Individual Quality Metric Scores w.r.t all Sources for Thresholds-based-QoI-Scheme 
	public double[][] qualityMetricScores_QoIT(double[][] qualityMetricValues, double[] QoICriteriaWeights, int noofsources, int noofcriteria, 
			double[][] qualityMetricScores) throws IOException {
		//QMS_temp represents: QMS_(QoI/QoIT/TE)wrtQoIT_temp
		for (int i = 0; i < noofsources; i++){	 
			for (int j = 0; j < noofcriteria; j++){	
				qualityMetricScores[i][j] = qualityMetricValues[i][j] * QoICriteriaWeights[j];
			}
		}
		return qualityMetricScores;
	}


	// QoIT Scheme without using UAVs
	/*	public  LinkedList<Object> sourceSelection_QoIT(int noofcriteria, int noofsources, int totalNetworkNodes, int[] informationSourceList, int destinationNode, Graph graph, classNode[] nodes, classLink[] links, int[] nodeKeys, 
			WriteFile SrcDstPath_Data_QoIT, String current_time, int runStep, LinkedList<LinkedList<List<classNode>>> shortestPathsArray_forSelectedSources_toAllDestinations, 	int fileNumber, HashMap<String, classLink> link_CAU_QoIT, 
			HashMap<Integer, ApplicationDataRates> applicationDataRate, HashMap<String, NetworkMetricThresholdValues> networkMetricThresholdValues, double[] relationshipWeights, double[] QoIMetricPairwiseComparisons, 
			WriteFile computationOverhead_QoIT_Data, HashMap<Integer, selectedSourceInformation> selectedSourceList_QoIT, boolean flag_updatedInfo, Map<Integer, LinkedHashSet<Integer>> srcDstRouteMap_QoIT_updated, 
			Map<Integer, LinkedHashSet<Integer>> srcDstRouteMap_QoIT, HashMap<Integer, QualityMetricScores> QMS_QoITwrtQoIT_temp, HashMap<Integer, networkData_HashMaps> pnp_SelectedSource_QoITwrtQoIT, 
			HashMap<Integer, networkData_HashMaps> qms_SelectedSource_QoITwrtQoIT, double[] QoICriteriaWeights, double[] priority_of_QualityMetrics, int iteration, WriteFile QoITSelectedSource_QoIScore_Data,
			WriteFile QoITSelectedSource_QoITScore_Data) throws Exception {*/

	public int sourceSelection_QoIT_withoutUAV(double[][] networkMetricValues, double[][] goodnessValue_wrtNetworkMetrics, ArrayList<ArrayList<Integer>> srcDestPaths,
			int destinationNode) throws Exception {

		int no_networkCriteria = 4, selectedSource_QoIT=-10, noofsources = srcDestPaths.size();
		double[] priority_of_QualityMetrics = {0.4, 0.2, 0.1, 0.3};  //A,   C,   T,   R Vector Matrix of QoI Criteria Weights
		double[] QoICriteriaWeights = {0.4, 0.2, 0.1, 0.3};  //A,   C,   T,   R Vector Matrix of QoI Criteria Weights
		double relationshipWeights[] = {0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5}; 
		int Noof_QualityMetrics_Met[] = new int[noofsources]; // Number of metrics met
		double qualityMetricValues[][] = new double[noofsources][no_networkCriteria];
		double SourceQoITScore[] = new double[noofsources];
		double PriorityScore[] = new double[noofsources];
		double qualityMetricScores[][] = new double[noofsources][no_networkCriteria];
		//LinkedList<Object> obj = new LinkedList<Object>();
		//LinkedHashSet<Integer> srcDstRouteNodes = new LinkedHashSet<Integer>();

		for (int i = 0; i < noofsources; i++) { PriorityScore[i] = 0;  Noof_QualityMetrics_Met[i] = 0; SourceQoITScore[i] = 0; } 

		// Individual Quality Metric VALUES and SCORES w.r.t column: A, C, T, R
		qualityMetricValues =  sourceVsNetworkMetricRelationshipWeights(qualityMetricValues, goodnessValue_wrtNetworkMetrics, relationshipWeights, noofsources, no_networkCriteria);
		qualityMetricScores =  qualityMetricScores_QoIT(qualityMetricValues, QoICriteriaWeights, noofsources, no_networkCriteria, qualityMetricScores);

		for (int i = 0; i < noofsources; i++){	
			for (int j = 0; j < no_networkCriteria; j++){
				if(Double.compare(qualityMetricScores[i][j], QoICriteriaWeights[j]) == 0) { // Sum of Priorities of metrics met, Number of metric met, Sum of all -> QM_Score/QM_Threshold					
					PriorityScore[i] += priority_of_QualityMetrics[j]; 	Noof_QualityMetrics_Met[i] += 1;}
				SourceQoITScore[i] += qualityMetricScores[i][j];					
			}
		}

		for (int i = 0; i < noofsources; i++)
			System.out.println("PriorityScore: " + PriorityScore[i] + "; Noof_QualityMetrics_Met: " + Noof_QualityMetrics_Met[i] + "; QoIT Score: " + SourceQoITScore[i]);

		// Selecting Source w.r.t QoIT Scheme
		selectedSource_QoIT = calculating_parameters_QoIT(qualityMetricScores, QoICriteriaWeights, noofsources, destinationNode, PriorityScore, Noof_QualityMetrics_Met, 
				SourceQoITScore, srcDestPaths);
		
		System.out.println("selectedSource_QoIT: " + selectedSource_QoIT);
		
		try {
			int selectedSource_index = -1;
			for(int source_index = 0; source_index < networkMetricValues.length; source_index++) {
				if (selectedSource_QoIT == dtnrouting.theNodes.get(srcDestPaths.get(source_index).get(0)).ID-1) {
					selectedSource_index = source_index;
					break;
				}					
			}
			NetworkMetricValues_SelectedSource.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART, destinationNode, selectedSource_QoIT, networkMetricValues[selectedSource_index][2],
					networkMetricValues[selectedSource_index][0], networkMetricValues[selectedSource_index][3],networkMetricValues[selectedSource_index][1]);
			QualityMetricsScore_SelectedSource.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART, destinationNode, selectedSource_QoIT, qualityMetricScores[selectedSource_index][0], 
					qualityMetricScores[selectedSource_index][1], qualityMetricScores[selectedSource_index][2], qualityMetricScores[selectedSource_index][3]);
			PNP_SelectedSource.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART,  dtnrouting.deployedUAVs.size(),destinationNode, selectedSource_QoIT, PriorityScore[selectedSource_index], Noof_QualityMetrics_Met[selectedSource_index], 
					SourceQoITScore[selectedSource_index]);
			NetworkMetricsScore_SelectedSource.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART, destinationNode, selectedSource_QoIT, goodnessValue_wrtNetworkMetrics[selectedSource_index][2], goodnessValue_wrtNetworkMetrics[selectedSource_index][0],
					goodnessValue_wrtNetworkMetrics[selectedSource_index][3],goodnessValue_wrtNetworkMetrics[selectedSource_index][1]);
		
		} catch (IOException e) { e.printStackTrace(); }
		
		return selectedSource_QoIT;
		/*
		// Storing updated info wrt "pnp_SelectedSource_QoIwrtQoIT"
		for(int i = 0; i < infSourceList.length; i++) { if(selectedSource_QoIT == infSourceList[i]) { sourceIndex = i; break; } }

		if(selectedSource_QoIT != -1) {
			HashMap<String,String> predecessors = new HashMap<String,String>();
			Dijkstra dijkstra = new Dijkstra(graph, Integer.toString(selectedSource_QoIT), link_CAU_QoIT);
			predecessors =  dijkstra.predecessorsList(graph, Integer.toString(selectedSource_QoIT), link_CAU_QoIT).get(0);
			double Bandwidth = Double.MAX_VALUE, informationUtility = 0.0, linkIntegrity = 0.0, bandwidth_and_communicationOverhead[];
			double accuracy = 0.0, completeness = 0.0, timeliness = 0.0, reliability = 0.0;
			boolean flag_sameAs_previousPath = false;
			List<classNode> path = shortestPathsArray_forSelectedSources_toAllDestinations.get(destinationNode).get(sourceIndex);

			// Exploring Network Metric Values possessed by Src-Dst route
			double assigned_bandwidth = networkMetrics.allotedBandwidth(Integer.toString(selectedSource_QoIT), Bandwidth, graph, nodes[destinationNode].getLabel(), predecessors, applicationDataRate, link_CAU_QoIT, 
					networkMetricThresholdValues);	
			hopCount = networkMetrics.hopCount(Integer.toString(destinationNode), graph, nodes, selectedSource_QoIT, link_CAU_QoIT);
			linkIntegrity = networkMetrics.getLinkIntegrity(nodeKeys, Integer.toString(selectedSource_QoIT), linkIntegrity, graph, nodes[destinationNode], predecessors);
			informationUtility = networkMetrics.getInformationUtility(informationUtility, Integer.toString(selectedSource_QoIT));

			// New implementation			
			//populating srcDstRouteNodes with src-dst path nodes
			for(int pathLength = 0; pathLength <= hopCount; pathLength++)  srcDstRouteNodes.add(Integer.parseInt(path.get(pathLength).getLabel()));		

			// Storing selected src-dst route into "srcDstRouteMap_QoI_updated" (for each iteration) and "srcDstRouteMap_QoI" (only when n/w info is propagated)
			srcDstRouteMap_QoIT_updated.put(destinationNode, srcDstRouteNodes);
			if(flag_updatedInfo == true) srcDstRouteMap_QoIT.put(destinationNode, srcDstRouteNodes);

			// To decide what to write to the "SrcDstPath_Data_QoI" file i.e. either -1 or the path (if )
			LinkedHashSet<Integer> srcDstRouteNodes_updated  = srcDstRouteMap_QoIT_updated.get(destinationNode), srcDstRouteNodes_previous = srcDstRouteMap_QoIT.get(destinationNode);
			Iterator<Integer> srcDstUpdatedRoute_Iterator = srcDstRouteNodes_updated.iterator(), srcDstPreviousRoute_Iterator = srcDstRouteNodes_previous.iterator();

			if(srcDstRouteNodes_updated.size() == srcDstRouteNodes_previous.size()) {
				for(int i = 0; i < srcDstRouteNodes_previous.size(); i++) {				
					if(srcDstUpdatedRoute_Iterator.next() == srcDstPreviousRoute_Iterator.next())  flag_sameAs_previousPath = true;
					else                                                                         { flag_sameAs_previousPath = false; break; }
				}
			}

			if(flag_sameAs_previousPath == true) { 
				for(int pathLength = 0; pathLength <= hopCount; pathLength++) 
					SrcDstPath_Data_QoIT.writeToFile(iteration, path.get(pathLength).getLabel(), hopCount, pathLength);
			}
			else SrcDstPath_Data_QoIT.writeToFile(iteration, destinationNode, -3);

			Object KeySet[] = QMS_QoITwrtQoIT_temp.keySet().toArray();
			for(int i = 0; i < KeySet.length; i++) {
				QualityMetricScores qms = QMS_QoITwrtQoIT_temp.get((int) KeySet[i]);
				if(qms.get_sourceNode() == selectedSource_QoIT) {
					accuracy = qms.get_Accuracy(); completeness = qms.get_Completeness(); timeliness = qms.get_Timeliness(); reliability = qms.get_Reliability();
					break;   					
				}
			}	

			// Storing non-updated info wrt "pnp_SelectedSource_QoITwrtQoIT"
			if (flag_updatedInfo == true) {
				pnp_SelectedSource_QoITwrtQoIT.put(destinationNode, new networkData_HashMaps(iteration, destinationNode, selectedSource_QoIT, PriorityScore[sourceIndex], Noof_QualityMetrics_Met[sourceIndex], SourceQoITScore[sourceIndex]));					
				qms_SelectedSource_QoITwrtQoIT.put(destinationNode, new networkData_HashMaps(iteration, destinationNode, selectedSource_QoIT, accuracy, completeness, timeliness, reliability));
				QoITSelectedSource_QoITScore_Data.writeToFile(runStep, destinationNode, selectedSource_QoIT, SourceQoITScore[sourceIndex]);
			}
			else {
				srcDstRouteNodes_updated  = srcDstRouteMap_QoIT_updated.get(destinationNode); srcDstRouteNodes_previous = srcDstRouteMap_QoIT.get(destinationNode); 
				srcDstUpdatedRoute_Iterator  = srcDstRouteNodes_updated.iterator(); srcDstPreviousRoute_Iterator = srcDstRouteNodes_previous.iterator();
				flag_sameAs_previousPath = false;

				if(srcDstRouteNodes_updated.size() == srcDstRouteNodes_previous.size()) {
					for(int i = 0; i < srcDstRouteNodes_previous.size(); i++) {
						if( srcDstUpdatedRoute_Iterator.next() == srcDstPreviousRoute_Iterator.next())  flag_sameAs_previousPath = true;
						else 									                                      { flag_sameAs_previousPath = false; break; }
					}

					if (flag_sameAs_previousPath == true) {
						pnp_SelectedSource_QoITwrtQoIT.put(destinationNode, new networkData_HashMaps(iteration, destinationNode, selectedSource_QoIT, PriorityScore[sourceIndex], Noof_QualityMetrics_Met[sourceIndex], SourceQoITScore[sourceIndex]));
						qms_SelectedSource_QoITwrtQoIT.put(destinationNode, new networkData_HashMaps(iteration, destinationNode, selectedSource_QoIT, accuracy, completeness, timeliness, reliability));
					}
					else {
						pnp_SelectedSource_QoITwrtQoIT.put(destinationNode, new networkData_HashMaps(iteration, destinationNode, selectedSource_QoIT, 0, 0, 0));	
						qms_SelectedSource_QoITwrtQoIT.put(destinationNode, new networkData_HashMaps(iteration, destinationNode, selectedSource_QoIT, 0.0, 0.0, 0.0, 0.0));
					}
				}
				else {
					pnp_SelectedSource_QoITwrtQoIT.put(destinationNode, new networkData_HashMaps(iteration, destinationNode, selectedSource_QoIT, 0, 0, 0));	
					qms_SelectedSource_QoITwrtQoIT.put(destinationNode, new networkData_HashMaps(iteration, destinationNode, selectedSource_QoIT, 0.0, 0.0, 0.0, 0.0));
				}
			}

			obj.add(selectedSource_QoIT);
			obj.add(predecessors);
			obj.add(assigned_bandwidth);
			obj.add(hopCount);
			obj.add(linkIntegrity);
			obj.add(informationUtility);
			return obj;
		}

		else {	
			SrcDstPath_Data_QoIT.writeToFile(iteration, destinationNode, -1);	
			pnp_SelectedSource_QoITwrtQoIT.put(destinationNode, new networkData_HashMaps(iteration, destinationNode, -1, 0, 0, 0));
			qms_SelectedSource_QoITwrtQoIT.put(destinationNode, new networkData_HashMaps(iteration, destinationNode, -1, 0.0, 0.0, 0.0, 0.0));
			obj.add(selectedSource_QoIT);
			return obj;
		}*/
	}

	public int calculating_parameters_QoIT(double[][] qualityMetricScores, double[] QoICriteriaWeights, int noofsources, int destinationNode, 
			double[] PriorityScore, int[] Noof_QualityMetrics_Met, double[] SourceQoITScore, ArrayList<ArrayList<Integer>> srcDestPaths) throws IOException {

		// array of only maximum Priority elements, array of number of metrics met for maximum Priority elements, array of sources of maximum Priority elements
		int selectedSource = -1, sourceCounter = 0, noofMetricsMet_maxPriorityScore[] = null, infSrcList_maxPriorityScore[] = null;
		double sourceQoITScore_maxPriorityScore[] = null, maxPriorityScore[] = null, maxPriority = 0.0;

		// A. 1
		// Counting No. of Sources satisfying all Quality Metric Thresholds 
		for (int i = 0; i < noofsources; i++){		
			if((qualityMetricScores[i][0] == QoICriteriaWeights[0]) && (qualityMetricScores[i][1] == QoICriteriaWeights[1]) && 
					(qualityMetricScores[i][2] == QoICriteriaWeights[2]) && (qualityMetricScores[i][3] == QoICriteriaWeights[3])) 
				sourceCounter++;				
		}

		// Identifying Sources which satisfy all Quality Metric Thresholds
		int thresholdSatisfyingSources [] = new int[sourceCounter], k = 0;
		for (int i = 0; i < noofsources; i++) {		
			if((qualityMetricScores[i][0] == QoICriteriaWeights[0]) && (qualityMetricScores[i][1] == QoICriteriaWeights[1]) && (qualityMetricScores[i][2] == QoICriteriaWeights[2]) && (qualityMetricScores[i][3] == QoICriteriaWeights[3])) {
				thresholdSatisfyingSources[k] = dtnrouting.theNodes.get(srcDestPaths.get(i).get(0)).ID-1;
				k++;
			} }

		// A. 2
		// PriorityScore ={9,6,7,7,9,5}; Noof_QualityMetrics_Met = {3,3,3,2,3,2}; infSrcList ={0,1,2,3,4,5}
		// Finding Maximum of Array: PriorityScore i.e. {9, 6, 7, 7, 9, 5} = 9
		if(thresholdSatisfyingSources.length == 0) {
			
			maxPriority = PriorityScore[0]; 
			for (int i = 1; i < PriorityScore.length; i++) 	 
				if(Double.compare(maxPriority, PriorityScore[i]) < 0) 
					maxPriority = PriorityScore[i];  

			if(Double.compare(maxPriority, 0.0) > 0) {
				int  count = 0, a = 0;
				
				// Count elements with maximum PriorityScore i.e. {9, 6, 7, 7, 9, 5} = 2(9)
				for(int i = 0; i < PriorityScore.length; i++) 
					if(Double.compare(maxPriority, PriorityScore[i]) == 0) 
						count++; 

				maxPriorityScore = new double[count];     
				noofMetricsMet_maxPriorityScore = new int[count]; 
				infSrcList_maxPriorityScore = new int[count]; 
				sourceQoITScore_maxPriorityScore = new double[count];

				// populating arrayof_maxPriorityMet, noofMetricsMet_maxPriorityScore, infSrcList_maxPriorityScore, sourceQoITScore_maxPriorityScore
				// in accordance to maxPriority
				for(int i = 0; i < PriorityScore.length; i++) {
					if(Double.compare(maxPriority, PriorityScore[i]) == 0) {
						maxPriorityScore[a] = PriorityScore[i];				// set array to 9, 9
						noofMetricsMet_maxPriorityScore[a] = Noof_QualityMetrics_Met[i];     // set array to 3, 3
						infSrcList_maxPriorityScore[a] = dtnrouting.theNodes.get(srcDestPaths.get(i).get(0)).ID-1;  // set array to 0, 4
						sourceQoITScore_maxPriorityScore[a] = SourceQoITScore[i];
						a++;
					} 
				} 
			} 
		}

		selectedSource = selectedSource_QoIT(thresholdSatisfyingSources, SourceQoITScore, selectedSource, maxPriority, maxPriorityScore, infSrcList_maxPriorityScore, 
				noofMetricsMet_maxPriorityScore, sourceQoITScore_maxPriorityScore, destinationNode, noofsources, srcDestPaths);

		return selectedSource;
	}

	// QoIT-based Source Selection: w.r.t user's Network/Quality Metric Threshold Values
	public int selectedSource_QoIT(int[] thresholdSatisfyingSources, double[] SourceQoITScore, int selectedSource, double maxPriority, double[] maxPriorityScore, 
			int[] infSrcList_maxPriorityScore, int[] noofMetricsMet_maxPriorityScore, double[] sourceQoITScore_maxPriorityScore, int destinationNode, int noofsources,
			ArrayList<ArrayList<Integer>> srcDestPaths) throws IOException{

		int  min_NoofMetricsMet; 
		Random random = new Random(); 
		double maxQoITScore; 

		// Selecting QoIT Source
		if(thresholdSatisfyingSources.length >= 1)	{		 			// if number of thresholdSatisfyingSources >= 1
			selectedSource = thresholdSatisfyingSources[0];
			for(int i = 1; i < thresholdSatisfyingSources.length; i++) { 
				if (random.nextBoolean() == true) 
					selectedSource = thresholdSatisfyingSources[i]; 
			}
		}
		else if (Double.compare(maxPriority, 0.0) > 0){
			if(maxPriorityScore.length == 1) 
				selectedSource = infSrcList_maxPriorityScore[0];  		// if only one source having maximum Priority Score
			else {                         								// if multiple sources having maximum Priority Score
				min_NoofMetricsMet = noofMetricsMet_maxPriorityScore[0]; 
				maxQoITScore = sourceQoITScore_maxPriorityScore[0]; 
				selectedSource = infSrcList_maxPriorityScore[0];

				for (int i = 1; i < noofMetricsMet_maxPriorityScore.length; i++) {
					if (min_NoofMetricsMet > noofMetricsMet_maxPriorityScore[i])  {					// 3 (4,2,1) > 2 (4,3) , min_NoofMetricsMet = 2							 
						min_NoofMetricsMet = noofMetricsMet_maxPriorityScore[i]; 
						maxQoITScore = sourceQoITScore_maxPriorityScore[i]; 
						selectedSource = infSrcList_maxPriorityScore[i];	
					}
					else if (min_NoofMetricsMet == noofMetricsMet_maxPriorityScore[i]) {		    // 2 (4,3) == 2 (4,3)	
						if(Double.compare(maxQoITScore, sourceQoITScore_maxPriorityScore[i]) < 0) { 
							maxQoITScore = sourceQoITScore_maxPriorityScore[i]; 
							selectedSource = infSrcList_maxPriorityScore[i]; 
						}
						else if ((Double.compare(maxQoITScore, sourceQoITScore_maxPriorityScore[i]) == 0) && (random.nextBoolean() == true)) 
							selectedSource = infSrcList_maxPriorityScore[i];	
					} 
				} 
			} 
		}
		else if (Double.compare(maxPriority, 0.0) == 0) { 
			maxQoITScore = SourceQoITScore[0]; 
			selectedSource = dtnrouting.theNodes.get(srcDestPaths.get(0).get(0)).ID-1;
			for (int i = 1; i < SourceQoITScore.length; i++) {
				if(maxQoITScore < SourceQoITScore[i]) { 
					maxQoITScore = SourceQoITScore[i]; 
					selectedSource = dtnrouting.theNodes.get(srcDestPaths.get(i).get(0)).ID-1; 
				}
				else if (maxQoITScore == SourceQoITScore[i] && random.nextBoolean() == true) 
					selectedSource = dtnrouting.theNodes.get(srcDestPaths.get(i).get(0)).ID-1; 
			} 
		}
		else  selectedSource = -1; 

		return selectedSource;
	}

}

