package QueryOnRidesData;

import java.util.*;

class Event {
    public String user;
    public String city;
    public String eventType;
    public long timestamp;

    public Event(String user, String city, String eventType, long timestamp) {
        this.user = user;
        this.city = city;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }
}

class SegmentTree {
    private int n;
    private int[] tree;

    public SegmentTree(int n) {
        this.n = n;
        this.tree = new int[2 * n];
    }

    public void build(int[] arr, int start, int end) {
        if (start == end) {
            tree[start + n] = arr[start];
            return;
        }
        int mid = (start + end) / 2;
        build(arr, start, mid);
        build(arr, mid + 1, end);
        tree[start + n] = tree[2 * start + 1 + n] + tree[2 * start + 2 + n];
    }

    public void update(int i, int val) {
        i += n;
        tree[i] = val;
        while (i > 1) {
            i /= 2;
            tree[i] = tree[2 * i] + tree[2 * i + 1];
        }
    }

    public int query(int l, int r) {
        l += n;
        r += n;
        int res = 0;
        while (l <= r) {
            if (l % 2 == 0) {
                res += tree[l];
                l /= 2;
            }
            if (r % 2 == 1) {
                res += tree[r];
                r /= 2;
            }
            l /= 2;
            r /= 2;
        }
        return res;
    }
}

public class RideRequestCount {

    public static void main(String[] args) {
        // Sample events
        List<Event> events = new ArrayList<>();
        events.add(new Event("user1", "Blgr", "RIDE_REQUESTED", 1));
        events.add(new Event("user2", "Blgr", "RIDE_REQUESTED", 3));
        events.add(new Event("user1", "Blgr", "RIDE_CANCELLED", 2));
        events.add(new Event("user1", "Blgr", "COMPLETED", 4));

        // Sort events by timestamp
        Collections.sort(events, (a, b) -> Long.compare(a.timestamp, b.timestamp));

        // Create a map to store city-wise event counts using SegmentTree
        Map<String, SegmentTree> cityCounts = new HashMap<>();
        for (Event event : events) {
            if (!cityCounts.containsKey(event.city)) {
                cityCounts.put(event.city, new SegmentTree(100000)); // Assuming max timestamp is 100000
            }
            SegmentTree tree = cityCounts.get(event.city);
            if (event.eventType.equals("RIDE_REQUESTED")) {
                tree.update((int) event.timestamp, 1);
            } else if (event.eventType.equals("RIDE_CANCELLED") || event.eventType.equals("COMPLETED")) {
                tree.update((int) event.timestamp, -1);
            }
        }

        // **Get the number of RIDE_REQUESTED events in Blgr from 1 to 5:**
        int numRequests = cityCounts.get("Blgr").query(1, 5);
        System.out.println("Number of RIDE_REQUESTED events in Blgr from 1 to 5: " + numRequests);

        // **Get the number of COMPLETED events in Blgr from 6 to 8:**
        int numCompleted = cityCounts.get("Blgr").query(6, 8);
        System.out.println("Number of COMPLETED events in Blgr from 6 to 8: " + numCompleted);
    }
}

