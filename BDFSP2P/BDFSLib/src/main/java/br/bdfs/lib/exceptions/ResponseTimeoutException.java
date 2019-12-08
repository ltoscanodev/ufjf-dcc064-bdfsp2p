package br.bdfs.lib.exceptions;

/**
 *
 * @author ltosc
 */
public class ResponseTimeoutException extends DfsException
{
    public ResponseTimeoutException() 
    {
        super("A resposta da mensagem enviada demorou mais tempo do que o esperado");
    }
}