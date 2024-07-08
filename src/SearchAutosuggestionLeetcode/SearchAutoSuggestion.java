package SearchAutosuggestionLeetcode;

import java.util.List;
import java.util.*;

interface Searchable {
    List<List<String>> getSuggestions(String searchWord);
}

class ProductSuggestionSystem implements Searchable {
    private final ProductRepository productRepository;

    public ProductSuggestionSystem(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<List<String>> getSuggestions(String searchWord) {
        List<List<String>> suggestions = new ArrayList<>();
        String[] products = productRepository.getProducts();
        for (int i = 0; i < searchWord.length(); i++) {
            String prefix = searchWord.substring(0, i + 1);
            int index = productRepository.binarySearch(products, prefix);
            List<String> suggestionList = new ArrayList<>();
            for (int j = index; j < products.length && suggestionList.size() < 3; ++j) {
                if (products[j].startsWith(prefix)) {
                    suggestionList.add(products[j]);
                    continue;
                }
                break;
            }
            suggestions.add(suggestionList);
        }
        return suggestions;
    }
}

 class ProductRepository {
    private String[] products;

    public ProductRepository(String[] products) {
        this.products = products;
        Arrays.sort(this.products);
    }

    public String[] getProducts() {
        return products;
    }

    public int binarySearch(String[] products, String prefix) {
        int left = 0, right = products.length - 1, mid;
        while (left <= right) {
            mid = (left + right) / 2;
            if (products[mid].compareTo(prefix) >= 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return right + 1;
    }
}

public class SearchAutoSuggestion {
    public static void main(String[] args) {
        String[] products = {"mobile", "mouse", "moneypot", "monitor", "mousepad"};
        ProductRepository productRepository = new ProductRepository(products);
        Searchable productSuggestionSystem = new ProductSuggestionSystem(productRepository);

        String searchWord = "mouse";
        List<List<String>> suggestions = productSuggestionSystem.getSuggestions(searchWord);

        for (List<String> suggestion : suggestions) {
            System.out.println(suggestion);
        }
    }
}



