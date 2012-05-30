import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.border.LineBorder;

public class GUI extends JFrame implements ActionListener, ShareInterface
{
    private final String errorLogName = "errorLog";
    private final String eventLogName = "log";
    private Share share;
    private FileTree tree;
    private JTextField textBox1;
    private DownloadArea dla;
    private JSplitPane jsp;
    private ShareLogger errorLog;
    private ShareLogger eventLog;
    private LogForm logForm;
   
    public GUI(ShareSetup setup) throws Exception
    {
       super("Share");
       share = new Share(setup, this);
       logForm = new LogForm("Log");
       errorLog = new ShareLogger(errorLogName,  share.getLogPath());
       eventLog = new ShareLogger(eventLogName);
       eventLog.streamLoggingOn(logForm.getStream());
       createFrame();
       createMenu();
       this.tree = new FileTree("Users", share);
       JScrollPane treeScroll = new JScrollPane();
       treeScroll.getViewport().add(tree);
       dla = new DownloadArea();
       createSplitPlane(treeScroll, dla);
       createLeftPanel();
       this.pack();
       this.setVisible(true);
       onFrameLoad();
       
    }
    
    private void onFrameLoad()
    {
        if(share.getAutoConnect()) {
            connect();
        }
    }
    
    public void newDownload(ShareHandle handle)
    {
        dla.addDownloadBar(handle);
    }
    
    public void newUpload(ShareHandle handle)
    {
        dla.addUploadBar(handle);
    }
    
    public void transferFinished(ShareHandle handle)
    {
        try {
            Thread.sleep(6000);
        }
        catch(Exception e){}
        dla.removeBar(handle);
        this.revalidate();
    }
    
    public void tickProgress(ShareHandle handle, int percent)
    {
        dla.tickProgress(handle, percent);
    }
    
    public void serverConnected(ShareHandle handle)
    {
        
        try{ 
            eventLog.addEntry("server connected");
        }
        catch(Exception e) {
            System.err.println(e);
        }
        
    }
    public void newUserList(User[] users)
    {
        tree.addUsers(users);
    }
    public void newUser(User user)
    {
        tree.addUser(user);
    }
    
    public void disconnectUser(User user)
    {
        tree.removeUser(user);
    }
    
    private void createFrame()
    {
        this.addWindowListener(new java.awt.event.WindowAdapter() {
           public void windowClosing(WindowEvent winEvt) {
               
            try {   
                share.shutdown();
            }
            catch(Exception e) {
                System.err.println(e);
            }
            System.exit(0); 
           }
        });
        this.setPreferredSize(new Dimension(1000, 800));
        this.setLocationRelativeTo(null);
    }
    
    private void createMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem menuItem; 
        menuItem = new JMenuItem("settings");
        menuItem.addActionListener(this);
        menuItem.setActionCommand("settings");
        menu.add(menuItem);
        menuItem = new JMenuItem("addShares");
        menuItem.addActionListener(this);
        menuItem.setActionCommand("addShares");
        menu.add(menuItem);
        menuItem = new JMenuItem("view logs");
        menuItem.addActionListener(this);
        menuItem.setActionCommand("view_logs");
        menu.add(menuItem);

        menuBar.add(menu);
        this.setJMenuBar(menuBar);
    }
    
    private void createSplitPlane(Component component1, Component component2)
    {
        jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                           component1, component2);
        this.getContentPane().add(jsp, BorderLayout.CENTER);
    }  
    
    private void createLeftPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        //textBox1 = new JTextField();
        JButton button1 = new JButton("Connect");
        button1.addActionListener(this);
        button1.setActionCommand("connect");
        //panel.add(textBox1);
        panel.add(button1);
        this.add(panel, BorderLayout.LINE_END);
    }
    
   
    public void actionPerformed(ActionEvent e) 
    {
        try {
        if(e.getActionCommand().equals("connect")){
            connect();
        }
        else if(e.getActionCommand().equals("addShares")){
            share.addShares();
        }
        else if(e.getActionCommand().equals("settings")){
            SettingsDialog sDiag = new SettingsDialog(this, "settings", share);
            sDiag.setVisible(true);
        }
        else if(e.getActionCommand().equals("view_logs")){
            logForm.setVisible(true);
        }
    }
    catch(Exception exc) {
        System.err.println(e);
    }
    }
    
    public void connect()
    {
        share.connectToServer(share.getDefaultServer());
    }
     
    public void windowClosing(WindowEvent winEvt) 
    {
        // Perhaps ask user if they want to save any unsaved files first.
        System.out.println("asfdasfas");
        System.exit(0); 
    }
    
    public void serverDisconnected(ShareHandle handle)
    {
        try{ 
            eventLog.addEntry("server disconnected");
        }
        catch(Exception e) {
            System.err.println(e);
        }
    }
    
    public void shareException(ShareException se)
    {
        try {
            eventLog.addEntry(se.getMessage());
        }
        catch(Exception e){
            System.err.println(e);
        }
    }
    public void protocolException(ProtocolException pe)
    {
        try {   
            eventLog.addEntry(pe.getMessage());
        }
        catch(Exception e){
            System.err.println(e);
        }
    }
    public void protocolRefusedException(ProtocolRefusedException pre)
    {
        try {
            eventLog.addEntry(pre.getMessage());
        }
        catch(Exception e){
            System.err.println(e);
        }
    }
    public void exception(Exception e)
    {
        try {
            errorLog.addEntry(e.getMessage());
        }
        catch(Exception ex){
            System.err.println(ex);
        }
    }
    
    public void shareEvent(ShareHandle handle)
    {
      try {
            eventLog.addEntry(handle.getText ());
        }
        catch(Exception e){
            System.err.println(e);
        }  
    }

}
