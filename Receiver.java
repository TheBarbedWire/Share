import java.io.*;
import java.net.*;

public class Receiver extends Thread
{
    private InputStream in;
    private Socket socket;
    private OutputStream out;
    
    public Receiver(OutputStream out, Socket socket) throws IOException
    {
        super("receiver");
        this.out = out;
        this.socket = socket;
        in = socket.getInputStream();
    }
    public void run()
    {
        byte[] buf = new byte[1024];
        int len;
        try {
            while (( len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            socket.close();
        }
        catch (SocketTimeoutException ste) {
            System.out.println("timeout receiving file");
        }
        catch (IOException e) {
            System.err.println("Receiver: " + e);
        }
    }

}
