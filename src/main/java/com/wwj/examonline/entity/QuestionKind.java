package com.wwj.examonline.entity;

import lombok.Data;

import java.util.List;

@Data
public class QuestionKind {

    private int kindId;
    private String kindName;
    private List<Question> questions;

}
