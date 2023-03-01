//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import java.awt.*; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.IOException;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Random;


//*****************************************************************************
// START OF CREATENODE CLASS
public class CreateNode extends dtnrouting  implements  ActionListener, TextListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Instance variables
	public Random rand=new Random();
	public JFrame jf=new JFrame("Create Node");
	public Label lregular=new Label("Number of Nodes");
	public TextField tregular=new TextField("94");
	
	public Label lsource= new Label("Source Nodes");
	public TextField tsource= new TextField("6");
	
	public Label ldestination= new Label("Dest. Nodes");
	public TextField tdestination= new TextField("20");
	
	public Label luav= new Label("UAV(s)");
	public TextField tuav= new TextField("4");
	

	public Label lspeed=new Label("Speed(m/s)");
	public TextField cspeed= new TextField("0");
	
	public Label radiorange=new Label("Radio Range(m)");
	public TextField cradio= new TextField("2"); //1 is equal to 100 meter square

	
	public Button Add=new Button("Add");
	public Button Close=new Button("Close");

	//In order to pass values to the object of CreateNode`
	public int numberofnodes, speedofnode, radiorangeofnode;
	public NoviceUAVPlacement uav_placement = new NoviceUAVPlacement();
	

	//******************************************************************************

	//CONSTRUCTOR
	public CreateNode()
	{}

	public void GenerateFrame() {
		jf.setLayout(new GridLayout(10,2,5,5));

		//A regular node can be an end device, it can be either held by
		tregular.setEnabled(true);                
		tsource.setEnabled(true);
		tdestination.setEnabled(true);
		tuav.setEnabled(true);
		
		cspeed.setEnabled(true);
		cradio.setEnabled(true);



		//Components in Frame window

		
		jf.add(lregular); 		jf.add(tregular);      
		jf.add(lsource);        jf.add(tsource);
		jf.add(ldestination); 	jf.add(tdestination);      
		jf.add(luav);           jf.add(tuav);
		
		jf.add(lspeed);             jf.add(cspeed);
		jf.add(radiorange);         jf.add(cradio);
		jf.add(Add);                jf.add(Close);

		//registering events
		tregular.addTextListener((TextListener) this);
		tsource.addTextListener((TextListener) this);
		tdestination.addTextListener((TextListener) this);
		tuav.addTextListener((TextListener) this);
		
		Add.addActionListener(this);
		Close.addActionListener(this);

		//Frame metrics
		jf.setSize(new Dimension(300,300));
		jf.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		jf.setVisible(true);
		jf.setResizable(false);
		
		

	}

	//******************************************************************************

	public void actionPerformed(ActionEvent e)
	{

		String action;
		action=e.getActionCommand();

		if(action.equals("Close"))  jf.dispose();

		//Set metrics of new node
		else if (action.equals("Add"))
		{
			// Add regular, source and destination nodes
			addNodes();
		}
			   

		else if (action.equals("Clear"))  
			Resetmetrics();


	}
	
	// *****************************************************************************
	public void addNodes() {
		// THE SOURCE NODES
					for (int l=0;l<Integer.parseInt(tsource.getText());l++)
						{
							Node node=new Node();
							
							// Keep reliability maximum for the source node
							node.reliability=4;
							Node.ID_INCREMENTER+=1;
							node.ID=Node.ID_INCREMENTER;
							node.name="S"+node.ID;
							node.speed= 0;
							node.setRadioRange(2);
							node.wholeQueueSize=node.queueSizeLeft=500;
							dtnrouting.theSources.add(node);
							dtnrouting.theNodes.add(node);
							dtnrouting.liveNodeIDs.add(node.ID);
							dtnrouting.liveSourceIDs.add(node.ID);
							node.nodePosition();
							
					   }


		// THE USER NODES (REGULAR)
					   first_regular_node_index=dtnrouting.theNodes.size();
						for (int l=0;l<Integer.parseInt(tregular.getText());l++)
						{
							Node node=new Node();
							
							//Randomly choose the reliability
							node.reliability = rand.nextInt(4)+1;
							if(node.reliability==5) {
							  node.reliability =4;}
							
							
							Node.ID_INCREMENTER+=1;
							node.ID=Node.ID_INCREMENTER;
							node.name="R"+ node.ID;
							node.setRadioRange(2);
							node.speed = Integer.parseInt(cspeed.getText());
							node.setRadioRange(Integer.parseInt(cradio.getText()));
							//Limited buffer-size  5 times maximum packet size
							node.wholeQueueSize=node.queueSizeLeft=500;//0.02048;
							
							dtnrouting.theNodes.add(node);
							node.nodePosition();
							dtnrouting.liveNodeIDs.add(node.ID);

						}  
						
			// THE UAV NODES
						dtnrouting.numberUAVs =  Integer.parseInt(tuav.getText());

					

			// THE DESTINATION NODES: Choose randomly from regular user nodes
					// Randomly choose the destination nodes
						int total_size = dtnrouting.theNodes.size();
						int d=Integer.parseInt(tdestination.getText());
						if(d > total_size)
							d = total_size;
						ArrayList<Integer> num = new ArrayList<Integer>();

						for (int i=0; i<d; i++ ){
							int rand_number = rand.nextInt(total_size);
							//If the node is already chosen as destination or the chosen node is source then do selection again
							while(num.contains(rand_number)==true || dtnrouting.theNodes.get(rand_number).name.substring(0,1).equals("S")) 
								rand_number = rand.nextInt(total_size); 
							num.add(rand_number);
							
							// A new destination node has been decided
							Node node=dtnrouting.theNodes.get(rand_number);
							//node.reliability=4;
							node.name = "D"+node.name.substring(1); //Rename it.
							// RequirementsofDestination(node);				
							// Identify packets needed by the destination
							// PacketsforDestination(node);
						}
						
					// Arrange the destinations in ascending order of ID
						for(int i=0; i < dtnrouting.theNodes.size(); i ++) {
							Node node = dtnrouting.theNodes.get(i);
							if(node.name.substring(0,1).equals("D")) {
						dtnrouting.theDestinations.add(node);
						dtnrouting.liveDestinationIDs.add(node.ID);}}
					
				
	}
   
	// *****************************************************************************
	public void RequirementsofDestination(Node node) {
		node.neworkMetricRequirements[0] = rand.nextInt(9) + 2;                    // 0 = hopCount
		node.neworkMetricRequirements[1] = rand.nextDouble() * (10.0 - 1.0) + 1.0; // 1 = informationUtility 
		node.neworkMetricRequirements[2] = rand.nextDouble() * (2.0 - 0.1) + 0.1;  // 2 = bandwidth = 0.1 Mbps or 100 Kbps
		node.neworkMetricRequirements[3] = rand.nextInt(4) + 1;                    // 3 = pathIntegrity		
		
		try {
			UserRequirements_wrt_NetworkMetricValues.writeToFile(dtnrouting.currentTime, dtnrouting.SIMULATION_PART, node.ID, node.neworkMetricRequirements[2], node.neworkMetricRequirements[0],
					node.neworkMetricRequirements[3], node.neworkMetricRequirements[1]);
			}catch (IOException e) { e.printStackTrace(); }
	}
	// *****************************************************************************

	//Packets required by a destination
	public void PacketsforDestination(Node node) // reset metrics of a node
	{

		// Specify the packet destined for this node
		// Move this code to when destinations are created
		node.num_packets = rand.nextInt(5)+5; //rand.nextInt(30)+10;
		node.packets_ttl = 100; //rand.nextInt(100)+50;

		//Below code generates packets for each destination				
		for(int j=0; j< node.num_packets; j++) {//number of packets that each source will transmit..
			Packet p =new Packet();
			Packet.ID_incrementer= Packet.ID_incrementer+1;
			p.ID = (Packet.ID_incrementer)+"";
			p.maxTTL=node.packets_ttl;
			p.destNode_ofpacket=node;
			p.refreshPacketSettings();
			node.nodePackets.add(p);
			//These packets are yet to be send
			dtnrouting.packetTracker.add(p);}
    }
	//******************************************************************************
	
	//Random placement of UAV
    public void CreateRandomUAV(int num_uav) {
    Node.ID_INCREMENTER = dtnrouting.theNodes.size()-1;

	for (int l=0;l < num_uav;l++)
	{	
		Node node=new Node();
		Node.ID_INCREMENTER+=1;
		node.ID=Node.ID_INCREMENTER;
		node.name="U"+ node.ID;
		node.speed=0;
		node.setRadioRange(dtnrouting.uavRange);
		node.wholeQueueSize=node.queueSizeLeft=500;
		dtnrouting.theNodes.add(node);
		dtnrouting.liveNodeIDs.add(node.ID);
		dtnrouting.deployedUAVs.add(node);
		//Random position of uav
		node.nodePosition();
		node.reliability = 4;
		
	} 
}	
//******************************************************************************
    
//Distance-based placement of UAV
    public void CreateDistanceUAV(int num_uav) {
    Node.ID_INCREMENTER = dtnrouting.theNodes.size()-1;
	for (int l=0;l < num_uav;l++)
	{
		Node node=new Node();
		Node.ID_INCREMENTER+=1;
		node.ID=Node.ID_INCREMENTER;
		node.name="U"+ node.ID;
		node.speed=0;
		node.setRadioRange(dtnrouting.uavRange);
		node.wholeQueueSize=node.queueSizeLeft=500;
		dtnrouting.theNodes.add(node);
		dtnrouting.liveNodeIDs.add(node.ID);
		dtnrouting.deployedUAVs.add(node);
		
		node.location = uav_placement.getUAVLocation(node, dtnrouting.theNodes);
		node.reliability = 4;
		
	} 
}
	//******************************************************************************
	//RESET metrics OF CREATE NODE CLASS
	public void Resetmetrics() // reset metrics of a node
	{
		tregular.setText("30");            
		cradio.setText("4");                 cspeed.setText("0");
		tsource.setText("6");			     tdestination.setText("6");
		tuav.setText("1");
		tsource.setEnabled(true);            tregular.setEnabled(true);
		tdestination.setEnabled(true);

	}

	@Override
	public void textValueChanged(TextEvent e) {
		// TODO Auto-generated method stub
		
	}

	//******************************************************************************



} //END OF CREATENODE CLASS