import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.security.MessageDigest;

public class FileList
{
    private static final String FILENAME = "filelist.xml";
    private String path;
    private File fList;
    private PrintWriter out;
    private List<String> filePaths;
    private Document doc;
    private Element root;
    private User user;
    private int fileSize;
    
    public FileList(String newFilePath) throws Exception
    {
        try {
            fList = new File(newFilePath);
            createDocument();
            writeFile();
        }
        catch (Exception e) {
            throw new Exception("FileList: XML read error: " + e.getMessage());
        }
        
    }
    
    public FileList(File fileList, User user) throws Exception
    {
        try {
            setFile(fileList);
        }
        catch (Exception e) {
            throw new Exception("FileList: XML read error: " + e.getMessage());
        }
        this.user = user;
    }
    
    private String generateMD5(File file) throws Exception
    {
        byte[] checksum = getChecksum(file);
        String md5 = "";
        for(int i = 0; i < checksum.length; i++) {
            md5 += Integer.toString((checksum[i] & 0xff) + 0x100, 16).substring(1);
        }
        return md5;
    }
    
    private byte[] getChecksum(File file) throws Exception
    {
        InputStream input = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        MessageDigest md = MessageDigest.getInstance("MD5");
        int read = 0;
        fileSize = 0;
        do {
            fileSize += read;
            read = input.read(buffer);
            if(read > 0) {
                md.update(buffer, 0, read);
            }
        } while(read != -1);
        input.close();
        return md.digest();
        
    }
        
    public void setFile(File fileList) throws ParserConfigurationException, SAXException, IOException
    {
        this.fList = fileList;
        readInFile();
    }
    
    public void readInFile() throws ParserConfigurationException, SAXException, IOException
    {
        readXML();
    }
    
    public void readInFile(File file) throws ParserConfigurationException, SAXException, IOException
    {
        this.fList = file;
        readXML();
    }
    
    private void readXML() throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(fList);
        doc.getDocumentElement().normalize();
        root = doc.getDocumentElement();
    }
    
    public SharedFile[] getTopLevelFiles()
    {
        return getFileNodes(root, "");
    }
    
    public SharedFile[] getChildFiles(SharedFile parentFile)
    {
        return getFileNodes(findNode(root, parentFile.getPath()), parentFile.getPath());
 
    }
    
    private SharedFile[] getFileNodes(Element root, String rootPath)
    {
       ArrayList<SharedFile> fileList = new ArrayList<SharedFile>();
       ArrayList<Element> list = getChildren(root);
       Iterator<Element> iter = list.iterator();
       while(iter.hasNext()) {
           Element el = iter.next();
           String path = rootPath + el.getAttribute("name");
           Boolean isDir = false;
           if(el.getTagName().equals("dir")) {
               isDir = true;
               SharedFile file = new SharedFile(path, user, isDir);
               fileList.add(file);
           }
           else {
               SharedFile file = new SharedFile(path, user, isDir, Integer.parseInt(el.getAttribute("size")), el.getAttribute("MD5"));
               fileList.add(file);
           }
           
       }
       return fileList.toArray(new SharedFile[fileList.size()]); 
    }
    
    
    
    public SharedFile getFile(String filePath, String rootPath)
    {
        Element element = findNode(root, filePath);
        if(element == null) {
            return null;
        }
        Boolean isDir = element.getTagName().equals("dir");
        SharedFile file = new SharedFile(rootPath + filePath, user, isDir, Integer.parseInt(element.getAttribute("size")), element.getAttribute("MD5"));
        return file;
    }
    
    public void getChildren()
    {
        NodeList nl = root.getChildNodes();
        for(int i = 0; i < nl.getLength(); i++) {
            System.out.println(nl.item(i).getNodeName());
            System.out.println(nl.item(i).getNodeValue());
        }
    
    }
    
    
    public boolean exists(String path)
    {
        if(findNode(root, path) == null) {
            return false;
        }
        else {
            return true;
        }
    }
    private void createDocument() throws ParserConfigurationException
    {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        this.doc = docBuilder.newDocument();
        root = doc.createElement("filelist");
        doc.appendChild(root);
    }
  
    private Element findNode(Element element, String path)
    {
        ArrayList<Element> children = getChildren(element);
        if(children.size() == 0) {
            return null;
        }
        String name = getTopLevel(path);
        String type, dirName;
        dirName = isDir(name);
        if(dirName != null ) {
            type = "dir";
            name = dirName;
        }
        else {
            type = "file";
        } 
        Iterator<Element> iter = children.iterator();
        while(iter.hasNext()) {
            Element el = iter.next();
            if((el.getAttribute("name").equals(name)) && (el.getTagName().equals(type))) {
                if(getNextLevel(path) == null) {
                    return el;
                }
                return findNode(el, getNextLevel(path));
            }
        }
        return null;
        
    }
    
    private ArrayList<Element> getChildren(Element element)
    {
        ArrayList<Element> children = new ArrayList<Element>();
        NodeList nl = element.getChildNodes();
        for(int i = 0; i < nl.getLength(); i++) {
            if(nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                children.add((Element) nl.item(i));
            }
        }
        return children;
        
    }
    
    
    private String isDir(String path)
    {
        if(path.charAt(path.length() - 1) == '/') {
            return path;
        }
        else if(path.charAt(path.length() - 1) == '\\') {
            char[] str = path.toCharArray();
            str[path.length() -1] = '/';
            path = new String(str);
            return path;
        }
        return null;
    }
    
    private void addDir(String path, String name)
    {
    }
    
    private void addFile(String path, String name)
    {
        
    }
    
    private String getTopLevel(String path)
    {
        return path.substring(0, getFirstLevelIndex(path));
    }
    
    private String getNextLevel(String path)
    {
        int index = getFirstLevelIndex(path);
        if(index == path.length()) {
            return null;
        }
        return path.substring(index);
    }
    
    private int getFirstLevelIndex(String path)
    {
        int i = 0;
        while(i < path.length()) {
            if((path.charAt(i) == '/') || (path.charAt(i) == '\\')) {
                return i + 1;
            }
            i++;
        }
        return i;
    }
    
    public String[] getFiles()
    {
       return filePaths.toArray(new String[filePaths.size()]);
    }
    
    public SharedFile getFileList()
    {
        return new SharedFile(fList.getPath(), user);
    }
    
    //public boolean addPath(String path, String rootPath)
    //{
        //return addPathElements(root, path, rootPath);
    //}
    
    public boolean addPathContents(String path, String rootPath) throws Exception
    {
        File pathFile = new File(path);
        if(!pathFile.exists()) {
            return false;
        }
        if(pathFile.isDirectory()) {
            File[] paths = pathFile.listFiles();
            for(int i = 0; i < paths.length; i++) {
                addPathElements(root, paths[i].getPath(), rootPath);
            }
        }
        else {
            return addPathElements(root, path, rootPath);
        }
        return true;
    }
    
    private boolean addPathElements(Element element, String path, String rootPath) throws Exception
    {
        Element newElement;
        if(path == null) {
            return false;
        }
        File pathFile = new File(path);
        if(!pathFile.exists()) {
            return false;
        }
        if(pathFile.isDirectory()) {
            newElement = addElement(element, pathFile.getName() +"/",  "dir");
            File[] dirContent = pathFile.listFiles();
            for(int i = 0; i < dirContent.length; i++) {
                 addPathElements(newElement, dirContent[i].getPath(), rootPath);
            }
        }
        else {
            fileSize = 0;
            String md5 = generateMD5(pathFile);
            addElement(element, pathFile.getName(),  "file", md5, fileSize);
        }
        return true;
    }
    
    private Element addElement(Element parent, String name, String type)
    {
        Element newElement = doc.createElement(type);
        newElement.setAttribute("name", name);
        parent.appendChild(newElement);
        return newElement;
    }
    
    private Element addElement(Element parent, String name, String type, String md5, int fileSize)
    {
        Element newElement = doc.createElement(type);
        newElement.setAttribute("MD5", md5);
        newElement.setAttribute("size", Integer.toString(fileSize));
        newElement.setAttribute("name", name);
        parent.appendChild(newElement);
        return newElement;
    }
    
    private int compPath(String path, String rootPath)
    {
        int i = 0, num = 0;
        while(i < path.length()  && i < rootPath.length()) {
            if(path.charAt(i) == rootPath.charAt(i)) {
                num++;
            }
            i++;
        }
        return num;
    }
    
    private boolean checkPath(String path, String rootPath)
    {
        File fPath = new File(path);
        if(compPath(path, rootPath) != rootPath.length()) {
            return false;
        }
     
        if(fPath.isDirectory()) {
            int i = 0;
            while(i < fPath.listFiles().length) {
               filePaths.remove(fPath.listFiles()[i].getPath().substring(rootPath.length()));
               i++;
            }
        }
        else {
            filePaths.remove(path.substring(rootPath.length()));
        }
        return true;
    }
 
      
    public void writeFile() throws Exception 
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        fList.delete();
        fList.createNewFile();
        PrintWriter writer = new PrintWriter(new FileOutputStream(fList));
        writer.println(xmlString);
        writer.flush();  
    }
    

}
