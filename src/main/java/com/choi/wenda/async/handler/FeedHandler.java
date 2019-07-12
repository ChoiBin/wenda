package com.choi.wenda.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.choi.wenda.async.EventHandler;
import com.choi.wenda.async.EventModel;
import com.choi.wenda.async.EventType;
import com.choi.wenda.model.*;
import com.choi.wenda.redis.JedisAdapter;
import com.choi.wenda.redis.RedisKey;
import com.choi.wenda.service.FeedService;
import com.choi.wenda.service.FollowService;
import com.choi.wenda.service.QuestionService;
import com.choi.wenda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeedHandler implements EventHandler {
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    QuestionService questionService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public void doHandle(EventModel eventModel) {
        //方便测试用
        Random random = new Random();
        eventModel.setActorId(1+random.nextInt(10));
//        System.out.println(hostHolder.getUser().getId());
//        eventModel.setActorId(hostHolder.getUser().getId());

        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(eventModel.getType().getValue());
        feed.setUserId(eventModel.getActorId());
        feed.setData(buildFeedData(eventModel));
        if(feed.getData() == null){
            return;
        }
        feedService.addFeed(feed);

        // 获得所有粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, eventModel.getActorId(), Integer.MAX_VALUE);
        // 系统队列
        followers.add(0);
        // 给所有粉丝推事件
        for (int follower : followers) {
            String timelineKey = RedisKey.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
            // 限制最长长度，如果timelineKey的长度过大，就删除后面的新鲜事
        }
    }

    private String buildFeedData(EventModel eventModel) {
        Map<String,String> map = new HashMap<>();
        User actor = userService.getUser(eventModel.getActorId());
        if(actor == null){
            return null;
        }
        map.put("userId",String.valueOf(actor.getId()));
        map.put("userHead",actor.getHeadUrl());
        map.put("userName",actor.getName());

        if(eventModel.getType() == EventType.COMMENT ||
                (eventModel.getType() == EventType.FOLLOW && eventModel.getEntityType() == EntityType.ENTITY_QUESTION)){
            Question question = questionService.selectById(eventModel.getEntityId());
            if(question == null){
                return null;
            }
            map.put("questionId",String.valueOf(question.getId()));
            map.put("questionTitle",question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.FOLLOW,EventType.COMMENT});
    }
}
