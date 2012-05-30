import java.io.*;
import java.net.*;

public class PingListener extends Listener
{
    
    public PingListener(int port, ShareInterface inter) 
    {
        super(port, "PingListener", inter);
    }

    protected void connection(Socket socket, BufferedReader reader, PrintWriter writer)
    {
        try {
            socket.close();
            reader.close();
            writer.close();
        }
        catch (IOException ioe) {
            inter.shareException(new ShareException(this.toString() + ": " + ioe.getMessage(), ioe));
            return;
        }
    }
}
