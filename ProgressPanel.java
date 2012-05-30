import javax.swing.*;
import java.awt.*;

public class ProgressPanel extends JPanel
{
    JProgressBar bar;
    JLabel label;
    String text;
    
    public ProgressPanel(String text, Object color)
    {
        super();
        this.setLayout(new GridLayout(0, 3, 10 ,0));
        //this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        //this.setBorder(BorderFactory.createLineBorder(Color.black));
        UIManager.put("ProgressBar.background", Color.WHITE); 
        UIManager.put("ProgressBar.foreground", color);
        UIManager.put("ProgressBar.selectionForeground",Color.BLACK);
        label = new JLabel(text);
        bar = new JProgressBar();
        bar.setValue(0);
        bar.setMaximumSize(new Dimension(200,20));
        bar.setMinimumSize(new Dimension(200,20));
        bar.setStringPainted(true);
        //GridBagConstraints c = new GridBagConstraints();
        //c.insets = new Insets(10, 10, 10, 10);
        //c.gridx = 0;
        //c.gridy = 0;
  
        
        this.add(bar);
        //c.gridx = 1;
        this.add(label);
    }
    
    public void progress(int percent)
    {
        bar.setValue(percent);
        bar.revalidate();
    }
   
}
