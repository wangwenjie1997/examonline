package com.wwj.examonline.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wwj.examonline.entity.PaperContent;
import com.wwj.examonline.entity.Question;
import com.wwj.examonline.entity.Result;
import com.wwj.examonline.entity.User;
import com.wwj.examonline.globle.Constants;
import com.wwj.examonline.service.PaperContentService;
import com.wwj.examonline.service.ResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class ResultController {

    @Autowired
    private ResultService resultServiceImpl;

    @Autowired
    private PaperContentService paperContentServiceImpl;

    @RequestMapping("/updateresult")
    @ResponseBody
    public boolean updateResult(int paperContentId, String answer,int paperId,HttpSession session){
        User user= (User) session.getAttribute(Constants.USER_KEY);
        if(resultServiceImpl.updateResult(answer,user.getUserId(),paperContentId,paperId)>0)
            return true;
        else
            return false;
    }

    @RequestMapping("/getexamquestionresult")
    @ResponseBody
    public String getExamQuestionResult(int paperContentId,int paperId,HttpSession session){
        PaperContent paperContent=paperContentServiceImpl.getPaperContentByContentId(paperContentId);
        Question question=paperContent.getQuestion();
        question.setQuestionAnsewer("");
        paperContent.setQuestion(question);

        User user= (User) session.getAttribute(Constants.USER_KEY);
        Result result=resultServiceImpl.getResultByUserIdAndContentIdAndPaperId(user.getUserId(),paperContentId,paperId);
        result.setPaperContent(paperContent);
        String JSON_root=new JSONObject().toJSONString(result);
        log.info("一条考试记录");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/getpaperresult")
    @ResponseBody
    public String getPaperContent(int resultId){
        String JSON_root=new JSONObject().toJSONString(resultServiceImpl.getResultByResultId(resultId));
        log.info("一条result");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/submitresultmark")
    @ResponseBody
    public boolean submitResultMark(int resultId,String resultMark){
        if(resultMark=="")
            return false;
        else
            return resultServiceImpl.updateResultGrade(resultId,Float.valueOf(resultMark));
    }

}
