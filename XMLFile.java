import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.security.MessageDigest;

public abstract class XMLFile
{
    protected Document doc;
    protected Element root;
    protected File file;
    protected String path;
    
    public XMLFile()
    {
    }
  
    public XMLFile(String path)
    {
        this.path = path;
        this.file = new File(path);
    }
    
    public XMLFile(File file)
    {
        this.file = file;
        this.path = file.getPath();
    }
    
    public void setFile(File file)
    {
        this.file = file;
        this.path = file.getPath();
    }
    
    protected void createFile(String rootName) throws Exception
    {
        createDocument(rootName);
        writeOutFile();
    }
    
    protected void createDocument(String rootName) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        this.doc = docBuilder.newDocument();
        root = doc.createElement(rootName);
        doc.appendChild(root);
    }
    
    protected ArrayList<Element> getChildren(Element element)
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
    
    protected ArrayList<Element> getElementsOfTag(ArrayList<Element> children, String tagName)
    {
        ArrayList<Element> els = new ArrayList<Element>();
        Iterator<Element> iter = children.iterator();
        while(iter.hasNext()) {
            Element el = iter.next();
            if(el.getTagName().equals(tagName)) {
                els.add(el);
            }
        }
        return els;
    }
    
    protected ArrayList<Element> getElementsOfValue(ArrayList<Element> children, String attribute, String attributeValue)
    {
        ArrayList<Element> els = new ArrayList<Element>();
        Iterator<Element> iter = children.iterator();
        while(iter.hasNext()) {
            Element el = iter.next();
            if(el.getAttribute(attribute).equals(attributeValue)) {
                els.add(el);
            }
        }
        return els;
    }
    
    protected Element getElement(Element element, String tagName, String attribute, String attributeValue)
    {
         ArrayList<Element> els = getChildren(element);
         els = getElementsOfTag(els, tagName);
         els = getElementsOfValue(els, attribute, attributeValue);
         if(els.isEmpty()) {
            return null;
         }
         return els.get(0);
    }
    
    protected Element[] getElementList(Element element, String tagName, String attribute, String attributeValue)
    {
         ArrayList<Element> els = getChildren(element);
         els = getElementsOfTag(els, tagName);
         els = getElementsOfValue(els, attribute, attributeValue);
         return (Element[]) els.toArray();
    }
    
    protected void readXML(File file) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        root = doc.getDocumentElement();
    }
    
    public void writeOutFile() throws Exception
    {
        if(file.exists()) {
            file.delete();
        }
        file.createNewFile();
        writeFile();
    }
    
    public void writeFile() throws Exception  
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        PrintWriter writer = new PrintWriter(new FileOutputStream(file));
        writer.println(xmlString);
        writer.flush();  
    }

}
