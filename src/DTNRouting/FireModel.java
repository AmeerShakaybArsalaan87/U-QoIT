package DTNRouting;
//import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class FireModel {

	Random rand = new Random();
	public FireModel() { }
	
	public LinkedList<Object> fire_spread_rate()
	{
		LinkedList<Object> obj = new LinkedList<Object>();
		double fuel_age    = 0.0 + (22.0 - 0.0) * rand.nextDouble();     // 0-22 years
		double M_fs        = 5.6 + (9.6 - 5.6) * rand.nextDouble();      // Fuel_moisture_content (5.6% - 9.6%)
		double slope_angle =  0.00 + (30.0 - 0.00) * rand.nextDouble();  // 0.00-30 upwards degrees
		double b1   = 0.8576, b2 = 0.9301, b3 = 0.6366;                  // Regression Constants for Adjusted Rate of Spread i.e. R_A
		double B1   = 1.03;										         // Bias B1 for R_A
		double U_10 = 7.3 + (26.0 - 7.3) * rand.nextDouble();            // Wind Speed at 10 meters height in open (km/hr)
		double U_t  = 5.0;         							             // Threshold Wind Speed (km/hr)
		double R_t  = 30;  								                 // Threshold rate of spread (meters/hr)
		
		// Fuel hazard scores 
		double FHS_s  = FHS_s(fuel_age);                                 // surface fuel hazard score 
		double FHS_ns = FHS_ns(fuel_age);                                // near surface fuel hazard score 
		double H_ns   = H_ns(fuel_age);                                  // near surface fuel height
		

		double R_A = R_A(R_t, U_10, U_t, FHS_s, FHS_ns, H_ns, B1, b1, b2, b3);  // adjusted rate of spread  (R_A)
		double M_f = M_f(M_fs);										            // fuel moisture function   (M_f)
		double slope_function = slope(slope_angle); 					        // slope function     
		double R_ss = R_ss(R_A, M_f, slope_function);	                        // potential rate of spread (R_ss)				
	
		obj.add(R_ss);
		obj.add(R_A);
		
		return (obj);
	}
	
	public double flame_height(double R_A, String fire_coordinates)
	{//System.out.println("inside flame_height: " + fire_coordinates);
		double H_e = dtnrouting.fuel_height.get(fire_coordinates); // Elevated Fuel Height (25-400 cm) on the given 2-D coordinates
		double b1 = 0.723,  b2 = 0.0064;  				// Regression Constants for Fuel Height i.e. H_f
		double B2 = 1.07;							    // Bias B2 for H_f		
		double H_f = H_f(H_e, B2, R_A, b1, b2);         // Flame height (H_f)
		//System.out.println("H_f = " + H_f + "; exponent = " + Math.E + "; H_e = " + H_e + "; B2 = " + B2 + "; R_A = " + R_A + "; b1 = " + b1 + "; b2 = " + b2);		
		return (H_f);
	}
	
	public void fuel_height()                           // H_e
	{
		for(int x = dtnrouting.x_start; x <= dtnrouting.width ; x++) { 
			for(int y = dtnrouting.y_start; y <= dtnrouting.height ; y++) {
				//double H_e = 25.0 + (400.0 - 25.0) * rand.nextDouble();    // Elevated Fuel Height H_e in range (25-400 cm)	
				double H_e = 0.25 + (4.00 - 0.25) * rand.nextDouble();    // Elevated Fuel Height H_e in range (25-400 cm)		
				String fuel_coordinates = Long.toString(Math.round(x)).concat("-").concat(Long.toString(Math.round(y)));
				dtnrouting.fuel_height.put(fuel_coordinates, H_e);
				//System.out.println(fuel_coordinates +": " + H_e);
			}
		} 
	}
	
	// Fuel Moisture Content Function
	public static double M_f(double M_fs) { return ((18.35 * Math.pow(M_fs, -1.495))); }
	
	// Slope Function
	public static double slope(double slope_angle) { return (Math.pow(Math.E, (0.069 * slope_angle))); }
	
	// Flame height
	public static double H_f(double H_e, double B2, double R_A, double b1, double b2) {
		return (0.0193 * Math.pow(R_A, b1) * Math.pow(Math.E, (b2 * H_e)) * B2);
	}
	
	// R_A w.r.t Fuel Hazard Score (1st Model)
	public static double R_A(double R_t, double U_10, double U_t, double FHS_s, double FHS_ns, double H_ns, double B1, double b1, double b2, double b3) {		
		return (R_t + ((1.5308 * Math.pow((U_10 - U_t), b1)) * (Math.pow(FHS_s, b2)) * (Math.pow((FHS_ns * H_ns), b3)) * B1));		
	}

	// Potential quasi-steady rate of fire spread w.r.t FHS (in meters/hour)
	public static double R_ss(double R_A, double M_f, double slope_function) { return (R_A * M_f * slope_function); }
	
	// Fuel Hazard Score on Surface (FHS_s)
	public static double FHS_s(double fuel_age) { 
		double FHS_s = 0.0;
		if(Double.compare(fuel_age, 3.0) <= 0)
			FHS_s = 2.0;
		else if ((Double.compare(fuel_age, 4.0) == 0) || (Double.compare(fuel_age, 4.0) > 0 && Double.compare(fuel_age, 5.0) < 0) || Double.compare(fuel_age, 5.0) == 0)
			FHS_s = 2.5;
		else if ((Double.compare(fuel_age, 6.0) == 0) || (Double.compare(fuel_age, 6.0) > 0 && Double.compare(fuel_age, 10.0) < 0) || Double.compare(fuel_age, 10.0) == 0)
			FHS_s = 3.0;
		else if (Double.compare(fuel_age, 10.0) > 0)
			FHS_s = 3.5;	
		return FHS_s;
	}

	// Fuel Hazard Score on Near-Surface (FHS_ns)
	public static double FHS_ns(double fuel_age) { 
		double FHS_ns = 0.0;
		if(Double.compare(fuel_age, 3.0) <= 0)
			FHS_ns = 1.5;
		else if ((Double.compare(fuel_age, 4.0) == 0) || (Double.compare(fuel_age, 4.0) > 0 && Double.compare(fuel_age, 5.0) < 0) || Double.compare(fuel_age, 5.0) == 0)
			FHS_ns = 2.0;
		else if ((Double.compare(fuel_age, 6.0) == 0) || (Double.compare(fuel_age, 6.0) > 0 && Double.compare(fuel_age, 10.0) < 0) || Double.compare(fuel_age, 10.0) == 0)
			FHS_ns = 2.5;
		else if (Double.compare(fuel_age, 10.0) > 0)
			FHS_ns = 3.0;	
		return FHS_ns;
	}

	// Fuel Height Near-Surface (H_ns)
	public static double H_ns(double fuel_age) { 
		double H_ns = 0.0;
		if(Double.compare(fuel_age, 3.0) <= 0)
			H_ns = 15.0;
		else if ((Double.compare(fuel_age, 4.0) == 0) || (Double.compare(fuel_age, 4.0) > 0 && Double.compare(fuel_age, 5.0) < 0) || Double.compare(fuel_age, 5.0) == 0)
			H_ns = 17.5;
		else if ((Double.compare(fuel_age, 6.0) == 0) || (Double.compare(fuel_age, 6.0) > 0 && Double.compare(fuel_age, 10.0) < 0) || Double.compare(fuel_age, 10.0) == 0)
			H_ns = 20.0;
		else if (Double.compare(fuel_age, 10.0) > 0)
			H_ns = 25.0;	
		return H_ns;
	}


}
