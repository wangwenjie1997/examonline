package com.wwj.examonline.service;

import com.wwj.examonline.entity.PaperGroup;
import com.wwj.examonline.entity.User;

import java.util.List;

public interface PaperGroupService {

    PaperGroup checkPaperGroup(int papergroupcreator,String paperGroupName);

    boolean addPaperGroup(int userid,String paperGroupName);

    List<PaperGroup> getPaperGroupByUserId(User user);

    List<PaperGroup> searchPaperGroup(String paperGroupName,int userId);

    boolean editPaperGroupName(String paperGroupName,int paperGroupId);

    PaperGroup getPaperGroupByGroupId(int paperGroupId);

    boolean deletePaperGroup(int paperGroupId);

}
