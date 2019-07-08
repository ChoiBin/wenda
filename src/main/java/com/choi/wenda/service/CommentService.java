package com.choi.wenda.service;

import com.choi.wenda.dao.CommentDao;
import com.choi.wenda.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;

    public List<Comment> getCommentsByEntity(int entityId,int entityType){
        return commentDao.selectByEntity(entityId,entityType);
    }

    public int addComment(Comment comment){
        return commentDao.addComment(comment);
    }

    public int getCommentCount(int entityId,int entityType){
        return commentDao.getCommentCount(entityId,entityType);
    }

    public void deleteComment(int entityId, int entityType){
        commentDao.updateStstus(entityId,entityType,1);
    }

    public Comment getCommentById(int id){
        return commentDao.getCommentById(id);
    }

    public int getUserCommentCount(int userId){
        return commentDao.getUserCommentCount(userId);
    }

}
