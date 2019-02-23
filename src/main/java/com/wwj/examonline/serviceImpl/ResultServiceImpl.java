package com.wwj.examonline.serviceImpl;

import com.wwj.examonline.entity.*;
import com.wwj.examonline.mapper.ExamUserMapper;
import com.wwj.examonline.mapper.PaperContentMapper;
import com.wwj.examonline.mapper.PaperInfoMapper;
import com.wwj.examonline.mapper.ResultMapper;
import com.wwj.examonline.service.ResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class ResultServiceImpl implements ResultService {

    @Autowired
    private ResultMapper resultMapper;

    @Autowired
    private ExamUserMapper examUserMapper;

    @Autowired
    private PaperContentMapper paperContentMapper;

    @Autowired
    private PaperInfoMapper paperInfoMapper;

    @Override
    public int updateResult(String resultAnsewer, int userId, int paperContentId,int paperId) {

        return resultMapper.updateResult(resultAnsewer,userId,paperContentId,paperId);
    }

    @Override
    public boolean joinExam(int userId, int paperId) {
        boolean flag=true;
        ExamUser examUser=examUserMapper.getExamUserByUserIdAndPaperId(userId,paperId);
        if(examUser.getJoinExam()==0){
            if(examUserMapper.updateJoinExam(userId,paperId)>0){
                for(PaperContent paperContent:paperContentMapper.selectPaperContentByPaperIdTwo(paperId)){
                    if(resultMapper.insertResult(userId,paperContent.getPaperCotentId(),paperId)<=0)
                        flag=false;
                }
            }
            else
                flag=false;
        }
        return flag;
    }

    @Override
    public Result getResultByUserIdAndContentIdAndPaperId(int userId, int paperContentId,int paperId) {
        return resultMapper.selectResultByUserIdAndContentIdAndPaperId(userId,paperContentId,paperId);
    }

    @Override
    public String getAutoCheckGrade(int userId, int paperId) {
        List<PaperContent> paperContents=paperContentMapper.selectPaperContentByPaperId(paperId);
        PaperInfo paperInfo=paperInfoMapper.selectPaperByPaperId(paperId);
        Question question;
        Result result;

        Float grade=0F;
        boolean flag=true;//是否有未批改的题目

//        String test=",xx";
//        String[] arrTest=test.split(",");
//        for(int i=0;i<arrTest.length;i++)
//            log.info("arrTest:"+arrTest[i]);
//        String test2="";
//        String[] arrTest2=test2.split(",");
//        for(int i=0;i<arrTest2.length;i++)
//            log.info("arrTest2:"+arrTest2[i]);

        for (PaperContent paperContent:paperContents){
            question=paperContent.getQuestion();

            if(question.getQuestionKind().getKindId()==1){//单选
                if(paperInfo.getOneOptionCheck()==1){//是否自动批改
                    result=resultMapper.selectResultByUserIdAndContentIdAndPaperId(userId,paperContent.getPaperCotentId(),paperId);
                    if(result.getResultCheck()==0){//是否批改过
                        if(result.getResultAnsewer().equals(question.getQuestionAnsewer())){
                            resultMapper.updateResultGrade(paperInfo.getOneOptionMark(),result.getResultId());
                            grade+=paperInfo.getOneOptionMark();
                        }
                        else {
                            resultMapper.updateResultGrade(0F,result.getResultId());
                        }
                    }
                }
                else
                    flag=false;
            }
            else if(question.getQuestionKind().getKindId()==2){//多选
                if(paperInfo.getMoreOptionCheck()==1){
                    result=resultMapper.selectResultByUserIdAndContentIdAndPaperId(userId,paperContent.getPaperCotentId(),paperId);
                    if(result.getResultCheck()==0){
                        if(result.getResultAnsewer().equals(question.getQuestionAnsewer())){
                            resultMapper.updateResultGrade(paperInfo.getMoreOptionMark(),result.getResultId());
                            grade+=paperInfo.getMoreOptionMark();
                        }
                        else{
                            resultMapper.updateResultGrade(0F,result.getResultId());
                        }
                    }
                }
                else
                    flag=false;
            }
            else if(question.getQuestionKind().getKindId()==3){//判断
                if(paperInfo.getTrueOrFalseCheck()==1){
                    result=resultMapper.selectResultByUserIdAndContentIdAndPaperId(userId,paperContent.getPaperCotentId(),paperId);
                    if(result.getResultCheck()==0){
                        if(result.getResultAnsewer().equals(question.getQuestionAnsewer())){
                            resultMapper.updateResultGrade(paperInfo.getTrueOrFalseMark(),result.getResultId());
                            grade+=paperInfo.getTrueOrFalseMark();
                        }
                        else{
                            resultMapper.updateResultGrade(0F,result.getResultId());
                        }
                    }
                }
                else
                    flag=false;
            }
            else if(question.getQuestionKind().getKindId()==4){//填空
                if(paperInfo.getFillInBlankCheck()==1){
                    result=resultMapper.selectResultByUserIdAndContentIdAndPaperId(userId,paperContent.getPaperCotentId(),paperId);
                    if(result.getResultCheck()==0){
                        String trueAnswer=question.getQuestionAnsewer();
                        String[] arrTrueAnswer=trueAnswer.split(",");
                        String resultAnswer=result.getResultAnsewer();
                        String[] arrResultAnswer=resultAnswer.split(",");
                        Float resultGrade=0F;
                        for(int i=0;i<arrResultAnswer.length;i++){
                            if(arrResultAnswer[i].equals(arrTrueAnswer[i]))
                                resultGrade+=paperInfo.getFillInBlankMark();
                        }
                        resultMapper.updateResultGrade(resultGrade,result.getResultId());
                        grade+=resultGrade;
                    }
                }
                else
                    flag=false;
            }
            else if(question.getQuestionKind().getKindId()==5){//简答
                flag=false;
            }
        }
        if(flag==true) {
            examUserMapper.updateGrade(grade,userId,paperId);
            return "" + grade;
        }
        else
            return "批改中";
    }

    @Override
    public Result getResultByResultId(int resultId) {
        return resultMapper.selectResultByResultId(resultId);
    }

    @Override
    public boolean updateResultGrade(int resultId, Float resultMark) {
        if(resultMapper.updateResultGrade(resultMark,resultId)>0)
            return true;
        else
            return false;
    }


}
