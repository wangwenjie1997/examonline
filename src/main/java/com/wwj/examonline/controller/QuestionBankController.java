package com.wwj.examonline.controller;

import com.wwj.examonline.entity.QuestionBank;
import com.wwj.examonline.entity.User;
import com.wwj.examonline.globle.Constants;
import com.wwj.examonline.service.QuestionBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class QuestionBankController {

    @Autowired
    private QuestionBankService questionBankServiceImpl;

    @RequestMapping("/addquestionbank")
    public String addQuestionBank(String bankname, HttpSession session, HttpServletRequest request){
        User u= (User) session.getAttribute(Constants.USER_KEY);
        if(questionBankServiceImpl.addQuestionBank(u.getUserid(),bankname)){
            request.setAttribute(Constants.ERROR_KEY,"添加成功");
        }
        else
            request.setAttribute(Constants.ERROR_KEY,"添加失败");
        return "addquestionbank";
    }

    @RequestMapping("/checkbankname")
    @ResponseBody
    public boolean checkBankName(String bankname,HttpSession session){
        User u= (User) session.getAttribute(Constants.USER_KEY);
        if(questionBankServiceImpl.checkQuestionBank(u,bankname)!=null)
            return false;
        else
            return true;
    }

}
