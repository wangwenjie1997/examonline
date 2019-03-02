package com.wwj.examonline.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wwj.examonline.entity.ExamUser;
import com.wwj.examonline.entity.PaperGroup;
import com.wwj.examonline.entity.PaperInfo;
import com.wwj.examonline.entity.User;
import com.wwj.examonline.globle.Constants;
import com.wwj.examonline.mapper.*;
import com.wwj.examonline.service.ExamUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ExamUserServiceImpl implements ExamUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ExamUserMapper examUserMapper;

    @Autowired
    private ResultMapper resultMapper;

    @Autowired
    private PaperGroupMapper paperGroupMapper;

    @Autowired
    private PaperInfoMapper paperInfoMapper;

    @Override
    public String addOneExamUser(String mail,String name,int paperId) {
        User user=new User();
        user.setMail(mail);
        if(name!=""){
            user.setName(name);
            user.setPassword(Constants.DETAULT_PASSWORD);
            user.setPicPath(Constants.PIC_PATH);
            userMapper.insertUser(user);
        }
        user=userMapper.selectUserByMail(mail);
        if(examUserMapper.selectExamUserByUserIdAndPaperId(user.getUserId(),paperId)==0){
            if(examUserMapper.insertExamUser(user.getUserId(),paperId)>0)
                return "添加成功";
            else
                return "添加失败";
        }
        else
            return "该学生已参加该考试";

    }

    @Override
    public boolean addExcelExamUser(List<User> users, int paperId) {
        boolean flag=true;
        User u;
        for (User user:users){
            u=userMapper.selectUserByMail(user.getMail());
            if(u==null){//未注册
                user.setPassword(Constants.DETAULT_PASSWORD);
                user.setPicPath(Constants.PIC_PATH);
                userMapper.insertUser(user);
                u=userMapper.selectUserByMail(user.getMail());
            }
            if(examUserMapper.selectExamUserByUserIdAndPaperId(u.getUserId(),paperId)==0){
                if(examUserMapper.insertExamUser(u.getUserId(),paperId)>0)
                    flag=true;
                else
                    flag=false;
            }
            else
                continue;
        }
        return flag;
    }

    @Override
    public int getAllExamUserNum(int paperId) {
        return examUserMapper.selectAllExamUserNum(paperId);
    }

    @Override
    public Page<ExamUser> getPageExamUser(int pageNum, int pageSize, int paperId) {
        PageHelper.startPage(pageNum,pageSize);
        return examUserMapper.selectAllExamUser(paperId);
    }

    @Override
    public List<User> getAllExamUser(int paperId,String search) {
        List<User> fuzzySearchUser=userMapper.getUserFuzzySearch(search,search);
        List<User> returnSearchUser=new ArrayList<User>();
        User user=new User();
        for (int i=0;i<fuzzySearchUser.size();i++){
            user=fuzzySearchUser.get(i);
            if(examUserMapper.selectExamUserByUserIdAndPaperId(user.getUserId(),paperId)==1) {
                returnSearchUser.add(user);
            }
        }
        return returnSearchUser;
    }

    @Override
    public boolean deleteExamUserByUserIdAndPaperId(int userId, int paperId) {
        boolean flag=true;
        examUserMapper.deleteExamUserByUserIdAndPaperId(userId,paperId);
        return flag;
    }

    @Override
    public Page<ExamUser> getPageExamUserTwo(int userId,int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return examUserMapper.selectExamUserByUserId(userId);
    }

    @Override
    public List<ExamUser> selectExamUserByUserId(int userId) {
        return examUserMapper.selectExamUserByUserIdTwo(userId);
    }

    @Override
    public ExamUser getExamUserByUserIdAndPaperId(int userId, int paperId) {
        return examUserMapper.getExamUserByUserIdAndPaperId(userId,paperId);
    }

    @Override
    public Float getExamGrade(int userId, int paperId) {
        return examUserMapper.getExamGrade(userId,paperId);
    }

    @Override
    public boolean submitExam(int userId, int paperId) {
        if(examUserMapper.updateSubmitExam(userId,paperId)>0)
            return true;
        else
            return false;
    }

    @Override
    public boolean setExamGrade(int paperId, int userId) {
        resultMapper.updateAllResultCheck(userId,paperId);
        Float examgrade=resultMapper.getUserAllGrade(userId,paperId);
        if(examUserMapper.updateGrade(examgrade,userId,paperId)>0)
            return true;
        else
            return false;
    }

    @Override
    public boolean editExamGrade(int paperId,int userId,Float examGrade) {
        if(examUserMapper.updateGrade(examGrade,userId,paperId)>0)
            return true;
        else
            return false;
    }

    @Override
    public int getCheckPaperNum(User user) {
        int checkPaperNum=0;
        List<PaperGroup> paperGroups=paperGroupMapper.selectPaperGroupByCreator(user.getUserId());
        for(int i=0;i<paperGroups.size();i++){
            List<PaperInfo> paperInfos=paperInfoMapper.selectPaperInfoByPaperGroupId(paperGroups.get(i).getPaperGroupId());
            for(int j=0;j<paperInfos.size();j++){
                checkPaperNum+=examUserMapper.selectNotCheckNum(paperInfos.get(j).getPaperId());
            }
        }
        return checkPaperNum;
    }

    @Override
    public int getExamUserNum(int paperId) {
        return examUserMapper.selectExamUserNum(paperId);
    }
}
