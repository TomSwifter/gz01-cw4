import java.lang.Math;

public class DV implements RoutingAlgorithm {
    
    static int LOCAL = -1;
    static int UNKNOWN = -2;
    static int INFINITY = 60;
    
    //set table data structure
    //initiate router ob]ect 
    Router router;
    //set interval for time out 
    int updateInterval;
    //set boolean for poison reverse
    boolean allowPReverse;
    
    boolean allowExpire;
    
    //count timeouts on interfaces so we know when links are down
    //set boolean for enabling split horizon
    
    //keep track of when the last update was sent for each interface
    
    
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
    	//initialize the table to contain only one interface - the local one:
    	
    	//intitaliaze a routing table entry to put in the table together with the first router
    	//DVRoutingTableEntry tableEntry = new DVRoutingTableEntry(int d, int i, int m, int t);
    	//need to get the router id for that
    	
    	//track when was the last update sent for this router 
    	
    	//might need to add a field to expire 
    	
    }
    
    public int getNextHop(int destination)
    {
    	//if destination is in routing table
    		//check if destination is smaller than infinity 
    		//if it is, return its interface.
    	
    	//default 
        return UNKNOWN;
    }
    
    public void tidyTable()
    {
    	//check on interfaces on the router 
    	//for each inerface: if its state (given by getInterfaceState) is false
    		//set all entries for that interface to inifinty
    	
    	//check if expired (isExpired == true)
    		//if expired --> add to garbage collectiom 
    }
    
    public Packet generateRoutingPacket(int iface)
    {
       	//get current time of router
    	//get interface state defined by iface --> know is the link is down or not
    	//get the last update at iface
    	//if link is not down and its time for the next update (if time >= from  interval + last time)
    	//or if it is the first update 
    	//we pass the intergface to the link:
    		
    	//we generate a payload for the interface 
    	//we construct a packet to deliver the data on (Packet) --> each packet contains the router id and what to do with it --> broadcast
    	//we set the generated payload for this packet packet.geneareaetPyload(payld)
    	//we set the type of the packet --> routing 
    	//we update the last time we sent to current time 
    	//we return the packet 

    	//default
    	return null; 
    	
    }
    
    public void processRoutingPacket(Packet p, int iface)
    {
    	//we need to set a vector that gets the packet table entires
    	//we set metric to be ther router intragace weight 
    	//we merge the vector table with the iface
    }
    
    public void showRoutes()
    {
    	
    	//just print router id:
    		//for each entry for this router id print getDestination, getInterface, getMetric 
    }
    
    
    //i might need here some helper methods such as garbage collection, and a methods that 
    //sets all distance entries in a routing table to inifintity, 
    
    
    
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
    
    public String toString() 
	{
	    return "";
	}
}

