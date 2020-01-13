package demo;

import java.util.Arrays;
import java.util.List;

public final class Helpers
{
    private Helpers()
    {

    }

    public static int getEnvVar(String varName, int defaultValue)
    {
        try
        {
            return Integer.parseInt(System.getenv().get(varName));
        }
        catch(NumberFormatException e)
        {
            return defaultValue;
        }

    }

    private static Boolean getEnvVar(String varName)
    {
        List<String> trueValues = Arrays.asList(new String[] {"1", "y", "yes", "s", "sim"});
        String s = System.getenv().get(varName);
        return s != null && ( trueValues.contains(s.toLowerCase()) || Boolean.parseBoolean(s) );
    }

}