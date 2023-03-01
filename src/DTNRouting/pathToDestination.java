package DTNRouting;

import java.util.ArrayList;

public class pathToDestination {

	    public ArrayList<ArrayList<Integer>> paths;
	    public  double dest_distance[];
	    public  int dest_number[];
	    public  int destinations=0;
	  
	 public pathToDestination(int destinations) {
	 paths= new ArrayList<>(destinations);
	 dest_distance =new double[destinations];
	 dest_number =new int[destinations];
	 }
	 
}
