package com.wwj.examonline.entity;

import lombok.Data;

@Data
public class Result {

    private int resultId;
    private PaperContent paperContent;
    private User user;
    private PaperInfo paperInfo;
    private String resultAnsewer;
    private Float resultMark;
    private int resultCheck;

}
