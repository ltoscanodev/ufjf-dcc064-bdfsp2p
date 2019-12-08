package br.bdfs.lib.protocol.event;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.exceptions.ResponseTimeoutException;
import br.bdfs.lib.socket.DfsSocketFactory;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.socket.DfsSocketConnection;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 *
 * @author ltosc
 */
public abstract class DfsSendEvent
{
    public enum MessageWithFile { SendFile, ReceiveFile }
    
    public abstract DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException;
    
    private DfsEventMessage sendMessageWithFile(
            DfsSocketConnection socketConnection, DfsEventMessage sendEventMessage, 
            File msgFile, MessageWithFile msgType, boolean waitForResponse) throws DfsException, IOException
    {
        long startTime = System.nanoTime();
        
        sendEventMessage.getEventParamList().put("WAIT_FOR_RESPONSE", String.valueOf(waitForResponse));
        String strSendMsg = sendEventMessage.toString();
        
        socketConnection.writeString(strSendMsg);
        DfsLogger.logDebug(String.format("Mensagem enviada para %s: %s", socketConnection.getAddress().toString(), strSendMsg));
        
        DfsEventMessage responseEventMessage = null;
        
        if(msgType.equals(MessageWithFile.SendFile))
        {
            byte[] buffer = new byte[DfsConfig.RW_BUFFER_LENGTH];
            long dataLength = msgFile.length();
            long totalRead = 0;
            long read;

            DfsLogger.logDebug(String.format("Enviando dados para %s...", socketConnection.getAddress().toString()));

            try (FileInputStream fileInputStream = new FileInputStream(msgFile)) {
                do {
                    read = Math.min((dataLength - totalRead), DfsConfig.RW_BUFFER_LENGTH);
                    fileInputStream.read(buffer, 0, Math.toIntExact(read));
                    totalRead += read;

                    socketConnection.writeBuffer(buffer, 0, Math.toIntExact(read));
                } while (totalRead < dataLength);

                socketConnection.flushBuffer();
            }

            DfsLogger.logDebug(String.format("Dados enviados para %s", socketConnection.getAddress().toString()));
        }
        else
        {
            String response = socketConnection.readString();
            responseEventMessage = DfsEventMessage.fromString(response);

            if (responseEventMessage.getEventParamList().containsKey("STATUS"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
            else if (!responseEventMessage.getEventParamList().containsKey("FILE_LENGTH"))
            {
                throw new InvalidEventMessageException();
            }

            String strDataLength = responseEventMessage.getEventParamList().get("FILE_LENGTH");
            
            byte[] buffer = new byte[DfsConfig.RW_BUFFER_LENGTH];
            long dataLength = Long.valueOf(strDataLength);
            long totalRead = 0;
            long read;
            
            DfsLogger.logDebug(String.format("Recebendo dados de %s...", socketConnection.getAddress().toString()));

            try (FileOutputStream fileOutputStream = new FileOutputStream(msgFile))
            {
                do 
                {
                    read = Math.min((dataLength - totalRead), DfsConfig.RW_BUFFER_LENGTH);
                    socketConnection.readBuffer(buffer, 0, Math.toIntExact(read));
                    totalRead += read;

                    fileOutputStream.write(buffer, 0, Math.toIntExact(read));
                } 
                while (totalRead < dataLength);

                fileOutputStream.flush();
            }

            DfsLogger.logDebug(String.format("Dados recebidos de %s", socketConnection.getAddress().toString()));
        }
        
        if (waitForResponse) 
        {
            try 
            {
                DfsLogger.logDebug(String.format("Aguardando resposta da mensagem enviada para %s...", socketConnection.getAddress().toString()));
                
                String strResponseMsg = socketConnection.readString();
                DfsLogger.logDebug(String.format("Resposta recebida da mensagem enviada para %s: %s", socketConnection.getAddress().toString(), strResponseMsg));
                
                responseEventMessage = DfsEventMessage.fromString(strResponseMsg);
            } 
            catch (SocketTimeoutException ex) 
            {
                throw new ResponseTimeoutException();
            }
        }
        
        DfsLogger.logDebug(String.format("Tempo para processamento da mensagem enviada: %s ms", ((System.nanoTime() - startTime) / 1000000)));
        
        return responseEventMessage;
    }
    
    private DfsEventMessage sendMessageWithBuffer(
            DfsSocketConnection socketConnection, DfsEventMessage sendEventMessage, 
            byte[] buffer, MessageWithFile msgType, boolean waitForResponse) throws DfsException, IOException
    {
        long startTime = System.nanoTime();
        
        sendEventMessage.getEventParamList().put("WAIT_FOR_RESPONSE", String.valueOf(waitForResponse));
        String strSendMsg = sendEventMessage.toString();
        
        socketConnection.writeString(strSendMsg);
        DfsLogger.logDebug(String.format("Mensagem enviada para %s: %s", socketConnection.getAddress().toString(), strSendMsg));
        
        DfsEventMessage responseEventMessage = null;
        
        if(msgType.equals(MessageWithFile.SendFile))
        {
            long dataLength = buffer.length;
            long totalRead = 0;
            long read;

            DfsLogger.logDebug(String.format("Enviando dados para %s...", socketConnection.getAddress().toString()));

            do 
            {
                read = Math.min((dataLength - totalRead), DfsConfig.RW_BUFFER_LENGTH);
                socketConnection.writeBuffer(buffer, Math.toIntExact(totalRead), Math.toIntExact(read));
                totalRead += read;
            } while (totalRead < dataLength);

            socketConnection.flushBuffer();

            DfsLogger.logDebug(String.format("Dados enviados para %s", socketConnection.getAddress().toString()));
        }
        else
        {
            String response = socketConnection.readString();
            responseEventMessage = DfsEventMessage.fromString(response);

            if (responseEventMessage.getEventParamList().containsKey("STATUS"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
            else if (!responseEventMessage.getEventParamList().containsKey("FILE_LENGTH"))
            {
                throw new InvalidEventMessageException();
            }

            String strDataLength = responseEventMessage.getEventParamList().get("FILE_LENGTH");
            
            long dataLength = Long.valueOf(strDataLength);
            long totalRead = 0;
            long read;
            
            DfsLogger.logDebug(String.format("Recebendo dados de %s...", socketConnection.getAddress().toString()));

            do 
            {
                read = Math.min((dataLength - totalRead), DfsConfig.RW_BUFFER_LENGTH);
                socketConnection.readBuffer(buffer, Math.toIntExact(totalRead), Math.toIntExact(read));
                totalRead += read;
            } 
            while (totalRead < dataLength);
            
            DfsLogger.logDebug(String.format("Dados recebidos de %s", socketConnection.getAddress().toString()));
        }
        
        if (waitForResponse) 
        {
            try 
            {
                DfsLogger.logDebug(String.format("Aguardando resposta da mensagem enviada para %s...", socketConnection.getAddress().toString()));
                
                String strResponseMsg = socketConnection.readString();
                DfsLogger.logDebug(String.format("Resposta recebida da mensagem enviada para %s: %s", socketConnection.getAddress().toString(), strResponseMsg));
                
                responseEventMessage = DfsEventMessage.fromString(strResponseMsg);
            } 
            catch (SocketTimeoutException ex) 
            {
                throw new ResponseTimeoutException();
            }
        }
        
        DfsLogger.logDebug(String.format("Tempo para processamento da mensagem enviada: %s ms", ((System.nanoTime() - startTime) / 1000000)));
        
        return responseEventMessage;
    }
    
    private DfsEventMessage sendMessageWithoutFile(DfsSocketConnection socketConnection, DfsEventMessage sendEventMessage, boolean waitForResponse) 
            throws DfsException, IOException
    {
        long startTime = System.nanoTime();
        
        sendEventMessage.getEventParamList().put("WAIT_FOR_RESPONSE", String.valueOf(waitForResponse));
        String strSendMsg = sendEventMessage.toString();
        
        socketConnection.writeString(strSendMsg);
        DfsLogger.logDebug(String.format("Mensagem enviada para %s: %s", socketConnection.getAddress().toString(), strSendMsg));
        
        DfsEventMessage responseEventMessage = null;
        
        if (waitForResponse) 
        {
            try 
            {
                DfsLogger.logDebug(String.format("Aguardando resposta da mensagem enviada para %s...", socketConnection.getAddress().toString()));
                
                String strResponseMsg = socketConnection.readString();
                DfsLogger.logDebug(String.format("Resposta recebida da mensagem enviada para %s: %s", socketConnection.getAddress().toString(), strResponseMsg));
                
                responseEventMessage = DfsEventMessage.fromString(strResponseMsg);
            } 
            catch (SocketTimeoutException ex) 
            {
                throw new ResponseTimeoutException();
            }
        }
        
        DfsLogger.logDebug(String.format("Tempo para processamento da mensagem enviada: %s ms", ((System.nanoTime() - startTime) / 1000000)));
        
        return responseEventMessage;
    }
    
    protected DfsEventMessage sendFile(DfsAddress remoteAddress, DfsEventMessage sendEventMessage, 
            File msgFile, MessageWithFile msgType, boolean waitForResponse) throws DfsException, IOException
    {
        DfsSocketConnection socketConnection = null;
        DfsEventMessage responseEventMessage = null;
        
        try
        {
            socketConnection = new DfsSocketConnection(DfsSocketFactory.newSocket(remoteAddress, DfsConfig.USE_SSL_SOCKET), remoteAddress);
            socketConnection.setTimeout(DfsConfig.SOCKET_TIMEOUT);
            responseEventMessage = sendMessageWithFile(socketConnection, sendEventMessage, msgFile, msgType, waitForResponse);
        }
        finally
        {
            socketConnection.close();
            DfsLogger.logDebug(String.format("Conexão com %s encerrada", remoteAddress.toString()));
        }
        
        return responseEventMessage;
    }
    
    protected DfsEventMessage sendBuffer(DfsAddress remoteAddress, DfsEventMessage sendEventMessage, 
            byte[] buffer, MessageWithFile msgType, boolean waitForResponse) throws DfsException, IOException
    {
        DfsSocketConnection socketConnection = null;
        DfsEventMessage responseEventMessage = null;
        
        try
        {
            socketConnection = new DfsSocketConnection(DfsSocketFactory.newSocket(remoteAddress, DfsConfig.USE_SSL_SOCKET), remoteAddress);
            socketConnection.setTimeout(DfsConfig.SOCKET_TIMEOUT);
            responseEventMessage = sendMessageWithBuffer(socketConnection, sendEventMessage, buffer, msgType, waitForResponse);
        }
        finally
        {
            socketConnection.close();
            DfsLogger.logDebug(String.format("Conexão com %s encerrada", remoteAddress.toString()));
        }
        
        return responseEventMessage;
    }
    
    protected DfsEventMessage sendMessage(DfsAddress remoteAddress, DfsEventMessage sendEventMessage, boolean waitForResponse)
            throws DfsException, IOException
    {
        DfsSocketConnection socketConnection = null;
        DfsEventMessage responseEventMessage = null;
        
        try
        {
            socketConnection = new DfsSocketConnection(DfsSocketFactory.newSocket(remoteAddress, DfsConfig.USE_SSL_SOCKET), remoteAddress);
            socketConnection.setTimeout(DfsConfig.SOCKET_TIMEOUT);
            responseEventMessage = sendMessageWithoutFile(socketConnection, sendEventMessage, waitForResponse);
        }
        finally
        {
            if(!ObjectChecker.isNull(socketConnection))
            {
                socketConnection.close();
                DfsLogger.logDebug(String.format("Conexão com %s encerrada", remoteAddress.toString()));
            }
        }
        
        return responseEventMessage;
    }
}
