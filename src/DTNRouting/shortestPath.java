package DTNRouting;

import java.util.ArrayList;


// A Java program for Dijkstra's 
// single source shortest path  
// algorithm. The program is for 
// adjacency matrix representation 
// of the graph. 
  
public class shortestPath { //Dijkstra algorithm
  
    private  final int NO_PARENT = -1; 
    //public  ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> paths;
    //public  ArrayList<Integer> single_path =new ArrayList<Integer>();
    public  double dest_distance[];
    public  int dest_number[];
    // Function that implements Dijkstra's 
    // single source shortest path 
    // algorithm for a graph represented  
    // using adjacency matrix 
    // representation 
    private  void dijkstra(double[][] adjacencyMatrix, int startVertex, int [] destinations) 
    { 
        int nVertices = adjacencyMatrix[0].length; // If total nodes are m 
        // shortestDistances[i] will hold the 
        // shortest distance from src to i 
        double[] shortestDistances = new double[nVertices]; //There will be m shortDistances
        // added[i] will true if vertex i is 
        // included  in shortest path tree 
        // or shortest distance from src to  
        // i is finalized 
        boolean[] added = new boolean[nVertices]; 
  
        // Initialize all distances as  
        // INFINITE and added[] as false 
        for (int vertexIndex = 0; vertexIndex < nVertices;  
                                            vertexIndex++) 
        { 
            shortestDistances[vertexIndex] = 10000.0; 
            added[vertexIndex] = false; 
        } 
          
        // Distance of source vertex from 
        // itself is always 0 
        shortestDistances[startVertex] = 0; 
  
        // Parent array to store shortest 
        // path tree 
        //System.out.println(": nVertices= "+ nVertices);
        int[] parents = new int[nVertices]; 
  
        // The starting vertex does not  
        // have a parent 
        parents[startVertex] = NO_PARENT; 
  
        // Find shortest path for all  
        // vertices 
        for (int i = 1; i < nVertices; i++) 
        { 
  
            // Pick the minimum distance vertex 
            // from the set of vertices not yet 
            // processed. nearestVertex is  
            // always equal to startNode in  
            // first iteration. 
            int nearestVertex = -1; 
            double shortestDistance = 10000.0; 
            for (int vertexIndex = 0; 
                     vertexIndex < nVertices;  
                     vertexIndex++) 
            { 
                if (!added[vertexIndex] && 
                    shortestDistances[vertexIndex] <  
                    shortestDistance)  
                { 
                    nearestVertex = vertexIndex; 
                    shortestDistance = shortestDistances[vertexIndex]; 
                } 
            } 
           //System.out.println(": nearest Index = "+nearestVertex);
           //System.out.println(": shortestDistance = "+shortestDistance);
            // Mark the picked vertex as 
            // processed 
            if(nearestVertex < 0) break;
            added[nearestVertex] = true; 
  
            // Update dist value of the 
            // adjacent vertices of the 
            // picked vertex. 
            for (int vertexIndex = 0; 
                     vertexIndex < nVertices;  
                     vertexIndex++)  
            { 
                double edgeDistance = adjacencyMatrix[nearestVertex][vertexIndex]; 
                  
                if (edgeDistance > 0
                    && ((shortestDistance + edgeDistance) <  
                        shortestDistances[vertexIndex]))  
                { 
                    parents[vertexIndex] = nearestVertex; 
                    shortestDistances[vertexIndex] = shortestDistance +  
                                                       edgeDistance; 
                    //System.out.println(": parents[vertexIndex] = "+nearestVertex);
                    //System.out.println(": shortestDistances[vertexIndex] = "+shortestDistances[vertexIndex]);
                } 
            } 
        } 
      
        for (int vertexIndex = 0;  
                 vertexIndex < destinations.length;  
                 vertexIndex++)  { 
        	    //System.out.println(": destinations[vertexIndex] = "+ destinations[vertexIndex]);
                //System.out.println(": startVertex = "+startVertex);
      
            if (destinations[vertexIndex] != startVertex)  
            { 
            	//System.out.println(": size of dest_number[] = "+dest_number.length);
            	//System.out.println(": size of destinations[] = "+destinations.length);
                dest_number[vertexIndex]= destinations[vertexIndex];
                paths.add(new ArrayList<Integer>());
                if(shortestDistances[destinations[vertexIndex]] < 1000.0) {
                	//System.out.println("parents: " + parents + "; vertexIndex: " + vertexIndex + "; destinations[vertexIndex]: " + destinations[vertexIndex]);
                	displayPath(destinations[vertexIndex], parents, vertexIndex);
                }
                else {
                	//System.out.println(": size of paths = "+paths.size());
                	//System.out.println(": vertex = "+vertexIndex);
                	paths.get(vertexIndex).add(startVertex); //No path	
                	}
                	
                dest_distance[vertexIndex] = shortestDistances[destinations[vertexIndex]];
                //single_path.clear();
                //System.out.println("Paths: "+ paths.get(vertexIndex));
                
            } /* THIS FOLLOWING ELSE WAS NOT PRESENT BEFORE */
            else { 
            	//System.out.println(": size of paths = "+paths.size());
            	//System.out.println(": vertex = "+vertexIndex);
            		paths.add(new ArrayList<Integer>());
            		paths.get(vertexIndex).add(startVertex);
            	}
            
        }
        
    } 
  
    // Function to print shortest path 
    // from source to currentVertex 
    // using parents array 

private  void displayPath(int currentVertex, 
            int[] parents, int vertexIndex) 
{ 
 ArrayList<Integer> dummy_path= new ArrayList<Integer>();
	while (currentVertex != NO_PARENT) 
	{ 
	 //System.out.println("Hello World");
	 dummy_path.add( currentVertex);
	 currentVertex = parents[currentVertex];
	} 

	for(int i=dummy_path.size()-1; i >= 0 ; i --) {
	paths.get(vertexIndex).add(dummy_path.get(i));
	}

} 
    
    
        // Driver Code 
            //sp.runDijkstra(dtnrouting.adjacencyMatrix, dtnrouting.dest_index, source_index, sourcenode);

    public  void runDijkstra (double adjacencyMatrix[][], int[] destinations, int source_index, Node sourceNode)
    { 
    	 paths= new ArrayList<>(destinations.length);
    	 dest_distance =new double[destinations.length];
    	 dest_number =new int[destinations.length];
         
        //dijkstra(double[][] adjacencyMatrix, int startVertex, int [] destinations) 
         dijkstra(adjacencyMatrix, source_index, destinations);
         for(int i=0; i < destinations.length; i++) {
        	 sourceNode.ptD.paths.add(new ArrayList<Integer>());
        	 for(int j=0; j < paths.get(i).size(); j++) {
        		 int vertex = dtnrouting.liveNodeIDs.get(paths.get(i).get(j));   	 
        		 sourceNode.ptD.paths.get(i).add(vertex);	 
        	 }}
         sourceNode.ptD.dest_distance= dest_distance;
         sourceNode.ptD.dest_number= dest_number;

			
     } // End of method
    
}// End of class 
  
