import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JTree;
import javax.swing.tree.*;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class FileTree extends JTree //implements TreeSelectionListener
{
    JTree tree;
    private Share share;
    private DefaultTreeModel model;
    private HashMap<String, DefaultMutableTreeNode> hMap;
 
    public FileTree(String root, Share share)
    {
        super(new DefaultMutableTreeNode(root));
        tree = this;
        this.getSelectionModel().setSelectionMode
            (TreeSelectionModel.SINGLE_TREE_SELECTION);
        //this.addTreeSelectionListener(this);
        this.addMouseListener(ml);
        this.model = (DefaultTreeModel) this.getModel();
        this.share = share; 
        this.hMap = new HashMap<String, DefaultMutableTreeNode>();
      
    }
    
    public void addUsers(User[] users)
    {
        for(int i = 0; i < users.length; i++) {
            addUser(users[i]);
        }
    }
    
    public void addUser(User user)
    {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getModel().getRoot();
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(user);
        root.add(node);
        hMap.put(user.getAlias(), node);
        this.updateUI();
        this.revalidate();
    }
    
    public void removeUser(User user)
    {
        DefaultMutableTreeNode node = hMap.get(user.getAlias());   
        getNewTreeSelect(node, user);
        node.removeAllChildren();
        model.removeNodeFromParent(node);
        hMap.remove(user.getAlias());
        this.updateUI();
        this.revalidate();
    }
    
    public void getNewTreeSelect(DefaultMutableTreeNode node, User user)
    {
        DefaultMutableTreeNode newSelect, selectedNode;
        selectedNode = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        Object object = selectedNode.getUserObject();
        if(object instanceof SharedFile) {
            SharedFile sf = (SharedFile) object;
            if(sf.getUser() == user) {
                selectedNode = getUserParent(node, user);
            }
            
        }
        if(selectedNode == node) {
            newSelect = node.getPreviousSibling();
            if(newSelect == null) {
                newSelect = node.getNextSibling();
            }
            if(newSelect == null) {
                newSelect = (DefaultMutableTreeNode) node.getParent();
            }
            TreeNode[] nodes = model.getPathToRoot(newSelect);
            TreePath path = new TreePath(nodes);
            this.setSelectionPath(path);
           
        }
    }
    
    
    private DefaultMutableTreeNode getUserParent(DefaultMutableTreeNode node, User user)
    {
        if(node.getUserObject() == user) {
            return node;
        }
        getUserParent((DefaultMutableTreeNode) node.getParent(), user);
        return null;
    }
    
    /*
    public void valueChanged(TreeSelectionEvent e)
    {
       try {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           this.getLastSelectedPathComponent();
        if(node == null) {
            return;
        }
        if(node.getUserObject() instanceof User) {
            node.removeAllChildren();
            populateTree(node);
        }
        else if(node.getUserObject() instanceof SharedFile) {
            System.out.println("wwwwwwwwww");
            getFile((SharedFile) node.getUserObject());
        }
        
        
    }
    catch (Exception excep) {
        System.err.println(excep);
    }
          
        
   }
   */
  
  public void nodeDoubleClick(DefaultMutableTreeNode node)
    {
       try {
        if(node == null) {
            return;
        }
        if(node.getUserObject() instanceof User) {
            node.removeAllChildren();
            populateTree(node);
        }
        else if(node.getUserObject() instanceof SharedFile) {
            getFile((SharedFile) node.getUserObject());
        }
        
        
    }
    catch (Exception excep) {
        System.err.println(excep);
    }
          
        
   }
   
   private void getFile(SharedFile file) throws Exception
   {
       if(file.isDir()) {
           getDir(file);
       }
       share.getFile(file);
   }
   
   private void getDir(SharedFile file) throws Exception
   {
       FileList list = file.getUser().getFileList();
       SharedFile[] files = list.getChildFiles(file);
       for(int i = 0; i < files.length; i++) {
           getFile(files[i]);
       }
   }
   
   private void populateTree(DefaultMutableTreeNode node) throws Exception
   {
       User user = (User) node.getUserObject();
       share.getUserFileList(user);
       FileList fileList = user.getFileList();
       SharedFile [] files = fileList.getTopLevelFiles();
       for(int i = 0; i < files.length; i++) {
           DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(files[i]);
           node.add(newNode);
           fillTreeBranch(fileList, newNode, user);
       }
       
   }
    
    
    private void fillTreeBranch(FileList list, DefaultMutableTreeNode rootNode, User user)
    {
        SharedFile [] files = list.getChildFiles((SharedFile) rootNode.getUserObject());
        for(int i = 0; i < files.length; i++) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(files[i]);
            rootNode.add(newNode);
            fillTreeBranch(list, newNode, user);
        }
        this.revalidate();
        return;
    }
    
    
     MouseListener ml = new MouseAdapter() 
     {
        public void mousePressed(MouseEvent e) {
           int selRow = tree.getRowForLocation(e.getX(), e.getY());
           TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
           if(e.getClickCount() == 2) {
               nodeDoubleClick((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
           }
        }
     };
     
    
}
