package com.wwj.examonline.service;

import com.github.pagehelper.Page;
import com.wwj.examonline.entity.QuestionBank;
import com.wwj.examonline.entity.User;

import java.util.List;

public interface QuestionBankService {

    boolean addQuestionBank(int userid,String bankname);

    QuestionBank checkQuestionBank(User user, String bankname);

    List<QuestionBank> getQuestionBankByUsrId(User user);

    boolean deleteQuestionBank(int bankId);

    boolean editQuestionBankName(String bankName,int bankId);

    List<QuestionBank> searchQuestionBank(String bankName,int userId);

    QuestionBank getQuestionBankByUserIdAndBankName(User user, String bankname);

    QuestionBank getQuestionBankByBankId(int bankId);

    List<QuestionBank> getAllQuestionBank(int userId);

    List<Integer> getAllBankId(int userId);

}
