package PubSub.publicInterface;

import PubSub.handler.TopicHandler;
import PubSub.model.Message;
import PubSub.model.Topic;
import PubSub.model.TopicSubscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Queue {
    private final Map<String, TopicHandler> topicProessors; // key = topicId, value = TopicHandler object

    public Queue() {
        this.topicProessors = new HashMap<>();
    }

    public Topic createTopic(String topicName) {
        final Topic topic = new Topic(topicName, UUID.randomUUID().toString());
        TopicHandler topicHandler = new TopicHandler(topic);
        topicProessors.put(topic.getTopicId(), topicHandler);
        System.out.println("Created topic : "+topic.getTopicName());
        return topic;
    }

    public void subscribe(ISubscriber subscriber, Topic topic){
        topic.addSubscriber(new TopicSubscriber(subscriber));
        System.out.println(subscriber.getId() + " subscribed to topic: "+ topic.getTopicName());
    }

    public void publish(Topic topic, Message message){
        topic.addMessage(message);
        System.out.println(message.getMsg() + " publisehd to topic "+ topic.getTopicName());
        new Thread(()->topicProessors.get(topic.getTopicId()).publish()).start();
    }

    public void resetOffset(Topic topic, ISubscriber subscriber, Integer newOffset){
        for(TopicSubscriber topicSubscriber : topic.getSubscribers()){
            if(topicSubscriber.getSubscriber().equals(subscriber)){
                topicSubscriber.getOffset().set(newOffset);
                System.out.println(topicSubscriber.getSubscriber().getId() + " offset reset to: "+ newOffset);
                new Thread(() -> topicProessors.get(topic.getTopicId()).publish()).start();
            }
        }
    }

}
