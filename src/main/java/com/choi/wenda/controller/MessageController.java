package com.choi.wenda.controller;

import com.choi.wenda.model.HostHolder;
import com.choi.wenda.model.Message;
import com.choi.wenda.model.User;
import com.choi.wenda.model.ViewObject;
import com.choi.wenda.service.MessageService;
import com.choi.wenda.service.UserService;
import com.choi.wenda.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @RequestMapping(path = {"/msg/addMessage"},method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content){
        try {
            if(hostHolder.getUser() == null){
                return WendaUtil.getJSONString(999,"未登录");
            }
            User user = userService.selectUserByName(toName);
            if(user == null){
                return WendaUtil.getJSONString(1,"您发送的用户不存在");
            }
            Message message = new Message();
            message.setContent(content);
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setCreatedDate(new Date());
            message.setConversationId(hostHolder.getUser().getId() < user.getId() ?
                    String.format("%d_%d", hostHolder.getUser().getId(), user.getId()) : String.format("%d_%d", user.getId(), hostHolder.getUser().getId()));
            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("增加站内信失败" + e.getMessage());
            return WendaUtil.getJSONString(1,"插入站内信失败");
        }
    }

    @RequestMapping(path = {"/msg/list"},method = {RequestMethod.GET})
    public String conversationList(Model model){
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationList(localUserId,0,10);
            for(Message message : conversationList){
                ViewObject viewObject = new ViewObject();
                viewObject.set("message",message);
                int targetId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
                User user = userService.getUser(targetId);
                viewObject.set("user",user);
                viewObject.set("unread",messageService.getConversationUnreadCount(targetId,message.getConversationId()));
                conversations.add(viewObject);
            }
            model.addAttribute("conversations",conversations);
        }catch (Exception e){
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"},method = {RequestMethod.GET})
    public String conversationDetail(Model model,@RequestParam("conversationId") String conversationId){
        try {
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for(Message message : conversationList){
                ViewObject viewObject = new ViewObject();
                viewObject.set("message",message);
                User user = userService.getUser(message.getFromId());
                if(user == null){
                    continue;
                }
                viewObject.set("headUrl",user.getHeadUrl());
                viewObject.set("userId",user.getId());
                messages.add(viewObject);
            }
            model.addAttribute("messages",messages);
        }catch (Exception e){
            logger.error("获取详情信息失败" + e.getMessage());
        }
        return "letterDetail";
    }

}
