package Model;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.Serializable;

public class DirectoryPath implements Serializable {
    private File[] rootDirectories;
    private File[] filesList;
    private DefaultMutableTreeNode directoryNode;

    public DirectoryPath() {
        this.rootDirectories = File.listRoots();
    }

    public File[] getRootDirectories() {
        return rootDirectories;
    }

    public void setRootDirectories(File[] rootDirectories) {
        this.rootDirectories = rootDirectories;
    }

    public File[] getFilesList() {
        return filesList;
    }

    public void setFilesList(File[] filesList) {
        this.filesList = filesList;
    }

    public DefaultMutableTreeNode getDirectoryNode() {
        return directoryNode;
    }

    public void setDirectoryNode(DefaultMutableTreeNode directoryNode) {
        this.directoryNode = directoryNode;
    }

}
