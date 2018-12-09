package com.wwj.examonline.serviceImpl;

import com.wwj.examonline.entity.QuestionBank;
import com.wwj.examonline.entity.User;
import com.wwj.examonline.mapper.QuestionBankMapper;
import com.wwj.examonline.service.QuestionBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuestionBankServiceImpl implements QuestionBankService {

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Override
    public boolean addQuestionBank(int userid, String bankname) {
        if(questionBankMapper.insertQuestionBank(userid,bankname)>0)
            return true;
        else
            return false;
    }

    @Override
    public QuestionBank checkQuestionBank(User user, String bankname) {
        return  questionBankMapper.selectBankByCreatorAndBankName(user.getUserid(),bankname);
    }
}
