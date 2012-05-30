import java.io.*;
import java.net.*;

public class CLI implements ShareInterface
{
    private Share share;
    private User[] users;
    private final String commands = "commands: addShares, listusers, getfilelist, showfilelist, getfile, connect";
    
    public void command()
    {
        String read;
        String[] args;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                read = reader.readLine();
                args = read.split(" ");
                if(args[0].equals("help")) {
                    System.out.println(commands);
                }
                else if(args[0].equals("addShares")) {
                    addShares();
                }
                else if(args[0].equals("listusers")) {
                    listusers();
                }
                else if(args[0].equals("getfilelist")) {
                    getfilelist(args);
                }
                else if(args[0].equals("showfilelist")) {
                    showfilelist(args);
                }
                else if(args[0].equals("getFile")) {
                    getfile(args);
                }
                else if(args[0].equals("connect")) {
                    connect(args);
                }
                else {
                    System.out.println(commands);
                }
            }
            catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    private void addShares() throws Exception
    {
        share.addShares();
    }
    
    
    private void connect(String args[])
    {
        share.connectToServer(args[1]);
    }
    
    private void getfile(String[] args) throws Exception
    {
        share.getFile(new SharedFile(args[1]));
    }
    
    private void getfilelist(String[] args) throws Exception
    {
        User user = share.findUser(args[1]);
        share.getUserFileList(user);
        FileList fList = user.getFileList();
    }
    
    private void listusers()
    {
        int i = 0;
        while(i < users.length) {
            System.out.println(users[i].getAlias() + " " + users[i].getIP());
            i++;
        }
    }
    
    private void showfilelist(String args[])
    {
        /*
        String[] files;
        User user;
        System.err.println("1");
        if(args.length < 2) {
            files = fileList.getFiles();
            System.err.println("2");
        }
        else {
            System.err.println("3");
            user = userList.findUser(args[1]);
            System.err.println("4");
            if(user == null) {
                System.out.println("User does not exist");
                return;
            }
            System.err.println("5");
            files = user.getFileList().getFiles();
            System.err.println("6");
        }
        for(int i = 0; i < files.length; i++) {
            System.err.println("g");
            System.out.println(files[i]);
        }
        */
    }
  
  
    public CLI(ShareSetup setup) throws Exception
    {
       share = new Share(setup, this);
    }
    
    public void serverConnected(ShareHandle handle)
    {
        System.out.println("Connected to server at: " + handle.getText());
    }
    
    public void serverDisconnected(ShareHandle handle)
    {
        System.out.println("Sever " + handle.getText() + " disconnected");
    }
    
    public void newUserList(User[] users)
    {
        this.users = users;
    }
    
    public void newUser(User user)
    {
        System.out.println(user.getAlias() + " connected");
    }
    
    public void disconnectUser(User user)
    {
        System.out.println(user.getAlias() + " disconnected");
    }
    
    public void transferFinished(ShareHandle handle)
    {
        System.out.println("Successfully received: " + handle.getText());
    }
    
    public void newDownload(ShareHandle handle)
    {
        System.out.println("Receiving: " + handle.getText());
    }
    public void newUpload(ShareHandle handle)
    {
        System.out.println("uploading: " + handle.getText() + " to " + handle.getUser().getAlias()); 
    }
    public void tickProgress(ShareHandle handle, int percent)
    {
    }
      
    public void shareException(ShareException se)
    {
        System.out.println(se);
    }
    public void protocolException(ProtocolException pe)
    {
        System.out.println(pe);
    }
    public void protocolRefusedException(ProtocolRefusedException pre)
    {
        System.out.println(pre);
    }
    public void exception(Exception e)
    {
        System.err.println(e);
    }
    public void shareEvent(ShareHandle handle)
    {
        System.out.println(handle.getText());
    }
 
}
