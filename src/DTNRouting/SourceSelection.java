package DTNRouting;

import java.io.IOException;
import java.util.*;;

public class SourceSelection extends dtnrouting{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SourceSelection() { }
	
	///*******************************************************************************
	// Calculating Source Vs Network Metric Relationship Weights
	public double[][] sourceVsNetworkMetricRelationshipWeights(double[][] qualityMetricValues, double[][] goodnessValue_wrtNetworkMetrics, double[] relationshipWeights, int noofsources, int no_networkCriteria){
		//networkCriteria legends => 0 = HC, 1 = IU, 2 = BW, 3 = PI
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
	
	
	///*******************************************************************************
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

	///*******************************************************************************
	// QoIT Scheme with Print Results
	public int sourceSelection_QoIT_PrintResults(double[][] networkMetricValues, double[][] goodnessValue_wrtNetworkMetrics, ArrayList<ArrayList<Integer>> srcDestPaths,
			int dest_id) throws Exception {
			
		int no_networkCriteria = 4, selectedSource_QoIT=-10, noofsources = srcDestPaths.size();
		double[] priority_of_QualityMetrics = {0.4, 0.2, 0.1, 0.3};  //A,   C,   T,   R Vector Matrix of QoI Criteria Weights
		double[] QoICriteriaWeights = {0.4, 0.2, 0.1, 0.3};  //A,   C,   T,   R Vector Matrix of QoI Criteria Weights
		double relationshipWeights[] = {0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5}; 
		int Noof_QualityMetrics_Met[] = new int[noofsources]; // Number of metrics met
		double qualityMetricValues[][] = new double[noofsources][no_networkCriteria];
		double SourceQoITScore[] = new double[noofsources];
		double PriorityScore[] = new double[noofsources];
		double qualityMetricScores[][] = new double[noofsources][no_networkCriteria];


		for (int i = 0; i < noofsources; i++) { 
			PriorityScore[i] = 0;  Noof_QualityMetrics_Met[i] = 0; SourceQoITScore[i] = 0; 
			} 

		// Individual Quality Metric VALUES and SCORES w.r.t column: A, C, T, R
		qualityMetricValues =  sourceVsNetworkMetricRelationshipWeights(qualityMetricValues, goodnessValue_wrtNetworkMetrics, relationshipWeights, noofsources, no_networkCriteria);
		qualityMetricScores =  qualityMetricScores_QoIT(qualityMetricValues, QoICriteriaWeights, noofsources, no_networkCriteria, qualityMetricScores);

		for (int i = 0; i < noofsources; i++){	
			for (int j = 0; j < no_networkCriteria; j++){
				if(Double.compare(qualityMetricScores[i][j], QoICriteriaWeights[j]) == 0) { 
					// Sum of Priorities of metrics met, Number of metric met, Sum of all -> QM_Score/QM_Threshold					
					PriorityScore[i] += priority_of_QualityMetrics[j]; 	
					Noof_QualityMetrics_Met[i] += 1;}
				    SourceQoITScore[i] += qualityMetricScores[i][j];					
			}
		}

		// Selecting Source w.r.t QoIT Scheme
		selectedSource_QoIT = calculating_parameters_QoIT(qualityMetricScores, QoICriteriaWeights, noofsources, dest_id, PriorityScore, Noof_QualityMetrics_Met, 
				SourceQoITScore, srcDestPaths);
		
		try {
			int selectedSource_index = -1;
			for(int source_index = 0; source_index < networkMetricValues.length; source_index++) {
				Node node_dummy = dtnrouting.theNodes.get(srcDestPaths.get(source_index).get(0));
				if (selectedSource_QoIT == dtnrouting.theNodes.indexOf(node_dummy)) {
					selectedSource_index = source_index;
					break;
				}					
			}
			
			//Only Print if Simulation Part is 1 and 2
			// Or if Simulation Part 3 But UQOIT IS DONE
			// RESULTS STORED IN FOLLOWING FILES
			NetworkMetricValues_SelectedSource.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART, dest_id, selectedSource_QoIT, networkMetricValues[selectedSource_index][2],
					networkMetricValues[selectedSource_index][0], networkMetricValues[selectedSource_index][3],networkMetricValues[selectedSource_index][1]);
			
			QualityMetricsScore_SelectedSource.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART, dest_id, selectedSource_QoIT, qualityMetricScores[selectedSource_index][0], 
					qualityMetricScores[selectedSource_index][1], qualityMetricScores[selectedSource_index][2], qualityMetricScores[selectedSource_index][3]);
			
			PNP_SelectedSource.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART, dtnrouting.deployedUAVs.size(), dest_id, selectedSource_QoIT, PriorityScore[selectedSource_index], Noof_QualityMetrics_Met[selectedSource_index], 
					SourceQoITScore[selectedSource_index]);
			
			NetworkMetricsScore_SelectedSource.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART, dest_id, selectedSource_QoIT, goodnessValue_wrtNetworkMetrics[selectedSource_index][2], goodnessValue_wrtNetworkMetrics[selectedSource_index][0],
					goodnessValue_wrtNetworkMetrics[selectedSource_index][3],goodnessValue_wrtNetworkMetrics[selectedSource_index][1]);
		
		    } catch (IOException e) { e.printStackTrace(); }
		
		return selectedSource_QoIT;
		
	}

	
	///*******************************************************************************
	// FOR QoIT
	public int sourceSelection_For_UQoIT(double[][] networkMetricValues, double[][] goodnessValue_wrtNetworkMetrics, ArrayList<ArrayList<Integer>> srcDestPaths,
				int dest_id) throws Exception {
				
			int no_networkCriteria = 4, selectedSource_QoIT=-10, noofsources = srcDestPaths.size();
			double[] priority_of_QualityMetrics = {0.4, 0.2, 0.1, 0.3};  //A,   C,   T,   R Vector Matrix of QoI Criteria Weights
			double[] QoICriteriaWeights = {0.4, 0.2, 0.1, 0.3};  //A,   C,   T,   R Vector Matrix of QoI Criteria Weights
			double relationshipWeights[] = {0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5}; 
			int Noof_QualityMetrics_Met[] = new int[noofsources]; // Number of metrics met
			double qualityMetricValues[][] = new double[noofsources][no_networkCriteria];
			double SourceQoITScore[] = new double[noofsources];
			double PriorityScore[] = new double[noofsources];
			double qualityMetricScores[][] = new double[noofsources][no_networkCriteria];


			for (int i = 0; i < noofsources; i++) { 
				PriorityScore[i] = 0;  Noof_QualityMetrics_Met[i] = 0; SourceQoITScore[i] = 0; 
				} 

			// Individual Quality Metric VALUES and SCORES w.r.t column: A, C, T, R
			qualityMetricValues =  sourceVsNetworkMetricRelationshipWeights(qualityMetricValues, goodnessValue_wrtNetworkMetrics, relationshipWeights, noofsources, no_networkCriteria);
			qualityMetricScores =  qualityMetricScores_QoIT(qualityMetricValues, QoICriteriaWeights, noofsources, no_networkCriteria, qualityMetricScores);

			for (int i = 0; i < noofsources; i++){	
				for (int j = 0; j < no_networkCriteria; j++){
					if(Double.compare(qualityMetricScores[i][j], QoICriteriaWeights[j]) == 0) { 
						// Sum of Priorities of metrics met, Number of metric met, Sum of all -> QM_Score/QM_Threshold					
						PriorityScore[i] += priority_of_QualityMetrics[j]; 	
						Noof_QualityMetrics_Met[i] += 1;}
					    SourceQoITScore[i] += qualityMetricScores[i][j];					
				}
			}

			// Selecting Source w.r.t QoIT Scheme
			selectedSource_QoIT = calculating_parameters_QoIT(qualityMetricScores, QoICriteriaWeights, noofsources, dest_id, PriorityScore, Noof_QualityMetrics_Met, 
					SourceQoITScore, srcDestPaths);
			return selectedSource_QoIT;
			
		}
	
	///*******************************************************************************	
	public int calculating_parameters_QoIT(double[][] qualityMetricScores, double[] QoICriteriaWeights, int noofsources, int dest_id, 
			double[] PriorityScore, int[] Noof_QualityMetrics_Met, double[] SourceQoITScore, ArrayList<ArrayList<Integer>> srcDestPaths) throws IOException {

		// array of only maximum Priority elements, array of number of metrics met for maximum Priority elements, array of sources of maximum Priority elements
		int selectedSource = -1, sourceCounter = 0, noofMetricsMet_maxPriorityScore[] = null, infSrcList_maxPriorityScore[] = null;
		double sourceQoITScore_maxPriorityScore[] = null, maxPriorityScore[] = null, maxPriority = 0.0;

		// A. 1
		// Counting No. of Sources satisfying all Quality Metric Thresholds 
		for (int i = 0; i < noofsources; i++){		
			if((qualityMetricScores[i][0] == QoICriteriaWeights[0]) && (qualityMetricScores[i][1] == QoICriteriaWeights[1]) && 
					(qualityMetricScores[i][2] == QoICriteriaWeights[2]) && (qualityMetricScores[i][3] == QoICriteriaWeights[3]))
			{
				
				Node node = dtnrouting.theNodes.get(dest_id);
				int index_of_dest = dtnrouting.theDestinations.indexOf(node);
				dtnrouting.dest_statisfying_all_requirements[index_of_dest] =  1;
				sourceCounter++;				
		     }}
		
		// if there is no Source that satisfies all Quality Metric Thresholds
		if(sourceCounter == 0) {
			dtnrouting.lowUserUsability_destinations.add(dest_id);
		}

		// Identifying Sources which satisfy all Quality Metric Thresholds
		int thresholdSatisfyingSources [] = new int[sourceCounter], k = 0;
		for (int i = 0; i < noofsources; i++) {		
			if((qualityMetricScores[i][0] == QoICriteriaWeights[0]) && (qualityMetricScores[i][1] == QoICriteriaWeights[1]) && 
					(qualityMetricScores[i][2] == QoICriteriaWeights[2]) && (qualityMetricScores[i][3] == QoICriteriaWeights[3])) {
				Node node_dummy = dtnrouting.theNodes.get(srcDestPaths.get(i).get(0));
				thresholdSatisfyingSources[k] = dtnrouting.theNodes.indexOf(node_dummy);
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
						Node node_dummy = dtnrouting.theNodes.get(srcDestPaths.get(i).get(0));
						infSrcList_maxPriorityScore[a] = dtnrouting.theNodes.indexOf(node_dummy);  // set array to 0, 4
						sourceQoITScore_maxPriorityScore[a] = SourceQoITScore[i];
						a++;
					} 
				} 
			} 
		}

		selectedSource = selectedSource_QoIT(thresholdSatisfyingSources, SourceQoITScore, selectedSource, maxPriority, maxPriorityScore, infSrcList_maxPriorityScore, 
				noofMetricsMet_maxPriorityScore, sourceQoITScore_maxPriorityScore, dest_id, noofsources, srcDestPaths);

		return selectedSource;
	}

	///*******************************************************************************	
	// QoIT-based Source Selection: w.r.t user's Network/Quality Metric Threshold Values
	public int selectedSource_QoIT(int[] thresholdSatisfyingSources, double[] SourceQoITScore, int selectedSource, double maxPriority, double[] maxPriorityScore, 
			int[] infSrcList_maxPriorityScore, int[] noofMetricsMet_maxPriorityScore, double[] sourceQoITScore_maxPriorityScore, int dest_id, int noofsources,
			ArrayList<ArrayList<Integer>> srcDestPaths) throws IOException{

		//int initialSource = -1, 
		int min_NoofMetricsMet; 
		Random random = new Random(); 
		double maxQoITScore; 

		// Selecting QoIT Source
		if(thresholdSatisfyingSources.length >= 1)	{		 			// if number of thresholdSatisfyingSources >= 1
			
			//selectedSource = thresholdSatisfyingSources[0];
			Node destNode = dtnrouting.theNodes.get(dest_id);
			double min_distance=100000;
			int index=0;
			for(int i = 0; i < thresholdSatisfyingSources.length; i++){
				Node sourceNode =  dtnrouting.theNodes.get(thresholdSatisfyingSources[i]);
				double d = (Math.sqrt(Math.pow(destNode.location.x - sourceNode.location.x,2) + Math.pow(destNode.location.y -sourceNode.location.y, 2)));
				if( min_distance > d ){
					min_distance = d;
					index = i;
				}
			}
			selectedSource = thresholdSatisfyingSources[index];
		 
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
			Node node_dummy= dtnrouting.theNodes.get(srcDestPaths.get(0).get(0));
			selectedSource = dtnrouting.theNodes.indexOf(node_dummy);
			for (int i = 1; i < SourceQoITScore.length; i++) {
				if(maxQoITScore < SourceQoITScore[i]) { 
					maxQoITScore = SourceQoITScore[i]; 
					node_dummy= dtnrouting.theNodes.get(srcDestPaths.get(i).get(0));
					selectedSource = dtnrouting.theNodes.indexOf(node_dummy);
				}
				else if (maxQoITScore == SourceQoITScore[i] && random.nextBoolean() == true) {
					node_dummy= dtnrouting.theNodes.get(srcDestPaths.get(i).get(0));
					selectedSource = dtnrouting.theNodes.indexOf(node_dummy);
				}
			} 
		}
		else  selectedSource = -1; 

		return selectedSource;
	}

}
