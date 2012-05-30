import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LogForm extends JFrame implements ActionListener 
{
    private PrintStream out;
  
    public LogForm(String title)
    {
        super(title);
        this.setLayout(new GridBagLayout());
        createFrame();
    }
    
    public PrintStream getStream()
    {
        return out;
    }
    
    private void createFrame()
    {
        this.setSize(new Dimension(800, 650));
        JTextArea textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(700, 550));
        out = new PrintStream(new TextAreaOutStream(textArea));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        this.add(textArea, c);
        JButton closeB = new JButton("Close");
        closeB.addActionListener(this);
        c.gridy = 1;
        this.add(closeB, c);
    }
    
    public void actionPerformed(ActionEvent e) 
    {
        this.setVisible(false);
    }
    
    
    private class TextAreaOutStream extends OutputStream
    {
        private JTextArea textArea;
        
        public TextAreaOutStream(JTextArea textArea)
        {
            this.textArea = textArea;
        }
        public void write(int b) throws IOException
        {
            textArea.append(String.valueOf((char) b));
        }
    }

}
