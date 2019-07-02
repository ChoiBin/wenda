package com.choi.wenda.service;

import com.choi.wenda.dao.UserDao;
import com.choi.wenda.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    public User getUser(int id){
        return userDao.selectById(id);
    }


}
