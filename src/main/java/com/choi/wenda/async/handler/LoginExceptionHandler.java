package com.choi.wenda.async.handler;

import com.choi.wenda.async.EventHandler;
import com.choi.wenda.async.EventModel;
import com.choi.wenda.async.EventType;
import com.choi.wenda.utils.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LoginExceptionHandler implements EventHandler {

    @Autowired
    private MailSender mailSender;
    @Override
    public void doHandle(EventModel eventModel) {
        Map<String,Object> map = new HashMap<>();
        map.put("username",eventModel.getExt("username"));
        mailSender.sendWithHTMLTemplate(eventModel.getExt("email"),"登录IP异常","mails/login_exception.html",map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
