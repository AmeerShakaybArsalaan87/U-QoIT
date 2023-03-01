package DTNRouting;
//import java.io.IOException;
//import java.io.PrintWriter;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Random;
import java.util.Stack;

public class NoviceUAVPlacement {

	private double minDist_km = 6.0;
	//private double maxDist_km = 5.0;
	private double uav_speed_kph = 2.0; // 120.0
	//private double radioRange_km = 5.0;
	private Location closestUAV;
	private int nUAVs;
	private Location closestUAV2;
	private int nUAVs2;
	private boolean toNode;
	private Location closestUAV3;
	private int nUAVs3;
	private double closestHullDist_km;
	private Location closestHull;
	private double dist;
	private double closestUnitDist_km;
	private Location closestUnit;
	private Location closestAll;
	private double moveDist_km;
	private boolean toNode2;
	private Location closestUAV4;
	private boolean toNode3;
	
	
//********************************************************************		
	// Distance between two locations
	double calcDistance_km(Location l1, Location l2) {
		double diffX = (l1.x-l2.x)*(l1.x-l2.x);
		double diffY = (l1.y-l2.y)*(l1.y-l2.y);
		
		double distanceKm = Math.sqrt(diffX + diffY);
		return distanceKm;
	}

//********************************************************************	
	//This methods returns the location for uav "thisUAV"
	//mid-point of largest empty space between two nodes 
	public Location getUAVLocation(Node thisUAV, ArrayList<Node> Nodes) {
		Location bestPoint = getLargestCircle(thisUAV, Nodes);
		thisUAV.x_coord.add(bestPoint.x);
		thisUAV.y_coord.add(bestPoint.y);
		return bestPoint;
	}
//********************************************************************		
	// This method seems to identify location of the closest uav to "thisUAV"
	// but does not use the information and instead switches to find 
	// mid point of largest empty circle
	// " AN INCOMPLETE FUNCTION"
	public Location getNewUAVLocationsDistances1(Node thisUAV, Location thisLocation, ArrayList<Node> Nodes) {


		//Location closestLocation = thisLocation;
		double closestUAVDist_km = Double.MAX_VALUE;
		setClosestUAV(thisLocation);
		setnUAVs(0);
		/* Take into consideration other UAVs locations 
		 * , identify the UAV closet to this UAV*/
		for (Node destUAV : Nodes) {	
			if (destUAV.isUAV()) {
				setnUAVs(getnUAVs() + 1);
				double dist = calcDistance_km(thisLocation, destUAV.getLocation());	
				if (dist < closestUAVDist_km && dist>0.0) {
					closestUAVDist_km = dist;
					setClosestUAV(destUAV.getLocation());
				}
			}
		}

		Location bestPoint = getLargestCircle(thisUAV, Nodes);

		return bestPoint;

	}

//********************************************************************	
	// Current code does not do anything
	/**
	 * This routine takes into consideration the nearest UAVS and Hull point to get the next optimal
	 * @param thisUAV
	 * @param thisLocation
	 * @param Nodes
	 * @param printListLimit
	 * @return
	 */
	public Location getNewUAVLocationsDistancesActual(Node thisUAV, Location thisLocation, ArrayList<Node> Nodes, int printListLimit) {


		Location closestLocation = thisLocation;
		double closestUAVDist_km = Double.MAX_VALUE;
		setClosestUAV2(thisLocation);
		setnUAVs2(0);
		/* Take into consideration other UAVs locations */
		// It finds nearest uav to "thisUAV"
		for (Node destUAV : Nodes) {	
			if (destUAV.isUAV()) {
				setnUAVs2(getnUAVs2() + 1);
				double dist = calcDistance_km(thisLocation, destUAV.getLocation());	
				if (dist < closestUAVDist_km && dist>0.0) {
					closestUAVDist_km = dist;
					setClosestUAV2(destUAV.getLocation());
				}
			}
		}

		setToNode(true);

		setMinDist_km(0.0);
		if (closestUAVDist_km < getMinDist_km()) { // Firstly don't get too close to another Unit 
			setToNode(false); 
//			closestLocation = Location.updatePosition(closestLocation, closestUAV, getMinDist_km(), toNode);
		}
		else {
//			closestLocation = Location.gotoNewLocation(thisUAV.getLocation(), bestPoint, getUav_speed_kph(), SimRun.getSim().getSimP().getUavupdatetimeSecs()/3600.0);
		}
		//	closestLocation = bestPoint; 
		return closestLocation;

	}
	

//********************************************************************
	
	/**
	 * Get the longest link between units' closest neighbours
	 * @param thisUAV
	 * @param Nodes
	 * @param nUAVs
	 * @return
	 */
	public Location getLongestCLosestLink(Node thisUAV, ArrayList<Node> Nodes, int nUAVs) {
		/* Get the largest minimum distance between two units */
		double maxDistance_km = 0.0;
		double networkDistance_km = 0.0;
		Location bestPoint = null;
		for (Node Unit1 : Nodes) {
			//	if (!Unit1.isUAV()) 
			if (Unit1 != thisUAV) {
				double minDistance_km =  Double.MAX_VALUE;
				Location bestMinPoint = null;
				for (Node Unit2 : Nodes) {	
					if (Unit1 != Unit2) {
						//		if (!Unit2.isUAV())
						if(Unit2 != thisUAV) {
							double dist = calcDistance_km(Unit1.getLocation(), Unit2.getLocation());	
							if (dist > networkDistance_km )  networkDistance_km = dist;
							if (dist < minDistance_km) {
								minDistance_km = dist;
								bestMinPoint = Unit1.getLocation().getMidpoint(Unit2.getLocation());
							}
						}
					}
				}
				if (minDistance_km >= maxDistance_km) {
					maxDistance_km = minDistance_km;
					bestPoint= bestMinPoint;
				}
			}

		}

		setMinDist_km(maxDistance_km/((double) (nUAVs+1))/2);
		//	minDist_km = 0.0;
		//		minDist_km = networkDistance_km/((double) (nUAVs+1));
		//		minDist_km = maxDistance_km;
		return (bestPoint);
	}

//********************************************************************	
	//The method is not doing anything.
	/**
	 * This routine takes the distance between units to get the new location
	 * @param thisUAV
	 * @param thisLocation
	 * @param Nodes
	 * @param GroupHull
	 * @param useHull
	 * @return
	 */
	public Location getNewUAVLocationsDistances(Node thisUAV, Location thisLocation, 
			                                    ArrayList<Node> Nodes, Stack<Location> GroupHull, 
			                                    boolean useHull) {
		Location closestLocation = thisLocation;
		double closestUAVDist_km = Double.MAX_VALUE;
		setClosestUAV4(thisLocation);

		/* Take into consideration other UAVs locations */
		for (Node destUAV : Nodes) {	
			if (destUAV.isUAV()) {
				double dist = calcDistance_km(thisLocation, destUAV.getLocation());	

				if (dist < closestUAVDist_km && dist>0.0) {
					closestUAVDist_km = dist;
					setClosestUAV4(destUAV.getLocation());
				}
			}
		}

		if (closestUAVDist_km < getMinDist_km()) { // Firstly don't get too close to another Unit 
			setToNode3(false);
		}
		return closestLocation;
	}


//********************************************************************	
	
	/**
	 * This routine takes into consideration the nearest UAVS and Hull point to get the next optimal
	 * @param thisUAV
	 * @param thisLocation
	 * @param Nodes
	 * @param GroupHull
	 * @param useHull
	 * @return
	 */
	public Location getNewUAVLocationsDistancesActualOld(Node thisUAV, Location thisLocation, 
														ArrayList<Node> Nodes, Stack<Location> GroupHull, 
														boolean useHull) {


		Location closestLocation = thisLocation;
		double closestUAVDist_km = Double.MAX_VALUE;
		setClosestUAV3(thisLocation);
		setnUAVs3(0);
		/* Take into consideration other UAVs locations */
		for (Node destUAV : Nodes) {	
			if (destUAV.isUAV()) {
				setnUAVs3(getnUAVs3() + 1);
				double dist = calcDistance_km(thisLocation, destUAV.getLocation());	
				if (dist < closestUAVDist_km && dist>0.0) {
					closestUAVDist_km = dist;
					setClosestUAV3(destUAV.getLocation());
				}
			}
		}
		//		Location bestPoint = getLongestCLosestLink(thisUAV, Nodes, nUAVs);
		Location bestPoint = getLargestCircle(thisUAV, Nodes);

		setClosestHullDist_km(Double.MAX_VALUE);
		setClosestHull(thisLocation);
		if (useHull) {
			for (Location hullPt : GroupHull) {
				setDist(calcDistance_km(thisLocation, hullPt));
			}
		}

		setClosestUnitDist_km(Double.MAX_VALUE);
		setClosestUnit(thisLocation);

		for (Node thisUnit : Nodes) {	
			if (!thisUnit.isUAV()) {
				double dist = calcDistance_km(thisLocation, thisUnit.getLocation());	
				if (dist < closestUAVDist_km && dist>0.0) {
					setClosestUnitDist_km(dist);
					setClosestUnit(thisUnit.getLocation());
				}
			}
		}


		/* Take into consideration the convex hull */
		double closestUnitAll_km = Double.MAX_VALUE;
		setClosestAll(thisLocation);

		for (Node thisUnit : Nodes) {	
			double dist = calcDistance_km(closestLocation, thisUnit.getLocation());	
			if (dist < closestUnitAll_km && dist>0.0) {
				closestUnitAll_km = dist;
				setClosestAll(thisUnit.getLocation());
			}
		}

		setMoveDist_km(Double.MAX_VALUE);
		setToNode2(true);

		if (closestUAVDist_km < minDist_km) { // Firstly don't get too close to another Unit 
			setToNode2(false); 
//			closestLocation = Location.updatePosition(closestLocation, closestUAV, minDist_km, toNode);
//			closestLocation = closestLocation;
		}
		/*		else if (closestUnitAll_km < minDist_km) { // Firstly don't get too close to another Unit 
			toNode = false; 
			closestLocation = updatePosition(closestLocation, closestAll, minDist_km, toNode);
		} */

		else {
			//			closestLocation  = midPoint(closestHull, closestUnit);
			closestLocation = bestPoint;
		}
		//	closestLocation = bestPoint;

		/*				

		if (closestUAVDist_km < minDist_km) { // Firstly don't get to close to another UAV 
			closestLocation = closestUAV;
			moveDist_km = minDist_km;
			toNode = false; 
			closestLocation = updatePosition(thisLocation, closestLocation, moveDist_km, toNode);
		}
		else if (closestHullDist_km < minDist_km) {
				closestLocation = closestHull;
				moveDist_km = minDist_km;
				toNode = false;
				closestLocation = updatePosition(thisLocation, closestLocation, moveDist_km, toNode);
		} else  {
		//	closestLocation = closestHull;
	
		//	moveDist_km = closestHullDist_km;
		}
		*/

		return closestLocation;

	}

//********************************************************************	
	// Finds the largest distance between two set of nodes
	// such that their is no node between them
	public Location getLargestCircle(Node thisNode, ArrayList<Node> Nodes) {
		/* Get the largest minimum distance between two units */

		ArrayList<Node> usedNodes = new ArrayList<Node>();
		for (Node unit : Nodes) {
			if (unit != thisNode) {
				usedNodes.add(unit);
			}	
		}

		double biggestCircle_km = 0.0;
		Location bestPoint = null;
		for (Node unit1 : usedNodes) {
			for (Node unit2 : usedNodes) {	
				if (unit1 != unit2) {	
					double dist = calcDistance_km(unit1.getLocation(), unit2.getLocation());	
					if (dist > biggestCircle_km )  {
						Location mp = unit1.getLocation().getMidpoint(unit2.getLocation());
						boolean emptyCircle = isEmptyCircle(unit1, unit2, usedNodes, mp, dist/2.0);
						if (emptyCircle) {
							biggestCircle_km = dist;
							bestPoint= mp;
						}
					} 
				}
			}

		}
//		minDist_km = Math.max(biggestCircle_km/((double) (numNodes+1))/2, 0.2);
		return bestPoint;
	}
	
//********************************************************************	
	// Find if the circle with center "Center" and radius "radius_km"
	// has no other node and is empty.
	public boolean isEmptyCircle(Node Unit1, Node Unit2, ArrayList<Node> usedNodes, Location Centre, double radius_km) {
		boolean empty = true;
		for (Node Unit : usedNodes) {
			if ((Unit != Unit1) &&  (Unit != Unit2)) {
				double dist = calcDistance_km(Centre, Unit.getLocation());	
				if (dist < radius_km) return false;
			}	
		}
		return empty;
	}

//********************************************************************		
	public double getUav_speed_kph() {
		return uav_speed_kph;
	}

	public void setUav_speed_kph(double uav_speed_kph) {
		this.uav_speed_kph = uav_speed_kph;
	}

	public double getMinDist_km() {
		return minDist_km;
	}

	public void setMinDist_km(double minDist_km) {
		this.minDist_km = minDist_km;
	}

	public int getnUAVs() {
		return nUAVs;
	}

	public void setnUAVs(int nUAVs) {
		this.nUAVs = nUAVs;
	}

	public Location getClosestUAV() {
		return closestUAV;
	}

	public void setClosestUAV(Location closestUAV) {
		this.closestUAV = closestUAV;
	}

	public boolean isToNode() {
		return toNode;
	}

	public void setToNode(boolean toNode) {
		this.toNode = toNode;
	}

	public int getnUAVs2() {
		return nUAVs2;
	}

	public void setnUAVs2(int nUAVs2) {
		this.nUAVs2 = nUAVs2;
	}

	public Location getClosestUAV3() {
		return closestUAV3;
	}

	public void setClosestUAV3(Location closestUAV3) {
		this.closestUAV3 = closestUAV3;
	}

	public int getnUAVs3() {
		return nUAVs3;
	}

	public void setnUAVs3(int nUAVs3) {
		this.nUAVs3 = nUAVs3;
	}

	public double getClosestHullDist_km() {
		return closestHullDist_km;
	}

	public void setClosestHullDist_km(double closestHullDist_km) {
		this.closestHullDist_km = closestHullDist_km;
	}

	public Location getClosestHull() {
		return closestHull;
	}

	public void setClosestHull(Location closestHull) {
		this.closestHull = closestHull;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

	public double getClosestUnitDist_km() {
		return closestUnitDist_km;
	}

	public void setClosestUnitDist_km(double closestUnitDist_km) {
		this.closestUnitDist_km = closestUnitDist_km;
	}

	public Location getClosestAll() {
		return closestAll;
	}

	public void setClosestAll(Location closestAll) {
		this.closestAll = closestAll;
	}

	public Location getClosestUnit() {
		return closestUnit;
	}

	public void setClosestUnit(Location closestUnit) {
		this.closestUnit = closestUnit;
	}

	public double getMoveDist_km() {
		return moveDist_km;
	}

	public void setMoveDist_km(double moveDist_km) {
		this.moveDist_km = moveDist_km;
	}

	public boolean isToNode2() {
		return toNode2;
	}

	public void setToNode2(boolean toNode2) {
		this.toNode2 = toNode2;
	}

	public Location getClosestUAV4() {
		return closestUAV4;
	}

	public void setClosestUAV4(Location closestUAV4) {
		this.closestUAV4 = closestUAV4;
	}

	public boolean isToNode3() {
		return toNode3;
	}

	public void setToNode3(boolean toNode3) {
		this.toNode3 = toNode3;
	}

	public Location getClosestUAV2() {
		return closestUAV2;
	}

	public void setClosestUAV2(Location closestUAV2) {
		this.closestUAV2 = closestUAV2;
	}
	
	
	
}

