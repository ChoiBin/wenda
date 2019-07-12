package com.choi.wenda.service;

import com.choi.wenda.redis.JedisAdapter;
import com.choi.wenda.redis.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {

    @Autowired
    private JedisAdapter jedisAdapter;

    /**
     * 用户关注了某个实体,可以关注问题,关注用户,关注评论等任何实体
     * @return
     */
    public boolean follow(int userId,int entityType,int entityId){
        String followerKey = RedisKey.getFollowerKey(entityType,entityId);
        String followeeKey = RedisKey.getFolloweeKey(entityType,userId);

        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        //开启事务
        Transaction transaction = jedisAdapter.multi(jedis);
        //实体的粉丝增加当前用户
        transaction.zadd(followerKey,date.getTime(),String.valueOf(userId));
        //当前用户对该实体关注+1
        transaction.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        //执行事务
        List<Object> result = jedisAdapter.exec(transaction, jedis);
        return result.size() == 2 && (long)result.get(0) > 0 && (long)result.get(1) > 0;
    }

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean unfollow(int userId,int entityType,int entityId){
        String followerKey = RedisKey.getFollowerKey(entityType,entityId);
        String followeeKey = RedisKey.getFolloweeKey(entityType,userId);

        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        //开启事务
        Transaction transaction = jedisAdapter.multi(jedis);
        //实体的粉丝删除当前用户
        transaction.zrem(followerKey,String.valueOf(userId));
        //当前用户对该实体关注-1
        transaction.zrem(followeeKey,String.valueOf(entityId));
        //执行事务
        List<Object> result = jedisAdapter.exec(transaction, jedis);
        return result.size() == 2 && (long)result.get(0) > 0 && (long)result.get(1) > 0;
    }

    /**
     * 获取某个实体所有的粉丝
     * @param entityType
     * @param entityId
     * @param count
     * @return
     */
    public List<Integer> getFollowers(int entityType,int entityId,int count){
        String followerKey = RedisKey.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,0,count));
    }

    public List<Integer> getFollowers(int entityType,int entityId,int offset,int count){
        String followerKey = RedisKey.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,offset,offset + count));
    }

    /**
     * 获取某个用户所有关注
     * @param userId
     * @param entityType
     * @param count
     * @return
     */
    public List<Integer> getFollowees(int userId,int entityType,int count){
        String followeeKey = RedisKey.getFolloweeKey(entityType, userId);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,0,count));
    }

    public List<Integer> getFollowees(int userId,int entityType,int offset,int count){
        String followeeKey = RedisKey.getFolloweeKey(entityType, userId);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,offset,offset + count));
    }

    /**
     * 获取某个实体所有的粉丝数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long getFollowerCount(int entityType,int entityId){
        String followerKey = RedisKey.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    /**
     * 获取某个用户所有关注数量
     * @param userId
     * @param entityType
     * @return
     */
    public long getFolloweeCount(int userId,int entityType){
        String followeeKey = RedisKey.getFolloweeKey(entityType,userId);
        return jedisAdapter.zcard(followeeKey);
    }

    /**
     *判断用户是否关注了某个实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean isFollower(int userId,int entityType,int entityId){
        String followerKey = RedisKey.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId)) != null;
    }

    private List<Integer> getIdsFromSet(Set<String> idset){
        List<Integer> ids = new ArrayList<>();
        if(idset != null){
            for(String str : idset){
                ids.add(Integer.parseInt(str));
            }
        }
        return ids;
    }



}
