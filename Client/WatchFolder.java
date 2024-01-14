package Client;
import Model.*;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WatchFolder implements Runnable{
    public static WatchService watchService;
    private ClientChat clientChat;
    public WatchFolder(ClientChat clientChat) {
        this.clientChat = clientChat;
        watchService = null;
    }

    @Override
    public void run() {
        try {
            System.out.println("Start watching folder");

            watchService = FileSystems.getDefault().newWatchService();

            Path path = Path.of(clientChat.getDefaultPath());

            WatchKey watchKey = path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {
                for (WatchEvent <?> event : watchKey.pollEvents()) {
                    WatchEvent.Kind<?> eventKind = event.kind();
                    WatchEvent <Path> pathEvent = (WatchEvent<Path>) event;
                    Path filename = pathEvent.context();

                    String datetime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());

                    if (eventKind == StandardWatchEventKinds.ENTRY_CREATE) {
                        ClientUI.addObjectToTable(clientChat.getDefaultPath(), datetime,
                                "Create", clientChat.getName(), filename + " has been created");

                        DataObject dataObject = new DataObject(clientChat.getName(), "create", filename
                                + " has been created ", clientChat.getDefaultPath(), ClientChat.directoryPath);
                        ClientChat.sendMessage(dataObject);
                    }
                    if (eventKind == StandardWatchEventKinds.ENTRY_MODIFY) {

                        DataObject dataObject = new DataObject(clientChat.getName(), "modify", filename
                                + " has been modified", clientChat.getDefaultPath(), ClientChat.directoryPath);
                        ClientChat.sendMessage(dataObject);

                        ClientUI.addObjectToTable(clientChat.getDefaultPath(), datetime,
                                "Modify", clientChat.getName(), filename + " has been modified");
                    }
                    if (eventKind == StandardWatchEventKinds.ENTRY_DELETE) {

                        DataObject dataObject = new DataObject(clientChat.getName(), "delete", filename
                                + " has been deleted", clientChat.getDefaultPath(), ClientChat.directoryPath);
                        ClientChat.sendMessage(dataObject);

                        ClientUI.addObjectToTable(clientChat.getDefaultPath(), datetime,
                                "Delete", clientChat.getName(), filename + " has been deleted");
                    }
                }
                boolean isReset = watchKey.reset();
                if (!isReset) {
                    break;
                }
            }
        } catch (IOException err) {
            err.getStackTrace();
        }
    }
}
