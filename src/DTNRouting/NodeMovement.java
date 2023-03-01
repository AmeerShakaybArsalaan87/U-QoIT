
//******************************************************************************
//PACKAGE

package DTNRouting;

//import java.util.ArrayList;
//******************************************************************************
//IMPORT FILES
import java.util.Random;

//******************************************************************************
//MOVEMENT PROCESS OF A NODE

public class NodeMovement extends dtnrouting
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Instance Variables
    Random rand;
    static int speedController=0; // controls the speed of different nodes
    static int divisor,numOfPoints;
    int value,h;
    static int[] limit_x;
    static int[] limit_y;
    static int pseudo_array[][][];   //first array show no of nodes, 2nd show the position and 3rd show x,y points
    static int[] nodePeriod;
    public static int warmupPeriod=0;
    static boolean event=false,changePositions=false,followPath=true;// decides whether to change the current  position of node or not
    public int numNodes=0,count=0;

//******************************************************************************
//CONSTRUCTOR

public NodeMovement()
{
        rand=new Random();
}

//******************************************************************************
//GIVING INITIAL POSITION TO A NODE

public  void InitialNodePositions(Node n)
{

	  n.x_coord.clear();
	  n.y_coord.clear();
	  n.z_coord.clear();
	  n.location.x =(double)(n.topleft_xcorner) + (n.xwidth - 0) * rand.nextDouble();
	  //rand.nextInt(dtnrouting.width-n.getRadioRange()) + dtnrouting.x_start;
      n.x_coord.add(n.location.x);  //generate random value for x and y positions of node
      n.location.y =(double)(n.topleft_ycorner) + (n.yheight - 0) * rand.nextDouble(); 
      //rand.nextInt(dtnrouting.height-n.getRadioRange()) + dtnrouting.y_start;
      n.y_coord.add(n.location.y);
      n.location.z = 0;
      if(n.name.substring(0,1).equals("U")) {
    	    n.location.z = 75.0;
    	    //System.out.println(n.location.z);
      		}
      n.prob_coord.add(rand.nextDouble());
      n.positionTracker=0;     
	  n.moveFlag=true;
}

//******************************************************************************
//RANDOM PATH FOR A NODE

public void RandomMovement(Node n)
{
 // Always use double the rand_speed value to the speed
  int rand_speed = n.speed+5;
  speedController=rand.nextInt(rand_speed)+1;
  
  // decide whether to change the position
  if((n.name.substring(0,1).equals("R")||
      n.name.substring(0,1).equals("D")))
	  //& n.moveFlag)
     {
        if(n.speed>=speedController)
        {
        	
        	if (n.x_coord.get(n.positionTracker)< n.location.x)  //nextX show the new position of x coordinates its value is randomly generated above and x_n[i] is the random no genarated above
                           n.location.x--;

            else if(n.x_coord.get(n.positionTracker)>n.location.x)
                            n.location.x++;

       
        	if(n.y_coord.get(n.positionTracker)< n.location.y)  //nextX show the new position of x coordinates its value is randomly generated above and x_n[i] is the random no genarated above
                           n.location.y--;

            else if(n.y_coord.get(n.positionTracker)>n.location.y)
                            n.location.y++;
           
           // Choose random location for x-coordinate and y-coordinate
           // 20 different positions	
           if(n.x_coord.get(n.positionTracker)==n.location.x & n.y_coord.get(n.positionTracker)==n.location.y){
        	 
        	 if(n.positionTracker==39)
        		 	n.positionTracker =0;
        	 else {
		             n.x_coord.add((double)n.topleft_xcorner + (n.xwidth - 0) * rand.nextDouble());  
		             n.y_coord.add((double)n.topleft_ycorner + (n.yheight - 0) * rand.nextDouble()); 
		             n.y_coord.add(0.0);
		             n.positionTracker=n.positionTracker+1;
		             n.prob_coord.add(rand.nextDouble());}
        	 //n.moveFlag = (rand.nextDouble() > n.prob_coord.get(n.positionTracker)) ? true : false;
        	 
           }
            
         }
       }
 }






} //end of Program


