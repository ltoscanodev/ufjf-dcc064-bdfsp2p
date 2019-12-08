package br.bdfs.lib.exceptions;

/**
 *
 * @author ltosc
 */
public class ConnectionRefusedException extends DfsException
{
    public ConnectionRefusedException(String msg) 
    {
        super(msg);
    }
}
