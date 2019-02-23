package com.wwj.examonline.entity;

import lombok.Data;

import java.util.List;

@Data
public class PaperGroup {

    private int paperGroupId;
    private User user;
    private String paperGroupName;
    private List<PaperInfo> paperInfos;

}
