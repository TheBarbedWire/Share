import java.util.*;
import java.io.*;
import java.net.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

public class UserList extends XMLFile
{
    private final String UFDEFAULT = "usersFile.xml";
    private final String UFROOT = "userlist";
    private final String TAGUSER = "user";
    private final String USERIP = "ip";
    private final String USERALIAS = "username";
    
    private List<User> uList;
    
    public UserList()
    {
       super();
       uList = new ArrayList<User>();
    }
    
     public UserList(String path) throws Exception
    {
       super(path);
       uList = new ArrayList<User>();
       if(file.exists()) {
           file.delete();
       }
    }
    
    public void setUserFile(File file) throws Exception
    {
        super.setFile(file);
        super.readXML(file);
        fillList();
    }
    
    private void fillList()
    {
        uList = new ArrayList<User>();
        ArrayList<Element> els = getElementsOfTag(getChildren(root), TAGUSER);
        Iterator<Element> iter = els.iterator();
        while(iter.hasNext()) {
            Element el = iter.next();
            uList.add(new User(el.getAttribute(USERIP), el.getAttribute(USERALIAS)));
        }
    }
    
    private void createUserDocument() throws Exception
    {
        super.createDocument(UFROOT);
        Iterator<User> iter = uList.iterator();
        while(iter.hasNext()) {
            User user = iter.next();
            Element element = doc.createElement(TAGUSER);
            element.setAttribute(USERIP, user.getIP());
            element.setAttribute(USERALIAS, user.getAlias());
            root.appendChild(element);
        }
        
    }
    
    public void writeOutFile() throws Exception
    {
        createUserDocument();
        super.writeOutFile();
    }
    
    public boolean doesUserExist(String username)
    {
        if(findUser(username) == null) {
            return false;
        }
        return true;
            
    }
    
    public void createUserFile() throws Exception
    {
        super.createFile(UFROOT);
    }
    
    public User findUser(InetAddress ip)
    {
        User user;
        Iterator<User> iter = uList.iterator();
        while(iter.hasNext()) {
            user = iter.next();
            if(user.getIP().compareTo(ip.getHostAddress()) == 0) {
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
    
    public SharedFile getUserFile()
    {
        return new SharedFile(file.getPath());
    }
    
    public void addUser(User user)
    {
        uList.add(user);
    }
    
    public boolean removeUser(String alias)
    {
        User user = findUser(alias);
        if(user == null) {
            return false;
        }
        return uList.remove(user);
    }
    
    public boolean removeUser(User user)
    {
        return removeUser(user.getAlias());
    }
    
    public boolean removeUser(InetAddress ip)
    {
        User user = findUser(ip);
        if(user == null) {
            return false;
        }
        return uList.remove(user);
    }
    
    public User[] getUserList()
    {
       return uList.toArray(new User[uList.size()]);
    }
    
}
