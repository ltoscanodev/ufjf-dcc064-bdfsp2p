package br.bdfs.lib.ecc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 *
 * @author ltosc
 */
public class ReedSolomonDecoder 
{
    private final int BYTES_IN_INT = 4;
    
    private final int dataShards;
    private final int parityShards;
    private final int totalShards;
    
    public ReedSolomonDecoder(int dataShards, int parityShards)
    {
        this.dataShards = dataShards;
        this.parityShards = parityShards;
        this.totalShards = (this.dataShards + this.parityShards);
    }
    
    public void decode(String path) throws IOException
    {
        byte[][] shards = new byte [totalShards][];
        boolean[] shardPresent = new boolean [totalShards];
        
        int shardSize = 0;
        int shardCount = 0;
        
        for (int i = 0; i < totalShards; i++) 
        {
            File shardFile = new File(String.format(path + ".%s", i));
            
            if (shardFile.exists()) 
            {
                shardSize = (int)shardFile.length();
                shards[i] = new byte[shardSize];
                shardPresent[i] = true;
                shardCount += 1;
                
                try(InputStream inStream = new FileInputStream(shardFile))
                {
                    inStream.read(shards[i], 0, shardSize);
                }
            }
        }
        
        if (shardCount < dataShards) 
        {
            throw new IOException("Quantidade de partes necessárias para decodificação não foi encontrada");
        }
        
        for (int i = 0; i < totalShards; i++) 
        {
            if (!shardPresent[i]) 
            {
                shards[i] = new byte[shardSize];
            }
        }
        
        ReedSolomon reedSolomon = new ReedSolomon(dataShards, parityShards);
        reedSolomon.decodeMissing(shards, shardPresent, 0, shardSize);
        
        byte [] allBytes = new byte[shardSize * dataShards];
        
        for (int i = 0; i < dataShards; i++) 
        {
            System.arraycopy(shards[i], 0, allBytes, shardSize * i, shardSize);
        }
        
        int fileSize = ByteBuffer.wrap(allBytes).getInt();
        File decodedFile = new File(path);
        
        try(OutputStream out = new FileOutputStream(decodedFile))
        {
            out.write(allBytes, BYTES_IN_INT, fileSize);
        }
    }
}
