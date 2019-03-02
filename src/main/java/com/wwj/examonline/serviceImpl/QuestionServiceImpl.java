package com.wwj.examonline.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wwj.examonline.entity.Question;
import com.wwj.examonline.entity.QuestionBank;
import com.wwj.examonline.entity.User;
import com.wwj.examonline.mapper.QuestionBankMapper;
import com.wwj.examonline.mapper.QuestionMapper;
import com.wwj.examonline.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Override
    public boolean addQuestion(int selected_kind, String questioncontent, String optionacontent, String optionbcontent
            , String optionccontent, String optiondcontent, String answer,int selected_bank) {
        if(questionMapper.insertQuestion(questioncontent,selected_kind,answer,selected_bank
                ,optionacontent,optionbcontent,optionccontent,optiondcontent,0,1)>0)
            return true;
        else
            return false;
    }

    @Override
    public Page<Question> getPageQuestion(int pageNum, int pageSize, int bankId) {
        PageHelper.startPage(pageNum,pageSize);
        return questionMapper.selectQuestionByBankId(bankId);
    }

    @Override
    public int getAllQuestionNumByBankId(int bankId) {
        return questionMapper.selectAllQuestionNumByBankId(bankId);
    }

    @Override
    public Page<Question> searchPageQuestion(int pageNum, int pageSize,String questionContent, int bankId) {
        PageHelper.startPage(pageNum,pageSize);
        return questionMapper.selectQuestionByBankIdAndQuestioContentFuzzySearch(questionContent,bankId);
    }

    @Override
    public Question showQuestion(int questionId) {
        return questionMapper.selectQuestionByQuestionId(questionId);
    }

    @Override
    public boolean deleteQuestion(int questionId) {
        boolean flag=true;
        questionMapper.deleteUsedQuestionByQuestionId(questionId);
        questionMapper.deleteQuestionFromDBByQuestionId(questionId);
        return flag;
    }

    @Override
    public boolean updateQuestion(int questionKindId,String questionContent, String questionAnsewer, String optionA, String optionB, String optionC, String optionD, int questionId) {
        if(questionMapper.updateQuestion(questionKindId,questionContent,questionAnsewer,optionA,optionB,optionC,optionD,questionId)>0)
            return true;
        else
            return false;
    }

    @Override
    public int getAllSearchQuestion(String questionContent, int bankId) {
        return questionMapper.selectAllSearchQuestion(questionContent,bankId);
    }

    @Override
    public List<Integer> getAllQuestionIdByBankId(int bankId,String search) {
        if(search!="") {
            return questionMapper.selectAllQuestionIdByBankIdFuzzySearch(bankId, search);
        }
        else {
            return questionMapper.selectAllQuestionIdByBankId(bankId);
        }
    }

    @Override
    public Question getQuestion(int questionId) {
        return questionMapper.selectQuestionByQuestionIdTwo(questionId);
    }

    @Override
    public List<Integer> getAllQuestionIdByBankIdAndKind(int bankId, int kindId,String search) {
        if(search!="") {
            return questionMapper.selectAllQuestionIdByBankIdAndKindFuzzySearch(bankId, kindId, search);
        }
        else {
            return questionMapper.selectAllQuestionIdByBankIdAndKind(bankId, kindId);
        }
    }

    @Override
    public Map<String, Object> getQuestionsInfo(User user) {
        Map<String,Object> questionsNumMap=new HashMap<String,Object>();
        int allQuestionNum=0;
        int usedQuestionNum=0;
        int notUsedQuestionNum=0;
        List<QuestionBank> questionBanks=questionBankMapper.selectBankByCreator(user.getUserId());
        for(int i=0;i<questionBanks.size();i++){
            usedQuestionNum+=questionMapper.selectUsedQuestionNum(questionBanks.get(i).getBankId());
            notUsedQuestionNum+=questionMapper.selectNotUsedQuestionNum(questionBanks.get(i).getBankId());
        }
        allQuestionNum=usedQuestionNum+notUsedQuestionNum;
        questionsNumMap.put("allQuestionNum",allQuestionNum);
        questionsNumMap.put("usedQuestionNum",usedQuestionNum);
        questionsNumMap.put("notUsedQuestionNum",notUsedQuestionNum);
        return questionsNumMap;
    }

}
