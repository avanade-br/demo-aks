package demo;

import java.text.DecimalFormat;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

public class AppStats
{
    private double maxAvailableMemory;
    private double totalAllocatedMemory;
    private double freeMemory;
    private double usedMemory;                
    private double percentUsedMemory;
    private int activeRequests;
    private String clientIP;
    private Dictionary<String, String> requestHeaders;
    
    
    private long totalRequests;
    private String hostName;

    public static double toMegaBytes(long bytes) 
    {
        return round((double)bytes / 1048576.0);
    }

    public static double round(double value) 
    {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.valueOf(df.format(value));
    }

    public static AppStats create() 
    {
        return create(null);
    }

    public static AppStats create(HttpServletRequest request)
    {
        // get the current runtime associated with this process
        Runtime run = Runtime.getRuntime();
        
        // Create the Entity
        AppStats stats = new AppStats();

        // Set the entity properties
        stats.setMaxAvailableMemory(toMegaBytes(run.maxMemory()));
        stats.setTotalAllocatedMemory(toMegaBytes(run.totalMemory()));
        stats.setFreeMemory(toMegaBytes(run.freeMemory()));
        stats.setUsedMemory(round(stats.getTotalAllocatedMemory() - stats.getFreeMemory()));
        stats.setPercentUsedMemory(round(100.0 * stats.getUsedMemory() / stats.getMaxAvailableMemory()));

        stats.setHostName(System.getenv().get("HOSTNAME"));
        stats.setActiveRequests(RequestControl.getActiveRequestCount());
        stats.setTotalRequests(RequestControl.getTotalRequestCount());

        if ( request != null )
        {
            stats.setClientIP(request.getRemoteAddr());
            Dictionary<String, String> headers = new Hashtable<String, String>();

            Enumeration<String> reqHeaders = request.getHeaderNames();

            while (reqHeaders.hasMoreElements()) 
            {
                String name = reqHeaders.nextElement().split(":")[0];
                headers.put(name, request.getHeader(name));
            }

            stats.setRequestHeaders(headers);
        }

        // Return the built entity
        return stats;
    }



    public double getMaxAvailableMemory() 
    {
        return maxAvailableMemory;
    }
    public void setMaxAvailableMemory(double value) 
    {
        maxAvailableMemory = value;
    }


    public double getTotalAllocatedMemory() 
    {
        return totalAllocatedMemory;
    }
    public void setTotalAllocatedMemory(double value) 
    {
        totalAllocatedMemory = value;
    }


    public double getFreeMemory()
    {
        return freeMemory;
    }
    public void setFreeMemory(double value) 
    {
        freeMemory = value;
    }


    public double getUsedMemory() 
    {
        return usedMemory;
    }
    public void setUsedMemory(double value)
    {
        usedMemory = value;
    }


    public double getPercentUsedMemory() 
    {
        return percentUsedMemory;
    }
    public void setPercentUsedMemory(double value)
     {
        percentUsedMemory = value;
    }


    public long getTotalRequests() {
        return totalRequests;
    }
    public void setTotalRequests(long value) {
        totalRequests = value;
    }

    public String getHostName() {
        return hostName;
    }
    public void setHostName(String value) {
        hostName = value;
    }

    public int getActiveRequests() 
    {
        return activeRequests;
    }
    public void setActiveRequests(int value) 
    {
        activeRequests = value;
    }

    /**
     * @return String return the clientIP
     */
    public String getClientIP() {
        return clientIP;
    }

    /**
     * @param clientIP the clientIP to set
     */
    public void setClientIP(final String clientIP) {
        this.clientIP = clientIP;
    }


    /**
     * @return Dictionary return the requestHeaders
     */
    public Dictionary<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * @param requestHeaders the requestHeaders to set
     */
    public void setRequestHeaders(Dictionary<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

}