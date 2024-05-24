package PubSub.handler;

import PubSub.model.Message;
import PubSub.model.Topic;
import PubSub.model.TopicSubscriber;

public class SubscriberWorker implements Runnable {

    private final Topic topic;
    private final TopicSubscriber topicSubscriber;

    public SubscriberWorker(final Topic topic, final TopicSubscriber topicSubscriber) {
        this.topic = topic;
        this.topicSubscriber = topicSubscriber;
    }

    @Override
    public void run() {
        synchronized (topicSubscriber) {
            do {
                int currOffset = topicSubscriber.getOffset().get();
                while(currOffset >= topic.getMessages().size()) {
                    try {
                        topicSubscriber.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try{
                        Message message = topic.getMessages().get(currOffset);
                        topicSubscriber.getSubscriber().consume(message);
                        topicSubscriber.getOffset().compareAndSet(currOffset, currOffset+1);
                } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                }

            } while (true);
        }
    }

    synchronized public void wakeUpIfNeeded(){
        synchronized (topicSubscriber) {
            topicSubscriber.notify();
        }
    }
}
