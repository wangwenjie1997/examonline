package com.wwj.examonline.serviceImpl;

import com.wwj.examonline.entity.User;
import com.wwj.examonline.globle.Constants;
import com.wwj.examonline.mapper.UserMapper;
import com.wwj.examonline.service.UserService;
import com.wwj.examonline.util.SendQQMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SendQQMailUtil sendQQMailUtil;

    public User login(String mail,String password){
        return userMapper.selectUserByMailAndPassword(mail,password);
    }

    @Override
    public User regist(User user) {
        user.setPicpath(Constants.PIC_PATH);
        if(userMapper.insertUser(user)>0) {
            return checkMail(user.getMail());
        }
        else
            return null;
    }

    @Override
    public User checkMail(String mail) {
        return userMapper.selectUserByMail(mail);
    }

    @Override
    public boolean editPassword(String password, int userid) {
        if(userMapper.updatePassword(password,userid)>0)
            return true;
        else
            return false;
    }

    @Override
    public boolean editMail(String mail, int userid) {
        if(userMapper.updateMail(mail,userid)>0)
            return true;
        else
            return false;
    }

    @Override
    public boolean editName(String name, int userid) {
        if(userMapper.updateName(name,userid)>0)
            return true;
        else
            return false;
    }

    @Override
    public void sendMail(String sender, String receiver, String title, String text) {
        sendQQMailUtil.send(sender,receiver,title,text);
    }

}
