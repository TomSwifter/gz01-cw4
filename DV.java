import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class DV implements RoutingAlgorithm {
    
    static int LOCAL = -1;
    static int UNKNOWN = -2;
    static int INFINITY = 60;
    //As used in RFC2453:
    static int TIMEOUT = 180;
    static int DEFAULT_GARBAGE_COLLECTION_TIME = 120;
    
    //Data structure to store the routing table of a router
    Vector<DVRoutingTableEntry> routingTable;
    //initiate router ob]ect
    Router router;
    //set interval for time out 
    int updateInterval;
    //set boolean for poison reverse
    boolean allowPReverse;
    
    boolean allowExpire;
    
    
    //left emtpy as specified by the routing algorithm interface
    public DV()
    {
    }
    
    public void setRouterObject(Router obj)
    {
    	router = obj;
    }
    
    public void setUpdateInterval(int u)
    {
    	updateInterval = u;
    }
    
    public void setAllowPReverse(boolean flag)
    {
    	allowPReverse = flag;
    }
    
    public void setAllowExpire(boolean flag)
    {
    	allowExpire = flag;
    }
    
    public void initalise()
    {
        //new table
        routingTable = new Vector<DVRoutingTableEntry>();
    	
    	//Initialise the routing table with the local entry
    	DVRoutingTableEntry local = new DVRoutingTableEntry(router.getId(),LOCAL,0,router.getCurrentTime());
    	
        //adding the local router - self
    	routingTable.addElement(local);
    	
    	
    }
    
    /*
     * (non-Javadoc)
     * @see RoutingAlgorithm#getNextHop(int)
     * Computes the next hop interface to reach a particular destination from the current router
     */
    public int getNextHop(int destination)
    {
    	//Returns the interface in the routing table entry for a particular destination,
    	//if the destination is reachable i.e, metric is less than INFINITY
    	//Otherwise, it returns the value -2 i.e, UNKNOWN
    	for(DVRoutingTableEntry tableEntry:routingTable){
    		if(tableEntry.getDestination()==destination && tableEntry.getMetric()<INFINITY){
    			return tableEntry.getInterface();
    		}
    	}
    	//default 
        return UNKNOWN;
    }
    
    public void tidyTable()
    {
    	//Tidies the table before each step by checking if any link of the router is down 
    	//and marking the interface as down by setting the metric to INFINITY is the link is down
    	for(DVRoutingTableEntry tableEntry:routingTable){
    		//If any interface is down in the routing table, update the metric to infinity
    		if(!router.getInterfaceState(tableEntry.getInterface())){
    			if(tableEntry.getMetric()<INFINITY){
    				tableEntry.setMetric(INFINITY);
    			}
    		}
    		
    		//This code only executes if expire is ON
    		if(allowExpire){
	    		if(router.getCurrentTime()-tableEntry.getTime()>TIMEOUT){
	    			//If the last update for the entry is more than the timeout time i.e 180s,
	    			//we mark that entry for deletion by setting the metric to INFINITY
	    			// as per RFC2453 specifications
	    			tableEntry.setMetric(INFINITY);
	    		}
    		}
    	}
    	
    	//Garbage collection is done on every table entry whose metric is INFINITY i.e,
    	// destination is unreachable, every 120s
    	if(allowExpire)
    		doGarbageCollection();
    	
    }
    
    /*
     * This method deletes expired entries or entries with metric INFINITY (unreachable destination)
     * every 120 seconds
     */
    private void doGarbageCollection() {

    	if(router.getCurrentTime()%DEFAULT_GARBAGE_COLLECTION_TIME==0){
    		//The code inside the if loop gets executed only if the current router time is a 
    		//multiple of 120 (i.e at 120s, 240s, 360s, etc)
    		
    		//Iterating through each entry in the routing table and checking if the entry 
    		//is marked for deletion or if the destination is unreachable, i.e, if the metric 
    		//is INFINITY and removing that entry from the routing table
    		Iterator<DVRoutingTableEntry> itr = routingTable.iterator();
    		while(itr.hasNext()){
    			DVRoutingTableEntry entry = itr.next();
    			if(entry.getMetric()>=INFINITY){
    				itr.remove();
    			}
    		}
    	}
	}

    
	public Packet generateRoutingPacket(int iface)
    {
       	
    	Packet routingPacket;
    	int currentTime = router.getCurrentTime();
    	
    	//We send the routing packet only at the update intervals specified
    	//The code inside the if loop gets executed only if the current time is a multiple of the update interval
    	if(currentTime%updateInterval==0){
    		//Destination of the routing packet is 255, i.e BROADCAST address
    		//as per the problem statement
    		routingPacket = new Packet(router.getId(),Packet.BROADCAST);
    		routingPacket.setType(Packet.ROUTING);
    		Payload payload = new Payload();
    		for(DVRoutingTableEntry tableEntry:routingTable){
    			//The if loop gets executed if split horizon with poison reverse is ON
    			// and if the next hop for the destination of the table entry is the interface on which the routing packet is to be send
    			if(allowPReverse&&(iface==getNextHop(tableEntry.getDestination()))){
    				DVRoutingTableEntry poisonedEntry = new DVRoutingTableEntry(tableEntry.getDestination(),
    						tableEntry.getInterface(),INFINITY,tableEntry.getTime());
    				//We poison the entry by setting the metric to INFINITY
    				payload.addEntry(poisonedEntry);
    			} else {
    				//Regular flow
    				payload.addEntry(tableEntry);
    			}
    		}
    		routingPacket.setPayload(payload);
    		return routingPacket;
    	}
    		
    	return null; 
    	
    }
    
    public void processRoutingPacket(Packet p, int iface)
    {
    	Payload payload = p.getPayload();
    	
    	for(Object o:payload.getData()){
    		DVRoutingTableEntry recvdEntry=(DVRoutingTableEntry)o;
    		boolean found = false;
    		DVRoutingTableEntry matchedEntry = null;
    		//Calculating metric
    		//If the metric is greater than INFINITY i.e, 60, we set it to 60 itself for consistency
    		int m = router.getInterfaceWeight(iface)+recvdEntry.getMetric()>INFINITY
    					?INFINITY:router.getInterfaceWeight(iface)+recvdEntry.getMetric();
    		
    		//Check for the entry in the routing table of the current router whose destination matches with the destination 
    		//of the entries of the received routing table
    	
    		for(DVRoutingTableEntry routingTableEntry:routingTable){
    			if(routingTableEntry.getDestination()==recvdEntry.getDestination()){
    				found = true;
    				matchedEntry = routingTableEntry;
    				break;
    			}
    		}
    		
    		//if the destination of the received entry was not found in the current router
    		//we add the entry to the current router's routing table
    		if(!found){
    			DVRoutingTableEntry newEntry = new DVRoutingTableEntry(recvdEntry.getDestination(), iface, m, router.getCurrentTime());
    			routingTable.addElement(newEntry);
    		} else if(matchedEntry.getInterface()==iface){
    			matchedEntry.setMetric(m);
    		} else if(m < matchedEntry.getMetric()){
    			matchedEntry.setMetric(m);
    			matchedEntry.setInterface(iface);
    		}
    		
    		if(found && matchedEntry.getMetric()<INFINITY){
    			matchedEntry.setTime(router.getCurrentTime()); //Tracking the last update of a particular destination for the router
    		}
    	}
    }
    
    public void showRoutes()
    {
    	System.out.println("Router "+router.getId());
    	for(DVRoutingTableEntry tableEntry:routingTable){
    		System.out.println("d "+tableEntry.getDestination()+" i "+tableEntry.getInterface()+" m "+tableEntry.getMetric());
    	}
    } 
    
    
}

class DVRoutingTableEntry implements RoutingTableEntry
{
	//setting up table variables
	private int dest; 
	private int iface;
	private int metric;
	private int time;
    
    public DVRoutingTableEntry(int d, int i, int m, int t)
	{
    	//initializing table values
    	this.setDestination(d);
    	this.setInterface(i);
    	this.setMetric(m);
    	this.setTime(t);
	}
    public int getDestination() { return dest; } 
    public void setDestination(int d) { this.dest = d;}
    public int getInterface() { return iface; }
    public void setInterface(int i) { this.iface = i;}
    public int getMetric() { return metric;}
    public void setMetric(int m) { this.metric = m;} 
    public int getTime() {return time;}
    public void setTime(int t) { this.time = t;}
    
    
	@Override
	public String toString() {
		return "";
	}
	   
    
}

