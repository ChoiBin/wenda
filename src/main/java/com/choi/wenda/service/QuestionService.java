package com.choi.wenda.service;

import com.choi.wenda.dao.QuestionDao;
import com.choi.wenda.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private SensitiveService sensitiveService;

    public List<Question> getLatestQuestions(int userId, int offset, int limit){
        return questionDao.selectLatestQuestions(userId,offset,limit);
    }


    public int addQuestion(Question question){
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        //敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        return  questionDao.addQuestion(question) > 0 ? question.getId() : 0;
    }


    public Question selectById(int id){
        return questionDao.selectById(id);
    }

    public int updateCommentCount(int id,int count){
        return questionDao.updateCommentCount(id,count);
    }

}
