package com.wwj.examonline.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wwj.examonline.entity.Question;
import com.wwj.examonline.entity.QuestionBank;
import com.wwj.examonline.entity.User;
import com.wwj.examonline.mapper.QuestionBankMapper;
import com.wwj.examonline.mapper.QuestionMapper;
import com.wwj.examonline.service.QuestionBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class QuestionBankServiceImpl implements QuestionBankService {

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public boolean addQuestionBank(int userid, String bankname) {
        if(questionBankMapper.insertQuestionBank(userid,bankname)>0)
            return true;
        else
            return false;
    }

    @Override
    public QuestionBank checkQuestionBank(User user, String bankname) {
        return  questionBankMapper.selectBankByCreatorAndBankName(user.getUserId(),bankname);
    }

    @Override
    public List<QuestionBank> getQuestionBankByUsrId(User user) {
        return questionBankMapper.selectBankByCreator(user.getUserId());
    }

    @Override
    public boolean deleteQuestionBank(int bankId) {
        //删除问题
        boolean flag=true;
        questionMapper.deleteUsedQuestionByBankId(bankId);
        questionMapper.deleteQuestionFromDBByBankId(bankId);
        questionBankMapper.deleteQuestionBankByBankId(bankId);
        return flag;
    }

    @Override
    public boolean editQuestionBankName(String bankName, int bankId) {
        if(questionBankMapper.updateQuestionBankName(bankName,bankId)>0)
            return true;
        else
            return false;
    }

    @Override
    public List<QuestionBank> searchQuestionBank(String bankName, int userId) {
        return  questionBankMapper.selectBankByBankNameAndCreatorFuzzySearch(bankName,userId);
    }

    @Override
    public QuestionBank getQuestionBankByUserIdAndBankName(User user, String bankname) {
        return  questionBankMapper.selectBankByCreatorAndBankName(user.getUserId(),bankname);
    }

    @Override
    public QuestionBank getQuestionBankByBankId(int bankId) {
        return questionBankMapper.selectQuestionBankByBankId(bankId);
    }

    @Override
    public List<QuestionBank> getAllQuestionBank(int userId) {
        return questionBankMapper.selectBankByCreator(userId);
    }

    @Override
    public List<Integer> getAllBankId(int userId) {
        return questionBankMapper.selectBankIdByCreator(userId);
    }


}
