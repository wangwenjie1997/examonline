package com.wwj.examonline.service;

import com.github.pagehelper.Page;
import com.wwj.examonline.entity.ExamUser;
import com.wwj.examonline.entity.User;

import java.util.ArrayList;
import java.util.List;

public interface ExamUserService {

    String addOneExamUser(String mail,String name,int paperId);

    boolean addExcelExamUser(List<User> users, int paperId);

    int getAllExamUserNum(int paperId);

    Page<ExamUser> getPageExamUser(int pageNum, int pageSize,int paperId);

    List<User> getAllExamUser(int paperId,String search);

    boolean deleteExamUserByUserIdAndPaperId(int userId,int paperId);

    Page<ExamUser> getPageExamUserTwo(int userId,int pageNum,int pageSize);

    List<ExamUser> selectExamUserByUserId(int userId);

    ExamUser getExamUserByUserIdAndPaperId(int userId,int paperId);

    Float getExamGrade(int userId,int paperId);

    boolean submitExam(int userId,int paperId);

    boolean setExamGrade(int paperId,int userId);

    boolean editExamGrade(int paperId,int userId,Float examGrade);

    int getCheckPaperNum(User user);

    int getExamUserNum(int paperId);

}
