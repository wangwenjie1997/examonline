package com.wwj.examonline.entity;

import lombok.Data;

import java.util.List;

@Data
public class Question {

    private int questionId;
    private QuestionKind questionKind;
    private QuestionBank questionBank;
    private String questionContent;
    private String questionAnsewer;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int useNum;
    private int statu;
    private List<PaperContent> paperContents;
}
