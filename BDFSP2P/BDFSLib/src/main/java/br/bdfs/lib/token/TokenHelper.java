package br.bdfs.lib.token;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author ltosc
 */
public class TokenHelper 
{
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String CAPS_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SPECIAL_CHARACTERS = "!@$%*-_+:";
    private static final String NUMBER = "0123456789";
    
    private static final int randomStringLength = 32;
    private static final boolean isAlphanum = true;
    private static final boolean isNumeric = true;
    private static final boolean isAlpha = true;
    private static final boolean allowSpecialCharacters = true;
    private static final boolean allowDuplicates = true;
    private static final boolean mixCase = true;
    
    private static StringBuffer buildList() 
    {
        StringBuffer list = new StringBuffer(0);
        
        if (isNumeric || isAlphanum) 
        {
            list.append(NUMBER);
        }
        
        if (isAlpha || isAlphanum)
        {
            list.append(ALPHABET);
            
            if (mixCase)
            {
                list.append(CAPS_ALPHABET);
            }
        }
        
        if (allowSpecialCharacters) 
        {
            list.append(SPECIAL_CHARACTERS);
        }
        
        int currLen = list.length();
        String returnVal = "";
        
        for (int inx = 0; inx < currLen; inx++) 
        {
            int selChar = (int) (Math.random() * (list.length() - 1));
            returnVal += list.charAt(selChar);
            list.deleteCharAt(selChar);
        }
        
        list = new StringBuffer(returnVal);
        return list;
    }
    
    private static String getRandomString() 
    {
        String randomString = "";
        int specialCharactersCount = 0;
        int maxspecialCharacters = (randomStringLength / 4);

        StringBuffer values = buildList();

        for (int inx = 0; inx < randomStringLength; inx++) 
        {
            int selChar = (int) (Math.random() * (values.length() - 1));

            if (allowSpecialCharacters) 
            {
                if (SPECIAL_CHARACTERS.contains(String.valueOf(values.charAt(selChar)))) 
                {
                    specialCharactersCount++;

                    if (specialCharactersCount > maxspecialCharacters) 
                    {
                        while (SPECIAL_CHARACTERS.contains(String.valueOf(values.charAt(selChar)))) 
                        {
                            selChar = (int) (Math.random() * (values.length() - 1));
                        }
                    }
                }
            }

            randomString += values.charAt(selChar);

            if (!allowDuplicates)
            {
                values.deleteCharAt(selChar);
            }
        }

        return randomString;
    }
    
    public static String generate()
    {
        try 
        {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(getRandomString().getBytes());
            return new BigInteger(1, sha.digest()).toString(16);
        }
        catch (NoSuchAlgorithmException ex) 
        {
            return null;
        }
    }
}
