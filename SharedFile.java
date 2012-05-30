import java.io.*;

public class SharedFile extends File
{
    private String fileName;
    private String path;
    private User user;
    private boolean isDir = false;
    private String md5;
    private int size = 0;
    
    public SharedFile(String path, User user, boolean isDir, int size, String md5)
    {
        super(path);
        this.path = path;
        this.user = user;
        this.isDir = isDir;
        this.size = size;
        this.md5 = md5;
        fileName = getName(path);
    }
    
    public SharedFile(String path, User user, boolean isDir)
    {
        super(path);
        this.path = path;
        this.user = user;
        this.isDir = isDir;
        fileName = getName(path);
    }
    
    public SharedFile(String path, boolean isDir)
    {
        super(path);
        this.path = path;
        this.user = null;
        this.isDir = isDir;
        fileName = getName(path);
    }
  
    public SharedFile(String path, User user)
    {
        super(path);
        this.path = path;
        this.user = user;
        fileName = getName(path);
    }
    
    public SharedFile(String path)
    {
        super(path);
        this.path = path;
        this.user = null;
        fileName = getName(path);
    }
    public int getSize()
    {
        return size;
    }
    
    public String getMD5()
    {
        return md5;
    }
    
    public boolean isDir()
    {
        return isDir;
    }
    
    public String toString()
    {
        return fileName;
    }
    
    public String getPath()
    {
        return path;
    }
    
    public void setUser(User user)
    {
        this.user = user;
    }
    
    public User getUser()
    {
        return user;
    }
    
    private String getName(String path)
    {
        int i = 0;
        int lastSlash = 0, slashBefore = 0;
        while(i < path.length()) {
            if((path.charAt(i) == '/') || (path.charAt(i) == '\\')) {
                slashBefore = lastSlash;
                lastSlash = i;
            }
            i++;
        }
        if(lastSlash == path.length() -1) {
            isDir = true;
            if(slashBefore == 0) {
                return path.substring(slashBefore);
            }
            return path.substring(slashBefore + 1);
        }
        if(lastSlash == 0) {
            return path;
        }
        return path.substring(lastSlash + 1);
        
    }
    
    public boolean createNewFile() throws IOException
    {
        File parent = new File(this.getParent());
        parent.mkdirs();
        return super.createNewFile();
    }
    
    
}
