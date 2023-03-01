//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import java.io.IOException;
import java.util.*;


//------------------------------------------------------------------------------
//START OF CLASS packet
public class QoITPATH extends dtnrouting
{

	private static final long serialVersionUID = 1L;
	public int num_packets,ttl_packets,size_packets;
	public PlayField pf = new PlayField();
	public shortestPath  sp;
	Random rand=new Random();
    public networkMetricValues_AlongPath nmV = new networkMetricValues_AlongPath();

    //******************************************************************************

	public  QoITPATH(){}

	//******************************************************************************
	public void updateAdjacencyMatrix() {
		int total_nodes = dtnrouting.liveNodeIDs.size();
		dtnrouting.adjacencyMatrix = new double[total_nodes][total_nodes];
		for (int i = 0; i < (total_nodes-1); i++) dtnrouting.adjacencyMatrix[i][i] = 0;
		pf.FindNeighborhoods();
		dtnrouting.adjacencyMatrix[total_nodes-1][total_nodes-1]=0;	
	}
	
	//******************************************************************************
	public void ShortestPathsSD() {
	
		int total_nodes = dtnrouting.liveNodeIDs.size();
		int s_counter = 0, d_counter = 0, liveNodes_counter = 0, i=0;
		// Give size to the destination source pair
		dtnrouting.destsourcePair = new int [dtnrouting.liveDestinationIDs.size()];
     	// Initialize the Dynamic TSA arrays
		dtnrouting.RR=new int[total_nodes];
		dtnrouting.CR=new int[total_nodes];
		dtnrouting.RC=new int[total_nodes];
		dtnrouting.RA=new int[total_nodes]; 
		dtnrouting.PP=new int[total_nodes];
		dtnrouting.EP=new int[total_nodes];
		dtnrouting.Result=new int[total_nodes];
		
		// Compute number of sources and destinations
		dtnrouting.source_index = new int[dtnrouting.liveSourceIDs.size()];
		dtnrouting.dest_index = new int[dtnrouting.liveDestinationIDs.size()];
		dtnrouting.liveNodes_index = new int[dtnrouting.liveNodeIDs.size()];	
		
		//System.out.println("Last Live Node ID: "+dtnrouting.liveNodeIDs.get(dtnrouting.liveNodeIDs.size()-1));
		//System.out.println("Last Node ID: "+dtnrouting.theNodes.get(dtnrouting.theNodes.size()-1).ID);
		
		for (i = 0; i < (total_nodes-1); i++) {
			dtnrouting.liveNodes_index[liveNodes_counter++] = i;
			
			if(dtnrouting.theNodes.get(dtnrouting.liveNodeIDs.get(i)).name.substring(0,1).equals("S"))
				{
					dtnrouting.theNodes.get(dtnrouting.liveNodeIDs.get(i)).ptD = null;
					dtnrouting.source_index[s_counter++] = i;
				}
			else if(dtnrouting.theNodes.get(dtnrouting.liveNodeIDs.get(i)).name.substring(0,1).equals("D"))
				dtnrouting.dest_index[d_counter++] = i;
			}
		
		dtnrouting.liveNodes_index[liveNodes_counter] = i;
		//System.out.println("LineNodeIDs: "+dtnrouting.liveNodeIDs.get(total_nodes-1));
		if(dtnrouting.theNodes.get(dtnrouting.liveNodeIDs.get(total_nodes-1)).name.substring(0,1).equals("S"))
			dtnrouting.source_index[s_counter] = total_nodes-1;
		else if(dtnrouting.theNodes.get(dtnrouting.liveNodeIDs.get(total_nodes-1)).name.substring(0,1).equals("D"))
			dtnrouting.dest_index[d_counter] = total_nodes-1;
		
		// Update Adjacency Matrix
		updateAdjacencyMatrix();
		
		// Find shortest path from each source to each destination------
		for(int s=0; s < dtnrouting.liveSourceIDs.size(); s++) {
			sp = new shortestPath();	
			// For this node
			Node startnode = dtnrouting.theSources.get(dtnrouting.liveSourceIDs.get(s));
			startnode.ptD = new pathToDestination(dtnrouting.liveDestinationIDs.size());
			
			//dijkstra(double[][] adjacencyMatrix, int startVertex, int [] destinations) 
			sp.runDijkstra(dtnrouting.adjacencyMatrix, dtnrouting.dest_index, dtnrouting.source_index[s], startnode);	
			//sp.runDijkstra(dtnrouting.adjacencyMatrix, dtnrouting.liveNodes_index, dtnrouting.liveNodeIDs.get(s), startnode);	
			sp=null;}

		//Call best path
		BestPathsSD();
	}// End of Method
	
	

	//*****************************************************************************
	public void BestPathsSD() {

		//1. Destination-wise Source-Destination Paths i.e. s1:s2:s3..:sn-->d1, s1:s2:s3..:sn-->d2, ...s1:s2:s3..:sn-->dn
		ArrayList<ArrayList<ArrayList<Integer>>> srcDestPaths_All = new ArrayList<ArrayList<ArrayList<Integer>>>();
		ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> sub_srcDstPath_meetingThresholds = new ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>();
		ArrayList<ArrayList<Integer>> srcDestPaths = null;

		/******************************************************************************/
		
		if(dtnrouting.SIMULATION_PART==1) // only call one for a new simulation
			nmV.setInformationUtility();
		/******************************************************************************/  

		for(int di = 0; di < dtnrouting.dest_index.length; di++) {
			srcDestPaths = new ArrayList<ArrayList<Integer>>();
			int dest_id = dtnrouting.liveNodeIDs.get(dtnrouting.dest_index[di]);
			//CHANGE liveNodes to liveSources
			for(int s = 0; s < dtnrouting.source_index.length; s++) {
				int source_id = dtnrouting.liveNodeIDs.get(dtnrouting.source_index[s]);
				Node source = dtnrouting.theNodes.get(source_id);
							
				if((source.ptD.paths.get(di).size()) != 0) 
					srcDestPaths.add(source.ptD.paths.get(di));
	
			}
			//System.out.println("Paths to D"+dest_id+": "+srcDestPaths);
			srcDestPaths_All.add(srcDestPaths);}
		
		
		/******************************************************************************/ 
		//2. Exploring Network Metric Values possessed by particular Src-Dst path AND 
		// Evaluating goodness value of a metric i.e. percentage of user need met w.r.t a metric
		// Choose source for each destination node
		for(int di = 0; di < dtnrouting.dest_index.length; di++) {
			int dest_id = dtnrouting.liveNodeIDs.get(dtnrouting.dest_index[di]);
			Node node = dtnrouting.theNodes.get(dest_id);
			int noof_availableSources = srcDestPaths_All.get(di).size();
			int sourceCount = 1;
			
			for(int i=0; i< srcDestPaths_All.get(di).size();i++) // Number of available sources for a particular querying node
				if (srcDestPaths_All.get(di).get(i).size() > sourceCount)
						sourceCount = srcDestPaths_All.get(di).get(i).size();		
			
			    if(sourceCount > 1 & !dtnrouting.packetDelivedDestinations.contains(node)) {
				// row: no. of available sources; [column: no. of network metrics; 0 = HC; 1 = IU; 2 = BW; 3 = PI] 
				double networkMetricValues[][] = new double[noof_availableSources][4];
				double goodnessValue_wrtNetworkMetrics[][] = new double[noof_availableSources][4];

				for(int source_index = 0; source_index < noof_availableSources; source_index++) {
					int hopCount = srcDestPaths_All.get(di).get(source_index).size() - 1;
					
					// Exploring Network Metric Values possessed by particular Src-Dst route
					if(hopCount != 0) {	
						networkMetricValues_AlongPath nmv = new networkMetricValues_AlongPath();				
						networkMetricValues[source_index][0] = hopCount;
						networkMetricValues[source_index][1] = dtnrouting.theNodes.get(dtnrouting.liveNodeIDs.get(srcDestPaths_All.get(di).get(source_index).get(0))).informationUtility;							
						networkMetricValues[source_index][2] = nmv.getPathBandwidth(srcDestPaths_All.get(di).get(source_index));
						networkMetricValues[source_index][3] = nmv.getPathReliability(srcDestPaths_All.get(di).get(source_index)); 
      				} 
					
					//System.out.print("\nS:"+source_index+",D:"+dest_id+",HC: " + networkMetricValues[source_index][0]+", IU: " + networkMetricValues[source_index][1]+", BW: " + networkMetricValues[source_index][2]+", PR: " + networkMetricValues[source_index][3]);
					// Evaluating goodness value of a metric i.e. percentage of user need met w.r.t a metric	
					// Hop-Count
					
								goodnessValue_wrtNetworkMetrics[source_index][0] = 
										(networkMetricValues[source_index][0] <= node.neworkMetricRequirements[0])? 
												1: node.neworkMetricRequirements[0] / networkMetricValues[source_index][0];
			
								// Information Utility
								goodnessValue_wrtNetworkMetrics[source_index][1] = 
										(networkMetricValues[source_index][1] >= node.neworkMetricRequirements[1])?
												1: networkMetricValues[source_index][1] / node.neworkMetricRequirements[1];
			
								// Bandwidth
								goodnessValue_wrtNetworkMetrics[source_index][2] =
										(networkMetricValues[source_index][2] >= node.neworkMetricRequirements[2])?
												1: networkMetricValues[source_index][2] / node.neworkMetricRequirements[2];
			
								// Path-Integrity or Reliability
								goodnessValue_wrtNetworkMetrics[source_index][3] = 
										(networkMetricValues[source_index][3] >= node.neworkMetricRequirements[3])?
												1: networkMetricValues[source_index][3] / node.neworkMetricRequirements[3];
					}

				    // Choose best source for each destination AND update source-dest pair array


					//System.out.println(srcDestPaths_All.get(dest_index)+","+dest_index);
					SourceSelection ss = new SourceSelection();
					
					try {
						
						//Call it before UAV placement in Part 4
						if(dtnrouting.SIMULATION_PART == 4 && !dtnrouting.RunForUQoIT) {
							dtnrouting.destsourcePair[di] = ss.sourceSelection_For_UQoIT(networkMetricValues, goodnessValue_wrtNetworkMetrics, srcDestPaths_All.get(di), dest_id);
							//System.out.println("\n(NO uav) D: "+  dtnrouting.liveNodeIDs.get(dtnrouting.dest_index[di]) + ", Selected S: " + dtnrouting.destsourcePair[di]);
						}
						// //Call it after UAV placement or in PART 1, 2 and 3
						if(dtnrouting.SIMULATION_PART < 4 || dtnrouting.RunForUQoIT){
							dtnrouting.destsourcePair[di] = ss.sourceSelection_QoIT_PrintResults(networkMetricValues, goodnessValue_wrtNetworkMetrics, srcDestPaths_All.get(di), dest_id);
						    //System.out.println("\n(UAV) D: "+  dtnrouting.liveNodeIDs.get(dtnrouting.dest_index[di]) + ", Selected S: " + dtnrouting.destsourcePair[di]);
								
						}
						
					    } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();}
			    	}
			
			  	    else if((dtnrouting.SIMULATION_PART < 4 || dtnrouting.RunForUQoIT) & !dtnrouting.packetDelivedDestinations.contains(node)){ // If destination has no path to any source
						try {
							    // Select the source with shortest euclidean
								// distance to destination
								double min_distance=100000;
								int index=dtnrouting.liveSourceIDs.get(0);
								for(int i = 0; i < srcDestPaths_All.get(di).size(); i++){
									Node sourceNode =  dtnrouting.theSources.get(i);
									double d = (Math.sqrt(Math.pow(node.location.x - sourceNode.location.x,2) + Math.pow(node.location.y -sourceNode.location.y, 2)));
									if( min_distance > d ){
										min_distance = d;
										index = dtnrouting.liveSourceIDs.get(i);}}
								dtnrouting.destsourcePair[di] = index;
								//System.out.println("\nD: "+  dtnrouting.liveNodeIDs.get(dtnrouting.dest_index[di]) + ", Nearest Selected S: " + dtnrouting.destsourcePair[di]);
								dtnrouting.lowUserUsability_destinations.add(dest_id);
								NetworkMetricValues_SelectedSource.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART,  dest_id, -1, 0, 0, 0, 0);
								QualityMetricsScore_SelectedSource.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART, dest_id, -1, 0, 0, 0, 0);
								PNP_SelectedSource.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART,dtnrouting.deployedUAVs.size(), dest_id, -1, 0, 0, 0);
								NetworkMetricsScore_SelectedSource.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART, dest_id, -1, 0, 0, 0, 0);
								} catch (IOException e) { e.printStackTrace(); }				
				  			}} 

	} //End Method

	//******************************************************************************	
	public void setInitialSource() {		
		// For each destination and source pair
		for(int di = 0; di < dtnrouting.dest_index.length; di++) {
			int dest_id = dtnrouting.liveNodeIDs.get(dtnrouting.dest_index[di]);
			// Destination chooses number of packets randomly
			Node destNode = dtnrouting.theNodes.get(dest_id);
			Node sourceNode = dtnrouting.theNodes.get(dtnrouting.destsourcePair[di]);
			
			int numberPackets = destNode.nodePackets.size();
			dtnrouting.sdpTA.append("\n "+sourceNode.ID+"-->"+destNode.ID+": "+destNode.nodePackets.size());
			
			
			// Stores the path a packet will take from the shortest path from its source to destination
			// Stores them in its selected source buffer
			for(int j=0; j< numberPackets; j++) {//number of packets that each source will transmit..
				Packet packetObj = destNode.nodePackets.get(j);
				int numberHops = sourceNode.ptD.paths.get(di).size();
				
				// Packet will follow the shortest path from source to destination---------
				for(int c=0; c < numberHops; c ++) {
							if(sourceNode.ptD.paths.get(di).get(c)==(-1)) 
								{ // store no path for packet
								break;} 
							else {
								// Path that packet will follow from 
								packetObj.pathHops.add(dtnrouting.theNodes.get(sourceNode.ptD.paths.get(di).get(c)));
								}
				}
				
				

				// Store packet to inside source buffer -----------------------------------
				if(sourceNode.queueSizeLeft > packetObj.packetSize)
				{    
					sourceNode.queueSizeLeft-=packetObj.packetSize; //update queue space after putting packet in source
					sourceNode.packetID.add(packetObj.ID); //Store ID of packet in the source as Hash value
					packetObj.sourceNode_ofpacket=sourceNode;
					// These packets have no path left
					// THE HOP IS NOT THERE
					if(packetObj.pathHops.size()==0) {
						sourceNode.DestNPacket.put(packetObj,null);
						sourceNode.number_packet_arrived+=1;}
					else
						sourceNode.DestNPacket.put(packetObj,destNode);

					//dtnrouting.sdpTA.append("\n "+sourceNode.ID+"-->"+destNode.ID+" ("+packetObj.ID+")");
				} else;}  //all packets of the destination assigned to the source of destination.     
	}} // End of Method 

	//******************************************************************************
	//Nodes included in the Paths of Source to Destination
	public boolean arePathsConnected() {
			  // For each destination and source pair-------------------
			  for(int di = 0; di < dtnrouting.liveDestinationIDs.size(); di++) {
					Node destNode = dtnrouting.theNodes.get(dtnrouting.liveDestinationIDs.get(di));
				
				if(!packetDelivedDestinations.contains(destNode)) {
					Node sourceNode = dtnrouting.theNodes.get(dtnrouting.destsourcePair[di]);
					for(int c=0; c < (sourceNode.ptD.paths.get(di).size()-1); c ++) {
						if(sourceNode.ptD.paths.get(di).get(c)==(-1)) 
								break;
						else {
								Node n1 = dtnrouting.theNodes.get(sourceNode.ptD.paths.get(di).get(c));
							    Node n2 = dtnrouting.theNodes.get(sourceNode.ptD.paths.get(di).get(c+1));
								double distance_km = Math.sqrt(Math.pow((n1.location.y-n2.location.y),2) + Math.pow((n1.location.x-n2.location.x),2));
								double r = n1.getRadioRange() + n2.getRadioRange();
								if(distance_km > r) return false;
							}
								
							}}
				}
			  return true;
	}
	
	//******************************************************************************
	public void pathRelays() {
		  // For each destination and source pair-------------------
		  for(int di = 0; di < dtnrouting.dest_index.length; di++) {
			Node destNode = dtnrouting.theNodes.get(dtnrouting.liveNodeIDs.get(dtnrouting.dest_index[di]));
			
			if(!packetDelivedDestinations.contains(destNode)) {
				Node sourceNode = dtnrouting.theNodes.get(dtnrouting.destsourcePair[di]);
				for(int c=0; c < sourceNode.ptD.paths.get(di).size(); c ++) {
					if(sourceNode.ptD.paths.get(di).get(c)==(-1)) 
							break;
					else {
						Node n1 = dtnrouting.theNodes.get(sourceNode.ptD.paths.get(di).get(c));
						if(!dtnrouting.relayNodes.contains(n1)) 
							dtnrouting.relayNodes.add(n1);}
							
						}}
			}

	}

	//******************************************************************************
	// CHANGE SOURCE NODE AFTER FIRE DESTROYS A NODE
	public void changeSource() {
		
		// For each destination and source pair-------------------
		for(int di = 0; di < dtnrouting.dest_index.length; di++) {
			int dest_id = dtnrouting.liveNodeIDs.get(dtnrouting.dest_index[di]);
			
			// Destination chooses number of packets randomly
			Node destNode = dtnrouting.theNodes.get(dest_id);
			Node newSourceNode = dtnrouting.theNodes.get(dtnrouting.destsourcePair[di]);
			Node oldSourceNode = destNode.nodePackets.get(0).sourceNode_ofpacket;
			
			if(!dtnrouting.packetDelivedDestinations.contains(destNode)) {
			int numberPackets = destNode.nodePackets.size();
			dtnrouting.sdpTA.append("\nChange "+newSourceNode.ID+"-->"+destNode.ID+": "+destNode.nodePackets.size());
			
			for(int j=0; j< numberPackets; j++) {
				//Identify the packets not yet delivered to the destination nor expired
				if(!destNode.nodePackets.get(j).ispacketDelivered & !destNode.nodePackets.get(j).isTTLExpired ) {
					Packet packetObj = destNode.nodePackets.get(j);
					
					// Remove it from old source if not yet transmitted and move to new source
					if(oldSourceNode.DestNPacket.containsKey(packetObj)) {

						oldSourceNode.packetID.remove(packetObj.ID);
						oldSourceNode.queueSizeLeft+=packetObj.packetSize; // the whole space 
						oldSourceNode.DestNPacket.remove(packetObj);
				
					    int numberHops = newSourceNode.ptD.paths.get(di).size();
					    packetObj.pathHops.clear(); //Remove old path of the packet
					    
						// Packet will follow the shortest path from source to destination
						for(int c=0; c < numberHops; c ++) {
									if(newSourceNode.ptD.paths.get(di).get(c)==(-1)) 
										{ // store no path for packet
										break;} 
									else {
										// Path that packet will follow from 
										packetObj.pathHops.add(dtnrouting.theNodes.get(newSourceNode.ptD.paths.get(di).get(c)));
										}
						}
	

						// Store packet inside source buffer------------------------------
						if(newSourceNode.queueSizeLeft > packetObj.packetSize)
						{    
							newSourceNode.queueSizeLeft-=packetObj.packetSize; //update queue space after putting packet in it
							newSourceNode.packetID.add(packetObj.ID); //Store ID of packet in the source as Hash value
							packetObj.sourceNode_ofpacket=newSourceNode;
							
							//System.out.println(packetObj.packetName+", old source:"+oldSource.name+", newsource:"+sourceNode.name);
							if(packetObj.pathHops.size()==0) {
								newSourceNode.DestNPacket.put(packetObj,null);
								newSourceNode.number_packet_arrived+=1;}
							else
								newSourceNode.DestNPacket.put(packetObj,destNode);
							//System.out.println("Old SOURCE: "+oldSource.name+", of DEST: "+destNode.name);

							//dtnrouting.sdpTA.append("\n "+sourceNode.ID+"--"+destNode.ID+" ("+packetObj.ID+")");
						}   
					}  //all packets of the destination assigned to the source of dest.     
				} } }
			}// End of for loop

	} // End of Method
	//******************************************************************************
}//END OF packet CLASS
