package com.choi.wenda.controller;

import com.choi.wenda.async.EventModel;
import com.choi.wenda.async.EventProducer;
import com.choi.wenda.async.EventType;
import com.choi.wenda.model.*;
import com.choi.wenda.service.CommentService;
import com.choi.wenda.service.FollowService;
import com.choi.wenda.service.QuestionService;
import com.choi.wenda.service.UserService;
import com.choi.wenda.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowerController {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private FollowService followService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;


    @RequestMapping(path = {"/followUser"},method = {RequestMethod.POST})
    @ResponseBody
    public  String followUser(@RequestParam("userId") int userId){
        if(hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }
        boolean result = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
        .setActorId(hostHolder.getUser().getId())
        .setEntityId(userId)
        .setEntityType(EntityType.ENTITY_USER)
        .setEntityOwnerId(userId));

        //返回关注的人数
        return WendaUtil.getJSONString(result ? 0 : 1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));

    }

    @RequestMapping(path = {"/unfollowUser"},method = {RequestMethod.POST})
    @ResponseBody
    public  String unfollowUser(@RequestParam("userId") int userId){
        if(hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }
        boolean result = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
        .setActorId(hostHolder.getUser().getId())
        .setEntityId(userId)
        .setEntityType(EntityType.ENTITY_USER)
        .setEntityOwnerId(userId));

        //返回关注的人数
        return WendaUtil.getJSONString(result ? 0 : 1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }

    @RequestMapping(path = {"/followQuestion"},method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public  String followQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }
        Question question = questionService.selectById(questionId);
        if(question == null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }
        boolean result = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityOwnerId(question.getUserId()));

        Map<String,Object> info = new HashMap<>();
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        info.put("name",hostHolder.getUser().getName());
        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return WendaUtil.getJSONString(result ? 0 : 1,info);
    }

    @RequestMapping(path = {"/followQuestion/{questionId"},method = {RequestMethod.GET})
    public  String followQuestionByPath(@PathVariable("questionId") int questionId){
        if(hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }
        Question question = questionService.selectById(questionId);
        if(question == null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }
        boolean result = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityOwnerId(question.getUserId()));

        Map<String,Object> info = new HashMap<>();
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        info.put("name",hostHolder.getUser().getName());
        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return "/";
    }

    @RequestMapping(path = {"/unfollowQuestion"},method = {RequestMethod.POST})
    @ResponseBody
    public  String unfollowQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }
        Question question = questionService.selectById(questionId);
        if(question == null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }
        boolean result = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityOwnerId(question.getUserId()));

        Map<String,Object> info = new HashMap<>();
        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return WendaUtil.getJSONString(result ? 0 : 1,info);
    }

    /**
     * 获取指定用户的关注者
     * @param model
     * @param userId
     * @return
     */
    @RequestMapping(path = {"/user/{uid}/followees"},method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid")int userId){
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);

        if(hostHolder.getUser() != null){
            model.addAttribute("followees",getUsersInfo(hostHolder.getUser().getId(),followeeIds));
        }else {
            model.addAttribute("followees",getUsersInfo(0,followeeIds));

        }
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }

    /**
     * 获取指定用户的粉丝
     * @param model
     * @param userId
     * @return
     */
    @RequestMapping(path = {"/user/{uid}/followers"},method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid")int userId){
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);

        if(hostHolder.getUser() != null){
            model.addAttribute("followers",getUsersInfo(hostHolder.getUser().getId(),followerIds));
        }else {
            model.addAttribute("followers",getUsersInfo(0,followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> followeeIds) {
        List<ViewObject> userInfos = new ArrayList<>();
        for(Integer uid : followeeIds){
            User user = userService.getUser(uid);
            if(user == null){
                continue;
            }
            ViewObject viewObject = new ViewObject();
            viewObject.set("user",user);
            viewObject.set("commentCount",commentService.getUserCommentCount(uid));
            viewObject.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,uid));
            viewObject.set("followeeCount",followService.getFolloweeCount(uid,EntityType.ENTITY_USER));
            if(localUserId != 0){
                viewObject.set("followed",followService.isFollower(localUserId,EntityType.ENTITY_USER,uid));
            }else{
                viewObject.set("followed",false);
            }
            userInfos.add(viewObject);
        }
        return userInfos;
    }


}
