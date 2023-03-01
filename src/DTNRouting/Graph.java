package DTNRouting;

// JAVA program to print all
// paths from a source to
// destination.
import java.util.ArrayList;
import java.util.List;

// A directed graph using
// adjacency list representation
public class Graph {

	// No. of vertices in graph
	private int v;

	// adjacency list
	private ArrayList<Integer>[] adjList;
	public ArrayList<ArrayList<Integer>> allsimplePaths = new ArrayList<ArrayList<Integer>>();
    

	// Constructor
	public Graph(int vertices)
	{

		// initialize vertex count
		this.v = vertices;

		// initialize adjacency list
		initAdjList();
	}

	// utility method to initialize
	// adjacency list
	@SuppressWarnings("unchecked")
	private void initAdjList()
	{
		adjList = new ArrayList[v];

		for (int i = 0; i < v; i++) {
			adjList[i] = new ArrayList<>();
		}
	}

	// add edge from u to v
	public void addEdge(int u, int v)
	{
		// Add v to u's list.
		adjList[u].add(v);
	}

	// Prints all paths from
	// 's' to 'd'
	public void printAllPaths(int s, int d)
	{
		boolean[] isVisited = new boolean[v];
		ArrayList<Integer> pathList = new ArrayList<>();

		// add source to path[]
		pathList.add(s);

		// Call recursive utility
		printAllPathsUtil(s, d, isVisited, pathList);
	}

	// A recursive function to print
	// all paths from 'u' to 'd'.
	// isVisited[] keeps track of
	// vertices in current path.
	// localPathList<> stores actual
	// vertices in the current path
	private void printAllPathsUtil(Integer u, Integer d,
								boolean[] isVisited,
								List<Integer> localPathList)
	{
         
		if (u.equals(d)) {
			allsimplePaths.add(new ArrayList<Integer>());
			for(int i = 0; i < localPathList.size(); i++)
				allsimplePaths.get(allsimplePaths.size()-1).add(localPathList.get(i));
			
		}

		// Mark the current node
		isVisited[u] = true;

		// Recur for all the vertices
		// adjacent to current vertex
		for (Integer i : adjList[u]) {
			if (!isVisited[i]) {
				// store current node
				// in path[]
				localPathList.add(i);
				printAllPathsUtil(i, d, isVisited, localPathList);

				// remove current node
				// in path[]
				localPathList.remove(i);
			}
		}

		// Mark the current node
		isVisited[u] = false;
	}
}

// This code is contributed by Himanshu Shekhar.
