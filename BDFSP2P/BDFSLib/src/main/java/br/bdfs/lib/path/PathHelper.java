package br.bdfs.lib.path;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.exceptions.InvalidFileNameException;
import br.bdfs.lib.exceptions.InvalidPathException;
import java.nio.file.Paths;

/**
 *
 * @author ltosc
 */
public class PathHelper 
{
    public static boolean isValidName(String name)
    {
        return (name.length() <= DfsConfig.PATH_MAX_NAME_LENGTH);
    }
    
    public static boolean isValidPath(String path)
    {
        if(path.length() <= DfsConfig.PATH_MAX_LENGTH)
        {
            try 
            {
                Paths.get(path);
                return true;
            } 
            catch (java.nio.file.InvalidPathException | NullPointerException ex) 
            {
                return false;
            }
        }
        
        return false;
    }
    
    public static String getName(String path) throws InvalidPathException
    {
        if(!isValidPath(path))
        {
            throw new InvalidPathException();
        }
        
        return Paths.get(path).getFileName().toString();
    }
    
    public static String getFileExtension(String path) throws InvalidFileNameException 
    {
        if(!isValidName(path))
        {
            throw new InvalidFileNameException();
        }
        
        int lastIndex = path.lastIndexOf(".");
        
        if(lastIndex > 0)
        {
            return path.substring(lastIndex + 1);
        }
        else
        {
            throw new InvalidFileNameException();
        }
    }
    
    public static String getFileDirectoryPath(String filePath) throws InvalidPathException
    {
        if(!isValidPath(filePath))
        {
            throw new InvalidPathException();
        }
        
        return Paths.get(filePath).getParent().toString();
    }
    
    public static String concatPath(String path1, String path2)
    {
        if(!path2.startsWith(DfsConfig.PATH_SEPARATOR))
        {
            path1 = path1.concat(DfsConfig.PATH_SEPARATOR);
        }
        
        return path1.concat(path2);
    }
    
    public static String basePath(String path) throws InvalidPathException
    {
        DfsPath dfsPath = new DfsPath(path);
        return dfsPath.get(0);
    }
    
    public static String previousPath(String path) throws InvalidPathException
    {
        DfsPath dfsPath = new DfsPath(path);
        
        StringBuilder pathBuilder = new StringBuilder();
        int maxIndex = dfsPath.size() - 1;
        
        if(maxIndex > 0)
        {
            int currIndex = 0;

            while (currIndex < maxIndex) {
                pathBuilder.append(dfsPath.get(currIndex++));
            }

            return pathBuilder.toString();
        }
        else
        {
            return path;
        }
    }
}