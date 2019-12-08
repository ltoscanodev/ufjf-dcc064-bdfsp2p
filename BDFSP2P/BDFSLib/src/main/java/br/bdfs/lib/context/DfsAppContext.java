package br.bdfs.lib.context;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author ltosc
 */
public class DfsAppContext 
{
    private static ClassPathXmlApplicationContext APP_CONTEXT;
    
    public static void initialize()
    {
        if (APP_CONTEXT == null)
        {
            APP_CONTEXT = new ClassPathXmlApplicationContext("META-INF/spring-jpa-config.xml");
        }
    }
    
    public static void close()
    {
        if (APP_CONTEXT != null)
        {
            APP_CONTEXT.close();
            System.exit(0);
        }
    }
    
    public static <T> T createInstance(Class<T> classType, Object... params)
    {
        if (APP_CONTEXT == null)
        {
            initialize();
        }
        
        return APP_CONTEXT.getBean(classType, params);
    }
}
