package com.choi.wenda.controller;

import com.choi.wenda.model.*;
import com.choi.wenda.service.CommentService;
import com.choi.wenda.service.FollowService;
import com.choi.wenda.service.QuestionService;
import com.choi.wenda.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;
    @Autowired
    private FollowService followService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = {"/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model){
        model.addAttribute("vos",getQuestions(0,0,10));
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET,RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId){
        model.addAttribute("vos",getQuestions(userId,0,10));
        User user = userService.getUser(userId);
        ViewObject viewObject = new ViewObject();
        viewObject.set("user",user);
        viewObject.set("commentCount",commentService.getUserCommentCount(userId));
        viewObject.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,userId));
        viewObject.set("followeeCount",followService.getFolloweeCount(userId,EntityType.ENTITY_USER));
        if(hostHolder.getUser() != null){
            viewObject.set("followed",followService.isFollower(hostHolder.getUser().getId(),EntityType.ENTITY_USER,userId));
        }else{
            viewObject.set("followed",false);
        }
        model.addAttribute("profileUser",viewObject);

        return "profile";
    }

    /**
     *获取指定用户或者全部用户的问题
     */
    private List<ViewObject> getQuestions(int userId,int offset,int limit){
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> viewObjects = new ArrayList<>();
        for(Question question : questionList){
            ViewObject viewObject = new ViewObject();
            if(question.getContent().length() > 100){
                question.setContent(question.getContent().substring(0,50));
            }
            viewObject.set("question",question);
            viewObject.set("followCount",followService.getFollowerCount(EntityType.ENTITY_QUESTION,question.getId()));
            viewObject.set("user",userService.getUser(question.getUserId()));
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }
}
