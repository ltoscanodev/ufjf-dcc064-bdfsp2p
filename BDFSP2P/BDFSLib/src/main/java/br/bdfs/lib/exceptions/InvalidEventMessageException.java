package br.bdfs.lib.exceptions;

/**
 *
 * @author ltosc
 */
public class InvalidEventMessageException extends DfsException
{
    public InvalidEventMessageException() 
    {
        super("A mensagem de evento recebida é inválida");
    }
}
