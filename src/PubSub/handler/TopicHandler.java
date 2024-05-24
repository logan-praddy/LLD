package PubSub.handler;

import PubSub.model.Topic;
import PubSub.model.TopicSubscriber;

import java.util.HashMap;
import java.util.Map;
// each worker is there for one subscriber for a particular topic
public class TopicHandler {
    private final Topic topic;
    private final Map<String, SubscriberWorker> subscriberWorkers; // key = subscriberId, value = SubscriberWorker object

    public TopicHandler(final Topic topic){
        this.topic = topic;
        this.subscriberWorkers = new HashMap<>();
    }

    public void publish(){
        System.out.println("publish test 1");
        for(TopicSubscriber topicSubscriber : topic.getSubscribers()){
            startSubscriberWorker(topicSubscriber);
        }
    }

    public void startSubscriberWorker(final TopicSubscriber topicSubscriber){
        final String subscriberId = topicSubscriber.getSubscriber().getId();
        //lazy creation
        if(!subscriberWorkers.containsKey(subscriberId)){
            final SubscriberWorker subscriberWorker = new SubscriberWorker(topic, topicSubscriber);
            subscriberWorkers.put(subscriberId, subscriberWorker);
            new Thread(subscriberWorker).start();
        }
        final SubscriberWorker subscriberWorker = subscriberWorkers.get(subscriberId);
        subscriberWorker.wakeUpIfNeeded();

    }
}
