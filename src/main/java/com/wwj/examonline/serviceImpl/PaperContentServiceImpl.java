package com.wwj.examonline.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wwj.examonline.entity.PaperContent;
import com.wwj.examonline.entity.PaperInfo;
import com.wwj.examonline.entity.Question;
import com.wwj.examonline.mapper.PaperContentMapper;
import com.wwj.examonline.mapper.PaperInfoMapper;
import com.wwj.examonline.mapper.QuestionMapper;
import com.wwj.examonline.service.PaperContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class PaperContentServiceImpl implements PaperContentService {

    @Autowired
    private PaperContentMapper paperContentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private PaperInfoMapper paperInfoMapper;

    @Override
    public Page<PaperContent> getPagePaperContentQuestion(int paperId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<PaperContent> paperContents=paperContentMapper.selectPagePaperContentByPaperId(paperId);
        for (PaperContent paperContent:paperContents)
            paperContent.setQuestion(questionMapper.selectQuestionByQuestionId(paperContent.getQuestion().getQuestionId()));
        return paperContents;
    }

    @Override
    public List<PaperContent> getPaperContentByPaperId(int paperId) {
        List<PaperContent> paperContents=paperContentMapper.selectPaperContentByPaperId(paperId);
        for (PaperContent paperContent:paperContents)
            paperContent.setQuestion(questionMapper.selectQuestionByQuestionId(paperContent.getQuestion().getQuestionId()));
        return paperContents;
    }

    @Override
    public boolean editPaperContentMark(int paperCotentId, float questionMark) {
        int result=paperContentMapper.updatePaperContentMark(questionMark,paperCotentId);
        if(result>0)
            return true;
        else
            return false;
    }

    @Override
    public boolean deletePaperContent(int paperCotentId) {
        boolean flag=true;
        PaperContent paperContent=paperContentMapper.selectPagePaperContentByPaperContentId(paperCotentId);
        Question question=paperContent.getQuestion();
        int useNum=question.getUseNum();
        int questionId=question.getQuestionId();
        paperContentMapper.deletePaperContentByPaperContentId(paperCotentId);

        questionMapper.updateUseNumByQuestionId(useNum-1,questionId);

        return flag;
    }

    @Override
    public int getQuestionNumByPaperIdAndQuestionId(int paperId, int questionId) {
        return paperContentMapper.selectQuestionNumByPaperIdAndQuestionId(paperId,questionId);
    }

    @Override
    public boolean addQuestionToPaper(int questionId, int paperId) {
        PaperInfo paperInfo=paperInfoMapper.selectPaperByPaperId(paperId);
        Question question=questionMapper.selectQuestionByQuestionId(questionId);
        int questionTypeId=question.getQuestionKind().getKindId();
        float mark=0;
        if(questionTypeId==1)
            mark=paperInfo.getOneOptionMark();
        else if(questionTypeId==2)
            mark=paperInfo.getMoreOptionMark();
        else if(questionTypeId==3)
            mark=paperInfo.getTrueOrFalseMark();
        else if(questionTypeId==4) {
            String[] answer=question.getQuestionAnsewer().split(",");
            mark = paperInfo.getFillInBlankMark()*answer.length;
        }
        else if(questionTypeId==5)
            mark=paperInfo.getShortAnswerQuestionMark();
        int useNum=question.getUseNum()+1;

        boolean flag=false;

        if(paperContentMapper.insertPaperContent(paperId,questionId,mark)>0
                &&questionMapper.updateUseNumByQuestionId(useNum,questionId)>0)
            flag=true;
        else
            flag=false;

        return flag;
    }

    @Override
    public boolean deleteQuestionToPaper(int questionId, int paperId) {
        Question question=questionMapper.selectQuestionByQuestionId(questionId);
        int useNum=question.getUseNum()-1;
        boolean flag=true;
        paperContentMapper.deletePaperContentByPaperContentIdAndQuestionId(paperId,questionId);
        questionMapper.updateUseNumByQuestionId(useNum,questionId);
        return flag;
    }

    @Override
    public List<PaperContent> getQuestionByPaperId(int paperId) {
        return paperContentMapper.selectQuestionByPaperId(paperId);
    }

    @Override
    public int getQuestionNumByPaperId(int paperId) {
        return paperContentMapper.selectQuestionNumByPaperId(paperId);
    }

    @Override
    public List<PaperContent> getPaperQuestionByPaperId(int paperId) {
        return paperContentMapper.selectPaperContentByPaperId(paperId);
    }

    @Override
    public PaperContent getPaperContentByContentId(int paperContentId) {
        return paperContentMapper.selectPagePaperContentByPaperContentIdTwo(paperContentId);
    }

    @Override
    public Float getPaperAllGrade(int paperId) {
        return paperContentMapper.getPaperAllGrade(paperId);
    }
}
