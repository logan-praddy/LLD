package LRU;

import java.util.HashMap;
import java.util.Map;

class Node{
    Node prev;
    Node next;
    int key;
    int value;
    Node(int _key, int _value){
        this.key = _key;
        this.value = _value;
    }
}
class LRUCache{
    Node head = new Node(0,0);
    Node tail = new Node(0,0);
    Map<Integer, Node> cache = new HashMap<>();
    int capacity ;
    public LRUCache(int capacity){
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }
    public int get(int key){
        if(cache.containsKey(key)){
            Node node = cache.get(key);
            remove(node);
            insert(node);
            return node.value;
        }
        else{
            return -1;
        }
    }
    public void put(int key, int value){
        if(cache.containsKey(key)){
            remove(cache.get(key));
        }
        if(cache.size() == capacity){
            remove(tail.prev);
        }
        insert(new Node(key, value));
    }
    private void remove(Node node){
        cache.remove(node.key);
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    private void insert(Node node){
        cache.put(node.key, node);
        node.next = head.next;
        node.next.prev = node;
        head.next = node;
        node.prev = head;
    }
}

public class LRU {

    public static void main(String [] args) {
        LRUCache lruCache = new LRUCache(2);
        lruCache.put(1, 1);
        lruCache.put(2,2);
        int x = lruCache.get(1);
        System.out.println(x);
        lruCache.put(3,3);
        int y = lruCache.get(2);
        System.out.println(y);
        lruCache.put(4,4);
        int z = lruCache.get(1);
        System.out.println(z);
        int r = lruCache.get(3);
        System.out.println(r);
        int w = lruCache.get(4);
        System.out.println(w);

        //output : 1,-1,-1,3,4

    }

}
