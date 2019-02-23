package com.wwj.examonline.service;

import com.wwj.examonline.entity.User;

import java.util.List;

public interface UserService {

    User login(String mail, String password);

    User regist(User user);

    User checkMail(String mail);

    boolean editPassword(String password,int userid);

    boolean editMail(String mail,int userid);

    boolean editName(String name,int userid);

    boolean editPicPath(String picpath,int userid);

    void sendMail(String sender,String receiver,String title,String text);

    User getUser(String mail, String password);


}
