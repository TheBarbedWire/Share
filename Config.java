import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class Config
{
    private String path;
    private final String pathDefault = "/";
    private final String configFileName = "config.xml";
    private File configFile;
    private Document doc;
    private String defaultRoot = "config";
    private Element root;
    
    private final String tagUserName = "UserName";
    private final String tagDownloadPath = "downloadPath";
    private final String tagTempDownloadPath = "tempDownloadPath";
    private final String tagSharePath = "sharePath";
    private final String tagFileListPath = "fileListPath";
    private final String tagFileListsDirPath = "fileListsDirPath";
    private final String tagDefaultServer = "defaultServer";
    private final String tagAutoConnect = "autoConnect";
    private final String tagLogPath = "logPath";
    
    private final String attributeName = "value";
    
    private final String defaultUserName = "User";
    private final String defaultDownloadPath = "./downloads/";
    private final String defaultTempDownloadPath = "./temporary_downloads/";
    private final String defaultSharePath = "./share/";
    private final String defaultFileListPath = "./filelist.xml";
    private final String defaultFileListsDirPath = "./file_lists/";
    private final String defaultLogPath = "./logs/";
    private final Boolean defaultAutoConnect = false;
    
    private String userName;
    private String downloadPath;
    private String tempDownloadPath;
    private String sharePath;
    private String fileListPath;
    private String fileListsDirPath;
    private String defaultServer = "127.0.0.1";
    private String logPath;
    private Boolean autoConnect;
    
  
    public Config(String configPath) throws Exception
    {
        this.path = configPath + configFileName;
        configFile = new File(path);
        if(configFile.exists()) {
            readXML();
            readValues();
        }
        else {
            createNewConfig();
        }
        
    }
    
    public Config() throws Exception
    {
        this.path = pathDefault + configFileName;
        configFile = new File(path);
        if(configFile.exists()) {
            readXML();
            readValues();
        }
        else {
            createNewConfig();
        }

    }
    
    public String getUserName()
    {
        return userName;
    }
    
    public String getServer()
    {
        return defaultServer;
    }
    
    public String getDownloadPath()
    {
        return downloadPath;
    }
    
    public String getTempDownloadPath()
    {
        return tempDownloadPath;
    }
    
    public String getSharePath()
    {
        return sharePath;
    }
    
    public String getFileListPath()
    {
        return fileListPath;
    }
    
    public String getFileListsDirPath()
    {
        return fileListsDirPath;
    }
    
    public Boolean getAutoConnect()
    {
        return autoConnect;
    }
    
    public String getLogPath()
    {
        return logPath;
    }
    
    public void setUserName(String value) throws Exception
    {
        userName = value;
        getElement(tagUserName).setAttribute(attributeName, value);
        writeXML();
    }
    
    public void setServer(String value) throws Exception
    {
        defaultServer = value;
        getElement(tagDefaultServer).setAttribute(attributeName, value);
        writeXML();
    }
  
    public void setDownloadPath(String value) throws Exception
    {
        downloadPath = value;
        getElement(tagDownloadPath).setAttribute(attributeName, value);
        writeXML();
    }
    
    public void setTempDownloadPath(String value) throws Exception
    {
        tempDownloadPath = value;
        getElement(tagTempDownloadPath).setAttribute(attributeName, value);
        writeXML();
    }
    
    public void setSharePath(String value) throws Exception
    {
        sharePath = value;
        getElement(tagSharePath).setAttribute(attributeName, value);
        writeXML();
    }
    
    public void setFileListPath(String value) throws Exception
    {
        fileListPath = value;
        getElement(tagFileListPath).setAttribute(attributeName, value);
        writeXML();
    }
    
    public void setFileListsDirPath(String value) throws Exception
    {
        fileListsDirPath = value;
        getElement(tagFileListsDirPath).setAttribute(attributeName, value);
        writeXML();
    }
    
    public void setAutoConnect(Boolean bool) throws Exception
    {
        autoConnect = bool;
        getElement(tagAutoConnect).setAttribute(attributeName, bool.toString());
        writeXML();
    }
    
    public void setLogPath(String value) throws Exception
    {
        logPath = value;
        getElement(tagLogPath).setAttribute(attributeName, value);
        writeXML();
    }
    
    public void createNewConfig() throws ParserConfigurationException, Exception
    {
        createNewConfig(pathDefault);
    }
    
    public void createNewConfig(String path) throws ParserConfigurationException, Exception
    {
        if(configFile.exists()) {
            configFile.delete();
        }
        createConfig();
        
        setDefaults();
        
        Element newElement; 
        
        newElement = doc.createElement(tagUserName);
        newElement.setAttribute(attributeName, userName);
        root.appendChild(newElement);
        
        newElement = doc.createElement(tagDownloadPath);
        newElement.setAttribute(attributeName, downloadPath);
        root.appendChild(newElement);
        
        newElement = doc.createElement(tagTempDownloadPath);
        newElement.setAttribute(attributeName, tempDownloadPath);
        root.appendChild(newElement);
        
        newElement = doc.createElement(tagSharePath);
        newElement.setAttribute(attributeName, sharePath);
        root.appendChild(newElement);
        
        newElement = doc.createElement(tagFileListPath);
        newElement.setAttribute(attributeName, fileListPath);
        root.appendChild(newElement);
        
        newElement = doc.createElement(tagFileListsDirPath);
        newElement.setAttribute(attributeName, fileListsDirPath);
        root.appendChild(newElement);
        
        newElement = doc.createElement(tagDefaultServer);
        newElement.setAttribute(attributeName, defaultServer);
        root.appendChild(newElement);
        
        newElement = doc.createElement(tagAutoConnect);
        newElement.setAttribute(attributeName, autoConnect.toString());
        root.appendChild(newElement);
        
        newElement = doc.createElement(tagLogPath);
        newElement.setAttribute(attributeName, logPath);
        root.appendChild(newElement);
        
        writeXML();
    }
    
    private void setDefaults()
    {
        userName = defaultUserName;
        downloadPath = defaultDownloadPath;
        tempDownloadPath = defaultTempDownloadPath;
        sharePath = defaultSharePath;
        fileListPath = defaultFileListPath;
        fileListsDirPath = defaultFileListsDirPath;
        autoConnect = defaultAutoConnect;
        logPath = defaultLogPath;
    }
    
    private void readXML() throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(configFile);
        doc.getDocumentElement().normalize();
        root = doc.getDocumentElement();
    }
    
    private void readValues()
    {
        userName= getElement(tagUserName).getAttribute(attributeName);
        downloadPath = getElement(tagDownloadPath).getAttribute(attributeName);
        tempDownloadPath = getElement(tagTempDownloadPath).getAttribute(attributeName);
        sharePath = getElement(tagSharePath).getAttribute(attributeName);
        fileListPath = getElement(tagFileListPath).getAttribute(attributeName);
        fileListsDirPath = getElement(tagFileListsDirPath).getAttribute(attributeName);
        defaultServer = getElement(tagDefaultServer).getAttribute(attributeName);
        autoConnect = Boolean.valueOf(getElement(tagAutoConnect).getAttribute(attributeName));
        logPath = getElement(tagLogPath).getAttribute(attributeName);
    }
    
    private Element getElement(String name)
    {
        NodeList nl = root.getChildNodes();
        for(int i = 0; i < nl.getLength(); i++) {
            if(nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                if(nl.item(i).getNodeName().equals(name)) {
                    return (Element) nl.item(i);
                }
 
            }
        }
        return null;
        
    }
    
    private void writeXML() throws Exception
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        configFile.delete();
        configFile.createNewFile();
        PrintWriter writer = new PrintWriter(new FileOutputStream(configFile));
        writer.println(xmlString);
        writer.flush();  
    }
    
    private void createConfig() throws ParserConfigurationException
    {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        doc = docBuilder.newDocument();
        root = doc.createElement(defaultRoot);
        doc.appendChild(root);
    }
      
}
