// PACKAGE NAME
package DTNRouting;

import java.awt.TextArea;
//IMPORT PACKAGES
import java.io.IOException; 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

//import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
//******************************************************************************
// CLASS MADE FOR UPDATING THE INFORMATION DURING SIMULATION

public class UpdateInformation {
    //Instance Variables
	//RoutingProtocol rp;
    //RP_Performance rpp = new RP_Performance();
	FireLocation fire_loc = new FireLocation();
    Random rand;
    String result = "", tempresult="";
    public static int number_of_uavs = 0;
    public networkMetricValues_AlongPath nmV = new networkMetricValues_AlongPath();
	ProposedUAV propUAV = new ProposedUAV();

//******************************************************************************
//Constructor
public UpdateInformation(){ }

//******************************************************************************
//Update TTL and packet Latency
public void UpdateTTLandLatency()
{   
	  // String
	  
	  //1. Update simulation timer
	  dtnrouting.timer+=1;
      dtnrouting.dratioTA.setText("DELIVRED--EXPIRED--TOTAL\n");
      dtnrouting.dratioTA.append(dtnrouting.total_packetsDelivered+"--"+dtnrouting.total_packetsExpired+"--"+ dtnrouting.total_packetsDeliveredExpired);
	
	  //2. Update delivered and expired packets information
	  for(int h=0;h < dtnrouting.packetTracker.size();h++)
      {   Packet packetObj=dtnrouting.packetTracker.get(h);
          packetObj.packetTTL-=1;     
          if(!packetObj.isTTLExpired & !packetObj.ispacketDelivered) {
                  packetObj.packetLatency=(int) dtnrouting.timer;              
                  
           if(packetObj.packetTTL==0){
	            //if packet's TTL expires, it cannot be delivered else if
	            packetObj.isTTLExpired=true;
	            packetObj.packetLatency=packetObj.maxTTL;
	            dtnrouting.total_packetsDeliveredExpired += 1;
	            dtnrouting.total_packetsExpired += 1;
	            dtnrouting.performanceFile.append(dtnrouting.currentTime+","+dtnrouting.SIMULATION_N0+","+dtnrouting.SIMULATION_PART+","+packetObj.destNode_ofpacket.name+","+packetObj.sourceNode_ofpacket.name+","+packetObj.destNode_ofpacket.num_packets+","+packetObj.ID+","+packetObj.packetHops+","+(packetObj.packetLatency)+","+packetObj.packetReliability+","+0+"\n");
	        	dtnrouting.performanceFile.flush();
          
          }
           if(packetObj.ispacketDelivered & !dtnrouting.packetDelivedDestinations.contains(packetObj.destNode_ofpacket)) 
        	   packetObj.destNode_ofpacket.current_packet_count += 1;
           if(packetObj.destNode_ofpacket.current_packet_count== packetObj.destNode_ofpacket.num_packets) 
           	   dtnrouting.packetDelivedDestinations.add(packetObj.destNode_ofpacket);

          
          }}
      
	  	dtnrouting.dratioTA.setText("DELIVRED--EXPIRED--TOTAL\n");
	  	dtnrouting.dratioTA.append(dtnrouting.total_packetsDelivered+"--"+dtnrouting.total_packetsExpired+"--"+ dtnrouting.total_packetsDeliveredExpired);
	  	result = dtnrouting.total_packetsDelivered+"--"+dtnrouting.total_packetsExpired+"--"+ dtnrouting.total_packetsDeliveredExpired+"\n";
	  	tempresult = result;
	  	
	     //3. When packets are delivered or expired
		 //   stop simulation temporarily
	  	 //System.out.println(dtnrouting.total_packetsDeliveredExpired+"---"+dtnrouting.packetTracker.size());
	     if(dtnrouting.total_packetsDeliveredExpired==dtnrouting.packetTracker.size() 
	     & dtnrouting.packetTracker.size()!=0)
	     {
	    	 //System.out.print("Final Result of "+dtnrouting.SIMULATION_PART+" :");
	    	 //System.out.println(tempresult);
	    	 dtnrouting.latencyTA.append("\nSim-->"+dtnrouting.SIMULATION_N0+":"+dtnrouting.SIMULATION_PART+" :"+tempresult);
	    	 dtnrouting.dratioTA.setText("DELIVRED--EXPIRED--TOTAL");
		     dtnrouting.THIS_SIMULATION_ENDED=true;
		     
	     }
	     //4. Call Fire Model		
	     fire_loc.fireSpread_flameHeight();
	     QoITPATH qoit = new QoITPATH();
	     //5. If a center of a node is in the range of fire---consider it dead and remove from live list
	     // If deadnode is a relayNode
	     boolean flag=true;
	     if(fire_loc.deadNodes()) { 
	    	 	networkUpdate();
	    	 	flag=false;}
	     // If Movement is Mobile & It 25 time units are gone & changes have relays are not connected anymore
	     if(flag & dtnrouting.isMovementStatic==0 & dtnrouting.timer%25==0)
	    	 if(!qoit.arePathsConnected())  networkUpdate();
	     flag=true;
}

//******************************************************************************

public void nextPositionForMovement() throws IOException
{
	
	    for(int i=0; i< dtnrouting.liveNodeIDs.size();i++)
	    {     
	    	Node n = dtnrouting.theNodes.get(dtnrouting.liveNodeIDs.get(i));
	    	if(!n.name.substring(0,1).equals("U")) {
	    		   n.node_nm.RandomMovement(n);
	    }}
	
}

//******************************************************************************
//Clear all the settings when clear (eraser) button is clicked

public void clearSettings()
{
		//System.out.println(dtnrouting.changes);
	    dtnrouting.changes=0;
        dtnrouting.liveNodeIDs.clear();
        dtnrouting.theNodes.clear();
        dtnrouting.theSources.clear();
        dtnrouting.liveSourceIDs.clear();
        dtnrouting.theDestinations.clear();
        dtnrouting.liveDestinationIDs.clear();
        dtnrouting.deployedUAVs.clear();
        dtnrouting.prospectiveUAVs.clear();
        
    	dtnrouting.packetTracker.clear();		// contains all the packets. It is to track their delivery
    	dtnrouting.Last_LeftRssNodes.clear();// contains LeftRSS Nodes of destinations with un-fulfilled reqs.
    	dtnrouting.First_RightRssNodes.clear();// contains LeftRSS Nodes of destinations with un-fulfilled reqs.
      	dtnrouting.packetDelivedDestinations.clear();
        
        Node.ID_INCREMENTER=-1;
        dtnrouting.timer=0;
        dtnrouting.total_packetsDeliveredExpired=0;
        dtnrouting.total_packetsExpired=0;
        dtnrouting.total_packetsDelivered=0;
        dtnrouting.RunForUQoIT=false;
        dtnrouting.currentTime = System.currentTimeMillis();
        //Clearings the array lists of source, destination, their packets and their parameter

       
        //Set movement model to null
        dtnrouting.packetTracker.clear();
        //dtnrouting.SIMULATION_N0 = dtnrouting.TOTAL_SIMULATION_RUNS;
        Packet.ID_incrementer=0; 
               
        //Empty Text areas
    	dtnrouting.sdpTA.setText("Src ---> Dst: Pkt");  //create Textarea on p2
    	dtnrouting.latencyTA.setText("Simulation "+dtnrouting.SIMULATION_N0+"-----\nFinal Results");						//create Textarea on p2
    	dtnrouting.pathChangesTA.setText("SimPart: 1");					//create Textarea on p2
    	dtnrouting.dratioTA.setText(" ");						//create Textarea on p2
    	
        //rpp.clearData(); //clear data from table and charts
        dtnrouting.THIS_SIMULATION_ENDED=false;
        dtnrouting.SIMULATION_RUNNING=false;
        
        //Remove firepoints
        FireLocation.fire_points = 0;
        
}

//******************************************************************************
//When a simulation run completes
public void simulationSettings()
{
    if(dtnrouting.SIMULATION_PART==4) {
    	dtnrouting.SIMULATION_N0=dtnrouting.SIMULATION_N0-1;
    	//Clear all settings
    	clearSettings();
    	
    	//Restart the Nodes
    	CreateNode cn = new CreateNode();
    	cn.addNodes();
    	//System.out.println("START OF NEW SIMULATION SET----------------------");
    	
    	dtnrouting.SIMULATION_PART=1;}
    else
    	dtnrouting.SIMULATION_PART+=1;
    // UNCOMENT ABOVE AND REMMOVE BELOW
	// dtnrouting.SIMULATION_PART+=1;
 
    dtnrouting.sdpTA.setText("Src ---> Dst: Pkt");
 
    
    //...Display the result when all SIMULATIONS END
    if(dtnrouting.SIMULATION_N0==0) {  
    	dtnrouting.SIMULATION_RUNNING=false;
    	System.exit(0);
    }
   
    //...When a simulation run ends, update the average results
    else if(dtnrouting.SIMULATION_N0>0)  
    {
    	
    	CreateNode cnObj = new CreateNode();
   	    QoITPATH pathObj = new QoITPATH ();
    	dtnrouting.total_packetsDeliveredExpired = 0;
    	dtnrouting.total_packetsExpired = 0;
    	dtnrouting.total_packetsDelivered = 0;
    	dtnrouting.RunForUQoIT = false;
    	dtnrouting.relayNodes.clear();
    	dtnrouting.packetDelivedDestinations.clear();
    	dtnrouting.dest_statisfying_all_requirements = new int[dtnrouting.theDestinations.size()];
    	long STARTTIME=0;
    	//System.out.println("*************************");
    	//*******************************************************************************
    	//1.  FIRE IS THERE BUT NO UAV -------------------
    	if(dtnrouting.SIMULATION_PART==1) {
    		dtnrouting.pathChangesTA.setText("Sim: 1");
    		
    		
    		// Assign requirements to the Destinations and packets to them
    		for(int g=0;g<dtnrouting.theDestinations.size();g++) 
    	    	{	
    	    		 cnObj.RequirementsofDestination(dtnrouting.theDestinations.get(g));
    	    		 //System.out.println("D: "+dtnrouting.theDestinations.get(g).ID+", HC:"+ dtnrouting.theDestinations.get(g).neworkMetricRequirements[0]+", IU:"+dtnrouting.theDestinations.get(g).neworkMetricRequirements[1]+", BW:"+dtnrouting.theDestinations.get(g).neworkMetricRequirements[2]+", PR:"+dtnrouting.theDestinations.get(g).neworkMetricRequirements[3]);
     				 cnObj.PacketsforDestination(dtnrouting.theDestinations.get(g));
     				 dtnrouting.dest_statisfying_all_requirements[g] = -1; //Requirements not fullfilled
    	    	}
    		// Assign locations to fire 
    		fire_loc.firePosition();
    	    
    		//Take a break of one second
 	       	/*try
 	       		{ Thread.sleep(1000);
 	       		} catch (InterruptedException ex) {}
     	    */
    		STARTTIME= System.currentTimeMillis();
    	    }// End of Sim 1
    	

    	//*******************************************************************************
    	
		//2. FIRE BUT WITH UAV, PLACED AT EMPTY POSITIONS AT VERY START OF THE SIMULAION------
        if(dtnrouting.SIMULATION_PART==2) {
        			 dtnrouting.pathChangesTA.setText("Sim: 2");
        			 
        			 //Clear and Refresh Packet settings
        			 for(int n=0;n<dtnrouting.theNodes.size();n++)
                 		dtnrouting.theNodes.get(n).refreshNodeSettings();
              		 for(int p=0;p < dtnrouting.packetTracker.size(); p++) {
              			dtnrouting.packetTracker.get(p).refreshPacketSettings();
              		 }
        			 
        			 //Clear LiveNodes Trackers
        			 dtnrouting.liveNodeIDs.clear();
        			 dtnrouting.liveSourceIDs.clear();
        		     dtnrouting.liveDestinationIDs.clear();
        		     int g = -1;
        		     
        		     //Assign Values to liveNodes Trackers
        		     for(int n = 0; n < dtnrouting.theNodes.size(); n++) {
        		    	 dtnrouting.liveNodeIDs.add(dtnrouting.theNodes.get(n).ID);
        				 if(dtnrouting.theNodes.get(n).name.substring(0,1).equals("S"))
        				        	dtnrouting.liveSourceIDs.add(dtnrouting.theNodes.get(n).ID);
        				 if(dtnrouting.theNodes.get(n).name.substring(0,1).equals("D")) {
        				 //System.out.println("D: "+dtnrouting.theNodes.get(n).ID+", HC:"+ dtnrouting.theNodes.get(n).neworkMetricRequirements[0]+", IU:"+dtnrouting.theNodes.get(n).neworkMetricRequirements[1]+", BW:"+dtnrouting.theNodes.get(n).neworkMetricRequirements[2]+", PR:"+dtnrouting.theNodes.get(n).neworkMetricRequirements[3]);
        				        	dtnrouting.liveDestinationIDs.add(dtnrouting.theNodes.get(n).ID);
        				        	dtnrouting.dest_statisfying_all_requirements[++g] = -1;
        				 			}}
	         		 //Add UAV nodes
        		     STARTTIME= System.currentTimeMillis();
	         		 cnObj.CreateRandomUAV(dtnrouting.numberUAVs); 
	         		 
        	}// End of Sim 2
    	
    	//*******************************************************************************
    	
		//2. FIRE BUT WITH UAV, PLACED AT EMPTY POSITIONS AT VERY START OF THE SIMULAION--
        if(dtnrouting.SIMULATION_PART==3) {
        			 dtnrouting.pathChangesTA.setText("Sim: 3");
        			 
        	       	 //Clear LiveNodes Trackers---------------
             		 dtnrouting.liveNodeIDs.clear();
        			 dtnrouting.liveSourceIDs.clear();
        			 dtnrouting.liveDestinationIDs.clear();
        			 dtnrouting.prospectiveUAVs.clear();
        			 dtnrouting.deployedUAVs.clear();
        			 int g=0;
        			 //Assign Values to liveNodes Trackers-----
        			 ArrayList<Node> node_to_remove = new ArrayList<Node>();
        			 for(int n = 0; n < dtnrouting.theNodes.size(); n++) {
        			 Node node = dtnrouting.theNodes.get(n);
        		     if(node.name.substring(0,1).equals("U"))
        		        	node_to_remove.add(node);
        		     else {
        				        dtnrouting.liveNodeIDs.add(dtnrouting.theNodes.get(n).ID);
        				        if(dtnrouting.theNodes.get(n).name.substring(0,1).equals("S"))
        				        	dtnrouting.liveSourceIDs.add(dtnrouting.theNodes.get(n).ID);
        				        if(dtnrouting.theNodes.get(n).name.substring(0,1).equals("D")){
        				        	dtnrouting.dest_statisfying_all_requirements[g] = -1; 
        				        	dtnrouting.liveDestinationIDs.add(dtnrouting.theNodes.get(n).ID);
        				        	g++;
        				    }	
        				  }}
        			    
        			 //Remove UAVs added in Sim 3---------------
        			 for(int i=0; i < node_to_remove.size(); i++){
        			 		dtnrouting.theNodes.remove(node_to_remove.get(i));
        			 		Node.ID_INCREMENTER = Node.ID_INCREMENTER - 1;}
        		     
        			 //Clear and Refresh Node and Packet settings---------
        			 for(int n=0;n<dtnrouting.theNodes.size();n++)
                 		dtnrouting.theNodes.get(n).refreshNodeSettings();		 	
              		 for(int p=0;p < dtnrouting.packetTracker.size(); p++) 
              			dtnrouting.packetTracker.get(p).refreshPacketSettings();

  
	         		 //Add UAV nodes----------------------------
              		 STARTTIME= System.currentTimeMillis();
	         		 cnObj.CreateDistanceUAV(dtnrouting.numberUAVs); 
	         		 
        }// End of Sim 3
        
        
    	//4. FIRE BUT WITH UAV, PLACED AT PROPOSED POSITIONS WHEN ONLY LINKS BREAK------
        if(dtnrouting.SIMULATION_PART==4) {
        		dtnrouting.pathChangesTA.setText("Sim: 4");
        		//Clear LiveNodes Trackers
        		dtnrouting.liveNodeIDs.clear();
   			 	dtnrouting.liveSourceIDs.clear();
   			 	dtnrouting.liveDestinationIDs.clear(); 
   			    dtnrouting.prospectiveUAVs.clear();
   			    dtnrouting.deployedUAVs.clear();
   			    
   			 	int d = -1;
   			 	
   			    ArrayList<Node> node_to_remove = new ArrayList<Node>();
   			 	
   			    //Assign Values to liveNodes Trackers
   			 	for(int n = 0; n < dtnrouting.theNodes.size(); n++) {
   			 	Node node = dtnrouting.theNodes.get(n);
   		        if(node.name.substring(0,1).equals("U"))
   		        	node_to_remove.add(node);
   		        else {
   				        dtnrouting.liveNodeIDs.add(dtnrouting.theNodes.get(n).ID);
   				        if(dtnrouting.theNodes.get(n).name.substring(0,1).equals("S"))
   				        	dtnrouting.liveSourceIDs.add(dtnrouting.theNodes.get(n).ID);
   				        if(dtnrouting.theNodes.get(n).name.substring(0,1).equals("D")){
   				        	//System.out.println("D: "+dtnrouting.theNodes.get(n).ID+", HC:"+ dtnrouting.theNodes.get(n).neworkMetricRequirements[0]+", IU:"+dtnrouting.theNodes.get(n).neworkMetricRequirements[1]+", BW:"+dtnrouting.theNodes.get(n).neworkMetricRequirements[2]+", PR:"+dtnrouting.theNodes.get(n).neworkMetricRequirements[3]);
   				        	dtnrouting.liveDestinationIDs.add(dtnrouting.theNodes.get(n).ID);
   				        	// After destinations are identified initialize, following array to all FALSE
   				        	dtnrouting.dest_statisfying_all_requirements[++d] = -1;}}}
   			    
   			 	//Remove UAVs added in Sim 3
   			 	for(int i=0; i < node_to_remove.size(); i++){
   			 		dtnrouting.theNodes.remove(node_to_remove.get(i));
   			 		Node.ID_INCREMENTER = Node.ID_INCREMENTER - 1;}
   			 		
   			 	
        		//Clear and Refresh Packet settings
   			 	for(int n=0;n<dtnrouting.theNodes.size();n++)
            		dtnrouting.theNodes.get(n).refreshNodeSettings();
   			 	
         		for(int p=0;p < dtnrouting.packetTracker.size(); p++) 
         			dtnrouting.packetTracker.get(p).refreshPacketSettings();
         		// Run QoIT without QoIT
         		
         		
                fire_loc.setRadius();
                dtnrouting.RunForUQoIT = false;
        	    // Assign shortest path from source to destination
                //System.out.println("CALL QoIT BEFORE UAV PLACEMENT*************************");
                STARTTIME= System.currentTimeMillis();
             	pathObj.ShortestPathsSD();
             	
             	// Assign initial source node of the destination
             	//System.out.println("CALL OF QoIT AFTER UAV PLACEMENT*************************");
             	dtnrouting.RunForUQoIT = true;
             	
        		// Call ProposedUAV UQoIT to deploy UAVs for the unfulfilled requirements of Destinations
        		if(Arrays.stream(dtnrouting.dest_statisfying_all_requirements).anyMatch(x -> x == -1))
        				propUAV.UQoIT();
         		
         }// End of Sim 4
        
        System.out.println(dtnrouting.SIMULATION_PART +", "+ (System.currentTimeMillis()-STARTTIME));
        simulationSettings();
        /*
        //Set fire radius to initial radius---------------------------------
	    fire_loc.setRadius();        
	    // Assign shortest path from source to destination
     	pathObj.ShortestPathsSD();
     	// Assign initial source node of the destination
     	pathObj.pathRelays();
     	pathObj.setInitialSource();	
     	 //Run the Simulation
     	runSimulation();*/
     	
     	
     	//------------------------------------------------------------------
	   }
}//end of the method/////////////////////////////////////////////////////////////

//******************************************************************************
// Call the method when a node dies or moves out of range

//1.  FIRE IS THERE BUT NO UAV -------------------
public void networkUpdate(){
	QoITPATH pathObj = new QoITPATH ();
	dtnrouting.relayNodes.clear();
	dtnrouting.changes +=1;
	//System.out.println("An update occured*********************************");
	dtnrouting.pathChangesTA.append("\nAn update occured*******");
	dtnrouting.dest_statisfying_all_requirements = new int[dtnrouting.theDestinations.size()];
	// Assign requirements to the Destinations and packets to them
	for(int g=0;g<dtnrouting.theDestinations.size();g++) 
    	{
		Node node = dtnrouting.theDestinations.get(g);
			if(!dtnrouting.liveDestinationIDs.contains(new Integer(node.ID)) || dtnrouting.packetDelivedDestinations.contains(node))
				dtnrouting.dest_statisfying_all_requirements[g] = 0;	

			else 
				dtnrouting.dest_statisfying_all_requirements[g] = -1; }
	

	//Remove UAVs if any
	//if(dtnrouting.SIMULATION_PART==3 | dtnrouting.SIMULATION_PART==4 )
	removeUAV();
	 
	 if(dtnrouting.SIMULATION_PART == 2) {
		 CreateNode cnObj = new CreateNode();
		 cnObj.CreateRandomUAV(dtnrouting.numberUAVs);
	 }
	
	 if(dtnrouting.SIMULATION_PART == 3) {
		 CreateNode cnObj = new CreateNode();
		 cnObj.CreateDistanceUAV(dtnrouting.numberUAVs);
	 }
 	 dtnrouting.RunForUQoIT = false;
	 // Assign shortest path from source to destination
     pathObj.ShortestPathsSD();
    
    for(int g=0; g< dtnrouting.theDestinations.size(); g++) 
    	    if(dtnrouting.dest_statisfying_all_requirements[g] < 0) 
    		dtnrouting.RunForUQoIT = true;
    		
 
	 if(dtnrouting.SIMULATION_PART == 4 & dtnrouting.RunForUQoIT == true) {
	    //System.out.println("UQoIT is called again::::::::::");
		propUAV.UQoIT();
		pathObj.ShortestPathsSD();}
    pathObj.pathRelays();
	// Assign initial source node of the destination
	pathObj.changeSource();	
}

//******************************************************************************

public void removeUAV()
{
	 for(int i = 0; i < dtnrouting.deployedUAVs.size(); i++) {
	    	Node node = dtnrouting.deployedUAVs.get(i);
	    	int ID = node.ID;
	    	//System.out.println("Node to remove: "+node.name);
	    	if(dtnrouting.liveNodeIDs.contains(new Integer(node.ID)))
	    		 dtnrouting.liveNodeIDs.remove(new Integer(node.ID));
	    	dtnrouting.theNodes.remove(node);
	    	Node.ID_INCREMENTER = Node.ID_INCREMENTER - 1;}

dtnrouting.deployedUAVs.clear();
dtnrouting.prospectiveUAVs.clear();
}

//******************************************************************************
public void runSimulation()
{  
	dtnrouting.total_packetsDeliveredExpired=0;
	dtnrouting.total_packetsExpired=0;
    dtnrouting.timer=0;
    dtnrouting.THIS_SIMULATION_ENDED=false;
    dtnrouting.SIMULATION_RUNNING=true;

}
//******************************************************************************

} /// END OF CLASS///////////////////////////////////////////////////////////////
