package com.wwj.examonline.controller;

import com.wwj.examonline.entity.*;
import com.wwj.examonline.globle.Constants;
import com.wwj.examonline.service.*;
import com.wwj.examonline.serviceImpl.QuestionServiceImpl;
import com.wwj.examonline.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class PageController {

    @Autowired
    private QuestionKindService questionKindServiceImpl;

    @Autowired
    private QuestionServiceImpl questionServiceImpl;

    @Autowired
    private PaperInfoService paperInfoServiceImpl;

    @Autowired
    private QuestionBankService questionBankServiceImpl;

    @Autowired
    private PaperContentService paperContentServiceImpl;

    @Autowired
    private ResultService resultServiceImpl;

    @Autowired
    private ExamUserService examUserServiceImpl;

    @RequestMapping("/gotopage")
    public String pageName(HttpSession session,HttpServletRequest request, String pagename){
        if(pagename.equals("login")){//登录时记住密码判断
            String mail=CookieUtil.getCookie(request,Constants.MAIL_KEY);
            String password=CookieUtil.getCookie(request,Constants.PASSWORD_KEY);
            String checkbox_statue=CookieUtil.getCookie(request,Constants.CHECKBOX_STATUS_KEY);
            if(mail!=null &&password!=null && checkbox_statue!=null){
                request.setAttribute(Constants.MAIL_KEY,mail);
                request.setAttribute(Constants.PASSWORD_KEY,password);
                request.setAttribute(Constants.CHECKBOX_STATUS_KEY,checkbox_statue);
            }
        }
        return pagename;
    }

    @RequestMapping("/gotoaddquestionpage")
    public String gotoAddQuestionPage(HttpSession session, HttpServletRequest request, int selectbank
            , @RequestParam(value = "selectedkind",required = false) int selectedkind){
//        QuestionBank questionBank=questionBankServiceImpl.getQuestionBankByBankId(selectbank);
        QuestionBank questionBank= (QuestionBank) session.getAttribute(Constants.SELECT_BANK);
        if(selectbank!=questionBank.getBankId())
            log.info("PageController-gotoAddQuestionPage==>前端传送的选中题库Id和session中保存的选中题库Id不一致");
        List<QuestionKind> questionKinds=questionKindServiceImpl.getQuestionKind();
        request.setAttribute(Constants.QUESTIN_KIND,questionKinds);
        if(selectedkind>0){
            for(QuestionKind questionKind:questionKinds){
                if(questionKind.getKindId()==selectedkind){
                    request.setAttribute(Constants.SELECT_KIND,questionKind);
                    break;
                }
            }
        }
        else
            request.setAttribute(Constants.SELECT_KIND,questionKinds.get(0));
        return "forward:/gotopage?pagename=addquestion";
    }

    @RequestMapping("/gotoeditquestionpage")
    public String gotoEditQuestionPage(HttpSession session, HttpServletRequest request, int questionId){

        List<QuestionKind> questionKinds=questionKindServiceImpl.getQuestionKind();
        request.setAttribute(Constants.QUESTIN_KIND,questionKinds);
        Question question=questionServiceImpl.showQuestion(questionId);
        log.info(question.toString());
        request.setAttribute(Constants.EDIT_QUESTION,question);
        return "forward:/gotopage?pagename=editquestion";
    }

    @RequestMapping("/gotoaddpaperpage")
    public String gotoAddPaperPage(int selectedgroup,HttpSession session,HttpServletRequest request){
//        log.info("PageController-gotoAddPaperPage-84");
//        request.setAttribute(Constants.ADD_PAPER,paperInfoServiceImpl.getPaperByPaperId(12));

        User user= (User) session.getAttribute(Constants.USER_KEY);

        List<QuestionKind> questionKinds=questionKindServiceImpl.getQuestionKind();
        request.setAttribute(Constants.ALL_KIND,questionKinds);

        List<QuestionBank> questionBanks=questionBankServiceImpl.getAllQuestionBank(user.getUserId());
        request.setAttribute(Constants.ALL_BANK,questionBanks);

        PaperGroup paperGroup= (PaperGroup) session.getAttribute(Constants.SELECT_GROUP);
        if(paperGroup.getPaperGroupId()!=selectedgroup)
            log.info("PageController-gotoAddPaperPage==>前端传送的选中分组Id和session中保存的选中分组Id不一致");
        return "forward:/gotopage?pagename=addpaper";
    }

    @RequestMapping("/gotoeditpaperpage")
    public String gotoEditPaperPage(int paperid,HttpServletRequest request){
        PaperInfo paperInfo=paperInfoServiceImpl.getPaperByPaperId(paperid);
        request.setAttribute(Constants.EDIT_PAPER,paperInfo);
        return "forward:/gotopage?pagename=editpaper";
    }

    @RequestMapping("/gotoshowpaperpage")
    public String gotoShowPaperPage(int paperId,HttpServletRequest request){
        request.setAttribute("exam_paper_id",paperId);
        return "forward:/gotopage?pagename=showpaper";
    }

    @RequestMapping("/gotoexampaperpage")
    public String gotoExamPaperPage(int paperId,HttpSession session,HttpServletRequest request){
        request.setAttribute("exam_paper_id",paperId);
        User user= (User) session.getAttribute(Constants.USER_KEY);
        ExamUser examUser=examUserServiceImpl.getExamUserByUserIdAndPaperId(user.getUserId(),paperId);
        int joinStatu=examUser.getJoinExam();
        if(joinStatu==0||joinStatu==1) {
            resultServiceImpl.joinExam(user.getUserId(), paperId);
            request.setAttribute("joinStatu",'1');
        }
        else if(joinStatu==2)
            request.setAttribute("joinStatu",'2');
        return "forward:/gotopage?pagename=exampaper";
    }

    @RequestMapping("/gotomarkexam")
    public String gotoMarkExam(String pagename,int paperId,HttpServletRequest request){
        request.setAttribute("selectedPaper",paperId);
        return pagename;
    }

    @RequestMapping("/gotomarkpaperpage")
    public String gotoMarkPaperPage(int paperId,int userId,String type,String pagename
            ,HttpServletRequest request){
        //type:tea_show教师查看,tea_mark批改,stu_show学生查看
        request.setAttribute("exam_paper_id",paperId);
        request.setAttribute("exam_paper_userId",userId);
        request.setAttribute("exam_paper_type",type);

        return pagename;
    }

}
