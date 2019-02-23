package com.wwj.examonline.entity;

import lombok.Data;

import java.util.List;

@Data
public class PaperContent {

    private int paperCotentId;
    private PaperInfo papeInfo;
    private Question question;
    private float questionMark;
    private List<Result> results;

}
