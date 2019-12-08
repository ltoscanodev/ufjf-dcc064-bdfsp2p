package br.bdfs.lib.misc;

/**
 *
 * @author ltosc
 */
public class ObjectChecker 
{
    public static boolean isNull(Object obj)
    {
        return (obj == null);
    }
    
    public static boolean strIsNullOrEmpty(String str)
    {
        return (isNull(str) || str.isEmpty());
    }
}
