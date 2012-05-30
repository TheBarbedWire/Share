import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.URL;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.border.LineBorder;

public class DownloadArea extends JScrollPane
{
    JPanel panel;
    int row = 0;
    HashMap<ShareHandle, ProgressPanel> dlHash;
   
    public DownloadArea()
    {
        super();
        this.panel = new JPanel(new GridLayout(0 , 1, 0, 10));
        //panel.setBorder(BorderFactory.createLineBorder(Color.red ));
        this.getViewport().setLayout(new FlowLayout(FlowLayout.LEFT));
        this.getViewport().add(panel);
        this.dlHash = new HashMap<ShareHandle, ProgressPanel>();
       
    }
    
    public void addDownloadBar(ShareHandle handle)
    {
        ProgressPanel pro = new ProgressPanel(handle.getText(), Color.GREEN);
        //GridBagConstraints c = new GridBagConstraints();
        //c.gridx = 0;
        //c.gridy = row;
        dlHash.put(handle, pro);
        panel.add(pro);
        row++;
        this.revalidate();
        
    }
    
    public void addUploadBar(ShareHandle handle)
    {
        ProgressPanel pro = new ProgressPanel(handle.getText(), Color.BLUE);
        dlHash.put(handle, pro);
        panel.add(pro);
        this.revalidate();
        
    }
    
    public void removeBar(ShareHandle handle)
    {
        ProgressPanel pro = dlHash.get(handle);
        panel.remove(pro);
        this.revalidate();
        row--;
        dlHash.remove(handle);
    }
    
    public void tickProgress(ShareHandle handle, int percent)
    {
        ProgressPanel pro = dlHash.get(handle);
        pro.progress(percent);
    }
}
