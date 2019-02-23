package com.wwj.examonline.service;

import com.github.pagehelper.Page;
import com.wwj.examonline.entity.Question;

import java.util.List;

public interface QuestionService {

    boolean addQuestion(int selected_kind, String questioncontent,String optionacontent, String optionbcontent,
                        String optionccontent,String optiondcontent,String answer,int selected_bank);

    Page<Question> getPageQuestion(int pageNum,int pageSize,int bankId);

    int getAllQuestionNumByBankId(int bankId);

    Page<Question> searchPageQuestion(int pageNum, int pageSize,String questionContent,int bankId);

    Question showQuestion(int questionId);

    boolean deleteQuestion(int questionId);

    boolean updateQuestion(String questionContent,String questionAnsewer,String optionA
            ,String optionB,String optionC,String optionD,int questionId);

    int getAllSearchQuestion(String questionContent,int bankId);

    List<Integer> getAllQuestionIdByBankId(int bankId,String search);

    Question getQuestion(int questionId);

    List<Integer> getAllQuestionIdByBankIdAndKind(int bankId,int kindId,String search);

}
