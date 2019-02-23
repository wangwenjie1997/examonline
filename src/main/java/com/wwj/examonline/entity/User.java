package com.wwj.examonline.entity;

import lombok.Data;

import java.util.List;

@Data
public class User {

    private int userId;
    private String mail;
    private String password;
    private String name;
    private String picPath;
    private List<QuestionBank> questionBanks;
    private List<PaperGroup> paperGroups;
    private List<ExamUser> examUsers;
    private List<Result> results;

}
