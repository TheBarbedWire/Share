import java.io.*;
import java.net.*;
import java.security.MessageDigest;

public class Requester extends Thread
{
    private static String TEMP_EXT = ".tmp";
    
    private int port;
    private String ip;
    private Socket socket;
    private String filePath;
    private int timeout = 1000;
    
    private BufferedReader reader;
    private PrintWriter writer;
    private File outFile;
    private File tempFile;
    private int fileType;
    private User user;
    private String md5;
    private int fileSize = 0;
    private ShareInterface inter;
    private SharedFile file;
    private double percent = 0;
    private ShareHandle handle;
    private volatile Thread blinker;
    
  
    public Requester(String ip, int port, File outFile, File tempFile, SharedFile file, ShareInterface inter, ShareHandle handle)
    {
        this.port = port;
        this.ip = ip;
        this.filePath = file.getPath();
        this.outFile = outFile;
        this.tempFile = tempFile;
        this.fileType = 0;
        this.file = file;
        this.inter = inter;
        this.handle = handle;
       
    }
    
    public Requester(String ip, int port, int fileType, File outFile)
    {
        this.port = port;
        this.ip = ip;
        this.fileType = fileType;
        this.outFile = outFile;
       
    }
    
    public Requester(String ip, int port, int fileType, File outFile, User user)
    {
        this.port = port;
        this.ip = ip;
        this.fileType = fileType;
        this.outFile = outFile;
        this.user = user;
       
    }
    
    
    
    public void run()
    {
        blinker = Thread.currentThread();
        this.socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(ip, port), timeout);
            connect();
        }
        catch (SocketTimeoutException ste) {
            inter.shareException(new ShareException("File request: " + ip + " " + Integer.toString(port) + ": timeout", ste));
        }
        catch (SocketException scke) {
            inter.shareException(new ShareException("File request " + ip + " " + Integer.toString(port) + " : connection terminated prematurely", scke));
            return;
        }
        catch (IOException ioe) {
             inter.shareException(new ShareException("File request: " + ip + " " + Integer.toString(port) + ": failed to receive/save file: " + ioe.getMessage(), ioe));
        }
        catch (ShareException ste) {
             inter.shareException(new ShareException("File request: " + ip + " " + Integer.toString(port) + ": " + ste.getMessage()));
        }
        catch (InterruptedException ire) {
             inter.exception(new Exception("File request error: " + ip + " " + Integer.toString(port) + ": " + ire.getMessage(), ire));
        }
        catch (Exception e) {
             inter.exception(new Exception("File request error: " + ip + " " + Integer.toString(port) + ": " + e.getMessage(), e));
        }    
            
    }
    
    private void connect() throws ProtocolException, ProtocolRefusedException, SocketTimeoutException, InterruptedException, IOException, Exception
    {
        String read;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        Protocol protocol = new Protocol(reader, writer);
        protocol.sendProtocol();
        writer.println(Integer.toString(fileType));
        read = reader.readLine();
        if(read.equals(Protocol.PROTODECLINE)) {
            throw new ProtocolRefusedException("filetype refused");
        }
        else if(!read.equals(Protocol.PROTOACCEPT)) {
            throw new ProtocolException("unexpected response");
        }
        OutputStream stream;
        if(fileType == Protocol.FILE) {
            if(!sendPath()) return;
            tempFile = new File(outFile.getName() + TEMP_EXT);
            tempFile.createNewFile();  
            stream = new FileOutputStream(tempFile);
        }
        else {
           stream = new FileOutputStream(outFile); 
        }
     
        receiveFile(stream);   
        socket.close();
        if(fileType == Protocol.FILE) {
            if(checkMD5()) {
                if(!tempFile.renameTo(outFile)) {
                    throw new ShareException("Unable to copy file to downloads directory");
                }
            }
            else {
                throw new ShareException("File checksum error");
            }
        }
        else if(fileType == Protocol.FILELIST) {
            FileList filelist = new FileList(outFile, user);
            user.setFileList(filelist);
        }
        
        
    }
    
    private boolean sendPath()throws ProtocolException, ProtocolRefusedException, SocketTimeoutException, IOException
    {
        String read;
        writer.println(filePath);
        read = reader.readLine();
        if(read.equals(Protocol.PROTODECLINE)) {
            throw new ProtocolRefusedException("filepath refused");       
        }
        else if(!read.equals(Protocol.PROTOACCEPT)) {
            throw new ProtocolException ("filepath sent: unexpected response");
        }
        return true;
        
    }
    
    private void receiveFile(OutputStream out) throws SocketTimeoutException, IOException, Exception
    {
        InputStream in = socket.getInputStream();
        byte[] buf = new byte[1024];
        int amountDLed = 0;
        MessageDigest md = MessageDigest.getInstance("MD5");
        int len;
        while (( len = in.read(buf)) > 0) {
            amountDLed += (double) len;
            out.write(buf, 0, len);
            if(fileType == 0) {
                md.update(buf, 0, len);
                tick(amountDLed);
            }
        }
        in.close();
        out.close();
        if(fileType == 0) {
            generateMD5(md.digest());
            inter.tickProgress(handle, 100);
            inter.transferFinished(handle);
        }
        
        
    }   
    
    private void generateMD5(byte[] checkSum) throws Exception
    {
        md5 = "";
        for(int i = 0; i < checkSum.length; i++) {
            md5 += Integer.toString((checkSum[i] & 0xff) + 0x100, 16).substring(1);
        }
    }
    
    private boolean checkMD5()
    {
        if(file.getMD5().equals(md5)) {
            return true;
        }
        return false;
    }
    
    private void tick(double amountDLed)
    {
        double newPercent = (amountDLed / file.getSize()) * 100;
        if((newPercent - percent) >= 1) {
            inter.tickProgress(handle, (int) newPercent);
            percent = newPercent;
        }
    }
    
    public void end() throws IOException
    {
        socket.close();
        reader.close();
        writer.close();
        blinker = null;
    }
    
}
