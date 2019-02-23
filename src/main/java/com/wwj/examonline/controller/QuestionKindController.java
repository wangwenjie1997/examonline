package com.wwj.examonline.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwj.examonline.entity.QuestionKind;
import com.wwj.examonline.service.QuestionKindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class QuestionKindController {

    @Autowired
    private QuestionKindService questionKindServiceImpl;

    @RequestMapping("/getallkind")
    @ResponseBody
    public String getAllKind(){
        List<QuestionKind> questionKinds=questionKindServiceImpl.getQuestionKind();
        String json_kind= JSON.toJSONString(questionKinds);
        log.info("所有题型");
        log.info(json_kind);
        return json_kind;
    }

}
