import java.io.*;

public abstract class Pinger extends Thread
{
    private int port;
    private String IP;
    private int interval;
    private int timeout;
    private volatile Thread blinker;
    protected ShareInterface inter;
    
    public Pinger(int port, String IP, int interval, ShareInterface inter)
    {
       this.port = port;
       this.IP = IP;
       this.interval = interval;
       this.timeout = 1000;
       this.inter = inter;
    }
    
    public Pinger(int port, String IP, ShareInterface inter)
    {
        this(port, IP, 1000, inter);
    }
    
    public void run()
    {
        blinker = Thread.currentThread();
        try {
            while(Ping.ping(IP, port, timeout)) {
                Thread.sleep(interval);
            }
        }
        
        catch (ShareException se) {
            pingFailed();
        }
        catch (InterruptedException ire) {
            inter.exception(new Exception(this.toString() + ": " + ire.getMessage(), ire));
            run();
        }
        pingFailed();
     
        
    }
    
    public void endPinger()
    {
        blinker = null;
    }
    
    protected abstract void pingFailed();

}
