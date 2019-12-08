package br.bdfs.client.command;

import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class DfsCommandParser 
{
    private final String command;
    private final HashMap<String, String> paramList;
    
    public DfsCommandParser(String cmd)
    {
        String[] cmdSplit = cmd.split(" ");
        
        this.command = cmdSplit[0].toUpperCase().trim();
        this.paramList = new HashMap<>();
        
        int paramIndex = 0;
        
        for (int i = 1; i < cmdSplit.length; i++)
        {
            if(cmdSplit[i].startsWith("-"))
            {
                cmdSplit[i] = cmdSplit[i].toUpperCase().trim();
                this.paramList.put(cmdSplit[i], "");
            }
            else
            {
                this.paramList.put("PARAM_" + String.valueOf(paramIndex++), cmdSplit[i]);
            }
        }
    }

    /**
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @return the paramList
     */
    public HashMap<String, String> getParamList() {
        return paramList;
    }
}
