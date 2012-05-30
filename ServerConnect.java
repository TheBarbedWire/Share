import java.io.*;
import java.net.*;

public class ServerConnect extends Thread
{
    private UserList userList;
    private int pingport, serverport, FTport;
    private String serverAddress;
    private User me;
    private User[] uList;
    private int timeout = 1000;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ShareInterface inter;
    private volatile Thread blinker;
    private ServerPinger servPing;
    
    public ServerConnect(User me, UserList userList, int pingport, String serverAddress, int serverport, int FTport, ShareInterface inter)
    {
        this.userList = userList;
        this.me = me;
        this.pingport = pingport;
        this.serverport = serverport;
        this.serverAddress = serverAddress;
        this.uList = userList.getUserList();
        this.FTport = FTport;
        this.inter = inter;
    }
    
    public void run()
    {
        blinker = Thread.currentThread();
        try {
            Ping.ping(serverAddress, pingport);
            connectToServer();
            getUsers();
            inter.serverConnected(new ShareHandle(serverAddress));
            servPing = new ServerPinger(pingport, serverAddress, 2000, this, inter);
            servPing.start();
            serverMonitor();
        }
        catch (ShareException se) {
            inter.shareException(se);
        }
        catch (IOException ioe) {
            inter.exception(new Exception("ServerConnect Error: " + ioe.getMessage(), ioe));
        }
        catch (InterruptedException ire) {
            inter.exception(new Exception("ServerConnect Error: " + ire.getMessage(), ire));
        }
        catch (Exception e) {
            inter.exception(new Exception("ServerConnect Error: " + e.getMessage(), e));
        }
        finally {
            try {
                serverDisconnect();
            }
            catch(Exception e) {
                inter.exception(e);
            }
        }
        
    }
    
    private void connectToServer() throws SocketException, ShareException, ProtocolRefusedException, ProtocolException, IOException
    {
        try {
            this.socket = new Socket();
            socket.connect(new InetSocketAddress(serverAddress, serverport), timeout);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            Protocol proto = new Protocol(reader, writer);
            proto.sendProtocol();
            writer.println(me.getAlias());
            String read = reader.readLine();
            if(read.equals(Protocol.PROTODECLINE)) {
                throw new ProtocolRefusedException("username sent: username refused ;");
            }
            else if(!read.equals(Protocol.PROTOACCEPT)) {
                throw new ProtocolException("username sent: unexpected reponse ;");
            }
            writer.println(Protocol.PROTOREADY);
            read = reader.readLine();
            me.setAlias(read);
            writer.println(Protocol.PROTOACCEPT);
        }
        catch (ShareException se) {
            throw(new ShareException("Server connection failed: protocol refused ;", se));
        }
        catch (SocketTimeoutException ste) {
            throw(new ShareException("Server connection failed: timeout ;", ste));
        }
    }
    
    private void serverMonitor() throws IOException
    {
        try {
            String read;
            boolean bool = true;
            while(bool == true) {
                if(reader.ready() == true) {
                    read = reader.readLine();
                    
                    if(read.equals(Protocol.ADDUSER)) {
                        addUser();
                    }
                    else if(read.equals(Protocol.REMOVEUSER)) {
                        removeUser();
                    }
                    else if(read.equals(Protocol.SERVCLOSE)) {
                        bool = false;
                        serverDisconnect();
                    }
                    else {
                        throw new ProtocolException("Server command: Unexpected server message");
                    }
                }
            }      
        }
        catch (ProtocolException pe) {
            inter.protocolException(pe);
            serverMonitor();
        }
        serverDisconnect();
    }
    
    public void serverDisconnect() throws IOException
    {
        
        socket.close();
        reader.close();
        writer.close();
        servPing.endPinger();
        inter.serverDisconnected(new ShareHandle(serverAddress));
        blinker = null;
        
    }
    
    private void removeUser() throws ProtocolException, IOException
    {
        String username;
        try {
            username = reader.readLine(); 
        }
        catch (SocketTimeoutException ste) {
            throw new ProtocolException("Server command: REMOVEUSER: server failed to send expected username");
        }
        
        inter.disconnectUser(userList.findUser(username));
        userList.removeUser(username);
    }
    
    private void addUser() throws ProtocolException, IOException
    {
        String IP, username;
        try {
            IP = reader.readLine();
        }
        catch (SocketTimeoutException ste) {
            throw new ProtocolException("Server command: ADDUSER: server failed to send expected user IP");
        }
        try {
            username = reader.readLine();
        }
        catch (SocketTimeoutException ste) {
            throw new ProtocolException("Server command: ADDUSER: server failed to send expected username");
        }
        User newUser = new User(IP, username);
        userList.addUser(newUser);
        inter.newUser(newUser);  
    }
    
    
    private void getUsers() throws IOException, InterruptedException, Exception
    {
        File userFile = File.createTempFile("tempuserlist", ".xml");
        Requester requester = new Requester(serverAddress, FTport, 2, userFile);
        requester.start();
        requester.join();
        userList.setUserFile(userFile);
        inter.newUserList(userList.getUserList());

    }
    
   
}
