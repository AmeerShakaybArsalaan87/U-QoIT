package DTNRouting;

//import java.awt.Color;
//import java.awt.Font;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class FireLocation {
	//******************************************************************************

	public static int fire_points = 0;

	public static ArrayList<Double> x_fire     = new ArrayList<Double>();
	public static ArrayList<Double> y_fire     = new ArrayList<Double>();	
	public static HashMap<String, Double>  flame_height = new HashMap<String, Double>();
	public static HashMap<Integer, Double> fire_radius  = new HashMap<Integer, Double>();

	FireModel fire_model = new FireModel();
	Random    rand       = new Random();
	//EMPTY CONSTRUCTOR
	public  FireLocation() { }

	//******************************************************************************
    public void firePosition() {
    	fire_model.fuel_height();
		fire_points = rand.nextInt(10) + 15;
		for(int i = 0; i < fire_points; i++) {
			double fire_x = dtnrouting.x_start + (dtnrouting.width  - 0) * rand.nextDouble();
			double fire_y = dtnrouting.y_start + (dtnrouting.height - 0) * rand.nextDouble();

			if(fire_x <= dtnrouting.width && fire_y <= dtnrouting.height) {
				x_fire.add(fire_x);
				y_fire.add(fire_y);
				String fire_coordinates = Long.toString(Math.round(x_fire.get(i))).concat("-").concat(Long.toString(Math.round(y_fire.get(i))));
				flame_height.put(fire_coordinates, fire_model.flame_height(0.0, fire_coordinates)); // initially flame height = 0.0 meters for each point "i"
				dtnrouting.burned_area.putAll(flame_height);
			}
			else i--;
		}
	}

	//******************************************************************************
    public void setRadius() {
		for(int i = 0; i < fire_points; i++)
				fire_radius.put(i, 0.0);       // initially fire doesn't spread, therefore radius = 0.0 meters for each point "i"
	}
    
	public void fireSpread_flameHeight() {
		flame_height.clear();
		for(int i = 0; i < fire_points; i++) {	
			LinkedList<Object> obj = fire_model.fire_spread_rate();
			Object KeySet[] = fire_radius.keySet().toArray();
			double R_ss = (Double) obj.get(0) / 3600;                                // to convert from meters/hr to meters/sec from class FireModel
			double R_A  = (Double) obj.get(1);                                       // Adjusted Fire Spread Rate in meters/hr  from class FireModel
			double updated_fireRadius = fire_radius.get((Integer) KeySet[i]) + R_ss;  // updating the value of radius calculated for point "i"
			fire_radius.put(i, updated_fireRadius);                                   // storing the updated value of radius calculated for point "i"
			//System.out.println((Integer) KeySet[i] + ": " + fire_radius.get((Integer) KeySet[i]));
			
			for(int angle = 0; angle < 360; angle++) { 
				double x = x_fire.get(i) + (updated_fireRadius * Math.cos(Math.toRadians(angle)));  // to calculate x-coordinate of fire-circle point
				double y = y_fire.get(i) + (updated_fireRadius * Math.sin(Math.toRadians(angle)));  // to calculate y-coordinate of fire-circle point 
				String fire_coordinates = Long.toString(Math.round(x)).concat("-").concat(Long.toString(Math.round(y))); 

			if(((int) Math.round(x) <= dtnrouting.width  && (int) Math.round(x) >= dtnrouting.x_start) 
			&& ((int) Math.round(y) <= dtnrouting.height && (int) Math.round(y) >= dtnrouting.y_start) && 
				!flame_height.containsKey(fire_coordinates) && !dtnrouting.burned_area.containsKey(fire_coordinates))
					flame_height.put(fire_coordinates, fire_model.flame_height(R_A, fire_coordinates)); // flame_height at fire_coordinates (x, y)
			}
		}
		dtnrouting.burned_area.putAll(flame_height);
	}

	public boolean deadNodes()
	{
		boolean hasAnyNodeDead=false;

		for(int n=0; n < dtnrouting.theNodes.size();n++) {
			Node node= dtnrouting.theNodes.get(n);
			if(dtnrouting.liveNodeIDs.contains(node.ID) & !dtnrouting.deployedUAVs.contains(node)) {
				for(int fp = 0; fp < fire_points; fp++) {
					double x = x_fire.get(fp);   // fire at location x
					double y = y_fire.get(fp);   // fire at location y
					double d = Math.sqrt(Math.pow((x - node.location.x), 2) + Math.pow((y - node.location.y), 2));
					Object KeySet[] = FireLocation.fire_radius.keySet().toArray();
					double fireRadius = FireLocation.fire_radius.get((Integer) KeySet[fp]);
					
					
					if(Double.compare(d, fireRadius) <= 0 ) {
						dtnrouting.destsourcePairPrevious = dtnrouting.destsourcePair;
						int ID = node.ID;
						dtnrouting.liveNodeIDs.remove(new Integer(ID));
						//System.out.println("******Dead node is: "+node.name+"******");
						if(node.name.substring(0, 1).equals("S")) 
							dtnrouting.liveSourceIDs.remove(new Integer(ID));		
						
						if(node.name.substring(0, 1).equals("D"))
							{	
							dtnrouting.liveDestinationIDs.remove(new Integer(ID));
							dtnrouting.dest_statisfying_all_requirements[dtnrouting.theDestinations.indexOf(node)]=0;//Burnt
							}
						if(dtnrouting.relayNodes.contains(node))
							hasAnyNodeDead=true;
						break;
						}
				}
			}
		}

		// Even if a single node dies, Reset the paths
		if(hasAnyNodeDead) 
			return true;
		else
			return false;
	}
}// End of class
