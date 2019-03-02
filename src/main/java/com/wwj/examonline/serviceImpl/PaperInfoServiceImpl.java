package com.wwj.examonline.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wwj.examonline.entity.*;
import com.wwj.examonline.mapper.*;
import com.wwj.examonline.service.PaperInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private PaperGroupMapper paperGroupMapper;

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
        PaperInfo paperInfo=paperInfoMapper.selectPaperByPaperId(paperId);
        if(oneOptionMark!=paperInfo.getOneOptionMark()){
            for(PaperContent paperContent:paperContentMapper.selectPapperContentByIdAndMark(paperId,paperInfo.getOneOptionMark())){
                if(paperContent.getQuestion().getQuestionKind().getKindId()==1)
                    paperContentMapper.updatePaperContentMark(oneOptionMark,paperContent.getPaperCotentId());
            }
        }
        if(moreOptionMark!=paperInfo.getMoreOptionMark()){
            for(PaperContent paperContent:paperContentMapper.selectPapperContentByIdAndMark(paperId,paperInfo.getMoreOptionMark())){
                if(paperContent.getQuestion().getQuestionKind().getKindId()==2)
                    paperContentMapper.updatePaperContentMark(moreOptionMark,paperContent.getPaperCotentId());
            }
        }
        if(trueOrFalseMark!=paperInfo.getTrueOrFalseMark()){
            for(PaperContent paperContent:paperContentMapper.selectPapperContentByIdAndMark(paperId,paperInfo.getTrueOrFalseMark())){
                if(paperContent.getQuestion().getQuestionKind().getKindId()==3)
                    paperContentMapper.updatePaperContentMark(trueOrFalseMark,paperContent.getPaperCotentId());
            }
        }
        if(fillInBlankMark!=paperInfo.getFillInBlankMark()){
            for(PaperContent paperContent:paperContentMapper.selectPapperContentByIdAndMark(paperId,paperInfo.getFillInBlankMark())){
                if(paperContent.getQuestion().getQuestionKind().getKindId()==4)
                    paperContentMapper.updatePaperContentMark(fillInBlankMark,paperContent.getPaperCotentId());
            }
        }
        if(shortAnswerQuestionMark!=paperInfo.getShortAnswerQuestionMark()){
            for(PaperContent paperContent:paperContentMapper.selectPapperContentByIdAndMark(paperId,paperInfo.getShortAnswerQuestionMark())){
                if(paperContent.getQuestion().getQuestionKind().getKindId()==5)
                    paperContentMapper.updatePaperContentMark(shortAnswerQuestionMark,paperContent.getPaperCotentId());
            }
        }
        if(paperInfoMapper.updatePaperByPaperId(paperName,oneOptionCheck,moreOptionCheck,trueOrFalseCheck
                ,fillInBlankCheck,oneOptionMark,moreOptionMark,trueOrFalseMark,fillInBlankMark,shortAnswerQuestionMark
                ,paperId)>0) {

            return true;
        }
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
        PaperInfo paperInfo=paperInfoMapper.selectPaperByPaperId(paperId);
        String paperEndstr=paperInfo.getPaperEnd();
        if(paperEndstr!=null){
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date paperEndDate=df.parse(paperEndstr);
                Date nowDate=new Date();
                if(nowDate.getTime()<=paperEndDate.getTime())
                    return false;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(paperInfoMapper.updatePaperTime(paperStart,paperEnd,paperId)>0)
            return true;
        else
            return false;
    }

    @Override
    public PaperInfo getExamPaper(int paperId) {
        return paperInfoMapper.selectPaperByPaperIdTwo(paperId);
    }

    @Override
    public Map<String, Object> getPapersInfo(User user) {
        Map<String,Object> papersNumMap=new HashMap<String,Object>();
        int allPaperNum=0;
        int submitPaperNum=0;
        int notSubmitPaperNum=0;
        List<PaperGroup> paperGroups=paperGroupMapper.selectPaperGroupByCreator(user.getUserId());
        for(int i=0;i<paperGroups.size();i++){
            List<PaperInfo> paperInfos=paperInfoMapper.selectPaperInfoByPaperGroupId(paperGroups.get(i).getPaperGroupId());
            allPaperNum+=paperInfos.size();
            for(int j=0;j<paperInfos.size();j++){
                if(paperInfos.get(j).getPaperStart()!=null&&paperInfos.get(j).getPaperEnd()!=null)
                    submitPaperNum++;
                else
                    notSubmitPaperNum++;
            }

        }
        papersNumMap.put("allPaperNum",allPaperNum);
        papersNumMap.put("submitPaperNum",submitPaperNum);
        papersNumMap.put("notSubmitPaperNum",notSubmitPaperNum);
        return papersNumMap;
    }
}
