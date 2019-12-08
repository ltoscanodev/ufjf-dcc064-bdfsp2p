package br.bdfs.lib.config;

/**
 *
 * @author ltosc
 */
public class DfsConfig 
{
    private static boolean debug;
    
    public static boolean isDebug() 
    {
        return debug;
    }
    
    public static void setDebug(boolean dbg)
    {
        debug = dbg;
    }
    
    private static boolean enableLog;
    
    /**
     * @return the enableLog
     */
    public static boolean isEnableLog() {
        return enableLog;
    }

    /**
     * @param enable the enable to set
     */
    public static void setEnableLog(boolean enable) {
        enableLog = enable;
    }
    
    public static final int SOCKET_TIMEOUT = 10000;
    public static final int SOCKET_MIN_PORT = 5000;
    public static final int SOCKET_MAX_PORT = 10000;
    
    public static final boolean USE_SSL_SOCKET = true;
    public static final String SSL_SOCKET_KEY_PASSWORD = "WkAJDaLUp84QUXdH5zW7Yp9d";
    
    public static final int THREADPOOL_MIN_THREAD = 4;
    public static final int THREADPOOL_MAX_THREAD = Integer.MAX_VALUE;
    public static final int THREADPOOL_KEEP_TO_ALIVE_THREAD = 60;
    
    public static final String MSG_CHARSET = "UTF-8";
    public static final String MSG_NAME_AND_PARAM_SEPARATOR = ">";
    public static final String MSG_PARAM_SEPARATOR = ";";
    
    public static final String PATH_SEPARATOR = "/";
    public static final int PATH_MAX_LENGTH = 1024;
    public static final int PATH_MAX_NAME_LENGTH = 64;
    
    public static final int RW_BUFFER_LENGTH = 8192;
    
    public static final int REED_SOLOMON_DATA_SHARDS = 10;
    public static final int REED_SOLOMON_PARITY_SHARDS = 20;
    
    public static final int NETWORK_INTEGRITY_CHECK_TASK_DELAY = 5 * 60000;
    public static final int NETWORK_INTEGRITY_CHECK_TASK_PERIOD = 5 * 60000;
}
