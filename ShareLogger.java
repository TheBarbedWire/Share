import java.io.*;
import java.util.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.security.MessageDigest;

public class ShareLogger extends XMLFile
{
    final String ROOT = "log";
    final String ELEMENT = "entry";
    final String TIME = "timestamp";
    final String TEXT = "text";
    final String XMLTYPE = ".xml";
    String name;
    String logPath;
    Boolean logToFile, logToStream;
    Map<String, String> logContent;
    File logFile;
    PrintStream out;
    
    public ShareLogger(String name) throws Exception
    {
        logToFile = false;
        java.util.Date date= new java.util.Date();
        DateFormat dateFormat = new SimpleDateFormat("yyddMMHHmmss");
        this.name = name + "_" + dateFormat.format(date) + XMLTYPE;
        this.logPath = "";
        logContent = new HashMap<String, String>();
        createDocument(ROOT);
    }
    
    public ShareLogger(String name, String logPath) throws IOException, Exception
    {
        logToFile = true;
        java.util.Date date= new java.util.Date();
        DateFormat dateFormat = new SimpleDateFormat("yyddMMHHmmss");
        this.name = name + "_" + dateFormat.format(date) + XMLTYPE;
        this.logPath = logPath;
        logContent = new HashMap<String, String>();
        createDocument(ROOT);
    }
    
    public void addEntry(String entry) throws Exception
    {
        java.util.Date date= new java.util.Date();
        DateFormat dateFormat = new SimpleDateFormat("yy/dd/MM-HH:mm:ss");
        String tm = dateFormat.format(date);
        logContent.put(tm, entry);
        
        Element newElement;
        newElement = doc.createElement(ELEMENT);
        newElement.setAttribute(TIME, tm);
        newElement.setAttribute(TEXT, entry);
        super.root.appendChild(newElement);
        
        log(entry, tm);
    }
    
    private void log(String entry, String tm)throws IOException, Exception
    {
        if(logToFile) {
            if(super.file == null) {
                createLogFile();
            }
            super.writeOutFile();
        }
        if(logToStream) {
            out.println(tm + ": " +entry);
            out.flush();
        }
    }
    
    public String[] getLog()
    {
        List<String> log = new ArrayList<String>();
        Iterator iter = logContent.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry logEntry = (Map.Entry) iter.next();
            log.add((String)logEntry.getKey() + ": " + (String)logEntry.getValue());
        }
        return log.toArray(new String[log.size()]);
    }
    
    public void fileLoggingOn(String logPath) throws IOException, Exception
    {
        logToFile = true;
        this.logPath = logPath;
        if(logContent.size() < 0) {
            createLogFile();
            super.writeOutFile();
        }
    }
    
    public void streamLoggingOn(PrintStream out)
    {
        this.out = out;
        logToStream = true;
        outputLogToStream();
    }
    
    public void fileLoggingOff()
    {
        logToFile = false;
    }
    
    private void outputLogToStream()
    {
        Iterator iter = logContent.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry logEntry = (Map.Entry) iter.next();
            out.println((String)logEntry.getKey() + ": " + (String)logEntry.getValue());
        }
        out.flush();
    }
    
    private void createLogFile() throws IOException, Exception
    {
        logFile = new File(logPath + name);
        logFile.createNewFile();
        super.setFile(logFile);
    }
    
    
}
