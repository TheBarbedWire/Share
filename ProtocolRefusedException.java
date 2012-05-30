
public class ProtocolRefusedException extends ShareException
{
  
    public ProtocolRefusedException()
    {
       super();
    }
    
    public ProtocolRefusedException(String message)
    {
       super(message);
    }
    
    public ProtocolRefusedException(String message, Throwable cause)
    {
       super(message, cause);
    }
    
    public ProtocolRefusedException(Throwable cause)
    {
       super(cause);
    }

}