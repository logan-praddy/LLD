package InMemoryFileSystem;

import java.util.*;

class Trie {
    String name;
    boolean isFile;
    StringBuilder content = new StringBuilder();
    Map<String, Trie> children = new HashMap<>();

    Trie insert(String path, boolean isFile) {
        Trie node = this;
        String[] ps = path.split("/");
        for (int i = 1; i < ps.length; ++i) {
            String p = ps[i];
            if (!node.children.containsKey(p)) {
                node.children.put(p, new Trie());
            }
            node = node.children.get(p);
        }
        node.isFile = isFile;
        if (isFile) {
            node.name = ps[ps.length - 1];
        }
        return node;
    }

    Trie search(String path) {
        Trie node = this;
        String[] ps = path.split("/");
        for (int i = 1; i < ps.length; ++i) {
            String p = ps[i];
            if (!node.children.containsKey(p)) {
                return null;
            }
            node = node.children.get(p);
        }
        return node;
    }

    // Additional method to delete a file or directory
    void delete(String path) {
        Trie node = this;
        String[] ps = path.split("/");
        for (int i = 1; i < ps.length - 1; ++i) {
            String p = ps[i];
            if (!node.children.containsKey(p)) {
                return;
            }
            node = node.children.get(p);
        }
        node.children.remove(ps[ps.length - 1]);
    }

    // Additional method to move a file or directory
    boolean move(String source, String dest) {
        Trie sourceNode = search(source);
        Trie destNode = search(dest);
        if (sourceNode == null || destNode == null) {
            return false;
        }
        destNode.children.put(sourceNode.name, sourceNode);
        sourceNode = null;
        return true;
    }
}

public class FileSystem {
    private Trie root = new Trie();

    public FileSystem() {
    }

    public List<String> ls(String path) {
        List<String> ans = new ArrayList<>();
        Trie node = root.search(path);
        if (node == null) {
            return ans;
        }
        if (node.isFile) {
            ans.add(node.name);
            return ans;
        }
        for (String v : node.children.keySet()) {
            ans.add(v);
        }
        Collections.sort(ans);
        return ans;
    }

    public void mkdir(String path) {
        root.insert(path, false);
    }

    public void addContentToFile(String filePath, String content) {
        Trie node = root.insert(filePath, true);
        node.content.append(content);
    }

    public String readContentFromFile(String filePath) {
        Trie node = root.search(filePath);
        return node.content.toString();
    }

    public void delete(String path) {
        root.delete(path);
    }

    // Additional method to move a file or directory
    public boolean move(String source, String dest) {
        return root.move(source, dest);
    }

    public static void main(String[] args) {
    FileSystem fileSystem = new FileSystem();
        fileSystem.mkdir("/folder1");
        fileSystem.mkdir("/folder2");
        fileSystem.addContentToFile("/folder1/file1","Content of file1");
        fileSystem.addContentToFile("/folder2/file2","Content of file2");

        System.out.println("List of files in root: "+fileSystem.ls("/"));
        System.out.println("List of files in folder1: "+fileSystem.ls("/folder1"));
        System.out.println("List of files in folder2: "+fileSystem.ls("/folder2"));

        System.out.println("Content of /folder1/file1: "+fileSystem.readContentFromFile("/folder1/file1"));

        fileSystem.move("/folder1/file1","/folder2");
        System.out.println("List of files in folder1 after moving file1: "+fileSystem.ls("/folder1"));
        System.out.println("List of files in folder2 after moving file1: "+fileSystem.ls("/folder2"));

        fileSystem.delete("/folder1");
        System.out.println("List of files in root after deleting folder1: "+fileSystem.ls("/"));

    }
}

/**
 * Your FileSystem object will be instantiated and called as such:
 * FileSystem obj = new FileSystem();
 * List<String> param_1 = obj.ls(path);
 * obj.mkdir(path);
 * obj.addContentToFile(filePath,content);
 * String param_4 = obj.readContentFromFile(filePath);
 */
