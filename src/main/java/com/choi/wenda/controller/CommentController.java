package com.choi.wenda.controller;

import com.choi.wenda.model.Comment;
import com.choi.wenda.model.EntityType;
import com.choi.wenda.model.HostHolder;
import com.choi.wenda.service.CommentService;
import com.choi.wenda.service.QuestionService;
import com.choi.wenda.service.SensitiveService;
import com.choi.wenda.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

@Controller
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private SensitiveService sensitiveService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private QuestionService questionService;


    @RequestMapping(path = {"/addComment"},method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try {
            //过滤敏感词
            content = HtmlUtils.htmlEscape(content);
            content = sensitiveService.filter(content);
            Comment comment = new Comment();
            if(hostHolder.getUser() != null){
                comment.setUserId(hostHolder.getUser().getId());
            }else{
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
            }
            comment.setContent(content);
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setCreateDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);

            //更新问题的评论数量
            int count = commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(),count);

        }catch (Exception e){
            logger.error("增加评论失败" + e.getMessage());
        }
            return "redirect:/question/" + String.valueOf(questionId);
    }

}
