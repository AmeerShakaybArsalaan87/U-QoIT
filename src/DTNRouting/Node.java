//PACKAGhisSS
package DTNRouting;

//IMPORT PACKAGE
import java.util.*;

//******************************************************************************
//START OF CLASS NODE
public class Node
{
    //Instance variables
	public static int ID_INCREMENTER = -1;
    public ArrayList<Double> x_coord = new ArrayList<Double>();
    public ArrayList<Double> y_coord = new ArrayList<Double>();
    public ArrayList<Double> z_coord = new ArrayList<Double>();
   //probability of following a coordinate pair
    public ArrayList<Double>  prob_coord = new ArrayList<Double>();
    public int  ID, x_inc=0,y_inc=0,positionTracker, topleft_xcorner,topleft_ycorner, xwidth,yheight;
    // Used to follow part of a data set part
    public int startTracker,direction=1,reliability, number_packet_arrived=0;
    public int radioRange,speed=0,counter=0;//nodeTracker=0;
    public int centrality = 0;
    public boolean moveFlag=false;
   
    public String name; 
    public NodeMovement node_nm;
    private Random rand =new Random();
    public Location location=new Location();
    // Packets destined to this node and parameters
    public LinkedList <Packet> nodePackets =  new LinkedList<Packet>();
    public double transmit_prob,wholeQueueSize=1, queueSizeLeft; 
    

    public HashSet <String> packetID=new HashSet<String>();
    // The packet and its destination
    public HashMap <Packet, Node> DestNPacket=new HashMap<Packet, Node>();
    public int num_packets, packets_ttl, current_packet_count=0;
    
    public ArrayList<Integer>  Rss_left = new ArrayList<Integer>();
    public ArrayList<Integer>  Rss_right = new ArrayList<Integer>();
    
    public Integer L_ID, F_ID;
    public ArrayList<ArrayList<Integer>> nodeAllUAVPaths = new ArrayList<ArrayList<Integer>>();  
    // Node to be place for a SD path with 
    // unfulfilled requirements
    public ArrayList<Node> prospectiveUAVs = new ArrayList<Node>();
    // List of L node, Nu UAVs, and F node
    public ArrayList<Node>  Set_cn = new ArrayList<Node>();
    
    public ArrayList<Node> n1_neighborhood = new ArrayList<Node>(); 
    public ArrayList<Node> n2_neighborhood = new ArrayList<Node>();
    public ArrayList<Double>  link_capacity = new ArrayList<Double>();
    public double time_slot=1.0, capacity=0;
    public pathToDestination ptD;
    
    // When node is from Datasets
    public int nodePath[][],x_max,y_max;
    
    //hop count, bandwidth and path integrity requirements 
    //of a destination node
    public double neworkMetricRequirements[] =new double[4];
    public double informationUtility;
	
//******************************************************************************
//EMPTY CONSTRUCTOR
public  Node() {

			
}


//******************************************************************************
public void nodePosition() {
	// Each node will have a limited movement area within the simulation area (playfield)
	node_nm = new NodeMovement();
	this.topleft_xcorner = dtnrouting.x_start+this.getRadioRange();// + rand.nextInt(dtnrouting.width-this.getRadioRange()/2-(int)dtnrouting.width/4);
	this.topleft_ycorner = dtnrouting.y_start+this.getRadioRange();// + rand.nextInt(dtnrouting.height-this.getRadioRange()/2-(int)dtnrouting.height/4);
	this.xwidth  =  dtnrouting.width-2  *  this.getRadioRange();
	this.yheight =  dtnrouting.height-2 *  this.getRadioRange();
	node_nm.InitialNodePositions(this);
}


//******************************************************************************
public void clearNodeSettings()
{
	
	packetID.clear();	
    queueSizeLeft=wholeQueueSize;
	DestNPacket.clear();
	x_coord.clear();
	y_coord.clear();
	nodePackets.clear();
	prob_coord.clear();
	nodePosition();
	//msg_latency= msg_hops = msg_dl= msg_relibility =0;
	time_slot=1.0;
	capacity=0; 
	number_packet_arrived=0;
	
}

//******************************************************************************
//******************************************************************************
public void refreshNodeSettings()
{	
	packetID.clear();	
    queueSizeLeft=wholeQueueSize;
	DestNPacket.clear();
	positionTracker=0;
	prob_coord.clear();
	this.location.x=x_coord.get(0);
	this.location.y=y_coord.get(0);
	time_slot=1.0;
	capacity=0; 
    //msg_latency= msg_hops = msg_dl= msg_relibility =0;
	number_packet_arrived=0;
}


//******************************************************************************

//ASSIGNING RADIO RANGE TO A NODE
public void setRadioRange(int radioRange)
{
    this.radioRange=(radioRange+1)*15;
}

//******************************************************************************

//RETURNING RADIO RANGE OF A NODE
public int getRadioRange()
{
    return this.radioRange;
}

//******************************************************************************
public Location getLocation() {
	return(this.location);
}

//******************************************************************************

public boolean isUAV()
{
	if(this.name.contains("U")) return true;
	else return false;
}

public Random getRand() {
	return rand;
}

public void setRand(Random rand) {
	this.rand = rand;
}
}
