
//******************************************************************************
//PACKAGE NAME

package DTNRouting;

//******************************************************************************
//IMPORT CLASSES
import java.awt.image.BufferedImage;
import java.awt.*;
import java.applet.*;
import java.io.*;
import java.util.*;

//import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import AdapterPackage.*;
import java.util.logging.Level;
import java.util.logging.Logger;


//******************************************************************************
//MAIN APPLET CLASS
// 0-5 km/hr, for running 7.5-13.5 km/hr, for cycling 16-24 km/hr, and for cars 50-90 km/hr
public class dtnrouting extends Applet  implements Runnable
{
	private static final long serialVersionUID = 1L;
	// VARIABLES USED THROUGHOUT THE SIMULATION
	public static long timer = 0, currentTime;
	//  source and destination indices declare static and other parameters are initially 0
	public static int dataset_simulation_index=0,appletWidth, appletHeight, area_x_axis = 20, area_y_axis = 20, changes=0;
	// dimensions of Applet parameters
	public static int width, height, x_start, y_start, first_regular_node_index=0, uavRange=8;

	// PERFORMANCE METTRICS
	// After multiple simulation averaging the results of the three metrics
	//public static int latency_avg=0, load_avg=0, bandwidth_avg=0, packetCounter=0, DR_avg=0, nodecount=0;
	public static int   source_index[] , dest_index[], liveNodes_index[]; //
	public static int destsourcePair[], destsourcePairPrevious[];
	public static  int [] RR,CR, RC, RA, PP, EP, Result; //For dynamic time slot allocation
    public static int numberUAVs;
    public static int[] dest_statisfying_all_requirements;
   
	// Variables related to movement speeds
	public static boolean  RunForUQoIT =false, THIS_SIMULATION_ENDED=false, SIMULATION_RUNNING=false;
	public static int    total_packetsDeliveredExpired=0, SIMULATION_PART=0, SIMULATION_N0=30, isMovementStatic= 1; //0 for mobile and 1 for static//TOTAL_SIMULATION_RUNS=1, 
	public static double [][] adjacencyMatrix;
	public static int total_packetsDelivered=0, total_packetsExpired=0;//
	public static ArrayList<Integer> liveNodeIDs=new ArrayList<Integer>();
	public static ArrayList<Integer> liveDestinationIDs=new ArrayList<Integer>();
	public static ArrayList<Integer> liveSourceIDs=new ArrayList<Integer>();
	
	/* function "fuel_height" of class "FireModel" called by button name "QoIT Path" of class "MyActionAdapter" 
	 to populate HashMap fuel_height */	
	public static HashMap<String, Double>  fuel_height = new HashMap<String, Double>();  // used to store height of fuel at each coordinate (x, y)
	public static HashMap<String, Double>  burned_area = new HashMap<String, Double>();  // used to keep record of coordinates (x, y) already burned
	
	//******************************************************************************
	//DIFFERENT OBJECTS
	public static NodeMovement nodemovement;
	public static double[][] p;    //predictability value
	Graphics graphics;
	private Rectangle rect=null;
	PlayField playField=new PlayField();
	UpdateInformation updateInformation = new UpdateInformation();  	
	//******************************************************************************
	// CSV Files
    //Performance-------------------------------------------------------------
	//State the path where you want to keep the results
	public static String path="";
	//Create a folder of Dataset
    public static String folder=path+"Dataset/";
	public static PrintWriter performanceFile = null;
	public static String basicPerformance =  folder+"Performance.csv";

	// Network Metric Threshold Values w.r.t Users
	String userRequirements_wrt_NetworkMetricValues = folder+"UserRequirements_wrt_NetworkMetricValues.csv";
	WriteFile UserRequirements_wrt_NetworkMetricValues = new WriteFile(userRequirements_wrt_NetworkMetricValues, true);
	//UserRequirements_wrt_NetworkMetricValues.clearTheFile();

	// Network Metric Values for SELECTED Source w.r.t Users
	String networkMetricValues_SelectedSource = folder+"NetworkMetricValues_SelectedSource.csv"; 
	WriteFile NetworkMetricValues_SelectedSource = new WriteFile(networkMetricValues_SelectedSource, true);
	String networkMetricValues_SelectedSource_usingUAVs = folder+"NetworkMetricValues_SelectedSource_usingUAVs.csv"; 
	WriteFile NetworkMetricValues_SelectedSource_usingUAVs = new WriteFile(networkMetricValues_SelectedSource_usingUAVs, true);

	// Individual Network Metric Goodness Score of SELECTED Source w.r.t Users 
	String networkMetricsScore_SelectedSource = folder+"NetworkMetricsScore_SelectedSource.csv";
	WriteFile NetworkMetricsScore_SelectedSource = new WriteFile(networkMetricsScore_SelectedSource, true);
	String networkMetricsScore_SelectedSource_usingUAVs = folder+"NetworkMetricsScore_SelectedSource_usingUAVs.csv";
	WriteFile NetworkMetricsScore_SelectedSource_usingUAVs = new WriteFile(networkMetricsScore_SelectedSource_usingUAVs, true);

	// Individual Quality Metric Score of SELECTED Source w.r.t Users 
	String qualityMetricsScore_SelectedSource = folder+"QualityMetricsScore_SelectedSource.csv";
	WriteFile QualityMetricsScore_SelectedSource = new WriteFile(qualityMetricsScore_SelectedSource, true);
	String qualityMetricsScore_SelectedSource_usingUAVs = folder+"QualityMetricsScore_SelectedSource_usingUAVs.csv";
	WriteFile QualityMetricsScore_SelectedSource_usingUAVs = new WriteFile(qualityMetricsScore_SelectedSource_usingUAVs, true);

	// "Priorities, Number, Percentage" of Metrics met of SELECTED Source w.r.t Users
	String pnp_SelectedSource = folder+"PNP_SelectedSource.csv";
	WriteFile PNP_SelectedSource = new WriteFile(pnp_SelectedSource, true);
	
	
	//******************************************************************************
	//Set layout for panel
	BorderLayout bl =new BorderLayout(10,10);    //Create object of layout
	//TOP AND LEFT PANEL
	Panel p1=new Panel();
	Panel p2=new Panel();
	static String s;
	BufferedImage  bf = new BufferedImage(800,600, BufferedImage.TYPE_INT_RGB);

	// MENU BARS
	JMenuBar jmb=new JMenuBar(); // Menu bar containing menus and menu items

	//Menus and menu items in menu bar jmb
	JButton nodeMenu=new JButton("Node");
	

	//******************************************************************************
	//Image Icons and RestButtons
	ImageIcon clearIcon=new ImageIcon(path+"clear.png");
	ImageIcon runIcon=new ImageIcon(path+"run.png");
	ImageIcon map;
	Border refreshBorder = new LineBorder(Color.lightGray, 1);
	Border clearBorder = new LineBorder(Color.lightGray, 1);
	JButton clear=new JButton(clearIcon);
	Border runBorder = new LineBorder(Color.lightGray, 1);
	JButton run=new JButton(runIcon);

	//******************************************************************************
 
	// Array Lists of all node types
	public static ArrayList<Node>   theNodes=new ArrayList<Node>();             // contains all nodes i.e source + destination + relay // LIVE NODES
	public static ArrayList<Node>   theSources=new ArrayList<Node>();			// contains only source nodes
	public static ArrayList<Node>   theDestinations=new ArrayList<Node>();		// contains only destination nodes
	public static ArrayList<Packet> packetTracker=new ArrayList<Packet>();		// contains all the packets. It is to track their delivery
	public static ArrayList<Node>   prospectiveUAVs = new ArrayList<Node>();	// contains prospective UAVs
	public static ArrayList<Node>   deployedUAVs = new ArrayList<Node>();		// contains UAVs that are to be deployed
	public static ArrayList<Node>   Last_LeftRssNodes = new ArrayList<Node>();	// contains LeftRSS Nodes of destinations with un-fulfilled reqs.
	public static ArrayList<Node>   First_RightRssNodes = new ArrayList<Node>();// contains LeftRSS Nodes of destinations with un-fulfilled reqs.
	public ArrayList<Node>          DestUnReq = new ArrayList<Node>();          // Destinations with unfulfilled requirements
	public static ArrayList<Node>   packetDelivedDestinations = new ArrayList<Node>();          // Destinations with unfulfilled requirements
	public static ArrayList<Node>   relayNodes = new ArrayList<Node>();          // Destinations with unfulfilled requirements
	//public static Node recentDeadNode = null;
	//Array Lists for storing different values

	public static ArrayList<Node>      TransferNodes=new ArrayList<Node>();			 // contains only relay nodes
	public static ArrayList<Integer>   lowUserUsability_destinations=new ArrayList<Integer>();  // destinations for which selected source doesn't meet all quality metric needs
	//******************************************************************************

	//LABELS FOR COMPONENTS
	Label hd=new Label("SIMULATION OF AD HOC NEWORK" , Label.CENTER);
	Label sdp=new Label("End Nodes" ,Label.LEFT);//create label on p2
	Label latency=new Label("Latency" ,Label.LEFT);//create label on p2
	Label pathChanges=new Label("Path Changes" ,Label.LEFT);//create label on p2    
	Label dratio=new Label("Delivery Ratio", Label.CENTER);

	//******************************************************************************
	// TEXT AREAS USED IN SECOND PANEL
	public static TextArea sdpTA=new TextArea("Src ---> Dst: Pkt");  //create Textarea on p2
	public static TextArea latencyTA=new TextArea("Simulation "+SIMULATION_N0+"----\nFinal Results");						//create Textarea on p2
	public static TextArea pathChangesTA=new TextArea("Sim: 1");					//create Textarea on p2
	public static TextArea dratioTA=new TextArea(" ");						//create Textarea on p2
	

	//******************************************************************************

	
	
	//Called when an Applet starts execution
	//******************************************************************************

	@Override
	public void init()
	{
		setLayout(bl);      //set border layout
		setParameters();    //set parameters for GUI
		addComponents_Panel1();
		addComponents_Panel2();
		currentTime = System.currentTimeMillis();
		
		 
		  BufferedWriter bw = null;
		  FileWriter fw = null;
		  try{
		     fw = new FileWriter(basicPerformance, true);
		     bw = new BufferedWriter(fw);
		     performanceFile = new PrintWriter(bw);
		     performanceFile.println("Time, Run, Part, Dest, Source, TotalPackets, PacketID, Hops,Latency ,Reliability, Delivered");
		  }
		  catch( IOException e ){
		     // File writing/opening failed at some stage.
		  }
	}

	//******************************************************************************

	public void addComponents_Panel1()
	{
		p1.setLayout(new GridLayout(2,1));

		//set color and font for heading on p1
		hd.setForeground(Color.black);
		hd.setFont(new Font("San Serif", Font.BOLD, 16));

		//Add menus in node Menu
		nodeMenu.setSize(10, 10);
		nodeMenu.setBorderPainted(false);
		nodeMenu.setContentAreaFilled(false);
		nodeMenu.setOpaque(false);
		nodeMenu.setFont(new Font("Dialog",Font.PLAIN,10));
		nodeMenu.addActionListener(new MyActionAdapter(this)); // when clicked on Node Button, it opens


		//setting border and name of "run, refresh, clear" button
		run.setBorder(runBorder);
		run.setActionCommand("Run");
		clear.setBorder(clearBorder);
		clear.setActionCommand("Clear");
		//Register reset  button to the listener
		run.addActionListener(new MyActionAdapter(this));
		clear.addActionListener(new MyActionAdapter(this));

		//Adding Menus

		jmb.add(nodeMenu);  
		jmb.add(run);
		jmb.add(clear);

		p1.add(hd);
		p1.add(jmb);
		p1.setBackground(new Color(0xb0c4de));  //set the background color of p1
		p1.setPreferredSize(new Dimension(appletWidth,40));
		y_start=p1.getHeight()+50;
		height=appletHeight-y_start-130;//70;
		add(p1, BorderLayout.PAGE_START);       
	}

	//******************************************************************************

	public void addComponents_Panel2()
	{
		//set layout and dimension of p2
		p2.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
		p2.setFont(new Font("San Serif", Font.BOLD,9));

		//set dimension for comments text area and add on p2
		//rhist.setFont(new Font("San Serif", Font.BOLD,11));
		sdpTA.setPreferredSize(new Dimension(140,150));
		latencyTA.setPreferredSize(new Dimension(140,150));
		pathChangesTA.setPreferredSize(new Dimension(140,150));
		dratioTA.setPreferredSize(new Dimension(140,150));

		// Add components to panel p2
		p2.add(sdp);
		p2.add(sdpTA);
		p2.add(latency);
		p2.add(latencyTA); //add comments text area on p2
		p2.add(pathChanges); //add current situation text area on p2
		p2.add(pathChangesTA); 
		p2.add(dratio);
		p2.add(dratioTA);

		p2.setPreferredSize(new Dimension(140, appletHeight));
		//Setting parameters for graphics
		x_start=p2.getWidth()+150;
		width=appletWidth-x_start-10;
		//setting color of panels
		p2.setBackground(new Color(0xb0c4de));  //set the background color of p2
		// add the panels on different region
		add(p2, BorderLayout.WEST);
	}

	//******************************************************************************

	public void setParameters()
	{
		setBackground(Color.white);  //set the rectangle color
		//Create an object of NodeMovement Class
		nodemovement = new NodeMovement();
		//Reset dimensions of width and height
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		appletHeight=(int)dim.getHeight();
		appletWidth=(int)dim.getWidth();
		this.setSize(new Dimension(appletWidth,appletHeight));
	}

	//******************************************************************************

	//start a thread by adding its run method
	@Override
	public void start ()
	{
		Thread th = new Thread (this);
		th.start();
	}

	//******************************************************************************

	//define the code that constitutes the new thread
	public void run()
	{
		//Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		while (true) {


			repaint();

			try{ Thread.sleep(1000);
			}catch (InterruptedException ex) { }

			// Run the simulation network
			if(SIMULATION_RUNNING) 
			{

				// Update simulation time lapse
				updateInformation.UpdateTTLandLatency();

				//Update positions of the relay-R and destination-D nodes
				if(isMovementStatic==0) {
				try { updateInformation.nextPositionForMovement(); } 
				catch (IOException ex) { Logger.getLogger(dtnrouting.class.getName()).log(Level.SEVERE, null, ex); }}


				//Call transfer function to deliver messages in each time unit
				if(!dtnrouting.THIS_SIMULATION_ENDED)
					playField.FivePhaseReservationProtocol();
			}
			if(THIS_SIMULATION_ENDED)
				updateInformation.simulationSettings();


		}
	}


	//******************************************************************************
	//Calls paint()
	@Override
	public void update(Graphics g){
		paint(g);
	}

	//******************************************************************************

	@Override
	public void paint(Graphics g){
		
		//resizes play field dimensions accordingly and calls animation() function
		if(!getBounds().equals(rect)){
			rect = this.getBounds();
			bf   = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		}
		try{
			animation(bf.getGraphics());
			g.drawImage(bf,0,0,null);
		}catch(Exception ex){ }
	} 

	//******************************************************************************
	//Draws graphics in play field
	public void animation(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(3));
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.RED);
		g2.drawRect(x_start, y_start, width, height);
		playField.drawNodesPackets(g);

	}

	//******************************************************************************
}//END OF CLASS

