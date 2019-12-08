package br.bdfs.lib.exceptions;

/**
 *
 * @author ltosc
 */
public class InvalidPathException extends DfsException
{
    public InvalidPathException() 
    {
        super("O caminho de arquivo é inválido");
    }
}
