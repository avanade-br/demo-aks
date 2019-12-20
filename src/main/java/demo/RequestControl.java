package demo;

public class RequestControl
{
    private static int activeCounter = 0;
    private static long totalCounter = 0;
    private static final Object locker = new Object();

    public static void startRequest()
    {
        synchronized (locker)
        {
            activeCounter++;
            totalCounter++;
        }
    }

    public static void endRequest()
    {
        synchronized (locker)
        {
            activeCounter--;
        }
    }

    public static int getActiveRequestCount()
    {
        return activeCounter;
    }

    public static long getTotalRequestCount()
    {
        return totalCounter;
    }
}