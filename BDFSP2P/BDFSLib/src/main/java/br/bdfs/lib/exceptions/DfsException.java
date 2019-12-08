package br.bdfs.lib.exceptions;

/**
 *
 * @author ltosc
 */
public class DfsException extends Exception
{
    public DfsException(String msg)
    {
        super(msg, null);
    }
    
    public DfsException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
