package br.bdfs.lib.log;

import br.bdfs.lib.config.DfsConfig;

/**
 *
 * @author ltosc
 */
public class DfsLogger 
{
    private static void log(String type, String msg)
    {
        if(!DfsConfig.isEnableLog())
        {
            return;
        }
        
        System.out.println(String.format("[DFS %s] > %s", type.toUpperCase(), msg.replaceAll("\n", "")));
    }
    
    public static void log(String msg)
    {
        log("LOG", msg);
    }
    
    public static void logError(String msg)
    {
        log("ERROR", msg);
    }
    
    public static void logInfo(String msg)
    {
        log("INFO", msg);
    }
    
    public static void logDebug(String msg)
    {
        if(DfsConfig.isDebug())
        {
            log("DEBUG", msg);
        }
    }
}
