package com.wwj.examonline.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.Page;
import com.wwj.examonline.entity.PaperContent;
import com.wwj.examonline.entity.Question;
import com.wwj.examonline.entity.QuestionBank;
import com.wwj.examonline.entity.User;
import com.wwj.examonline.globle.Constants;
import com.wwj.examonline.service.PaperContentService;
import com.wwj.examonline.service.QuestionBankService;
import com.wwj.examonline.service.QuestionService;
import com.wwj.examonline.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionServiceImpl;

    @Autowired
    private QuestionBankService questionBankServiceImpl;

    @Autowired
    private PaperContentService paperContentServiceImpl;

    @RequestMapping(value = "imgupload", method = RequestMethod.POST)
    @ResponseBody
    public String imgUpload(@RequestPart(required = false, value = "file") MultipartFile file) {
        String msg = "";
        if (file.getSize() / 1000 > 100) {
            msg = "图片大小不能超过100K";
        }
        // 要上传的目标文件存放路径
        String localPath = Constants.PIC_SAVE_FULL_PATH;

        //判断上传文件格式
        String fileType = file.getContentType();
        if (fileType.equals("image/jpeg") || fileType.equals("image/png") || fileType.equals("image/jpeg")) {
            if (FileUtils.upload(file, localPath, file.getOriginalFilename())) {
                // 上传成功，给出页面提示
                String fileName = file.getOriginalFilename();
                String filePath = Constants.PIC_SAVE_PATH + fileName;
                msg = filePath;
            } else
                msg = "请选择jpg或png格式图片";
        }
        return msg;
    }

    @RequestMapping("/addquestion")
    public String addQuestion(int selected_kind, int selected_bank,String questioncontent,
                              @RequestParam(value = "optionacontent",required = false) String optionacontent,
                              @RequestParam(value = "optionbcontent",required = false) String optionbcontent,
                              @RequestParam(value = "optionccontent",required = false) String optionccontent,
                              @RequestParam(value = "optiondcontent",required = false) String optiondcontent,
                              @RequestParam(value = "answer",required = false) String answer,
                              HttpServletRequest request, HttpSession session){
        QuestionBank questionBank= (QuestionBank) session.getAttribute(Constants.SELECT_BANK);
        if(selected_bank!=questionBank.getBankId())
            log.info("QuestionController-addQuestion出错==>前端传送的选中题库Id和session中保存的选中题库Id不一致");

        if(questionServiceImpl.addQuestion(selected_kind,questioncontent,optionacontent
                ,optionbcontent,optionccontent,optiondcontent,answer,selected_bank))
            request.setAttribute(Constants.ERROR_KEY,"添加成功");
        else
            request.setAttribute(Constants.ERROR_KEY,"添加失败");
        return "forward:/gotoaddquestionpage?selectbank="+selected_bank+"&selectedkind="+selected_kind;
    }

    @RequestMapping("/getquestion")
    @ResponseBody
    public String getQuestion(String bankId,int pageNum,int onePageNum,String search,HttpSession session){

        int question_page_all_num=1;//总页数
        int allQuestionNum=0;//问题总个数
        Map<String,Object> root=new HashMap<String, Object>();
        Page<Question> pageQuestions=null;
        if(bankId==""){//不存在题库
            root.put(Constants.ONE_PAGE_QUESTION,"{}");
        }
        else {
            QuestionBank questionBank= (QuestionBank) questionBankServiceImpl.getQuestionBankByBankId(Integer.valueOf(bankId));
            session.setAttribute(Constants.SELECT_BANK,questionBank);


            if(search!=""){//搜索

                allQuestionNum=questionServiceImpl.getAllSearchQuestion(search,Integer.valueOf(bankId));
                if(allQuestionNum!=0){
                    if(allQuestionNum%Constants.QUESTION_PAGE_SIZE==0)
                        question_page_all_num=allQuestionNum/Constants.QUESTION_PAGE_SIZE;
                    else
                        question_page_all_num=allQuestionNum/Constants.QUESTION_PAGE_SIZE+1;
                }
                else if(allQuestionNum==0){
                    question_page_all_num=1;
                }
                if(pageNum<=0)
                    pageNum=1;
                if(pageNum>question_page_all_num)
                    pageNum=question_page_all_num;

                pageQuestions=questionServiceImpl
                        .searchPageQuestion(pageNum,Constants.QUESTION_PAGE_SIZE,search,Integer.valueOf(bankId));
                if(pageNum>1&&pageQuestions.size()<=0){
                    pageNum-=1;
                    pageQuestions=questionServiceImpl
                            .searchPageQuestion(pageNum,Constants.QUESTION_PAGE_SIZE,search,Integer.valueOf(bankId));
                }
            }
            else if(search==""){//不搜索

                allQuestionNum=questionServiceImpl.getAllQuestionNumByBankId(Integer.valueOf(bankId));
                if(allQuestionNum!=0){
                    if(allQuestionNum%Constants.QUESTION_PAGE_SIZE==0)
                        question_page_all_num=allQuestionNum/Constants.QUESTION_PAGE_SIZE;
                    else
                        question_page_all_num=allQuestionNum/Constants.QUESTION_PAGE_SIZE+1;
                }
                else if(allQuestionNum==0){
                    question_page_all_num=1;
                }
                if(pageNum<=0)
                    pageNum=1;
                if(pageNum>question_page_all_num)
                    pageNum=question_page_all_num;

                pageQuestions=questionServiceImpl.getPageQuestion(pageNum,Constants.QUESTION_PAGE_SIZE,Integer.valueOf(bankId));
                if(pageNum>1&&pageQuestions.size()<=0){
                    pageNum-=1;
                    pageQuestions=questionServiceImpl.getPageQuestion(pageNum,Constants.QUESTION_PAGE_SIZE,Integer.valueOf(bankId));
                }
            }
            if(pageQuestions.size()>0)
                root.put(Constants.ONE_PAGE_QUESTION,pageQuestions);
            else if(pageQuestions.size()==0)
                root.put(Constants.ONE_PAGE_QUESTION,"{}");
        }
        root.put(Constants.QUESTION_PAGE_ALL_NUM,question_page_all_num);
        root.put(Constants.ALL_QUESTION_NUM,allQuestionNum);
        root.put(Constants.QUESTION_PAGE_NUM,pageNum);
        //关闭FastJSON循环引用
        String JSON_root=new JSONObject().toJSONString(root, SerializerFeature.DisableCircularReferenceDetect);
        log.info("一页题目");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/showquestion")
    @ResponseBody
    public String showQuestion(int questionId){
        Question question=questionServiceImpl.showQuestion(questionId);
        String JSON_question=new JSONObject().toJSONString(question);
        log.info("一题题目");
        log.info(JSON_question);
        return JSON_question;
    }

    @RequestMapping("/deletequestion")
    @ResponseBody
    public boolean deletequestion(int questionId){
        return questionServiceImpl.deleteQuestion(questionId);
    }

    @RequestMapping("/editquestion")
    public String editquestion(int edit_question_id,int selected_kind, int selected_bank,String questioncontent,
                               @RequestParam(value = "optionacontent",required = false) String optionacontent,
                               @RequestParam(value = "optionbcontent",required = false) String optionbcontent,
                               @RequestParam(value = "optionccontent",required = false) String optionccontent,
                               @RequestParam(value = "optiondcontent",required = false) String optiondcontent,
                               @RequestParam(value = "answer",required = false) String answer,
                               HttpServletRequest request, HttpSession session){
        QuestionBank questionBank= (QuestionBank) session.getAttribute(Constants.SELECT_BANK);
        if(selected_bank!=questionBank.getBankId())
            log.info("QuestionController-addQuestion出错==>前端传送的选中题库Id和session中保存的选中题库Id不一致");
        if(questionServiceImpl.updateQuestion(selected_kind,questioncontent,answer,optionacontent,optionbcontent
                ,optionccontent,optiondcontent,edit_question_id))
            request.setAttribute(Constants.ERROR_KEY,"修改成功");
        else
            request.setAttribute(Constants.ERROR_KEY,"修改失败");
        return "forward:/gotoeditquestionpage?questionId="+edit_question_id;
    }

    @RequestMapping("/getpagepapercontentquestion")
    @ResponseBody
    public String getPagePaperContent(int paperId,int pageNum,int onePageNum,String search){
        int paper_all_question_num=0;//试卷题目总数
        float paper_all_mark=0;//试卷总分
        int question_page_all_num=1;//总页数
        int allQuestionNum=0;//问题总个数
        Map<String,Object> root=new HashMap<String, Object>();
        List<PaperContent> paperContents=new ArrayList<PaperContent>();

        List<PaperContent> allPaperContents=paperContentServiceImpl.getPaperContentByPaperId(paperId);
        paper_all_question_num=allPaperContents.size();
        for(PaperContent paperContent:allPaperContents)
            paper_all_mark+=paperContent.getQuestionMark();
        root.put(Constants.PAPER_ALL_QUESTION_NUM,paper_all_question_num);
        root.put(Constants.PAPER_ALL_MARK,paper_all_mark);

        if(search!=""){
            List<PaperContent> pagePaperContent=new ArrayList<PaperContent>();
            for(PaperContent paperContent:allPaperContents){
                if(paperContent.getQuestion().getQuestionContent().indexOf(search)!=-1){
                    allQuestionNum++;
                    pagePaperContent.add(paperContent);
                }
            }
            if(allQuestionNum!=0){
                if (allQuestionNum%Constants.ADD_QUESTION_PAPER_PAGE_NUM==0)
                    question_page_all_num=allQuestionNum/Constants.ADD_QUESTION_PAPER_PAGE_NUM;
                else
                    question_page_all_num=allQuestionNum/Constants.ADD_QUESTION_PAPER_PAGE_NUM+1;
            }
            else if (question_page_all_num==0)
                question_page_all_num=1;

            if(pageNum<=0)
                pageNum=1;
            if(pageNum>question_page_all_num)
                pageNum=question_page_all_num;

            int start=(pageNum-1)*Constants.ADD_QUESTION_PAPER_PAGE_NUM;
            int end=pageNum*Constants.ADD_QUESTION_PAPER_PAGE_NUM;
            if(end>allQuestionNum)
                end=allQuestionNum;
            if(pageNum>1&&end<start) {
                pageNum-=1;
                start=(pageNum-1)*Constants.ADD_QUESTION_PAPER_PAGE_NUM+1;
                end=pageNum*Constants.ADD_QUESTION_PAPER_PAGE_NUM;
            }
            for(int i=start;i<end;i++)
                paperContents.add(pagePaperContent.get(i));
        }
        else if(search==""){
            allQuestionNum=paper_all_question_num;
            if(allQuestionNum!=0){
                if (allQuestionNum%Constants.ADD_QUESTION_PAPER_PAGE_NUM==0)
                    question_page_all_num=allQuestionNum/Constants.ADD_QUESTION_PAPER_PAGE_NUM;
                else
                    question_page_all_num=allQuestionNum/Constants.ADD_QUESTION_PAPER_PAGE_NUM+1;
            }
            else if (question_page_all_num==0)
                question_page_all_num=1;

            if(pageNum<=0)
                pageNum=1;
            if(pageNum>question_page_all_num)
                pageNum=question_page_all_num;

            paperContents=paperContentServiceImpl.getPagePaperContentQuestion(paperId,pageNum
                    ,Constants.ADD_QUESTION_PAPER_PAGE_NUM);
            if(pageNum>1&&paperContents.size()<=0){
                pageNum-=1;
                paperContents=paperContentServiceImpl.getPagePaperContentQuestion(paperId,pageNum
                        ,Constants.ADD_QUESTION_PAPER_PAGE_NUM);
            }
        }
        if(paperContents.size()>0)
            root.put(Constants.ONE_PAGE_QUESTION,paperContents);
        else if(paperContents.size()==0)
            root.put(Constants.ONE_PAGE_QUESTION,"{}");
        root.put(Constants.QUESTION_PAGE_ALL_NUM,question_page_all_num);
        root.put(Constants.ALL_QUESTION_NUM,allQuestionNum);
        root.put(Constants.QUESTION_PAGE_NUM,pageNum);

        String JSON_root=new JSONObject().toJSONString(root,SerializerFeature.DisableCircularReferenceDetect);
        log.info("一页试卷题目");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/getpagebankquestion")
    @ResponseBody
    public String getPageBankQuestion(HttpSession session,int pageNum,int onePageNum,String search
            ,int bankId,int kindId,int paperId){
        User user= (User) session.getAttribute(Constants.USER_KEY);

        int question_page_all_num=1;//总页数
        int allQuestionNum=0;//问题总个数
        int paper_all_question_num=0;//试卷题目总数
        float paper_all_mark=0;//试卷总分

        Map<String,Object> root=new HashMap<String, Object>();

        List<Question> pageQuestions=new ArrayList<Question>();

        List<PaperContent> paperContents=new ArrayList<PaperContent>();

        List<Integer> allBankIds=new ArrayList<Integer>();//题库Id
        List<Integer> bankQuestionId=new ArrayList<Integer>();//一个题库题目id
        List<List<Integer>> banksQuestionId=new ArrayList<List<Integer>>();//多个题库题目id


        if(bankId==0){//所有题库
            allBankIds=questionBankServiceImpl.getAllBankId(user.getUserId());
        }
        else if(bankId!=0){
            allBankIds.add(bankId);
        }

        if(kindId==0){//所有题型
            for(Integer allBankId:allBankIds){
                bankQuestionId=questionServiceImpl.getAllQuestionIdByBankId(allBankId,search);
                allQuestionNum+=bankQuestionId.size();
                banksQuestionId.add(bankQuestionId);
            }
        }
        else if(kindId!=0){//单个题型
            for(Integer allBankId:allBankIds) {
                bankQuestionId = questionServiceImpl.getAllQuestionIdByBankIdAndKind(allBankId, kindId,search);
                allQuestionNum += bankQuestionId.size();
                banksQuestionId.add(bankQuestionId);
            }
        }

        if(allQuestionNum!=0){
            if(allQuestionNum%Constants.ADD_QUESTION_BANK_PAGE_NUM==0)
                question_page_all_num=allQuestionNum/Constants.ADD_QUESTION_BANK_PAGE_NUM;
            else
                question_page_all_num=allQuestionNum/Constants.ADD_QUESTION_BANK_PAGE_NUM+1;
        }
        else if(allQuestionNum==0){
            question_page_all_num=1;
        }

        if(allQuestionNum<=0)
            pageNum=1;
        if(pageNum>question_page_all_num)
            pageNum=question_page_all_num;

        int start=(pageNum-1)*Constants.ADD_QUESTION_BANK_PAGE_NUM+1;//从1开始
        int end=pageNum*Constants.ADD_QUESTION_BANK_PAGE_NUM;
        int num=0;
        if(end>allQuestionNum)
            end=allQuestionNum;
        if(pageNum>1&&end<start) {
            pageNum-=1;
            start=(pageNum-1)*Constants.ADD_QUESTION_BANK_PAGE_NUM+1;
            end=pageNum*Constants.ADD_QUESTION_BANK_PAGE_NUM;
        }

        for(int i=0;i<banksQuestionId.size();i++){
            List<Integer> questionIds=banksQuestionId.get(i);
            int oneBankQuestionSize=questionIds.size();
            boolean breakFlag=false;

            if(num+oneBankQuestionSize<start) {
                num+=oneBankQuestionSize;
                continue;
            }
            for(int j=0;j<oneBankQuestionSize;j++){
                num++;
                if(num>=start&&num<=end){
                    Question question=questionServiceImpl.getQuestion(questionIds.get(j));
                    //用Question中的statu来表示该问题是否已经添加进试卷中--1已添加，0未添加
                    if(paperContentServiceImpl.getQuestionNumByPaperIdAndQuestionId(paperId,question.getQuestionId())>=1)
                        question.setStatu(1);
                    else
                        question.setStatu(0);
                    pageQuestions.add(question);
                }

                if(num>end){
                    breakFlag=true;
                    break;
                }
            }
            if(breakFlag)
                break;
        }

        paperContents=paperContentServiceImpl.getQuestionByPaperId(paperId);
        for (PaperContent paperContent:paperContents)
            paper_all_mark+=paperContent.getQuestionMark();
        paper_all_question_num=paperContents.size();
        root.put(Constants.PAPER_ALL_QUESTION_NUM,paper_all_question_num);
        root.put(Constants.QUESTION_PAGE_ALL_NUM,question_page_all_num);
        root.put(Constants.PAPER_ALL_MARK,paper_all_mark);

        if(pageQuestions.size()>0)
            root.put(Constants.ONE_PAGE_QUESTION,pageQuestions);
        else if(pageQuestions.size()==0)
            root.put(Constants.ONE_PAGE_QUESTION,"{}");
        root.put(Constants.ALL_QUESTION_NUM,allQuestionNum);
        root.put(Constants.QUESTION_PAGE_NUM,pageNum);

        String JSON_root=new JSONObject().toJSONString(root,SerializerFeature.DisableCircularReferenceDetect);
        log.info("一页题库题目");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/getquestionsinfo")
    @ResponseBody
    public String getQuestionsInfo(HttpSession session){
        User u=(User)session.getAttribute(Constants.USER_KEY);
        Map<String,Object> root=questionServiceImpl.getQuestionsInfo(u);
        String JSON_root=new JSONObject().toJSONString(root);
        return JSON_root;
    }

}
