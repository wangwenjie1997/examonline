package com.wwj.examonline.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wwj.examonline.entity.ExamUser;
import com.wwj.examonline.entity.PaperContent;
import com.wwj.examonline.entity.PaperInfo;
import com.wwj.examonline.entity.Question;
import com.wwj.examonline.mapper.ExamUserMapper;
import com.wwj.examonline.mapper.PaperContentMapper;
import com.wwj.examonline.mapper.PaperInfoMapper;
import com.wwj.examonline.mapper.ResultMapper;
import com.wwj.examonline.service.PaperInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class PaperInfoServiceImpl implements PaperInfoService {

    @Autowired
    private PaperInfoMapper paperInfoMapper;

    @Autowired
    private PaperContentMapper paperContentMapper;

    @Autowired
    private ExamUserMapper examUserMapper;

    @Autowired
    private ResultMapper resultMapper;

    @Override
    public List<PaperInfo> getPaperInfoByPaperGroupId(int paperGroupId) {
        return paperInfoMapper.selectPaperInfoByPaperGroupId(paperGroupId);
    }

    @Override
    public int getAllSearchPaperNum(String paperName, int paperGroupId) {
        return paperInfoMapper.selectAllSearchPaperNum(paperName,paperGroupId);
    }

    @Override
    public int getAllPaperNum(int paperGroupId) {
        return paperInfoMapper.selectAllPaperNum(paperGroupId);
    }

    @Override
    public Page<PaperInfo> searchPagePaper(int pageNum, int pageSize, String search, int groupId) {
        PageHelper.startPage(pageNum,pageSize);
        return paperInfoMapper.selectPaperByPaperNameAndGroupIdFuzzySearch(search,groupId);
    }

    @Override
    public Page<PaperInfo> getPagePaper(int pageNum, int pageSize,int paperGroupId) {
        PageHelper.startPage(pageNum,pageSize);
        return paperInfoMapper.selectPaperByGroupId(paperGroupId);
    }

    @Override
    public boolean getPaperByGroupIdAndPaperName(int paperGroupId, String paperName) {
        if(paperInfoMapper.selectPaperNumByGroupAndPaperName(paperGroupId,paperName)>0)
            return false;
        else
            return true;
    }

    @Override
    public PaperInfo addPaper(String paperName, int paperGroupId) {
        if(paperInfoMapper.insetPaper(paperName,paperGroupId)>0)
            return paperInfoMapper.selectPaperByPaperNameAndGroupId(paperName,paperGroupId);
        else
            return null;
    }

    @Override
    public boolean settingPaper(String paperName,int oneOptionCheck, int moreOptionCheck, int trueOrFalseCheck
            , int fillInBlankCheck, float oneOptionMark, float moreOptionMark, float trueOrFalseMark
            , float fillInBlankMark, float shortAnswerQuestionMark, int paperId) {
        if(paperInfoMapper.updatePaperByPaperId(paperName,oneOptionCheck,moreOptionCheck,trueOrFalseCheck
                ,fillInBlankCheck,oneOptionMark,moreOptionMark,trueOrFalseMark,fillInBlankMark,shortAnswerQuestionMark
                ,paperId)>0)
            return true;
        else
            return false;
    }

    @Override
    public PaperInfo getPaperByPaperId(int paperId) {
        return paperInfoMapper.selectPaperByPaperId(paperId);
    }

    @Override
    public boolean deletePaper(int paperId) {
        boolean flag=true;
        List<PaperContent> paperContents=paperContentMapper.selectPaperContentByPaperId(paperId);

        //删除回答表记录
        for(PaperContent paperContent:paperContents)
            resultMapper.deleteResultByResultQuestion(paperContent.getPaperCotentId());
        //删除考生表记录
        examUserMapper.deleteExamUserByPaperId(paperId);
        //删除试卷内容表
        paperContentMapper.deletePaperContentByPaperId(paperId);
        //删除试卷信息表
        paperInfoMapper.deletePaperInfoByPaperId(paperId);
        return flag;
    }

    @Override
    public boolean releasePaper(String paperStart, String paperEnd, int paperId) {
        if(paperInfoMapper.updatePaperTime(paperStart,paperEnd,paperId)>0)
            return true;
        else
            return false;
    }

    @Override
    public PaperInfo getExamPaper(int paperId) {
        return paperInfoMapper.selectPaperByPaperIdTwo(paperId);
    }
}
