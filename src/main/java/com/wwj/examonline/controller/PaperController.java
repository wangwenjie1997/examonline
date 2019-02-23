package com.wwj.examonline.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.Page;
import com.sun.mail.imap.protocol.ID;
import com.wwj.examonline.entity.*;
import com.wwj.examonline.globle.Constants;
import com.wwj.examonline.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
public class PaperController {

    @Autowired
    private PaperGroupService paperGroupServiceImpl;

    @Autowired
    private PaperInfoService paperInfoServiceImpl;

    @Autowired
    private PaperContentService paperContentServiceImpl;

    @Autowired
    private QuestionService questionServiceImpl;

    @Autowired
    private ExamUserService examUserServiceImpl;

    @Autowired
    private ResultService resultServiceImpl;

    @RequestMapping("/getpaper")
    @ResponseBody
    public String getPaper(String groupId, int pageNum, int onePageNum, String search, HttpSession session){
        int paper_page_all_num=1;//总页数
        int allPaperNum=0;//问题总个数
        Map<String,Object> root=new HashMap<String, Object>();
        Page<PaperInfo> pagePaperInfos=null;
        if(groupId==""){
            root.put(Constants.ONE_PAGE_PAPER,"{}");
        }
        else if(groupId!=""){
            PaperGroup paperGroup=paperGroupServiceImpl.getPaperGroupByGroupId(Integer.valueOf(groupId));
            session.setAttribute(Constants.SELECT_GROUP,paperGroup);
            if(search!=""){//搜索
                allPaperNum=paperInfoServiceImpl.getAllSearchPaperNum(search,Integer.valueOf(groupId));
                if(allPaperNum!=0){
                    if(allPaperNum%Constants.PAPER_PAGE_SIZE==0)
                        paper_page_all_num=allPaperNum/Constants.PAPER_PAGE_SIZE;
                    else
                        paper_page_all_num=allPaperNum/Constants.PAPER_PAGE_SIZE+1;
                }
                else if(allPaperNum==0)
                    paper_page_all_num=1;

                if(pageNum<=0)
                    pageNum=1;
                if(pageNum>paper_page_all_num)
                    pageNum=paper_page_all_num;

                pagePaperInfos=paperInfoServiceImpl
                        .searchPagePaper(pageNum,Constants.PAPER_PAGE_SIZE,search,Integer.valueOf(groupId));
                if (pageNum>1&&pagePaperInfos.size()<=0){
                    pageNum-=1;
                    pagePaperInfos=paperInfoServiceImpl
                            .searchPagePaper(pageNum,Constants.PAPER_PAGE_SIZE,search,Integer.valueOf(groupId));
                }
            }
            else if(search==""){//不搜索
                allPaperNum=paperInfoServiceImpl.getAllPaperNum(Integer.valueOf(groupId));
                if(allPaperNum!=0){
                    if(allPaperNum%Constants.PAPER_PAGE_SIZE==0)
                        paper_page_all_num=allPaperNum/Constants.PAPER_PAGE_SIZE;
                    else
                        paper_page_all_num=allPaperNum/Constants.PAPER_PAGE_SIZE+1;
                }
                else if(allPaperNum==0)
                    paper_page_all_num=1;

                if(pageNum<=0)
                    pageNum=1;
                if(pageNum>paper_page_all_num)
                    pageNum=paper_page_all_num;

                pagePaperInfos=paperInfoServiceImpl
                        .getPagePaper(pageNum,Constants.PAPER_PAGE_SIZE,Integer.valueOf(groupId));
                if (pageNum>1&&pagePaperInfos.size()<=0){
                    pageNum-=1;
                    pagePaperInfos=paperInfoServiceImpl
                            .getPagePaper(pageNum,Constants.PAPER_PAGE_SIZE,Integer.valueOf(groupId));
                }
            }
            if(pagePaperInfos.size()>0)
                root.put(Constants.ONE_PAGE_PAPER,pagePaperInfos);
            else if(pagePaperInfos.size()==0)
                root.put(Constants.ONE_PAGE_PAPER,"{}");
        }
        root.put(Constants.PAPER_PAGE_ALL_NUM,paper_page_all_num);
        root.put(Constants.ALL_PAPER_NUM,allPaperNum);
        root.put(Constants.PAPER_PAGE_NUM,pageNum);
        String JSON_root=new JSONObject().toJSONString(root);
        log.info("一页试卷");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/checkpapername")
    @ResponseBody
    public boolean checkPaperName(String paperName,HttpSession session){
        PaperGroup paperGroup= (PaperGroup) session.getAttribute(Constants.SELECT_GROUP);
        return paperInfoServiceImpl.getPaperByGroupIdAndPaperName(paperGroup.getPaperGroupId(),paperName);
    }

    @RequestMapping("/addpaper")
    @ResponseBody
    public String addPaper(String paperName, HttpSession session, HttpServletRequest request){
        Map<String,Object> root=new HashMap<String, Object>();
        PaperGroup paperGroup= (PaperGroup) session.getAttribute(Constants.SELECT_GROUP);
        PaperInfo paperInfo=paperInfoServiceImpl.addPaper(paperName,paperGroup.getPaperGroupId());
        boolean flag=false;
        if(paperInfo!=null){
            root.put("paperInfo",paperInfo);
            flag=true;
        }
        else {
            root.put("paperInfo", "{}");
            flag=false;
        }
        root.put("add_paper_result",flag);
        String JSON_root=new JSONObject().toJSONString(root);
        log.info("添加试卷信息");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/settingpaper")
    @ResponseBody
    public boolean settingpaper(String paperName,float oneOptionMark,float moreOptionMark,float trueOrFalseMark,float fillInBlankMark
            ,float shortAnswerQuestionMark,boolean oneOptionCheck,boolean moreOptionCheck,boolean trueOrFalseCheck
            ,boolean fillInBlankCheck,int paperId){
        int oneoptioncheck=oneOptionCheck?1:0;
        int moreoptioncheck=moreOptionCheck?1:0;
        int trueorfalsecheck=trueOrFalseCheck?1:0;
        int fillinblankcheck=fillInBlankCheck?1:0;
        return paperInfoServiceImpl.settingPaper(paperName,oneoptioncheck,moreoptioncheck,trueorfalsecheck,fillinblankcheck
                ,oneOptionMark,moreOptionMark,trueOrFalseMark,fillInBlankMark,shortAnswerQuestionMark,paperId);
    }

    @RequestMapping("/deletepaper")
    @ResponseBody
    public boolean deletePaper(int paperId){
        return paperInfoServiceImpl.deletePaper(paperId);
    }

    @RequestMapping("/editpapercontentmark")
    @ResponseBody
    public boolean editPaperContentMark(int paperContentId,float mark){
        return paperContentServiceImpl.editPaperContentMark(paperContentId,mark);
    }

    @RequestMapping("/deletepapercontentquestion")
    @ResponseBody
    public boolean deletePaperContentQuestion(int paperContentId){
        return paperContentServiceImpl.deletePaperContent(paperContentId);
    }

    @RequestMapping("/addquestiontopaper")
    @ResponseBody
    public boolean addQuestionToPaper(int questionId,int paperId){
        return paperContentServiceImpl.addQuestionToPaper(questionId,paperId);
    }

    @RequestMapping("/deletequestiontopaper")
    @ResponseBody
    public boolean deleteQuestionToPaper(int questionId,int paperId){
        return paperContentServiceImpl.deleteQuestionToPaper(questionId,paperId);
    }

    @RequestMapping("/getshowpaper")
    @ResponseBody
    public String getShowPaper(int paperId){

        Map<String,Object> root=new HashMap<String, Object>();
        List<Integer> oneOption=new ArrayList<Integer>();
        List<Integer> moreOption=new ArrayList<Integer>();
        List<Integer> trueOrFalse=new ArrayList<Integer>();
        List<Integer> fillInBlank=new ArrayList<Integer>();
        List<Integer> shortAnswerQuestion=new ArrayList<Integer>();
        List<PaperContent> paperContents=paperContentServiceImpl.getPaperQuestionByPaperId(paperId);
        PaperInfo paperInfo=paperInfoServiceImpl.getPaperByPaperId(paperId);
        for (PaperContent paperContent:paperContents){
            Question question=paperContent.getQuestion();
            int kindId=question.getQuestionKind().getKindId();
            int paperContentId=paperContent.getPaperCotentId();
            switch (kindId){
                case 1://单选
                    oneOption.add(paperContentId);
                    break;
                case 2://多选
                    moreOption.add(paperContentId);
                    break;
                case 3://判断
                    trueOrFalse.add(paperContentId);
                    break;
                case 4://填空
                    fillInBlank.add(paperContentId);
                    break;
                case 5://简答
                    shortAnswerQuestion.add(paperContentId);
                    break;
            }
        }
        root.put("paperName",paperInfo.getPaperName());
        root.put("oneOption",oneOption);
        root.put("moreOption",moreOption);
        root.put("trueOrFalse",trueOrFalse);
        root.put("fillInBlank",fillInBlank);
        root.put("shortAnswerQuestion",shortAnswerQuestion);
        String JSON_root=new JSONObject().toJSONString(root, SerializerFeature.DisableCircularReferenceDetect);
        log.info("一张试卷");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/getpapercontent")
    @ResponseBody
    public String getPaperContent(int paperContentId){
        PaperContent paperContent=paperContentServiceImpl.getPaperContentByContentId(paperContentId);
        Question question=paperContent.getQuestion();
        question.setQuestionAnsewer("");
        paperContent.setQuestion(question);
        String JSON_root=new JSONObject().toJSONString(paperContent);
        log.info("一条paperContent");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/releasepaper")
    @ResponseBody
    public boolean releasePaper(int paperId,String paperStart,String paperEnd){
        return paperInfoServiceImpl.releasePaper(paperStart,paperEnd,paperId);
    }

    @RequestMapping("/getpapertime")
    @ResponseBody
    public String getPaperTime(int paperId){
        PaperInfo paperInfo=paperInfoServiceImpl.getPaperByPaperId(paperId);
        String JSON_paperInfo=new JSONObject().toJSONString(paperInfo);
        return JSON_paperInfo;
    }

    @RequestMapping("/cancelrelease")
    @ResponseBody
    public boolean cancelRelease(int paperId){
        return paperInfoServiceImpl.releasePaper(null,null,paperId);
    }

    @RequestMapping("/getmyexam")
    @ResponseBody
    public String getMyExam(int pageNum,int onePageNum,String search,HttpSession session){
        User user= (User) session.getAttribute(Constants.USER_KEY);
        int exam_page_all_num=1;//总页数
        int allExamNum=0;//考试总个数
        Map<String,Object> root=new HashMap<String, Object>();
        List<ExamUser> examUsers=new ArrayList<ExamUser>();

        List<ExamUser> allExamUser=examUserServiceImpl.selectExamUserByUserId(user.getUserId());
        if(search!=""){
            List<ExamUser> pageEamUser=new ArrayList<ExamUser>();
            for (ExamUser examUser:allExamUser){
                if(examUser.getPapeInfo().getPaperName().indexOf(search)!=-1){
                    allExamNum++;
                    pageEamUser.add(examUser);
                }
            }

            if(allExamNum!=0){
                if(allExamNum%Constants.MYEXAM_PAGE_SIZE==0)
                    exam_page_all_num=allExamNum/Constants.MYEXAM_PAGE_SIZE;
                else
                    exam_page_all_num=allExamNum/Constants.MYEXAM_PAGE_SIZE+1;
            }
            else if (allExamNum==0)
                exam_page_all_num=1;

            if(pageNum<=0)
                pageNum=1;
            if(pageNum>exam_page_all_num)
                pageNum=exam_page_all_num;

            int start=(pageNum-1)*Constants.MYEXAM_PAGE_SIZE;
            int end=pageNum*Constants.MYEXAM_PAGE_SIZE;
            if(end>allExamNum)
                end=allExamNum;
            if(pageNum>1&&end<start) {
                pageNum-=1;
                start=(pageNum-1)*Constants.MYEXAM_PAGE_SIZE+1;
                end=pageNum*Constants.MYEXAM_PAGE_SIZE;
            }
            for(int i=start;i<end;i++)
                examUsers.add(pageEamUser.get(i));
        }
        else if (search==""){
            allExamNum=allExamUser.size();
            if(allExamNum!=0){
                if(allExamNum%Constants.MYEXAM_PAGE_SIZE==0)
                    exam_page_all_num=allExamNum/Constants.MYEXAM_PAGE_SIZE;
                else
                    exam_page_all_num=allExamNum/Constants.MYEXAM_PAGE_SIZE+1;
            }
            else if (allExamNum==0)
                exam_page_all_num=1;

            if(pageNum<=0)
                pageNum=1;
            if(pageNum>exam_page_all_num)
                pageNum=exam_page_all_num;

            examUsers=examUserServiceImpl.getPageExamUserTwo(user.getUserId(),pageNum,Constants.MYEXAM_PAGE_SIZE);

            if(pageNum>1&&examUsers.size()<=0){
                pageNum-=1;
                examUsers=examUserServiceImpl.getPageExamUserTwo(user.getUserId(),pageNum,Constants.MYEXAM_PAGE_SIZE);
            }
        }

        if(examUsers.size()>0)
            root.put("one_page_exam",examUsers);
        else
            root.put("one_page_exam","{}");
        root.put("exam_page_all_num",exam_page_all_num);
        root.put("all_exam_num",allExamNum);
        root.put("exam_pageNum",pageNum);
        String JSON_root=new JSONObject().toJSONString(root);
        log.info("一页我的考试");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/getexampaperinfo")
    @ResponseBody
    public String getExamPaperInfo(int paperId){
        Map<String,Object> root=new HashMap<String, Object>();
        float paperGrade=0;
        int paperQuestionNum=0;
        PaperInfo paperInfo=paperInfoServiceImpl.getExamPaper(paperId);
        User user=paperInfo.getPaperGroup().getUser();
        List<PaperContent> paperContents=paperContentServiceImpl.getQuestionByPaperId(paperId);
        if(paperContents!=null&&paperContents.size()>0){
            for (PaperContent paperContent:paperContents)
                paperGrade+=paperContent.getQuestionMark();
            paperQuestionNum=paperContents.size();
        }
        root.put("paperCreateName",user.getName());
        root.put("paperCreateMail",user.getMail());
        root.put("paperGrade",paperGrade);
        root.put("paperQuestionNum",paperQuestionNum);
        String JSON_root=new JSONObject().toJSONString(root);
        log.info("试卷信息");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/getexampaper")
    @ResponseBody
    public String getExamPaper(int paperId,HttpSession session){
        User user= (User) session.getAttribute(Constants.USER_KEY);
        Map<String,Object> root=new HashMap<String, Object>();
        List<Result> oneOption=new ArrayList<Result>();
        List<Result> moreOption=new ArrayList<Result>();
        List<Result> trueOrFalse=new ArrayList<Result>();
        List<Result> fillInBlank=new ArrayList<Result>();
        List<Result> shortAnswerQuestion=new ArrayList<Result>();
        List<PaperContent> paperContents=paperContentServiceImpl.getPaperQuestionByPaperId(paperId);

        PaperInfo paperInfo=paperInfoServiceImpl.getPaperByPaperId(paperId);
        Result result=new Result();
        for (int i=0;i<paperContents.size();i++){
            PaperContent paperContent=paperContents.get(i);
            Question question=paperContent.getQuestion();
            int kindId=question.getQuestionKind().getKindId();
            int paperContentId=paperContent.getPaperCotentId();

            result=resultServiceImpl.getResultByUserIdAndContentIdAndPaperId(user.getUserId(),paperContentId,paperId);
            PaperContent resultPaperContent=new PaperContent();
            resultPaperContent.setPaperCotentId(paperContentId);
            result.setPaperContent(resultPaperContent);

            switch (kindId){
                case 1: //单选
                    oneOption.add(result);
                    break;
                case 2://多选
                    moreOption.add(result);
                    break;
                case 3://判断
                    trueOrFalse.add(result);
                    break;
                case 4://填空
                    fillInBlank.add(result);
                    break;
                case 5://简答
                    shortAnswerQuestion.add(result);
                    break;
            }
        }
        root.put("paperName",paperInfo.getPaperName());
        root.put("oneOption",oneOption);
        root.put("moreOption",moreOption);
        root.put("trueOrFalse",trueOrFalse);
        root.put("fillInBlank",fillInBlank);
        root.put("shortAnswerQuestion",shortAnswerQuestion);
        String JSON_root=new JSONObject().toJSONString(root, SerializerFeature.DisableCircularReferenceDetect);
        log.info("一张试卷");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/getfinishexampaper")
    @ResponseBody
    public String getFinishExamPaper(String search,HttpSession session){
        User user= (User) session.getAttribute(Constants.USER_KEY);

        List<PaperGroup> paperGroups=paperGroupServiceImpl.getPaperGroupByUserId(user);
        List<PaperInfo> paperInfos=new ArrayList<PaperInfo>();


        for (PaperGroup paperGroup:paperGroups){
            for(PaperInfo paperInfo:paperInfoServiceImpl.getPaperInfoByPaperGroupId(paperGroup.getPaperGroupId())){
                if(paperInfo.getPaperEnd()!=null){
                    Date d=new Date();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String nowStrDate=df.format(d);
                    if(nowStrDate.compareTo(paperInfo.getPaperEnd())>0){
                        if(paperInfo.getPaperName().contains(search))
                            paperInfos.add(paperInfo);
                    }
                }
            }
        }

        String JSON_root=new JSONObject().toJSONString(paperInfos);
        log.info("已完成考试");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/getmarkpaper")
    @ResponseBody
    public String getMarkPaper(int paperId,int userId){

        Map<String,Object> root=new HashMap<String, Object>();
        List<Result> oneOption=new ArrayList<Result>();
        List<Result> moreOption=new ArrayList<Result>();
        List<Result> trueOrFalse=new ArrayList<Result>();
        List<Result> fillInBlank=new ArrayList<Result>();
        List<Result> shortAnswerQuestion=new ArrayList<Result>();

        ExamUser examUser=examUserServiceImpl.getExamUserByUserIdAndPaperId(userId,paperId);
        if(examUser.getJoinExam()!=0&&examUser.getExamGrade()==-1)//参加了考试且没有批改
            resultServiceImpl.getAutoCheckGrade(userId,paperId);//自动批改

        PaperInfo paperInfo=paperInfoServiceImpl.getPaperByPaperId(paperId);

        List<PaperContent> paperContents=paperContentServiceImpl.getPaperQuestionByPaperId(paperId);
        Question question;
        Result result;
        for (PaperContent paperContent:paperContents){
            question=paperContent.getQuestion();
            result=resultServiceImpl.getResultByUserIdAndContentIdAndPaperId(userId,paperContent.getPaperCotentId(),paperId);
//            result.setPaperContent(paperContent);
            switch (question.getQuestionKind().getKindId()){
                case 1: //单选
                    oneOption.add(result);
                    break;
                case 2://多选
                    moreOption.add(result);
                    break;
                case 3://判断
                    trueOrFalse.add(result);
                    break;
                case 4://填空
                    fillInBlank.add(result);
                    break;
                case 5://简答
                    shortAnswerQuestion.add(result);
                    break;
            }
        }

        root.put("paperName",paperInfo.getPaperName());
        root.put("oneOption",oneOption);
        root.put("moreOption",moreOption);
        root.put("trueOrFalse",trueOrFalse);
        root.put("fillInBlank",fillInBlank);
        root.put("shortAnswerQuestion",shortAnswerQuestion);
        String JSON_root=new JSONObject().toJSONString(root, SerializerFeature.DisableCircularReferenceDetect);
        log.info("一张已完成试卷");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/getpaperallgrade")
    @ResponseBody
    public String getPaperAllGrade(int paperId){
        return ""+paperContentServiceImpl.getPaperAllGrade(paperId);
    }

}
