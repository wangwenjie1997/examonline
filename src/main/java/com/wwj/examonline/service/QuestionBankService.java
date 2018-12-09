package com.wwj.examonline.service;

import com.wwj.examonline.entity.QuestionBank;
import com.wwj.examonline.entity.User;

public interface QuestionBankService {

    boolean addQuestionBank(int userid,String bankname);

    QuestionBank checkQuestionBank(User user, String bankname);

}
