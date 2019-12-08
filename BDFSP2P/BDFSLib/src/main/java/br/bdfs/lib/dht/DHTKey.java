package br.bdfs.lib.dht;

import br.bdfs.lib.hash.SHAHash;
import br.bdfs.lib.hash.SHAHash.SHAType;

/**
 *
 * @author ltosc
 */
public class DHTKey
{
//    public static String generate(String name) 
//    {
//        String hashBinString = SHAHash.getHashBinString(name, SHAType.SHA);
//        
//        int characters = (int) (Math.log(500) / Math.log(2));
//        characters = Math.min(characters, hashBinString.length());
//        
//        return hashBinString.substring(hashBinString.length() - characters - 1, hashBinString.length());
//    }
    
    public static String generate(String name)
    {
        return SHAHash.getHashHexString(name, SHAType.SHA);
    }
    
//    public static int compare(String strFirstKey, String strSecondKey)
//    {
//        float firstKey = Float.parseFloat(strFirstKey);
//        float secondKey = Float.parseFloat(strSecondKey);
//        
//        return Float.compare(firstKey, secondKey);
//    }
    
    public static int compare(String firstKey, String secondKey)
    {
        int result = firstKey.compareTo(secondKey);
        
        if(result < 0) // firstKey > secondKey
        {
            return 1;
        }
        else if(result > 0) // firstKey < secondKey
        {
            return -1;
        }
        else // firstKey == secondKey
        {
            return 0;
        }
    }
    
//    public static boolean between(String strKey, String strFirstKey, String strSecondKey)
//    {
//        float key = Float.parseFloat(strKey);
//        float firstKey = Float.parseFloat(strFirstKey);
//        float secondKey = Float.parseFloat(strSecondKey);
//
//        if (firstKey > secondKey) 
//        {
//            return key > firstKey || key <= secondKey;
//        } 
//        else if (firstKey < secondKey) 
//        {
//            return key > firstKey && key <= secondKey;
//        }
//        else 
//        {
//            return true;
//        }
//    }
    
    public static boolean between(String key, String firstKey, String secondKey)
    {
        switch (compare(firstKey, secondKey)) 
        {
            case 1:
                return ((compare(key, firstKey) == 1) || (compare(key, secondKey) == -1));
            case -1:
                return ((compare(key, firstKey) == 1) && (compare(key, secondKey) == -1));
            default:
                return true;
        }
    }
}
