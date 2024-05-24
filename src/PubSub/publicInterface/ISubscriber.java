package PubSub.publicInterface;

import PubSub.model.Message;

public interface ISubscriber {
    String getId();
    void consume(Message message) throws InterruptedException;
}
