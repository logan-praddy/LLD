package SearchAutosuggestionLeetcode;

import java.util.ArrayList;
import java.util.List;

class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean endOfWord = false;
}
interface Trie {
    void insertWord(String word);
    List<String> searchWord(String prefix);
}

class TrieImpl implements Trie {
    private TrieNode root;

    public TrieImpl() {
        this.root = new TrieNode();
    }

    @Override
    public void insertWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
        }
        node.endOfWord = true;
    }

    @Override
    public List<String> searchWord(String prefix) {
        List<String> result = new ArrayList<>();
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            int index = c - 'a';
            if (node.children[index] == null) {
                return result;
            }
            node = node.children[index];
        }
        dfs(node, prefix, result);
        return result;
    }

    private void dfs(TrieNode node, String prefix, List<String> result) {
        if (result.size() == 3) {
            return;
        }
        if (node.endOfWord) {
            result.add(prefix);
        }

        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                dfs(node.children[i], prefix + (char)(i + 'a'), result);
            }
        }
    }
}



interface ProductSuggestionSystem1 {
    List<List<String>> suggestedProducts(String[] products, String searchWord);
}

class ProductSuggestionSystemImpl implements ProductSuggestionSystem1 {
    private Trie trie;

    public ProductSuggestionSystemImpl(Trie trie) {
        this.trie = trie;
    }

    @Override
    public List<List<String>> suggestedProducts(String[] products, String searchWord) {
        for (String product : products) {
            trie.insertWord(product);
        }

        List<List<String>> result = new ArrayList<>();
        StringBuilder prefix = new StringBuilder();
        for (char c : searchWord.toCharArray()) {
            prefix.append(c);
            result.add(trie.searchWord(prefix.toString()));
        }
        return result;
    }
}


public class TrieSolution {
    public static void main(String[] args) {
        String[] products = {"mobile", "mouse", "moneypot", "monitor", "mousepad"};
        String searchWord = "mouse";

        Trie trie = new TrieImpl();
        ProductSuggestionSystem1 productSuggestionSystem = new ProductSuggestionSystemImpl(trie);

        List<List<String>> suggestions = productSuggestionSystem.suggestedProducts(products, searchWord);

        for (List<String> suggestion : suggestions) {
            System.out.println(suggestion);
        }
    }
}
