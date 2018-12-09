package com.wwj.examonline.entity;

import lombok.Data;

import java.util.List;

@Data
public class User {

    private int userid;
    private String mail;
    private String password;
    private String name;
    private String picpath;
    private List<QuestionBank> questionBanks;

}
