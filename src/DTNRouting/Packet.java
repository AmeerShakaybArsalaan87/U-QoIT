//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import java.util.ArrayList;
import java.util.Random;


//------------------------------------------------------------------------------
//START OF CLASS packet
public class Packet
{
//Instance Variables
    public Node destNode_ofpacket=new Node(); //destination node of the packet
    public Node sourceNode_ofpacket=new Node(); //source node of the packet
    public int packetTTL,maxTTL;
    
    // Features used to assess network transmission quality
    public int packetLoad=1,packetHops=0,packetLatency=0;
    public double packetReliability=4,packetSize;
    public static int ID_incrementer;
    public String ID;
    
    public boolean ispacketDelivered=false,isTTLExpired=false,isLargeSize=false,packetTransferedinSlice=false;
    
    
    public int num_packets=0;
    public ArrayList<Node>   pathHops=new ArrayList<Node>();
    Random rand=new Random();
    //dtnrouting dtn=new dtnrouting();
    


//******************************************************************************

public Packet()
{    }

//******************************************************************************

public void refreshPacketSettings()
{
	
	//packetID=packetID+1;
	// increment the id of packet and then add it in tpacketNumber
	//packetName ="p"+packetID; //packet Name for reference in code
	
	packetSize = 0.004096; //512 bytes UDP packet 
    ispacketDelivered=false;  
    isLargeSize=false;
    isTTLExpired=false;
    packetLoad=1;
    packetTTL=maxTTL;
    packetLatency=0;
    packetHops=0;
    pathHops.clear();
    packetReliability=4;
    packetTransferedinSlice=false;
}

//******************************************************************************

}//END OF packet CLASS
