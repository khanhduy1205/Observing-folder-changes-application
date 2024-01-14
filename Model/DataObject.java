package Model;

import java.io.Serializable;

public class DataObject implements Serializable {
    private String name, info, message, path;

    private DirectoryPath directoryPath;

    public DataObject(String name, String info, String message, String path, DirectoryPath directoryPath) {
        this.name = name;
        this.info = info;
        this.message = message;
        this.path = path;
        this.directoryPath = directoryPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DirectoryPath getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(DirectoryPath directoryPath) {
        this.directoryPath = directoryPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
