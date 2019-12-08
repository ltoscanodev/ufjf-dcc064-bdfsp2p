package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.path.PathHelper;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.model.DfsDirectory;
import br.bdfs.peer.model.DfsFile;
import br.bdfs.peer.model.controller.helper.DfsDirectoryHelper;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class LsReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "LS";

    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        DfsLogger.logDebug("LsEvent.receiveEvent()");
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String path = receivedEventMessage.getEventParamList().get("PATH");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(path))
        {
            throw new InvalidEventMessageException();
        }
        
        DfsDirectory rootDir = DfsDirectoryHelper.findUserDirectory(token, path);
        List<DfsDirectory> childDirList = DfsDirectoryHelper.getUserDirectories(token, path);
        
        if(rootDir.getDfsFileList().isEmpty() && childDirList.isEmpty())
        {
            receivedEventMessage.getEventParamList().put("STATUS", "Nenhum diretório ou arquivo");
        }
        else
        {
            StringBuilder childBuilder = new StringBuilder();

            for (DfsDirectory childDir : childDirList)
            {
                childBuilder.append(childDir.getPath());
                childBuilder.append(",");
            }
            
            for(DfsFile childFile : rootDir.getDfsFileList())
            {
                childBuilder.append(PathHelper.concatPath(rootDir.getPath(), childFile.getName()));
                childBuilder.append(",");
            }

            childBuilder.deleteCharAt(childBuilder.length() - 1);
            receivedEventMessage.getEventParamList().put("DIR", childBuilder.toString());
        }
        
        return receivedEventMessage;
    }
}
