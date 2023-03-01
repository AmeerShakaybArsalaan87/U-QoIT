//******************************************************************************
//PACKAGE
package DTNRouting;

//******************************************************************************
//BASE CLASS FOR ALL ROUTING PROTOCOLS

public abstract class Routing
{

    public abstract void DeliverData(Node n1, Node n2);
    public static boolean transfer=false, isDestination=false;

//******************************************************************************
   
public void setPerimeters(){}
public void setPerimeters(String filename, int size){}
   
 //******************************************************************************

//******************************************************************************

public void Transitivity(int i, int j)
{
     throw new UnsupportedOperationException("Not yet implemented");
}

//******************************************************************************

public void Aging(int i, int j)
{
     throw new UnsupportedOperationException("Not yet implemented");
}

//******************************************************************************

public void Encounter(int i, int j)
{
        throw new UnsupportedOperationException("Not yet implemented");
}

//******************************************************************************

public void ContactCounter(int i, int j){}

//******************************************************************************

public boolean expiredTTL_LargeSize(Node nx,Node ny, Packet packetObj)
{
	     boolean returnvalue=true;
         // if packet's TTL expires remove it from the node's memory
		 if(packetObj.isTTLExpired==true || packetObj.ispacketDelivered==true) {
		   
		   if(packetObj.packetTTL==0 & nx.queueSizeLeft==0) {
		   nx.queueSizeLeft+=packetObj.packetSize;} // the whole space}
		   
		   nx.packetID.remove(packetObj.ID);
		   returnvalue=true; 
		   } 
		 
		 //If size of packet is smaller than the buffer space of the node
		 // and packet be transmitted in the current slot
		 // and ny does not contain the packet
		 // and ny is in the path towards destination
		 // and this packet is not transmitted in this slice
		 else if(packetObj.packetSize <= nx.capacity && 
			!ny.packetID.contains(packetObj.ID) &&
		    packetObj.packetTransferedinSlice==false)
			{
			 if(packetObj.pathHops.size()>1) {
				 if(packetObj.pathHops.get(1).equals(ny))
				 {//System.out.println("next hop:"+ packetObj.pathHops.get(1).name);
					 returnvalue=false;}}
		     } 
		 
		 else returnvalue=true;
		 return returnvalue;
}




//******************************************************************************

public void deliver_Destination(Node nx, Node ny, Packet packetObj)
{
	 		// Give message to destination
		    //Since packet is transfered
			packetObj.packetReliability = (Math.min(packetObj.packetReliability, nx.reliability));
			packetObj.packetHops+=1;
			
            ny.DestNPacket.put(packetObj,null);
            ny.number_packet_arrived+=1;
            ny.packetID.add(packetObj.ID);
             
            dtnrouting.total_packetsDeliveredExpired+=1;
            
            // packet delivered and transfered in this slice
            packetObj.ispacketDelivered=true;
            dtnrouting.total_packetsDelivered = dtnrouting.total_packetsDelivered+1;
            dtnrouting.performanceFile.append(dtnrouting.currentTime+","+dtnrouting.SIMULATION_N0+","+dtnrouting.SIMULATION_PART+","+packetObj.destNode_ofpacket.name+","+packetObj.sourceNode_ofpacket.name+","+packetObj.destNode_ofpacket.num_packets+","+packetObj.ID+","+packetObj.packetHops+","+(packetObj.packetLatency)+","+packetObj.packetReliability+","+1+"\n");
            dtnrouting.performanceFile.flush();
            packetObj.pathHops.remove(0);
            //packetObj.packetTransferedinSlice=true;
            
            //update nx memory 
     		nx.capacity -= 1;//packetObj.packetSize;
            nx.queueSizeLeft+=packetObj.packetSize; // the whole space            
            nx.packetID.remove(packetObj.ID);
            
}

//******************************************************************************

public void deliver_Relay(Node nx, Node ny, Node destNode, Packet packetObj, boolean nx_remove_packet)
{
	 	  
		  //Since packet is transfered
		  packetObj.packetReliability = (Math.min(packetObj.packetReliability, nx.reliability));
		  packetObj.packetHops+=1; 
		  packetObj.packetTransferedinSlice=true;
		  if(packetObj.pathHops.size()>1)
		  {	packetObj.pathHops.remove(0);}
		  
          // Give message to relay
          ny.DestNPacket.put(packetObj,destNode);
          ny.packetID.add(packetObj.ID);
          ny.queueSizeLeft-=packetObj.packetSize;
          
          
          //update nx memory 
   		  nx.capacity -=1;// packetObj.packetSize;
   		  
   		  if(nx_remove_packet)
   		  {
   			  nx.packetID.remove(packetObj.ID);
   			  nx.queueSizeLeft+=packetObj.packetSize; // the whole space  
   		  }

}

}// End of class



