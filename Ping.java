import java.io.*;
import java.net.*;

public abstract class Ping
{
     private static final int timeout = 200;
     
     public static boolean ping(String ip, int port) throws ShareException
     {
         return ping(ip, port, timeout);
     }
    
     public static boolean ping(String ip, int port, int timeout) throws ShareException
     {
         boolean bool = true;
         try {
             Socket socket = new Socket();
             socket.connect(new InetSocketAddress(ip, port), timeout);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Protocol protocol = new Protocol(reader, writer);
             protocol.sendProtocol();
             reader.close();
             writer.close();
         }
         catch (SocketTimeoutException ste) {
             bool = false;
             throw new ShareException("ping timeout ;", ste);
         }
         catch (IOException ioe) {
             bool = false;
             throw new ShareException("ping failed ;", ioe);
         }
         catch (ShareException se) {
             bool = false;
             throw new ShareException("ping failed ;", se);
         }
         finally {
             return bool;
         }
      
                     
     }

}
