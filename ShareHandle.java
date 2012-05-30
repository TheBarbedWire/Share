

public class ShareHandle
{
    private String text;
    private String message;
    private User user;
    
    public ShareHandle(String text)
    {
      this.text = text;
    }
    
    public ShareHandle(String text, User user)
    {
      this.text = text;
      this.user = user;
    }
    
    public ShareHandle(String text, String message)
    {
      this.text = text;
      this.message = message;
    }
    
    public String getText()
    {
        return text;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String  message)
    {
        this.message = message;
    }
    
    public User getUser()
    {
        return user;
    }
    
    public void setUser(User user)
    {
        this.user = user;
    }
}
