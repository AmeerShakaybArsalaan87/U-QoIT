//PACKAGE NAME
package DTNRouting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//*****************************************************************************
// START OF CREATENODE CLASS
public class ProposedUAV
{
	 private static final long serialVersionUID = 1L;
	 public networkMetricValues_AlongPath nmV = new networkMetricValues_AlongPath();
	 public Random rand = new Random();
	 public ArrayList<Node>   DestUnReq = new ArrayList<Node>();
	 
	 //******************************************************************************
	 //EMPTY CONSTRUCTOR
	 public  ProposedUAV() {
	 }
	
	//******************************************************************************
	//REQUIREMETN SATISFYING PATHS
	public void ReqSatisfyingSubPaths(int di, int dest_id, Node node) {	
		//System.out.println("3. I am in ReqSS");
		//For each living destination node
		/*for(int di = 0; di < dtnrouting.dest_index.length; di++) {
		/	int dest_no = dtnrouting.dest_index[di];
			Node node = dtnrouting.theNodes.get(dtnrouting.liveNodeIDs.get(dest_no));
		
		//If all requirements of the destination node are not fulfilled
		System.out.println("\nAre requirements satisfy? "+dtnrouting.dest_statisfying_all_requirements[dtnrouting.theDestinations.indexOf(node)]);
		if(!dtnrouting.dest_statisfying_all_requirements[dtnrouting.theDestinations.indexOf(node)]) 
		{*/
			//Rss_left and Rss_right for destinations with incomplete requirements
		   //System.out.println("Destination "+ node.name +" path is "+dtnrouting.destsourcePair[di]);
		   	if(dtnrouting.destsourcePair[di]==0)
			{
		   		double min_distance=100000;
				int index=dtnrouting.liveSourceIDs.get(0);
				for(int i = 0; i < dtnrouting.liveSourceIDs.size(); i++){
					Node snode =  dtnrouting.theSources.get(dtnrouting.liveSourceIDs.get(i));
					double d = (Math.sqrt(Math.pow(node.location.x - snode.location.x,2) + Math.pow(node.location.y -snode.location.y, 2)));
					if( min_distance > d ){
						min_distance = d;
						index = dtnrouting.liveSourceIDs.get(i);}}
				dtnrouting.destsourcePair[di] = index;
			}
	
		
			Node sourceNode = dtnrouting.theSources.get(dtnrouting.destsourcePair[di]);
			node.Rss_left.clear();
			node.Rss_right.clear();
			//System.out.println(dtnrouting.destsourcePair[di]);
			node.Rss_left.add(sourceNode.ID);
			
			int numberHops = sourceNode.ptD.paths.get(di).size();
			int hops = 1;
			ArrayList<Integer> rss_l = new ArrayList<Integer>();
			//Note the first node in the list is source node
			rss_l.add(sourceNode.ptD.paths.get(di).get(0));
			
			// Updating Rss_left ************
			for(int c=1; c < numberHops; c ++) {
			// Path that packet will follow from 
				Node intermediateNode = dtnrouting.theNodes.get(sourceNode.ptD.paths.get(di).get(c));
				//Finding If requirements of this destination are fulfilled by the intermediateNode
				rss_l.add(sourceNode.ptD.paths.get(di).get(c));
				
				hops = hops + 1;
				if(intermediateNode.informationUtility >= node.neworkMetricRequirements[1] &
				  nmV.getPathBandwidth(rss_l) >= node.neworkMetricRequirements[2] &
				  intermediateNode.reliability >= node.neworkMetricRequirements[3] &
				  hops <= node.neworkMetricRequirements[0])
					{	
					node.Rss_left.add(intermediateNode.ID);
					
					} else
					{
					hops = hops - 1;
					break;}}
			
			ArrayList<Integer> rss_r = new ArrayList<Integer>();
			//Note the first node in the list is destination node
			rss_r.add(sourceNode.ptD.paths.get(di).get(numberHops-1));
			node.Rss_right.add(dest_id);
			
			// Updating Rss_right ************
			for(int c= numberHops-1; c > 0; c --) {
				Node intermediateNode = dtnrouting.theNodes.get(sourceNode.ptD.paths.get(di).get(c));
				//Finding If requirements of this destination are fulfilled by the intermediateNode
				rss_r.add(sourceNode.ptD.paths.get(di).get(c));	
				hops = hops + 1;
				if(intermediateNode.informationUtility >= node.neworkMetricRequirements[1] &
				  nmV.getPathBandwidth(rss_r) >= node.neworkMetricRequirements[2] &
				  intermediateNode.reliability >= node.neworkMetricRequirements[3] &
				  hops <= node.neworkMetricRequirements[0])
					{	
						node.Rss_right.add(intermediateNode.ID);
					
					} else
					{
						break;
					}}
			 // Last node ID of RssLeft and
			 // First node ID of RssRight 
			 node.L_ID = node.Rss_left.get(node.Rss_left.size()-1);
			 node.F_ID = node.Rss_right.get(node.Rss_right.size()-1);
			 //System.out.println("\nDest: "+node.name+", RSS LEFT: "+node.Rss_left);
			 //System.out.println("Dest: "+node.name+",RSS RIGHT: "+node.Rss_right);
		  		
		} //End of the method
	
	//*********************************************
	public int DetermineNoUAVs(double tHC, int Nu, Node destnode) {
		//System.out.println("4. I am in DetermineNoUAVs, Nu are: "+Nu);
		Nu = Nu + 1;
		//CreateNode uav = new CreateNode();
		//public ArrayList<Integer> Psud = new ArrayList<Integer>();
		
		//Number of hops with additional UAV
		double p_sud_len =  destnode.Rss_left.size()+destnode.Rss_right.size()+Nu-1;
		//System.out.println("SuD Len: "+p_sud_len+", tHC:"+tHC);
		while(p_sud_len > tHC) {
			if(destnode.Rss_left.size() > 1)
				destnode.Rss_left.remove(destnode.Rss_left.size()-1);
			else if(destnode.Rss_right.size() > 1)
				destnode.Rss_right.remove(destnode.Rss_right.size()-1);
			else
				return Nu;
		p_sud_len =  destnode.Rss_left.size()+destnode.Rss_right.size()+Nu-1;
		destnode.L_ID = new Integer(destnode.Rss_left.get(destnode.Rss_left.size()-1));
		destnode.F_ID = new Integer(destnode.Rss_right.get(destnode.Rss_right.size()-1));
        }
		//System.out.println("Source:"+destnode.Rss_left.get(0) + ",Left: "+destnode.Rss_left.get(destnode.Rss_left.size()-1));
		//System.out.println("Right:"+destnode.Rss_right.get(destnode.Rss_right.size()-1) + ",Dest: "+destnode.Rss_right.get(0));
		
		//Rss_left node IDs
		//for(int i = 0; i< destnode.Rss_left.size(); i ++)
		//	Psud.add(destnode.Rss_left.get(i));
		
		// Return the number of uavs
		return(Nu);
	
	}
	
	// The potential locations of all the uavs,
	// that are to be placed between L and F of 
	// a node
	//*********************************************
	public Location [] PotentialCoordinatePositions(int L_ID, int F_ID, int Nu)
	{
		//System.out.println("5. I am in Potential coordinates");
		// Node
		Node  L = dtnrouting.theNodes.get(L_ID);
		Node  F = dtnrouting.theNodes.get(F_ID);
		
		//System.out.println("Location of L: " + L.location.x +"," +L.location.y);
		//System.out.println("Location of F: " + F.location.x +"," +F.location.y);
		//Location of last node in Rss_left
		double x_l = L.location.x;
		double y_l = L.location.y;
		double z_l = 0;
		double du = 0;
		//Location of first node in Rss_right
		double x_f = F.location.x;
		double y_f = F.location.y;
		double z_f = 0;
		
		//Euclidean distance between point L and F
		 double d = Math.sqrt(Math.pow((x_f-x_l),2) + Math.pow((y_f-y_l),2));
		 //Location [] R = new Location[Nu+1];
		 Location [] P = new Location[Nu];
		 
		du =  d / (Nu+1);
		for(int j=1; j <= Nu ; j++) {
			 Location p = new Location();
			 p.x =  x_l - (j*du*(x_l-x_f))/d;
			 p.y =  y_l - (j*du*(y_l-y_f))/d;
			 p.z = 7.5 + (12.9 -7.5)* rand.nextDouble();
			 P[j-1] = p; 
			 //System.out.println("Location P"+j+": "+p.x +","+p.y);
			 }
		/*------------------
		 if(Nu == 1) 
			 du = d/(Nu+1);
		 else
			 du = d/Nu;
		 
		 // Reference Position of the uav nodes
		 for(int j=0; j <= Nu ; j++) {
			 Location r = new Location();
			 r.x =  x_l - (j*du*(x_l-x_f))/d;
			 r.y =  y_l - (j*du*(y_l-y_f))/d;
			 r.z =  0;
			 
			 R[j] = r;
			 }
		 
		 // 3D Potential Coordinates of the uav nodes
		 for(int i=1; i <= Nu ; i++) {
			 int j = i - 1;
			 Location p_i =  new Location();
			 if(Nu ==  1) {
				 p_i.x = R[j+1].x;
				 p_i.y = R[j+1].y;
			 }else {
				 p_i.x = R[j+1].x + (R[j+1].x - R[j].x)/2;
				 p_i.y = R[j+1].y + (R[j+1].y - R[j].y)/2;
			 }
			//r.z = 7500 + (12000-7500)* rand.nextDouble(); //UAV between 75 to 120 meter
			 p_i.z = 7.5 + (12.9 -7.5)* rand.nextDouble();
			 P[i-1] = p_i; 
			 System.out.println("Location P"+i+": "+p_i.x +","+p_i.y);
			 
		 }
		----------------------------------------------------- */
		 
		return(P);
		
	}

	//*********************************************
	//I have used the procedure just above 4.4
	public boolean PathEstablishment(ArrayList<Node> Set_cn) {
		//System.out.println("6. I am in Path Establishment, node is:"+Set_cn.get(Set_cn.size()-1).name+", size:"+Set_cn.size());
			int i, j = 2;
			Node u, a_l, a_r;
			
			//Set_cn consists of Rss Left, Nu and  Rss Right node ids
			while (j < Set_cn.size()) {
				//System.out.println("J: "+j+", Set_cn: "+ Set_cn.size());
				i = j - 2;
				u= Set_cn.get(i+1);
				// Only check for UAV nodes
				a_l = Set_cn.get(i);
				a_r = Set_cn.get(i+2);
				if((RadioRange(a_l, u) == 1) & (RadioRange(u, a_r) == 1)) 
					{
					 int k =0;
						while(!LineOfSight(a_l, u) || !LineOfSight(u, a_r)) {
							AdjustLocation(u.location, a_l.location, a_r.location);
							k = k + 1;
							if(k > 10){
								return false;
							}
						}
					}
				else
						return false; // means uavs are unable to connect with the L and F
				//To check for all UAVs placed between the regular nodes
				j = j + 1;
				}

			return true; // means that uavs are able to connect with L and F and more uavs are needed
	}	
	
	//*********************************************
	//Radio range
	public int RadioRange(Node n1, Node n2) {
		//System.out.println("7. I am in Radio Range");
		//******************************************************
		//mid point and radius of n1
		double x1 = n1.location.x;
		double y1 = n1.location.y;
		double r1 = n1.getRadioRange();//(ni.getRadioRange())/2;

		//mid point and radius of n2
		double x2 = n2.location.x;
		double y2 = n2.location.y;
		double r2 = n2.getRadioRange();//(nj.getRadioRange())/2;

		double distance_km = Math.sqrt(Math.pow((y2-y1),2) + Math.pow((x2-x1),2));
		double r = r1 + r2;
		//System.out.println("N1:("+x1+","+y1+"), N2: ("+x2+","+y2+")");
		//System.out.println("Distance:("+distance_km+"), R: ("+r+")");
		if(distance_km <= r) return 1;
		else return 0;
	}
	
	//*********************************************
	//Line of sight 
	public boolean LineOfSight(Node n1, Node n2) {
		//System.out.println("8. I am in LineOfSight");
		//******************************************************
		//mid point and radius of ni
		double x1 = n1.location.x;
		double y1 = n1.location.y;
		//double r1 = n1.getRadioRange();//(ni.getRadioRange())/2;

		//mid point and radius of nj
		double x2 = n2.location.x;
		double y2 = n2.location.y;
		//double r2 = n2.getRadioRange();//(nj.getRadioRange())/2;
		
		boolean isLoS = true;		
		// Fire locations
		for (int fp = 0; fp < FireLocation.fire_points; fp++)
		{
			Object KeySet[] = FireLocation.fire_radius.keySet().toArray();
			//System.out.println("Fire radius:"+FireLocation.fire_points);
			double r = FireLocation.fire_radius.get((Integer) KeySet[fp]);
			double x2_m = x2 - FireLocation.x_fire.get(fp);
			double y2_m = y2 - FireLocation.y_fire.get(fp);
			double x1_m = x1 - FireLocation.x_fire.get(fp);
			double y1_m = y1 - FireLocation.y_fire.get(fp);
			
			// Point of Intersection of line on the 
			// circle of fire radius
			double d_x = (x2_m - x1_m);
			double d_y = (y2_m -y1_m);
			double d_r = Math.sqrt(Math.pow(d_x, 2) + Math.pow(d_y, 2));
			double D = x1_m*y2_m - x2_m*y1_m;
			double sigma = Math.pow(r,2)* Math.pow(d_r,2) - Math.pow(D, 2);
			
			if(sigma >= 0.0) {
				isLoS =  false;
				break;
			}}
		
			return isLoS;		
								
	}

	//*********************************************
	//Adjust location
	//*********************************************
	public Location  AdjustLocation(Location u_loc, Location before_loc, Location after_loc) {
		//System.out.println("9. I am in adjust Location");
		// Change in x or y position
		Random rand = new Random();
		int movement=rand.nextInt(4); 
		if((movement==0) & ((u_loc.x+2) < after_loc.x)) {
			u_loc.x = u_loc.x +  2;
		}
		else if((movement==1) & ((u_loc.x-2) > before_loc.x)) {
			u_loc.x = u_loc.x -  2;
		
		}else if((movement==2) & ((u_loc.y+2) < after_loc.y)) {
			u_loc.y = u_loc.y +  2;
		
		}else if((movement==3) & ((u_loc.y-2) > before_loc.y)) {
			u_loc.y = u_loc.y -  2;
		}
		return u_loc;
	}
	
	
	
	//*********************************************
	// Identify the number of required UAVs for each 
	// of the SD paths in the network, whose requirements
	// are not fulfilled
	//*********************************************
	public void SinglePathProblem() {
		//System.out.println("2. I am in Single Path Problem");
		// Clear the previous assignments
		dtnrouting.Last_LeftRssNodes.clear();
		dtnrouting.First_RightRssNodes.clear();
		dtnrouting.prospectiveUAVs.clear();
		DestUnReq.clear();
		
		//For each living destination node
		for(int di = 0; di < dtnrouting.dest_index.length; di++) {
			int dest_id = dtnrouting.liveNodeIDs.get(dtnrouting.dest_index[di]);
			Node node = dtnrouting.theNodes.get(dest_id);
		    //Destination with un-fulfilled requirements
		
			if(!dtnrouting.packetDelivedDestinations.contains(node) & dtnrouting.dest_statisfying_all_requirements[dtnrouting.theDestinations.indexOf(node)]==(-1))
			{
				//System.out.println("Unfullfilled Destinations: "+node.name);
				ReqSatisfyingSubPaths(di, dest_id, node);
				boolean isPathEstablished = false;
				int Nu = 0;
				
				while((!isPathEstablished) & (Nu < dtnrouting.numberUAVs)) {
						//HopCount requirement of D, number of uavs in SD path, destination node
						// and return number of UAVs to be placed between L and F of SD
				    	Nu = DetermineNoUAVs(node.neworkMetricRequirements[0], Nu, node);
				    
				    	// Find the potential locations of the Nu uavs
				    	Location [] P = new Location[Nu];
				    	P = PotentialCoordinatePositions(node.L_ID, node.F_ID, Nu);
				    	// Set connected nodes to last node in 1. left RSS
				    	// 2. Nu UAVs and 3. right RSS nodes
				    	node.Set_cn.clear();
				    	node.Set_cn.add(dtnrouting.theNodes.get(node.L_ID));
				    	
				    	for(int n = 0; n < Nu; n++) {
				    		// Create potential uav node for SD
				    		// Make ID of the UAV as -1,
				    		// Do not add it to the Nodes List
				    		Node prospectiveUAVnode = new Node();
				    		// Assign the potential location to the uav node
				    		prospectiveUAVnode.location =  P[n];
				    		// -1 in list indicate presence of a uav in destination path
				    		prospectiveUAVnode.ID = -1;
				    		prospectiveUAVnode.setRadioRange(dtnrouting.uavRange);
				    		node.Set_cn.add(prospectiveUAVnode); }
				    	
				    	node.Set_cn.add(dtnrouting.theNodes.get(node.F_ID));
				    	// Check if path can be established with Nu UAVs
				    	// If not go to instruction calling DetermineNoUAVs
				    	isPathEstablished =  PathEstablishment(node.Set_cn);
				      }
				    
				//System.out.println("Is Path Established: "+isPathEstablished+", Nu: "+Nu);
				
				if(Nu <= dtnrouting.numberUAVs & ((dtnrouting.prospectiveUAVs.size()+ Nu) > (dtnrouting.numberUAVs*3))) break;
				// Once Nu UAVs of D are decided, add the
				// prospective UAVs to the list
				if(isPathEstablished & (Nu <= dtnrouting.numberUAVs)){
					DestUnReq.add(node);
					for(int n = 0; n < Nu; n++)
						dtnrouting.prospectiveUAVs.add(node.Set_cn.get(n+1));
					dtnrouting.Last_LeftRssNodes.add(dtnrouting.theNodes.get(node.L_ID));
					dtnrouting.First_RightRssNodes.add(dtnrouting.theNodes.get(node.F_ID));}
			  }
				
			}//End of the out most for loop

	} //End of method
	
	//***********************************************
	   // If prospectiveUAVs are more than the 
	   // available one, then reduce their number
	   //*********************************************
	
	public void reduceUAVs() {
	
		int start_index_uav = dtnrouting.Last_LeftRssNodes.size() + dtnrouting.First_RightRssNodes.size();
		int no_of_nodes_in_graph = start_index_uav + dtnrouting.prospectiveUAVs.size();
		
		BFS g = new BFS(no_of_nodes_in_graph);
		Node na =null, nb=null;
		
		// Identify edges in the graph----------------------------------
		for(int i = 0; i < (no_of_nodes_in_graph); i++ ) g.addEdge(i, i);
		for(int i = 0; i < (no_of_nodes_in_graph-1); i++ ) {
		
		//Node number 1 in the graph
			// 1. Add to the graph all L nodes
			if(i>=0 & i < dtnrouting.Last_LeftRssNodes.size()) 
			    na = dtnrouting.Last_LeftRssNodes.get(i);
			// 2. Add to the graph all F nodes
			else if(i >= dtnrouting.Last_LeftRssNodes.size() & i < (dtnrouting.Last_LeftRssNodes.size() + dtnrouting.First_RightRssNodes.size()))
				na = dtnrouting.First_RightRssNodes.get(i-dtnrouting.Last_LeftRssNodes.size());
			// 3. Add to the graph all Prospective UAVs
			else if(i>=(dtnrouting.Last_LeftRssNodes.size() + dtnrouting.First_RightRssNodes.size()))
				na = dtnrouting.prospectiveUAVs.get(i-(dtnrouting.Last_LeftRssNodes.size() + dtnrouting.First_RightRssNodes.size()));
			
			for(int j = i+1; j < no_of_nodes_in_graph; j++ ) {
			// Node number 2 in the graph
						if(j>=0 & j < dtnrouting.Last_LeftRssNodes.size()) 
							nb = dtnrouting.Last_LeftRssNodes.get(j);
						else if(j >= dtnrouting.Last_LeftRssNodes.size() & j < (dtnrouting.Last_LeftRssNodes.size() + dtnrouting.First_RightRssNodes.size()))
							nb = dtnrouting.First_RightRssNodes.get(j-dtnrouting.Last_LeftRssNodes.size());
						else if(j >=(dtnrouting.Last_LeftRssNodes.size() + dtnrouting.First_RightRssNodes.size()))
							nb = dtnrouting.prospectiveUAVs.get(j-(dtnrouting.Last_LeftRssNodes.size() + dtnrouting.First_RightRssNodes.size()));
						
						if(RadioRange(na, nb) == 1){ 
							g.addEdge(i, j);
							g.addEdge(j, i);}
			}} 
		
		
		Integer[] countPATHS = new Integer[DestUnReq.size()]; //It stores number of uav paths for each dest with unfulfilled req.
		Integer[] countPATHS_dummy = new Integer[DestUnReq.size()]; //Same as above but used for intermediate modification
		int range_of_paths=0;
		
		//Storing all simple paths from the L to F of a destination with unfulfilled requirements
		for(int i = 0; i < DestUnReq.size(); i++) {
			int l = i; //last node in RSS left
			int f = i + dtnrouting.Last_LeftRssNodes.size(); //first node in RSS right
			//System.out.println("l: "+l+ ", f: "+f);
			g.print_paths(g.adj, no_of_nodes_in_graph, l, f);

			ArrayList<ArrayList<Integer>> intermediatePath = new ArrayList<ArrayList<Integer>>(g.allsimplePaths);  
			if(i > 0){
			for(int k=0; k < range_of_paths; k++)
				intermediatePath.remove(0);
			}
			
			
			
			//if(intermediatePath.get(k).size()>2) 
			DestUnReq.get(i).nodeAllUAVPaths = intermediatePath;
			
			range_of_paths = g.allsimplePaths.size();
			//System.out.println("The paths are: " +DestUnReq.get(i).nodeAllUAVPaths);
			// Store the count of the Paths
			countPATHS[i] =  DestUnReq.get(i).nodeAllUAVPaths.size();
		}
		
		countPATHS_dummy = countPATHS;
		
		
		// Reduce UAV nodes .......
		// start from the index of the graph when first uav is encountered
		//For each uav check if it is removed and there is still path between each pair of L and F
		//int countUAV=dtnrouting.prospectiveUAVs.size();
		for(int u = start_index_uav; u < no_of_nodes_in_graph ;   u++ )
		{
				// Check for each LF path
				// If the UAV is removed
				// Is there still a path?
			    Boolean removeUAVpath = false;
				//For each unfulfilled dest
				for(int i = 0; i < DestUnReq.size(); i++) {
					int count = 0;
					//For each path of this unfulfilled dest
					for(int j=0; j < DestUnReq.get(i).nodeAllUAVPaths.size(); j++)
						{
				        	if(DestUnReq.get(i).nodeAllUAVPaths.get(j).contains(u))
				        	count = count + 1;
				        }
					// Should we remove or retain the paths
					if(countPATHS_dummy[i] > count) { 
						countPATHS_dummy[i]= countPATHS_dummy[i] -count;
						//if uav is removed from each LP pair
						removeUAVpath = true;
						}

					else {
						countPATHS_dummy[i] = countPATHS[i];
						//if uav should not be removed from at least a single LP pair
						removeUAVpath = false;
						break;
						}
					
					countPATHS[i] = countPATHS_dummy[i];
				}
				
				//Remove the uav when all LF pairs have other paths
				if(removeUAVpath){		
					//For each path of this unfulfilled dest
					for(int i = 0; i < DestUnReq.size(); i++) {
						int j=0;
						//System.out.println("U is: "+u+", and all paths of DEST "+ i+" are: "+DestUnReq.get(i).nodeAllUAVPaths);
						while(DestUnReq.get(i).nodeAllUAVPaths.size()>j)
							{
				        	if(DestUnReq.get(i).nodeAllUAVPaths.get(j).contains(u)){
				        	
				        		//System.out.println("Remove Path:"+DestUnReq.get(i).nodeAllUAVPaths.get(j));				    
				        		DestUnReq.get(i).nodeAllUAVPaths.remove(j);
				        	    	
				        		}else{ j = j+1;}}}
				//countUAV = countUAV - 1;
				//if(dtnrouting.numberUAVs==countUAV) break;
				}
				
		}
		
		//IF number of UAVs that are left are still greater than 
		// available UAVs
		//if(countUAV > dtnrouting.numberUAVs) {
		//System.out.println("I am in 1");
		//Find Shortest Path For Each Un-fullfilled Destination******************
		for(int i = 0; i < DestUnReq.size(); i++) {		
			int smallest_path = 1000, index_of_smallest_path =0;
			//Find smallest path between L and F for the unfulfilled destination
			for(int j=0; j < DestUnReq.get(i).nodeAllUAVPaths.size(); j++)
					{
		        		if(DestUnReq.get(i).nodeAllUAVPaths.get(j).size() < smallest_path ) {
		        			index_of_smallest_path = j;
		        			smallest_path = DestUnReq.get(i).nodeAllUAVPaths.get(j).size();}
		        	}
		   	        	
					// Identify deployed nodes
		        	ArrayList<Integer> small_path = (ArrayList<Integer>) DestUnReq.get(i).nodeAllUAVPaths.get(index_of_smallest_path);
		        	//System.out.println("Dest: "+i+", smallest path: "+ small_path+", Prospective UAV:"+dtnrouting.prospectiveUAVs.size()+", num of nodes:"+no_of_nodes_in_graph);
		        	for(int k = 1; k < small_path.size()-1; k++) 
		        	{
		        		int unodeid = small_path.get(k) - start_index_uav;
		        		//System.out.println(unodeid);
		        		//Add only UAV nodes if they come in the path
		        		if(unodeid >= 0) {
			        		Node unode	= dtnrouting.prospectiveUAVs.get(unodeid);
			        		unode.centrality =  unode.centrality + 1;
			        		if(unode.centrality == 1)
			        			dtnrouting.deployedUAVs.add(unode);}
		        		
		        		
		        	}}
			// Retain only those Deployed UAVs that Fall under Number of 
			// Available UAVs*******************************************************
			// Remove the nodes with lowest centrality
			while(dtnrouting.numberUAVs < dtnrouting.deployedUAVs.size()){
					int min_centrality = 1000;
					int index = 0;
					for(int i = 0; i <  dtnrouting.deployedUAVs.size(); i++){
						if(dtnrouting.deployedUAVs.get(i).centrality <  min_centrality){
							min_centrality =  dtnrouting.deployedUAVs.get(i).centrality;
							index = i;
						}
					}
					dtnrouting.deployedUAVs.remove(index);}
		
		/*}//If Statement
		else {
			
			for(int d =  (dtnrouting.prospectiveUAVs.size()-countUAV-1); d < (dtnrouting.prospectiveUAVs.size()-1); d++)
				dtnrouting.deployedUAVs.add( dtnrouting.prospectiveUAVs.get(d));}*/
		
		g = null;
	}
	
   //*********************************************
   // Minimization of UAVs so that from within
	// Prospective UAVS the number of deployed
	// UAVs are reduced to available
   //*********************************************
	public void UQoIT() {
		//System.out.println("1. I am in UQoIT");
		//Clear perspective and deployed UAVs and Identifiers of Node
	
		
		
		for(int i=0; i < dtnrouting.deployedUAVs.size(); i++){
			Node node = dtnrouting.deployedUAVs.get(i);
			dtnrouting.theNodes.remove(node);
			dtnrouting.liveNodeIDs.remove(new Integer(node.ID));
			Node.ID_INCREMENTER = Node.ID_INCREMENTER - 1;
			dtnrouting.deployedUAVs.remove(node);
		}
		
		dtnrouting.Last_LeftRssNodes.clear();
		dtnrouting.First_RightRssNodes.clear();
		dtnrouting.prospectiveUAVs.clear();
		// Solve Single Paths for Destinations
		// whose requirements are un-fullfilled
		SinglePathProblem();
		//System.out.println("Back to UQOIT");
		// Make Adjacency Matrix of all the Last and First
		// nodes as well as its prospective UAVs for the Destinations
		// with un-fulfilled requirements
		
		if(dtnrouting.prospectiveUAVs.size() > dtnrouting.numberUAVs) 
			reduceUAVs();
		else
			for(int i=0; i < dtnrouting.prospectiveUAVs.size() ; i++) dtnrouting.deployedUAVs.add(dtnrouting.prospectiveUAVs.get(i));
		

		//System.out.println("Prospective UAVs: "+dtnrouting.prospectiveUAVs.size());
		
		
	    
		//System.out.println("2. Reduced Deployed UAVs: "+dtnrouting.deployedUAVs.size());
		// Assign IDs and names to the Deployed UAVs
		for(int i = 0; i <  dtnrouting.deployedUAVs.size(); i++){
			Node node =  dtnrouting.deployedUAVs.get(i);
			Node.ID_INCREMENTER+=1;
			node.ID=Node.ID_INCREMENTER;
			node.name="U"+ node.ID;
			node.speed=0;
			node.x_coord.add(node.location.x);
			node.y_coord.add(node.location.y);
			node.setRadioRange(dtnrouting.uavRange);
			//System.out.println("UAV ID: "+node.ID);
			node.wholeQueueSize=node.queueSizeLeft=500;
			dtnrouting.theNodes.add(node);
			dtnrouting.liveNodeIDs.add(node.ID);
			node.reliability = 4;
		}
		
		//Additional UAVs
		int num_uav = dtnrouting.numberUAVs - dtnrouting.deployedUAVs.size();
		if(num_uav > 0 ) {
			    NoviceUAVPlacement uav_placement = new NoviceUAVPlacement();
		        Node.ID_INCREMENTER = dtnrouting.theNodes.size()-1;

		    	for (int l=0;l < num_uav;l++){
		    		//dtnrouting.uav_index[l]= dtnrouting.theNodes.size();	
		    		Node node=new Node();
		    		Node.ID_INCREMENTER+=1;
		    		node.ID=Node.ID_INCREMENTER;
		    		node.name="U"+ node.ID;
		    		node.speed=0;
		    		node.setRadioRange(dtnrouting.uavRange);
		    		node.wholeQueueSize=node.queueSizeLeft=500;
		    		dtnrouting.theNodes.add(node);
		    		dtnrouting.liveNodeIDs.add(node.ID);
		    		dtnrouting.deployedUAVs.add(node);
		    		//System.out.println("UAV ID: "+node.ID);
		    		//Random position of uav
		    		node.location = uav_placement.getUAVLocation(node, dtnrouting.theNodes);
		    		node.reliability = 4;}
		    		
		    	}
		   
			
		//System.out.println("Deployed UAVs: "+dtnrouting.deployedUAVs.size()+" *********");
		
	}//End of Method
//***************************	
}//End of class
