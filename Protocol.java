import java.io.*;

public class Protocol
{
    public static final String PROTO = "FSA2";
    public static final String PROTOVER = "0.1";
    public static final String PROTOACCEPT = "ACCEPT";
    public static final String PROTODECLINE = "DECLINE";
    public static final String PROTOREADY = "READY";
    
    public static final String ADDUSER = "ADDUSER";
    public static final String REMOVEUSER = "REMOVEUSER";
    public static final String SERVCLOSE = "SERVCLOSE";
    
    public static final int FILE = 0;
    public static final int FILELIST = 1;
    public static final int USERFILE = 2;
    
    BufferedReader reader;
    private PrintWriter writer;
    
    public Protocol(BufferedReader reader, PrintWriter writer )
    {
        this.reader = reader;
        this.writer = writer;
    }
    public void sendProtocol() throws ProtocolRefusedException, ProtocolException, IOException
    {
        String read;
        writer.println(PROTO);
        read = reader.readLine();
        if(read.equals(PROTODECLINE)) {
            throw new ProtocolRefusedException("Connection failed: protocol sent: protocol not supported");
        }
        else if(!read.equals(PROTOACCEPT)) {
            throw new ProtocolException("Connection failed: protocol sent: unexpected response");
        }
        writer.println(PROTOVER);
        read = reader.readLine();
        if(read.equals(PROTODECLINE)) {
            throw new ProtocolRefusedException("Connection failed: protocol version sent: version unsupported");
        }
        else if(!read.equals(PROTOACCEPT)) {
            throw new ProtocolException("Connection failed: protocol version sent: unexpected response");
        }
    }
    
    public void checkProtocol() throws ProtocolRefusedException, IOException
    {
        if(! reader.readLine().equals(PROTO)) {
            writer.println(PROTODECLINE);
            throw new ProtocolRefusedException("Attempted connection refused: protocol not supported");
        }
        writer.println(PROTOACCEPT);
        if(! reader.readLine().equals(PROTOVER)) {
            writer.println(PROTODECLINE);
            throw new ProtocolRefusedException("Attempted connection refused: protocol version not supported");
        }
        writer.println(PROTOACCEPT);
    }

}
