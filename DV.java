import java.lang.Math;

public class DV implements RoutingAlgorithm {
    
    static int LOCAL = -1;
    static int UNKNOWN = -2;
    static int INFINITY = 60;
    
    public DV()
    {
    }
    
    public void setRouterObject(Router obj)
    {
    }
    
    public void setUpdateInterval(int u)
    {
    }
    
    public void setAllowPReverse(boolean flag)
    {
    }
    
    public void setAllowExpire(boolean flag)
    {
    }
    
    public void initalise()
    {
    }
    
    public int getNextHop(int destination)
    {
        return 0;
    }
    
    public void tidyTable()
    {
    }
    
    public Packet generateRoutingPacket(int iface)
    {
        return null;
    }
    
    public void processRoutingPacket(Packet p, int iface)
    {
    }
    
    public void showRoutes()
    {
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
    
    public String toString() 
	{
	    return "";
	}
}

