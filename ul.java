import java.util.*;
import java.io.*;
import java.net.*;

public class ul
{
    private static final String UFDEFAULT = "usersFile.cfg";
    private static final char FIELDSEP = ';';
    private File userFile;
    private List<User> uList;
    
    public ul() throws Exception
    {
       uList = new ArrayList<User>();
       userFile = new File(UFDEFAULT);
       if(!userFile.exists()) {
           throw new Exception("no user file");
        }
        else {
            readInFile(userFile);
        }
            
    }
    
    public boolean doesUserExist(String username)
    {
        if(findUser(username) == null) {
            return false;
        }
        return true;
            
    }
    
    public User findUser(InetAddress IP)
    {
        User user;
        Iterator<User> iter = uList.iterator();
        while(iter.hasNext()) {
            user = iter.next();
            if(user.getIP().compareTo(IP.getHostAddress()) == 0) {
                return user;
            }
        }
        return null;
    }
    
    public User findUser(String alias)
    {
        User user;
        Iterator<User> iter = uList.iterator();
        while(iter.hasNext()) {
            user = iter.next();
            if(user.getAlias().equals(alias)) {
                return user;
            }
        }
        return null;
    }
    
    
    public void setUserFile(File file) throws IOException
    {
        uList = new ArrayList<User>();
        readInFile(file);   
        writeOutFile(UFDEFAULT);
    }
    
    public SharedFile getUserFile()
    {
        return new SharedFile(userFile.getPath());
    }
    
    public void addUser(User user) throws IOException
    {
        uList.add(user);
        writeOutFile(UFDEFAULT);
    }
    
    public void removeUser(User user) throws IOException
    {
        uList.remove(user);
        writeOutFile(UFDEFAULT);
    }
    
    public boolean removeUser(String alias) throws IOException
    {
        User user = findUser(alias);
        if(user == null) {
            return false;
        }
        uList.remove(user);
        writeOutFile(UFDEFAULT);
        return true;
    }
    
    public boolean removeUser(InetAddress IP) throws IOException
    {
        User user = findUser(IP);
        if(user == null) {
            return false;
        }
        uList.remove(user);
        writeOutFile(UFDEFAULT);
        return true;
    }
    
    public User[] getUserList()
    {
        return uList.toArray(new User[uList.size()]);
    }
    
    private void readInFile(File file) throws IOException
    {
        BufferedReader reader;
        String read;

        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        while((read = reader.readLine()) != null) {
            int i = 0;
            String ip;
            String alias;
            while(i < read.toCharArray().length  && read.toCharArray()[i] != FIELDSEP ) {
                i++;
            }
            alias = read.substring(0, i);
            ip = read.substring(i + 1, read.length());
            uList.add(new User(ip, alias));           
        }
        
    }
    
    private void writeToFile(PrintWriter pWrite) throws IOException
    {
        Iterator<User> iter = uList.iterator();
        while(iter.hasNext()) {
            User user = iter.next();
            pWrite.println(user.getAlias() + FIELDSEP + user.getIP());
        }
        pWrite.close();
    }
    
    private File writeOutFile(String path) throws IOException 
    {
        File newFile = new File(path);
        if(newFile.exists()) {
            newFile.delete();
        }
        newFile.createNewFile();
        PrintWriter printW = new PrintWriter(new FileOutputStream(newFile));
        writeToFile(printW);
                  
        return newFile;
      
    }
}