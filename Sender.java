import java.io.*;
import java.net.*;


public class Sender extends Thread 
{
    private InputStream in;
    private Socket socket;
    private OutputStream out;
    private ShareInterface inter;
    private SharedFile file;
    private double percent = 0;
    private ShareHandle handle;
    private volatile Thread blinker;
    
    public Sender(InputStream in, Socket socket, SharedFile file, ShareInterface inter, ShareHandle handle) throws ShareException
    {
        super("sender");
        this.in = in;
        this.socket = socket;
        this.file = file;
        this.inter = inter;
        this.handle = handle;
        try{
            out = socket.getOutputStream();
        }
        catch(IOException ioe) {
            throw new ShareException("File sender connection lost", ioe);
        }
    }
    
    public void run()
    {
        blinker = Thread.currentThread();
        byte[] buf = new byte[1024];
        int len;
        int amountDLed = 0;
        inter.newUpload(handle);
        try {
            while (( len = in.read(buf)) > 0) {
                amountDLed += (double) len;
                out.write(buf, 0, len);
                tick(amountDLed);
            }
            in.close();
            out.close();
            socket.close();
        }
        catch (IOException ioe) {
             inter.shareException(new ShareException("File sender: " + ioe.getMessage(), ioe));
        }
        inter.tickProgress(handle, 100);
        inter.transferFinished(handle);
    }
    
    private void tick(double amountDLed)
    {
        if(file.getSize() == 0) {
            return;
        }
        double newPercent = (amountDLed / file.getSize()) * 100;
        if((newPercent - percent) >= 1) {
            inter.tickProgress(handle, (int) newPercent);
            percent = newPercent;
        }
    }
    
    public void end() throws IOException
    {
        socket.close();
        blinker = null;
    }

}
