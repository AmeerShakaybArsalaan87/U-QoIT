package DTNRouting;

import java.util.ArrayList;
import java.util.HashMap;

public class UAVs_ShortestPaths {
	//  Find the waypoints and add them to the departure point
	//double WP = [0 0 0; Waypoints3D(S, R)];
	double WP = waypoints();
	private static int size;
	private static int count;
	private static int threshold;
	private static ArrayList<Node> xNodes;

	public static double waypoints(){
		// Inputs:  - S a set of sensor nodes
	    //          - R their respective range
	    // Outputs: - WP a set of waypoints covering altogether the whole set of sensor nodes

		// Apply QPSO algorithm multiple times until all sensor nodes are covered
		HashMap<Integer, ArrayList<Double>> wayPoint = new HashMap<Integer, ArrayList<Double>>();
		int wp_number = 0;
		//ArrayList<Node> networkNodes_notCovered_byWayPoint= dtnrouting.theNodes;

		while (dtnrouting.liveNodeIDs.size() != 0) {
			wp_number++;
			wayPoint.put(Integer.valueOf(wp_number), new ArrayList<Double>());

			// Apply the QPSO algorithm to find the optimal location for one waypoint
			//G = QuantumParticleSwarmOptimization();
			QuantumParticleSwarmOptimization();
		}

		return 0.00000000000;
	}
	
	public static double QuantumParticleSwarmOptimization() {
		// Inputs:  - S a set of sensor nodes
		//          - R their respective range
		// Outputs: - G the best particle's location (covering the most sensor nodes)
		
		//function G = QuantumParticleSwarmOptimization(S, R)
		    
		    setSize(dtnrouting.liveNodeIDs.size());
		    setCount(0);
		    setThreshold(100);
		    setxNodes(dtnrouting.theNodes);
		    
		    // G, the best particle's location is initialized as the average position of all sensors
		    HashMap<Integer, ArrayList<Double>> coordinates_globalBest = new HashMap<Integer, ArrayList<Double>>();
		    coordinates_globalBest.put(1, new ArrayList<Double>());

		    double Gx = 0.0, Gy = 0.0, Gz = 0.0;
		    for (int n = 1; n <= dtnrouting.liveNodeIDs.size(); n++) {
		        Gx = Gx + dtnrouting.theNodes.get(dtnrouting.liveNodeIDs.get(n)).location.x;
		        Gy = Gy +  dtnrouting.theNodes.get(dtnrouting.liveNodeIDs.get(n)).location.y;
		        //Gz = Gz;
		        
		        //System.out.println(dtnrouting.liveNodes.get(node).location.x  + "; " + dtnrouting.liveNodes.get(node).location.y);
		    }
		    Gx = Gx / dtnrouting.liveNodeIDs.size();
		    Gy = Gy / dtnrouting.liveNodeIDs.size();
		    Gz = Gz / dtnrouting.liveNodeIDs.size();
		    coordinates_globalBest.get(1).add(Gx);
		    coordinates_globalBest.get(1).add(Gy);
		    coordinates_globalBest.get(1).add(Gz);
		    
		    System.exit(0);
		    return 0.000000;
	}

	public static ArrayList<Node> getxNodes() {
		return xNodes;
	}

	public static void setxNodes(ArrayList<Node> xNodes) {
		UAVs_ShortestPaths.xNodes = xNodes;
	}

	public static int getThreshold() {
		return threshold;
	}

	public static void setThreshold(int threshold) {
		UAVs_ShortestPaths.threshold = threshold;
	}

	public static int getCount() {
		return count;
	}

	public static void setCount(int count) {
		UAVs_ShortestPaths.count = count;
	}

	public static int getSize() {
		return size;
	}

	public static void setSize(int size) {
		UAVs_ShortestPaths.size = size;
	}

}