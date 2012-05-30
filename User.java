
public class User
{
    private String ip = "127.0.0.1";
    private String alias;
    private FileList fList;

  
    public User(String ip, String alias)
    {
       this.ip = ip;
       this.alias = alias;
    }
    
     public User(String alias)
    {
       this.alias = alias;
    }
    
    public String getIP()
    {
        return ip;
    }
    public String getAlias()
    {
        return alias;
    }
    
    public String toString()
    {
        return alias;
    }
    
    public void setAlias(String alias)
    {
        this.alias = alias;
    }
    
    public FileList getFileList()
    {
        return fList;
    }
    
    public void setFileList(FileList fList)
    {
        this.fList = fList;
    }

}
