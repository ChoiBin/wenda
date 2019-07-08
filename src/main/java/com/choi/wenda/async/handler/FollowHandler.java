package com.choi.wenda.async.handler;

import com.choi.wenda.async.EventHandler;
import com.choi.wenda.async.EventModel;
import com.choi.wenda.async.EventType;
import com.choi.wenda.model.EntityType;
import com.choi.wenda.model.Message;
import com.choi.wenda.model.User;
import com.choi.wenda.service.MessageService;
import com.choi.wenda.service.UserService;
import com.choi.wenda.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class FollowHandler implements EventHandler {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(eventModel.getActorId());

        if (eventModel.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("用户" + user.getName()
                    + "关注了你的问题,http://127.0.0.1:8080/question/" + eventModel.getEntityId());
        } else if (eventModel.getEntityType() == EntityType.ENTITY_USER) {
            message.setContent("用户" + user.getName()
                    + "关注了你,http://127.0.0.1:8080/user/" + eventModel.getActorId());
        }
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
