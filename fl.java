import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class fl
{
    private static final String FILENAME = "filelist.cfg";
    private String path;
    private File fList;
    private PrintWriter out;
    private BufferedReader reader;
    private List<String> filePaths;
    private String sharePath;
    private Document doc;
    private Element root;
    
    
    public fl(String path, String sharePath) throws IOException
    {
        this.path = path;
        this.sharePath = sharePath;
        filePaths = new ArrayList<String>();
        this.fList = new File(path);
        if(fList.exists()) {
            createWR();
            readInFile();
        }
        else {
            fList.createNewFile();
            createWR();
        }
        
         
    }
    
    
    private void createDocument()
    {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        this.doc = docBuilder.newDocument();
        root = doc.createElement("share");
        doc.appendChild(root);
    }
    
    private Element findNode(Element node, String path)
    {
        String name = getTopLevel(path);
        NodeList nl;
        nl = node.getElementsByTagName("file");
        for(int i = 0; i < nl.getLength(); i++) {
            if(nl.item(i).getAttribute("name") == name) {
                return nl.item(i);
            }
        }
        
    }
    
    private void addFolder(String path, String name)
    {
        
    }
    
    private void addFile(String path, String name)
    {
        
    }
    
    private String getTopLevel(String path);
    {
        return path.subString(0, getFirstLevelIndex(path));
    }
    
    private String getNextLevel(String path)
    {
        int index = getFirstLevelIndex(path);
        if(index == path.length) {
            return null;
        }
        return path.subString(index);
    }
    
    private int getFirstLevelIndex(String path)
    {
        int i = 0;
        while((i < path.length) && (path.charAt[i] != '\\')) {
            i++;
        }
        return i;
    }
    
    public FileList(File filelist) throws IOException
    {
        this.fList = filelist;
        readInFile();
    }
    
    public String[] getFiles()
    {
       return filePaths.toArray(new String[filePaths.size()]);
    }
    
    public File getFileList()
    {
        return fList;
    }
    
    private void createWR() throws IOException
    {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(fList)));
    }
    
    private void readInFile()throws IOException
    {
        String read;
        while((read = reader.readLine()) != null) {
            filePaths.add(read);
        }
        
    }
    
    public boolean addPath(String path)
    {
        if(path == null) {
            return false;
        }
        File pathFile = new File(path);
        if(!pathFile.exists()) {
            return false;
        }
        if(pathFile.isDirectory()) {
            File[] dirContent = pathFile.listFiles();
            for(int i = 0; i < dirContent.length; i++) {
                addPath(dirContent[i].getPath());
            }
        }
        System.err.println(path.substring(sharePath.length(), path.length()));
        filePaths.add(path.substring(sharePath.length()));
        return true;
    }
    
    private int compPath(String path)
    {
        int i = 0, num = 0;
        while(i < path.length()  && i < sharePath.length()) {
            if(path.charAt(i) == sharePath.charAt(i)) {
                num++;
            }
            i++;
        }
        return num;
    }
    
    private boolean checkPath(String path)
    {
        File fPath = new File(path);
        if(compPath(path) != sharePath.length()) {
            return false;
        }
     
        if(fPath.isDirectory()) {
            int i = 0;
            while(i < fPath.listFiles().length) {
               filePaths.remove(fPath.listFiles()[i].getPath().substring(sharePath.length()));
               i++;
            }
        }
        else {
            filePaths.remove(path.substring(sharePath.length()));
        }
        return true;
    }
 
      
    public void writeFile() throws IOException 
    {
        Iterator<String> iter = filePaths.iterator();
        try {
            fList.delete();
            fList.createNewFile();
            out = new PrintWriter(new FileOutputStream(fList));
            while(iter.hasNext()) {
                out.println(iter.next());
            }
            
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            out.close();
            reader.close();
        }
      
    }
    
    public boolean exists(String filePath)
    {
       return filePaths.contains(filePath);         
    }  
    
    public String getFile(String path)
    {
        if(!exists(path)) {
            return null;
        }
        String newPath;
        newPath = sharePath + path;
        return newPath;
    }

}
