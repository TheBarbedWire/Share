import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SettingsDialog extends JDialog implements ActionListener 
{
    private Share share;
    
    private JTextField textAddress;
    private JTextField textUsername;
    private JTextField textShared;
    private JTextField textDL;
    private JCheckBox checkAC;
    
    public SettingsDialog(Frame owner, String title, Share share)
    {
        super(owner, title);
        this.share = share;
        createFrame();
        addItems();
        readConfigStates();
    }
    
    private void createFrame()
    {
        this.setSize(new Dimension(700, 300));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
    }
    
    private void addItems()
    {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.PAGE_START;
        
        JLabel label1 = new JLabel("Username:");
        c.gridx = 0;
        c.gridy = 0;
        this.add(label1, c);
        textUsername = new JTextField(20);
        c.gridx = 0;
        c.gridy = 1;
        this.add(textUsername, c);
        
        JLabel label2 = new JLabel("Default Server:");
        c.gridx = 0;
        c.gridy = 2;
        this.add(label2, c);
        textAddress= new JTextField(15);
        c.gridx = 0;
        c.gridy = 3;
        this.add(textAddress, c);
        checkAC = new JCheckBox("Auto-connect to server");
        c.gridx = 0;
        c.gridy = 4;
        this.add(checkAC, c);
        
        
        JLabel label3 = new JLabel("Shared Folder:");
        c.gridx = 1;
        c.gridy = 0;
        this.add(label3, c);
        textShared = new JTextField(20);
        c.gridx = 1;
        c.gridy = 1;
        this.add(textShared, c);
        JButton button1 = new JButton("Select Folder");
        button1.addActionListener(this);
        button1.setActionCommand("select_share_folder");
        c.gridx = 1;
        c.gridy = 2;
        this.add(button1, c);
       
        
        JLabel label4 = new JLabel("Downloads Folder:");
        c.gridx = 1;
        c.gridy = 3;
        this.add(label4, c);
        textDL = new JTextField(20);
        c.gridx = 1;
        c.gridy = 4;
        this.add(textDL, c);
        JButton button2 = new JButton("Select Folder");
        button2.addActionListener(this);
        button2.setActionCommand("select_dl_folder");
        c.gridx = 1;
        c.gridy = 5;
        this.add(button2, c);
        
        JButton ok = new JButton("OK");
        ok.addActionListener(this);
        ok.setActionCommand("ok");
        c.gridx = 0;
        c.gridy = 5;
        this.add(ok, c);
        JButton apply = new JButton("Apply");
        apply.addActionListener(this);
        apply.setActionCommand("apply");
        c.gridx = 1;
        c.gridy = 6;
        this.add(apply, c);
    }
    
    public void actionPerformed(ActionEvent e) 
    {
        try {
        if(e.getActionCommand().equals("select_share_folder")){
            String text;
            if(!(text = getFolderDialog()).equals("")) {
                textShared.setText(text);
            }
        }
        else if(e.getActionCommand().equals("select_dl_folder")){
            String text;
            if(!(text = getFolderDialog()).equals("")) {
                textDL.setText(text);
            }
        }
        else if(e.getActionCommand().equals("ok")){
            this.dispose();
        }
        else if(e.getActionCommand().equals("apply")){
            saveConfigStates();
        }
    }
    catch(Exception exc) {
        System.err.println(e);
    }
    }
    
    private String getFolderDialog()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select target directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            return file.getPath();
        }
        return "";
    }
    
    private void readConfigStates()
    {
        textUsername.setText(share.getUserName());
        textAddress.setText(share.getDefaultServer());
        textShared.setText(share.getSharePath());
        textDL.setText(share.getDownloadPath());
        checkAC.setSelected(share.getAutoConnect());
        
    }
    
    private void saveConfigStates()
    {
        try {
            share.setUserName(textUsername.getText());
            share.setDefaultServer(textAddress.getText());
            share.setSharePath(textShared.getText());
            share.setDownloadPath(textDL.getText());
            share.setAutoConnect(checkAC.isSelected());
        }
        catch(Exception e) {
            System.err.println(e);
        }
    }

}
