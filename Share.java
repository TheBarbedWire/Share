import java.io.*;
import java.net.*;
import java.util.*;

public class Share
{
    private ShareSetup setup;
    private UserList userList;
    private FileList fileList;
    private ServerConnect SC;
    private PingListener PL;
    private FileListener FL;
    private ShareInterface inter;
    private Config config;
    private User user;
    private List<Requester> reqList;
    
    public Share(ShareSetup setup, ShareInterface inter) throws Exception
    {
       this.setup = setup;
       this.inter = inter;
       createConfig();
       this.user = new User(config.getUserName());
       reqList = new ArrayList<Requester>();
       createUserList();
       createFileList();
       createPingListener();
       createFileListener();
    }
    
    public void createConfig() throws Exception
    {
        config = new Config(setup.configPath);
    }
    
    public ShareSetup getSetup()
    {
        return setup;
    }
    
    public User[] getUserList()
    {
        return userList.getUserList();
    }
    
    public void createUserList() throws Exception
    {
        userList = new UserList();
    }
    
    public void createFileList() throws Exception
    {
        File fList = new File(config.getFileListPath());
        if(fList.exists()) {
            fileList = new FileList(fList, user);
            fileList.readInFile();
        }
        else {
            fileList = new FileList(config.getFileListPath());
        }
        user.setFileList(fileList);
    }
    
    public void addShares() 
    {
        try {   
            fileList.addPathContents(config.getSharePath(), config.getSharePath());
            fileList.writeFile();
        }
        catch(Exception e) {
            System.err.println(e);
        }
    }
    
    private void createPingListener()
    {
        PL = new PingListener(setup.PLport, inter);
    }
    
    private void createFileListener()
    {
        FL = new FileListener(user, setup.FTport, fileList, userList, config.getSharePath(), inter);
    }
    
    public void connectToServer(String address)
    {
         SC = new ServerConnect(user, userList, setup.SPport, address, setup.CLport, setup.SFTport, inter);
         PL.start();
         FL.start();
         SC.start();
    }
    
    private void endListeners() throws IOException
    {
        PL.stopListener();
        FL.stopListener();
    }
    
    public void getUserFileList(User user) throws Exception
    {
        Requester requester = new Requester(user.getIP(), setup.FTport, 1 , new File(config.getFileListsDirPath() + user.getAlias() + ".xml"), user);
        reqList.add(requester);
        requester.start();
        requester.join();
    }
    
    public User findUser(String alias)
    {
        return userList.findUser(alias);
    }
    
    public User findUser(InetAddress ip)
    {
        return userList.findUser(ip);
    }
    
    public String getUserName()
    {
        return config.getUserName();
    }
    
    public void setUserName(String userName) throws Exception
    {
        config.setUserName(userName);
        user.setAlias(userName);
    }
    
    public void setDownloadPath(String path) throws Exception
    {
        config.setDownloadPath(path);
    }
    
    public void setSharePath(String path) throws Exception
    {
        config.setSharePath(path);
    }
    
    public void setAutoConnect(Boolean bool) throws Exception
    {
        config.setAutoConnect(bool);
    }
    
    public String getDefaultServer()
    {
        return config.getServer();
    }
    
    public String getSharePath()
    {
        return config.getSharePath();
    }
    
    public String getDownloadPath()
    {
        return config.getDownloadPath();
    }
    
    public Boolean getAutoConnect()
    {
        return config.getAutoConnect();
    }
    
    public String getLogPath()
    {
        return config.getLogPath();
    }
    
    public void setDefaultServer(String address) throws Exception
    {
        config.setServer(address);
    }
 
    public void getFile(SharedFile file)throws Exception
    {
        if(file.isDir()) {
            File newDir = new File(config.getSharePath() + file.getPath());
            newDir.mkdir();
            return;
        }
        ShareHandle handle = new ShareHandle(file.toString());
        Requester requester = new Requester(file.getUser().getIP(), setup.FTport, new SharedFile(config.getDownloadPath() + file.getPath()), file, inter, handle);
        inter.newDownload(handle);
        reqList.add(requester);
        requester.start();
        
    }
    
    private void endRequesters() throws IOException
    {
        Iterator<Requester> iter = reqList.iterator();
        while(iter.hasNext()) {
            iter.next().end();
        }
    }
    
    public void shutdown() throws Exception
    {
        SC.serverDisconnect();
        endListeners();
        endRequesters();
    }
}
