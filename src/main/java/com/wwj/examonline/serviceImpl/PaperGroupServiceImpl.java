package com.wwj.examonline.serviceImpl;

import com.wwj.examonline.entity.*;
import com.wwj.examonline.mapper.PaperContentMapper;
import com.wwj.examonline.mapper.PaperGroupMapper;
import com.wwj.examonline.mapper.PaperInfoMapper;
import com.wwj.examonline.mapper.QuestionMapper;
import com.wwj.examonline.service.PaperGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class PaperGroupServiceImpl implements PaperGroupService {

    @Autowired
    private PaperGroupMapper paperGroupMapper;

    @Autowired
    private PaperInfoMapper paperInfoMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private PaperContentMapper paperContentMapper;

    @Override
    public PaperGroup checkPaperGroup(int papergroupcreator, String paperGroupName) {
        return paperGroupMapper.selectPaperGroupByCreatorAndGroupName(papergroupcreator,paperGroupName);
    }

    @Override
    public boolean addPaperGroup(int userid, String paperGroupName) {
        if(paperGroupMapper.insertPaperGroup(userid,paperGroupName)>0)
            return true;
        else
            return false;
    }

    @Override
    public List<PaperGroup> getPaperGroupByUserId(User user) {
        return paperGroupMapper.selectPaperGroupByCreator(user.getUserId());
    }

    @Override
    public List<PaperGroup> searchPaperGroup(String paperGroupName, int userId) {
        return paperGroupMapper.selectGroupByGroupNameAndCreatorFuzzySearch(paperGroupName,userId);
    }

    @Override
    public boolean editPaperGroupName(String paperGroupName, int paperGroupId) {
        if(paperGroupMapper.updatePaperGroupName(paperGroupName,paperGroupId)>0)
            return true;
        else
            return false;
    }

    @Override
    public PaperGroup getPaperGroupByGroupId(int paperGroupId) {
        return paperGroupMapper.selectPaperGroupByGroupId(paperGroupId);
    }

    @Override
    public boolean deletePaperGroup(int paperGroupId) {
        boolean flag=true;
        paperGroupMapper.deletePaperGroupByGroupId(paperGroupId);
        return flag;
    }
}
