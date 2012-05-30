import java.io.*;
import java.net.*;

public abstract class Listener extends Thread
{
    private ServerSocket listenSocket;
    private int port;
    private volatile Thread blinker;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    protected ShareInterface inter;
    
    public Listener(int port, ShareInterface inter)
    {
        super("listener");
        this.port = port;
        this.inter = inter;
    }
    
    public Listener(int port, String threadName, ShareInterface inter)
    {
        super(threadName);
        this.port = port;
        this.inter = inter;
    }
    
    public void run()
    {
        blinker = Thread.currentThread();
        inter.shareEvent(new ShareHandle(this.toString() + " starting on port: " + Integer.toString(port)));
        listen();
        
    }
    
    public void stopListener() throws IOException
    {
        socket.close();
        reader.close();
        writer.close();
        listenSocket.close();
        blinker = null;
    }
    
    private void listen()
    {
        try {
            listenSocket = new ServerSocket(port);
            while(true) {
                socket = listenSocket.accept();
                socket.setSoTimeout(1000);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
                Protocol protocol = new Protocol(reader, writer);
                protocol.checkProtocol();
                connection(socket, reader, writer);
            }
        }
        catch(SocketException ske) {
            inter.shareException(new ShareException(this.toString() + ": client disconnected"));
            listen();
        }
        catch(ShareException se) {
            inter.shareException(new ShareException(this.toString() + ": " + se.getMessage(), se));
            listen();
        }
        catch(IOException ioe) {
            inter.shareException(new ShareException(this.toString() + ": error: " + ioe.getMessage()));
            listen();
        }
    }
    
    protected abstract void connection(Socket socket, BufferedReader reader, PrintWriter writer);
    
    
}
