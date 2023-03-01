package DTNRouting;

import java.util.*;

public class networkMetricValues_AlongPath {

	public Random rand = new Random();

	networkMetricValues_AlongPath() { }

	public int getPathReliability(ArrayList<Integer> srcDst_pathNodes) {
		int pathReliability = Integer.MAX_VALUE;
		//Do not include the reliability of destination-node for
		//computing the path reliability
		for(int i = 0; i < (srcDst_pathNodes.size()-1); i++) {
			Node node = dtnrouting.theNodes.get(srcDst_pathNodes.get(i));
			if(node.reliability < pathReliability)
				pathReliability = node.reliability;
		}
		return pathReliability;		
	}

	public double getPathBandwidth(ArrayList<Integer> srcDst_pathNodes) {
		double pathBandwidth = Double.MAX_VALUE, bandwidth = 0.0;
		for(int i = 0; i < srcDst_pathNodes.size()-1; i++) {
			//System.out.println("srcDst_pathNodes:"+i+","+srcDst_pathNodes.get(i));
			Node from_Node = dtnrouting.theNodes.get(srcDst_pathNodes.get(i)); 
			Node to_Node = dtnrouting.theNodes.get(srcDst_pathNodes.get(i+1));  
			bandwidth = 1/dtnrouting.adjacencyMatrix[dtnrouting.liveNodeIDs.indexOf(from_Node.ID)][dtnrouting.liveNodeIDs.indexOf(to_Node.ID)];

			if(bandwidth < pathBandwidth)
				pathBandwidth = bandwidth;
		}
		return pathBandwidth;
	}
	

	// Set source IU values randomly between 
	public void setInformationUtility() {
		
		for(int i = 0; i < dtnrouting.liveSourceIDs.size(); i++) {
			dtnrouting.theNodes.get(dtnrouting.liveSourceIDs.get(i)).informationUtility = rand.nextDouble() * (10.0 - 3.0) + 3.0;
		}
	}
	
	public ArrayList<Integer> subSrcDstPath_meeting_HCThreshold(ArrayList<Integer> srcDstPath, int HC_Threshold) {
		ArrayList<Integer> sub_srcDstPath_meetingHCThreshold = new ArrayList<Integer>();
		
		if(srcDstPath.size() > HC_Threshold) {
		// add source plus "2 < HC_Threshold" intermediate nodes to "sub_srcDstPath_meetingHCThreshold"
		for(int i = 0; i < HC_Threshold-1; i++) 
			sub_srcDstPath_meetingHCThreshold.add(srcDstPath.get(i));
		
		// add -1 (to later on replace it with a UAV) and destination node to "sub_srcDstPath_meetingHCThreshold"
		sub_srcDstPath_meetingHCThreshold.add(-1);
		sub_srcDstPath_meetingHCThreshold.add(srcDstPath.get(srcDstPath.size()-1));
		}
		
		else 
			sub_srcDstPath_meetingHCThreshold = srcDstPath;
		
		return sub_srcDstPath_meetingHCThreshold;
	}
	
	public ArrayList<Integer> subSrcDstPath_meeting_BWThreshold(ArrayList<Integer> srcDstPath, double BW_Threshold) {
		// -2 means ignore source; -1 means replace the original intermediate node with a UAV
		double link_bandwidth = 0.0;
		boolean insertDest = true;
		ArrayList<Integer> sub_srcDstPath_meetingBWThreshold = new ArrayList<Integer>();
		
		for(int i = 0; i < srcDstPath.size()-1; i++) {

			Node from_Node = dtnrouting.theNodes.get(srcDstPath.get(i)); 
			Node to_Node = dtnrouting.theNodes.get(srcDstPath.get(i+1));  
			
			link_bandwidth = 1/dtnrouting.adjacencyMatrix[dtnrouting.liveNodeIDs.indexOf(from_Node.ID)][dtnrouting.liveNodeIDs.indexOf(to_Node.ID)];
			
			// if "source -> 1st_intermediateNode" has link_bandwidth < BW_Threshold, then no need to consider this source for data retrieval
			if (i == 0 && link_bandwidth < BW_Threshold) {
				sub_srcDstPath_meetingBWThreshold.add(-2);
				insertDest = false;
				break;
			}
			// if Node_a->Node_b has link_bandwidth >= BW_Threshold, then insert Node_a in "sub_srcDstPath_meetingBWThreshold" for data forwarding
			else if (link_bandwidth >= BW_Threshold)  sub_srcDstPath_meetingBWThreshold.add(srcDstPath.get(i));
			/* if Node_a->Node_b has link_bandwidth < BW_Threshold, then insert "-1" (instead of Node_a) in "sub_srcDstPath_meetingBWThreshold" 
			   to indicate that we will need (later on) "UAV -> Node_b" to enable data forwarding with a rate >= BW_Threshold */
			else                                      sub_srcDstPath_meetingBWThreshold.add(-1);
		}
		// to insert the destinationNode at the end of "sub_srcDstPath_meetingBWThreshold" 
		if (insertDest == true)
		sub_srcDstPath_meetingBWThreshold.add(srcDstPath.get(srcDstPath.size()-1));
			
		return sub_srcDstPath_meetingBWThreshold;
	}
	
	public ArrayList<Integer> subSrcDstPath_meeting_PIThreshold(ArrayList<Integer> srcDstPath, int PI_Threshold) {
		// -2 means ignore source; -1 means replace the original intermediate node with a UAV
		ArrayList<Integer> sub_srcDstPath_meetingPIThreshold = new ArrayList<Integer>();
		boolean insertDest = true;
		
		for(int i = 0; i < srcDstPath.size()-1; i++) {
			Node node = dtnrouting.theNodes.get(srcDstPath.get(i));
			
			// if "source" has node.reliability < PI_Threshold, then no need to consider this source for data retrieval
			if (i == 0 && node.reliability < PI_Threshold) {
				sub_srcDstPath_meetingPIThreshold.add(-2);
				insertDest = false;
				break;
			}
			// if "Node_a" has node.reliability >= PI_Threshold, then insert Node_a in "sub_srcDstPath_meetingBWThreshold" for data forwarding
			else if (node.reliability >= PI_Threshold)  sub_srcDstPath_meetingPIThreshold.add(srcDstPath.get(i));
			/* if "Node_a" has node.reliability < PI_Threshold, then insert "-1" (instead of Node_a) in "sub_srcDstPath_meetingBWThreshold" 
			   to indicate that we will need (later on) "UAV" (instead of -1) to enable data forwarding with a UAV.reliability >= PI_Threshold */
			else                                      sub_srcDstPath_meetingPIThreshold.add(-1);
		}
		// to insert the destinationNode at the end of "sub_srcDstPath_meetingBWThreshold" 
		if (insertDest == true)
		sub_srcDstPath_meetingPIThreshold.add(srcDstPath.get(srcDstPath.size()-1));
			
		return sub_srcDstPath_meetingPIThreshold;
	}
	
	public ArrayList<Integer> subSrcDstPath_meeting_IUThreshold(ArrayList<Integer> srcDstPath, double IU_Threshold) {
		// -2 means ignore source
		ArrayList<Integer> sub_srcDstPath_meetingPIUThreshold = new ArrayList<Integer>();
		
		// if "source" has node.informationUtility < IU_Threshold, then no need to consider this source for data retrieval
		if(dtnrouting.theNodes.get(srcDstPath.get(0)).informationUtility < IU_Threshold)
			sub_srcDstPath_meetingPIUThreshold.add(-2);
		// use all the src-dst path
		else
			sub_srcDstPath_meetingPIUThreshold = srcDstPath;
			
		return sub_srcDstPath_meetingPIUThreshold;
	}
}
