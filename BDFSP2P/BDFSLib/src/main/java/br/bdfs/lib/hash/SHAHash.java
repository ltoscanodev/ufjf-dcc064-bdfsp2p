package br.bdfs.lib.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class SHAHash 
{
    public enum SHAType { SHA, SHA224, SHA256, SHA384, SHA512 }
    
    private static String fromByteArrayToHexString(byte[] hashBytes)
    {
        StringBuilder strBuilder = new StringBuilder();
        
        for (int i = 0; i < hashBytes.length; i++) 
        {
            String hex = Integer.toHexString(0xff & hashBytes[i]);
            
            if (hex.length() == 1) 
            {
                strBuilder.append('0');
            }
            
            strBuilder.append(hex);
        }
        
        return strBuilder.toString();
    }
    
    private static String fromByteArrayToBinString(byte[] hashBytes)
    {
        StringBuilder strBuilder = new StringBuilder();
        
        for (int i = 0; i < hashBytes.length; i++) 
        {
            strBuilder.append(Integer.toString(hashBytes[i], 2).substring(1));
        }
        
        return strBuilder.toString();
    }
    
    public static byte[] getHashBytes(String input, SHAType shaType)
    {
        try 
        {
            String shaAlgorithm;
            
            switch(shaType)
            {
                case SHA:
                    shaAlgorithm = "SHA";
                    break;
                case SHA224:
                    shaAlgorithm = "SHA-224";
                    break;
                case SHA256:
                    shaAlgorithm = "SHA-256";
                    break;
                case SHA384:
                    shaAlgorithm = "SHA-384";
                    break;
                case SHA512:
                    shaAlgorithm = "SHA-512";
                    break;
                default:
                    shaAlgorithm = "SHA-256";
            }
            
            MessageDigest msgDigest = MessageDigest.getInstance(shaAlgorithm);
            byte[] hashBytes = msgDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            
            return hashBytes;
        } 
        catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(SHAHash.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public static String getHashHexString(String input, SHAType shaType)
    {
        return fromByteArrayToHexString(getHashBytes(input, shaType));
    }
    
    public static String getHashBinString(String input, SHAType shaType)
    {
        return fromByteArrayToBinString(getHashBytes(input, shaType));
    }
}
