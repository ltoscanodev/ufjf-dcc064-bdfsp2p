package br.bdfs.lib.ecc;

import br.bdfs.lib.exceptions.DfsException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class ReedSolomonEncoder 
{
    private final int BYTES_IN_INT = 4;
    
    private final int dataShards;
    private final int parityShards;
    private final int totalShards;
    
    public ReedSolomonEncoder(int dataShards, int parityShards)
    {
        this.dataShards = dataShards;
        this.parityShards = parityShards;
        this.totalShards = (this.dataShards + this.parityShards);
    }
    
    public void encode(String path) throws IOException
    {
        File inputFile = new File(path);
        
        if (!inputFile.exists()) 
        {
            throw new FileNotFoundException(String.format("O arquivo %s não foi encontrado", path));
        }
        
        int fileSize = (int)inputFile.length();
        int storedSize = fileSize + BYTES_IN_INT;
        int shardSize = (storedSize + dataShards - 1) / dataShards;
        
        int bufferSize = shardSize * dataShards;
        byte[] allBytes = new byte[bufferSize];
        ByteBuffer.wrap(allBytes).putInt(fileSize);
        
        try(InputStream inStream = new FileInputStream(inputFile))
        {
            int bytesRead = inStream.read(allBytes, BYTES_IN_INT, fileSize);
            
            if (bytesRead != fileSize) 
            {
                throw new IOException("Não foi possível ler todos os bytes do arquivo");
            }
        }
        
        byte[][] shards = new byte [totalShards][shardSize];

        for (int i = 0; i < dataShards; i++) 
        {
            System.arraycopy(allBytes, i * shardSize, shards[i], 0, shardSize);
        }
        
        ReedSolomon reedSolomon = new ReedSolomon(dataShards, parityShards);
        reedSolomon.encodeParity(shards, 0, shardSize);
        
        for (int i = 0; i < totalShards; i++) 
        {
            try(OutputStream outStream = new FileOutputStream(new File(inputFile.getParentFile(), inputFile.getName() + "." + i)))
            {
                outStream.write(shards[i]);
            }
        }
    }
    
    public void encode(String path, List<String> guid) throws IOException, DfsException
    {
        File inputFile = new File(path);
        
        if (!inputFile.exists()) 
        {
            throw new FileNotFoundException(String.format("O arquivo %s não foi encontrado", path));
        }
        
        if(guid.size() != (totalShards))
        {
            throw new DfsException("A lista de GUID não tem o mesmo tamanho que pedaços.");
        }
        
        int fileSize = (int)inputFile.length();
        int storedSize = fileSize + BYTES_IN_INT;
        int shardSize = (storedSize + dataShards - 1) / dataShards;
        
        int bufferSize = shardSize * dataShards;
        byte[] allBytes = new byte[bufferSize];
        ByteBuffer.wrap(allBytes).putInt(fileSize);
        
        try(InputStream inStream = new FileInputStream(inputFile))
        {
            int bytesRead = inStream.read(allBytes, BYTES_IN_INT, fileSize);
            
            if (bytesRead != fileSize) 
            {
                throw new IOException("Não foi possível ler todos os bytes do arquivo");
            }
        }
        
        byte[][] shards = new byte [totalShards][shardSize];

        for (int i = 0; i < dataShards; i++) 
        {
            System.arraycopy(allBytes, i * shardSize, shards[i], 0, shardSize);
        }
        
        ReedSolomon reedSolomon = new ReedSolomon(dataShards, parityShards);
        reedSolomon.encodeParity(shards, 0, shardSize);
        
        for (int i = 0; i < totalShards; i++) 
        {
            try(OutputStream outStream = new FileOutputStream(new File(inputFile.getParentFile(), guid.get(i))))
            {
                outStream.write(shards[i]);
            }
        }        
    }
    
    public int shardSize(int fileSize)
    {
        int storedSize = fileSize + BYTES_IN_INT;
        return (storedSize + dataShards - 1) / dataShards;
    }
}
