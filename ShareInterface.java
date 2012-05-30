
public interface ShareInterface
{
  
    public void serverConnected(ShareHandle handle);
    public void serverDisconnected(ShareHandle handle);
    public void newUserList(User[] users);
    public void newUser(User user);
    public void disconnectUser(User user);
    public void transferFinished(ShareHandle handle);
    public void newDownload(ShareHandle handle);
    public void newUpload(ShareHandle handle);
    public void tickProgress(ShareHandle handle, int percent);
    public void shareException(ShareException se);
    public void protocolException(ProtocolException pe);
    public void protocolRefusedException(ProtocolRefusedException pre);
    public void exception(Exception e);
    public void shareEvent(ShareHandle handle);
}
