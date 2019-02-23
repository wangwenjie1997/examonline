package com.wwj.examonline.entity;

import lombok.Data;

import java.util.List;

@Data
public class QuestionBank {

    private int bankId;
    private User user;
    private String bankName;
    private List<Question> questions;

}
