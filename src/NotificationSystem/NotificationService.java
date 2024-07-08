package NotificationSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 class User {
    static NotificationService notificationService;
    private String name;
    private Integer id;
    User(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    public void registerForNotificationWhenStocksAreAvailable(Item item) {
        notificationService.register(item, this);
    }

    @Override
    public String toString() {
        return this.name;
    }
}

 class Item {
    static NotificationService notificationService;
    private Integer id, totalStock;
    private String name;
    public Item(String name, Integer id, Integer totalStock) {
        this.name = name;
        this.totalStock = totalStock;
        this.id = id;
    }
    public Boolean isAvailable() {
        return this.totalStock > 0;
    }
    public Integer addStock(int stock) {
        int lastStock = totalStock;
        totalStock += stock;
        if(lastStock == 0 && isAvailable()) notificationService.notifyUser(this);
        return totalStock;
    }
    public Integer removeStock(int stock) {
        if(stock > totalStock) return -1;
        totalStock -= stock;
        return totalStock;
    }

    // getters
    public Integer getId() {
        return id;
    }

    public Integer getTotalStock() {
        return totalStock;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

public class NotificationService {
    private Map<Integer, List<User>> notificationMap;
    NotificationService() {
        this.notificationMap = new HashMap<>();
    }
    public void register(Item item, User user) {
        if(item.getTotalStock() > 0) return;
        if(notificationMap.containsKey(item.getId())) {
            if(!notificationMap.get(item.getId()).contains(user))
                notificationMap.get(item.getId()).add(user);
            return;
        }
        List<User> userList = new ArrayList<>();
        userList.add(user);
        notificationMap.put(item.getId(), userList);
    }
    public void notifyUser(Item item) {
        if(!notificationMap.containsKey(item.getId())) return;
        for(User user: notificationMap.get(item.getId())) {
            sendNotification(user, item);
        }
    }

    private void sendNotification(User user, Item item) {
        System.out.println("Hi " + user + "! The item " + item + " is back in stock! Buy it now!");
    }

    public static void main(String[] args) {
        NotificationService notificationService = new NotificationService();
        Item.notificationService = notificationService;
        User.notificationService = notificationService;

        Item item1 = new Item("iPhone 13 Pro", 1, 20);
        Item item2 = new Item("S22+", 2, 2);
        Item item3 = new Item("Hair Dryer", 3, 0);
        User user1 = new User("Sameer", 1);
        User user2 = new User("Rahul", 2);
        User user3 = new User("Aprajita", 3);
        User user4 = new User("Mehul", 4);

        user1.registerForNotificationWhenStocksAreAvailable(item1);
        user1.registerForNotificationWhenStocksAreAvailable(item1);; // invalid registration. item already in stock.
        user2.registerForNotificationWhenStocksAreAvailable(item2); // invalid registration
        user1.registerForNotificationWhenStocksAreAvailable(item3); // valid registration
        user2.registerForNotificationWhenStocksAreAvailable(item3); // valid registration

        item3.addStock(3);

        item1.removeStock(20);
        user1.registerForNotificationWhenStocksAreAvailable(item1); // valid registration
        user3.registerForNotificationWhenStocksAreAvailable(item1); // valid registration
        item1.addStock(1);

        item2.removeStock(1);
        user4.registerForNotificationWhenStocksAreAvailable(item2); // invalid registration
        item2.removeStock(1);
        item2.addStock(1);
        item2.removeStock(1);
        user4.registerForNotificationWhenStocksAreAvailable(item2); // valid registration. stock is zero at this point
        item2.addStock(1);

        Item item4 = new Item("iPhone 14 Pro", 4, 20);
        item4.addStock(20);
        item4.removeStock(39);
        item4.removeStock(1);
        user3.registerForNotificationWhenStocksAreAvailable(item4); // valid
        item4.addStock(20);

        Item item5 = new Item("Boxing Gloves", 5, 0);
        user1.registerForNotificationWhenStocksAreAvailable(item5); // valid
        user1.registerForNotificationWhenStocksAreAvailable(item5); // invalid. user can't register for the same product more than once
        user3.registerForNotificationWhenStocksAreAvailable(item5);
        item5.addStock(1);
    }
}
