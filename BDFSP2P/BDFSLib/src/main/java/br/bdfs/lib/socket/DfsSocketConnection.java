package br.bdfs.lib.socket;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.protocol.DfsAddress;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author ltosc
 */
public class DfsSocketConnection 
{
    private final Socket socket;
    private final DfsAddress address;
    
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    
    private final BufferedInputStream bufferedInputStream;
    private final BufferedOutputStream bufferedOutputStream;
    
    public DfsSocketConnection(Socket socket) throws IOException
    {
        this.socket = socket;
        this.address = new DfsAddress(socket.getInetAddress(), socket.getPort());
        
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), DfsConfig.MSG_CHARSET));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), DfsConfig.MSG_CHARSET));
        
        this.bufferedInputStream = new BufferedInputStream(socket.getInputStream());
        this.bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
    }
    
    public DfsSocketConnection(Socket socket, DfsAddress address) throws IOException
    {
        this.socket = socket;
        this.address = address;
        
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), DfsConfig.MSG_CHARSET));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), DfsConfig.MSG_CHARSET));
        
        this.bufferedInputStream = new BufferedInputStream(socket.getInputStream());
        this.bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
    }
    
    public void setTimeout(int timeout) throws SocketException
    {
        socket.setSoTimeout(timeout);
    }
    
    public String readString() throws IOException
    {
        return bufferedReader.readLine();
    }
    
    public void writeString(String msg) throws IOException
    {
        bufferedWriter.write(msg);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }
    
    public void readBuffer(byte[] buffer, int offset, int length) throws IOException
    {
        bufferedInputStream.read(buffer, offset, length);
    }
    
    public void writeBuffer(byte[] buffer, int offset, int length) throws IOException
    {
        bufferedOutputStream.write(buffer, offset, length);
    }
    
    public void flushBuffer() throws IOException
    {
        bufferedOutputStream.flush();
    }
    
    public void close() throws IOException
    {
        bufferedReader.close();
        bufferedWriter.close();
        
        bufferedInputStream.close();
        bufferedOutputStream.close();
        
        socket.close();
    }

    /**
     * @return the address
     */
    public DfsAddress getAddress() {
        return address;
    }
}