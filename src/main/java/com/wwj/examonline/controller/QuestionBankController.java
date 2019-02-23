package com.wwj.examonline.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class QuestionBankController {

    @Autowired
    private QuestionBankService questionBankServiceImpl;

    @RequestMapping("/getquestionbank")
    @ResponseBody
    public String getQuestionBank(HttpSession session,String bankName,String addBankName,String editBankName){
        User u= (User) session.getAttribute(Constants.USER_KEY);
        ArrayList<QuestionBank> questionBanks=null;
        String json_bank="{}";
        if(bankName==""){
            questionBanks= (ArrayList<QuestionBank>) questionBankServiceImpl.getQuestionBankByUsrId(u);
        }
        else if(bankName!=""){
            questionBanks=
                    (ArrayList<QuestionBank>) questionBankServiceImpl.searchQuestionBank(bankName,u.getUserId());
        }
        json_bank= JSON.toJSONString(questionBanks);
        return json_bank;
    }

    @RequestMapping("/addquestionbank")
    @ResponseBody
    public boolean addQuestionBank(String bankName,String search, HttpSession session){
        User u= (User) session.getAttribute(Constants.USER_KEY);
        return questionBankServiceImpl.addQuestionBank(u.getUserId(),bankName);
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

    @RequestMapping("/deletequestionbank")
    @ResponseBody
    public boolean deleteQuestionBank(HttpSession session,int bankId,String search){
        return questionBankServiceImpl.deleteQuestionBank(bankId);
    }

    @RequestMapping("/editquestionbank")
    @ResponseBody
    public boolean editQuestionBank(HttpSession session,int bankId,String bankName,String search){
        return questionBankServiceImpl.editQuestionBankName(bankName,bankId);
    }

    @RequestMapping("/getallbank")
    @ResponseBody
    public String getAllBank(HttpSession session){
        User user= (User) session.getAttribute(Constants.USER_KEY);
        List<QuestionBank> questionBanks=questionBankServiceImpl.getAllQuestionBank(user.getUserId());
        String json_bank= JSON.toJSONString(questionBanks);
        log.info("当前对象所有题库");
        log.info(json_bank);
        return json_bank;
    }

}
