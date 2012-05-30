import java.io.*;

public class ServerPinger extends Pinger
{
    private ServerConnect servCon;
    
    public ServerPinger(int port, String IP, int interval, ServerConnect servCon, ShareInterface inter)
    {
       super(port, IP, interval, inter);
       this.servCon = servCon;
    }
    
    
    protected void pingFailed()
    {
        try {
            servCon.serverDisconnect();
        }
        catch (IOException ioe) {
            inter.shareException(new ShareException(this.toString() + ": " + ioe.getMessage(), ioe));
        }
    }
    
    
}
