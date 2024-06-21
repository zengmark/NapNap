package com.napnap.config.handler;

import com.napnap.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
public class MessagePoller {

    @Resource
    private MessageRepository messageRepository;

    @Resource
    private MessageWebSocketHandler webSocketHandler;

    private Date lastCheckTime = new Date();

    @Scheduled(fixedRate = 5000)
    public void checkForNewMessages() {
//        List<Message> newMessages = messageRepository.findNewMessages(lastCheckTime);
//        lastCheckTime = new Date();
//        System.out.println("轮询");
//        for (Message message : newMessages) {
//            webSocketHandler.sendMessageToAll("New message: " + message.toString());
//        }
    }
}
