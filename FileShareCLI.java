import java.io.*;
import java.net.*;

public class FileShareCLI
{
    private String sharePath = "C:/Users/Andrew/Dropbox/share/share/";
    private String fileListPath = "filelists/";
    private String FLpath = "filelist.xml";
    private String commands = "commands: addShares, listusers, getfilelist, showfilelist, getfile, connect";
    private int PLport = 1234;
    private int SPport = 1238;
    private int PCport = 1235;
    private int CLport = 1236;
    private int FTport = 1237;
    private int SFTport = 1239;
    private UserList userList;
    private FileList fileList;
    private ServerConnect SC;
    private PingListener PL;
    private FileListener FL;
    private User me;
 
    public FileShareCLI(String alias, String ip, String serverIP)
    {
        try {
            me = new User(ip, alias);
            userList = new UserList();
            File fList = new File(FLpath);
            if(fList.exists()) {
                fileList = new FileList(fList);
                fileList.readInFile   ();
            }
            else {
                fileList = new FileList(FLpath);
            }
            
            PL = new PingListener(PLport);
            PL.start();
            FL = new FileListener(me, FTport, fileList, userList, sharePath);
            FL.start();
            SC = new ServerConnect(me, userList, SPport, serverIP, CLport, SFTport);
            SC.start();
            command();
        }
        catch (Exception e) {
            System.err.println("arrrrr");
            e.printStackTrace();
            //System.exit(0);
        }
        
       
    }
    
    
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
                System.err.println("arrrrr 2");
                e.printStackTrace();
            }
        }
    }

    private void addShares() throws Exception
    {
        fileList.addPathContents(sharePath, sharePath);
        fileList.writeFile();
    }
    
    
    private void connect(String args[])
    {
        SC = new ServerConnect(me, userList, SPport, args[1], CLport, SFTport);
        SC.start();
    }
    
    private void getfile(String[] args)
    {
        //System.out.println(fileList.getFile(args[1], sharePath));
        Requester requester = new Requester(userList.findUser(args[1]).getIP(), FTport, args[2] , new File(sharePath + args[2]));
        requester.run();
    }
    
    private void getfilelist(String[] args)
    {
        Requester requester = new Requester(userList.findUser(args[1]).getIP(), FTport, 1 , new File(fileListPath + args[1] + ".xml"));
        requester.start();
    }
    
    private void addfile(String[] args) throws Exception
    {
        if(args[1] == null) {
            return;
        }
        fileList.addPath(args[1], sharePath);
        fileList.writeFile();
        System.out.println("file: " + args[1] + " added");
    }
    
    private void listusers()
    {
        int i = 0;
        User[] users = userList.getUserList();
        while(i < users.length) {
            System.out.println(users[i].getAlias() + " " + users[i].getIP());
            i++;
        }
    }
    
    private void showfilelist(String args[])
    {
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
        
    }
    
}
