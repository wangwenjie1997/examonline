package com.wwj.examonline.entity;

import lombok.Data;

import java.util.List;

@Data
public class PaperInfo {

    private int paperId;
    private PaperGroup paperGroup;
    private String paperStart;
    private String paperEnd;
    private String paperName;
    private int oneOptionCheck;
    private int moreOptionCheck;
    private int trueOrFalseCheck;
    private int fillInBlankCheck;
    private float oneOptionMark;
    private float moreOptionMark;
    private float trueOrFalseMark;
    private float fillInBlankMark;
    private float shortAnswerQuestionMark;
    private List<PaperContent> paperContents;
    private List<ExamUser> examUsers;

}
