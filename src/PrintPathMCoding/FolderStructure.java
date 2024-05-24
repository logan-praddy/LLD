package PrintPathMCoding;

import java.util.*;

class Folder {
    int id;
    Map<Integer, Folder> subfolders;
    String name;

    public Folder(int id, String name) {
        this.id = id;
        this.subfolders = new HashMap<>();
        this.name = name;
    }

    public void addSubfolder(Folder subfolder) {
        subfolders.put(subfolder.id, subfolder);
    }

    public Folder getSubfolder(int id) {
        return subfolders.get(id);
    }
}

public class FolderStructure {
    private Map<Integer, Folder> folders;

    public FolderStructure() {
        this.folders = new HashMap<>();
    }

    public void addFolder(Folder folder) {
        folders.put(folder.id, folder);
    }

    public String printPath(int index) {
        Folder folder = folders.get(index);
        if (folder == null) return "";

        Stack<String> stack = new Stack<>();
        while (folder.id != 0) {
            stack.push(folder.name + "/");
            folder = folders.get(folder.id);
            if (folder == null) return "";
        }
        StringBuilder path = new StringBuilder();
        while (!stack.isEmpty()) {
            path.append(stack.pop());
        }
        return path.toString();
    }

    public static void main(String[] args) {
        FolderStructure folderStructure = new FolderStructure();

        folderStructure.addFolder(new Folder(0, "root"));
        folderStructure.addFolder(new Folder(7, "abc"));
        folderStructure.addFolder(new Folder(3, "xyz"));
        folderStructure.addFolder(new Folder(9, "pqr"));
        folderStructure.addFolder(new Folder(8, "def"));
        folderStructure.addFolder(new Folder(7, "ijk"));
        folderStructure.addFolder(new Folder(9, "lmn"));

        System.out.println(folderStructure.printPath(9)); // Output: /abc/ijk/lmn
        System.out.println(folderStructure.printPath(8)); // Output: ""
    }
}

