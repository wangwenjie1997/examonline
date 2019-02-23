package com.wwj.examonline.entity;

import lombok.Data;

@Data
public class ExamUser {

    private PaperInfo papeInfo;
    private User user;
    private int joinExam;
    private float examGrade;

}
