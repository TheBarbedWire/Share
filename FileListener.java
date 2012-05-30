import java.io.*;
import java.net.*;
import java.util.*;

public class FileListener extends Listener
{
    private static final int MAXFTYPE = 2;
    private FileList fList;
    private UserList uList;
    private User me;
    private String sharePath;
    private File currentFile;
    private SharedFile sharedFile;
    private List<Sender> senderList;
    
    public FileListener(User me, int port, FileList fList, UserList uList, String sharePath, ShareInterface inter)
    {
        super(port, "FileListener", inter);
        this.fList = fList;
        this.uList = uList;
        this.sharePath = sharePath;
        this.me = me;
        senderList = new ArrayList<Sender>();
    }
    
    
    protected void connection(Socket socket, BufferedReader reader, PrintWriter writer)
    {
        InputStream inStream;
        try {
            inStream = getFile(reader, writer);
            if(inStream == null) {
                inter.shareException(new ShareException("FileListener: client requested file that does not exist"));
                return;
            }
            ShareHandle handle = new ShareHandle(sharedFile.toString(), uList.findUser(socket.getInetAddress()));
            Sender sender = new Sender(inStream, socket, sharedFile, inter, handle);
            senderList.add(sender);
            sender.start();
        }
        catch (ShareException se) {
            inter.shareException(new ShareException("FileListener: client at" + socket.getInetAddress().toString() + ": " + se.getMessage(), se));
            return;
        }
        catch (SocketTimeoutException ste) {
            inter.shareException(new ShareException("FileListener: client at" + socket.getInetAddress().toString() + ": connection lost", ste));
            return;
        }
        catch (IOException ioe) {
            inter.shareException(new ShareException(this.toString() + ": " + ioe.getMessage(), ioe));
            return;
        }
            
        
    }
    
    
    private InputStream getFile(BufferedReader reader, PrintWriter writer) throws ShareException, SocketTimeoutException, IOException
    {
        InputStream instr;
        int fileNum = 0;
        try {
            fileNum = Integer.parseInt(reader.readLine());
            if(fileNum > MAXFTYPE) {
                throw new ProtocolException("FileListener: client requested an invalid filetype");
            }
            switch(fileNum) {
                case 0:
                    writer.println(Protocol.PROTOACCEPT);
                    sharedFile = fList.getFile(reader.readLine(), sharePath);
                    if(sharedFile == null) {
                        writer.println(Protocol.PROTODECLINE);
                        return null;
                    } 
                    instr = getStream(sharedFile);
                    writer.println(Protocol.PROTOACCEPT);
                    return instr;
                case 1: 
                    instr = getStream(fList.getFileList());
                    sharedFile = fList.getFileList();
                    writer.println(Protocol.PROTOACCEPT);
                    return instr;
                case 2:
                    instr = getStream(uList.getUserFile());
                    sharedFile = uList.getUserFile();
                    writer.println(Protocol.PROTOACCEPT);
                    return instr;
                default:
                    instr = null;
                    writer.println(Protocol.PROTODECLINE);
                    throw new ProtocolException("FileListener: client requested an invalid filetype");
            }
        }       
        catch(ShareException se) {
            writer.println(Protocol.PROTODECLINE);
            throw se;
        }
        catch(NumberFormatException e) {
            writer.println(Protocol.PROTODECLINE);
            throw new ProtocolException("FileListener: client requested an invalid filetype");
        }
        catch (IOException ioe) {
            throw new ShareException("FileListener: client disconnected before request could be completed");
        }
    }
    
    private InputStream getStream(File file) throws ShareException
    {
       try {
            InputStream in = new FileInputStream(file);
            currentFile = file;
            return in;
        }
        catch (IOException ioe) {
            throw new ShareException("FileListener: Access to file denied");
        }

    }
    
    private void endSenders() throws IOException
    {
        Iterator<Sender> iter = senderList.iterator();
        while(iter.hasNext()) {
            iter.next().end();
        }
    }
    
    public void stopListener() throws IOException
    {
        endSenders();
        super.stopListener();
    }

}