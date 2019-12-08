package br.bdfs.lib.path;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.exceptions.InvalidPathException;
import br.bdfs.lib.misc.ObjectChecker;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class DfsPath
{
    private int index;
    private String[] subPaths;
    private List<String> subPathList;
    
    public DfsPath(String path) throws InvalidPathException
    {
        if(ObjectChecker.strIsNullOrEmpty(path) || !PathHelper.isValidPath(path))
        {
            throw new InvalidPathException();
        }
        
        this.index = -1;
        this.subPathList = new ArrayList<>();
        
        if(path.startsWith(DfsConfig.PATH_SEPARATOR))
        {
            path = path.replaceFirst(DfsConfig.PATH_SEPARATOR, "");
        }
        
        StringBuilder subPathBuilder = new StringBuilder();
        this.subPaths = path.split(DfsConfig.PATH_SEPARATOR);
        
        for(int i=0; i < subPaths.length; i++)
        {
            String subPath = this.subPaths[i];
            
            if(!PathHelper.isValidName(subPath))
            {
                throw new InvalidPathException();
            }
            
            subPathBuilder.append(DfsConfig.PATH_SEPARATOR);
            subPathBuilder.append(subPath);
            
            this.subPathList.add(subPathBuilder.toString());
        }
    }
    
    public String get(int index)
    {
        if((index >= 0) && (index < subPaths.length))
        {
            return ("/" + subPaths[index]);
        }
        
        return null;
    }
    
    public boolean hasPrevious()
    {
        return ((index - 1) >= 0);
    }
    
    public String previous()
    {
        if(index >= 0)
        {
            index--;
        }
        
        return subPathList.get(index);
    }
    
    public boolean hasNext()
    {
        return ((index + 1) < subPathList.size());
    }
    
    public String next()
    {
        if(index < (subPathList.size() - 1))
        {
            index++;
        }
        
        return subPathList.get(index);
    }
    
    public boolean isLast()
    {
        return (index == (subPathList.size() - 1));
    }
    
    public String current()
    {
        if((index >= 0) && (index < subPaths.length))
        {
            return subPaths[index];
        }
        else
        {
            return subPathList.get(0);
        }
    }
    
    public String name()
    {
        if((index >= 0) && (index < subPaths.length))
        {
            return subPaths[index];
        }
        else
        {
            return subPaths[0];
        }
    }
    
    public int size()
    {
        return subPaths.length;
    }
    
    @Override
    public String toString()
    {
        return subPathList.get(subPathList.size() - 1);
    }
}
