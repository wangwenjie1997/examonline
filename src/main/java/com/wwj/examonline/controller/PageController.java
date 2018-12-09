package com.wwj.examonline.controller;

import com.wwj.examonline.globle.Constants;
import com.wwj.examonline.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class PageController {

    @RequestMapping("/gotopage")
    public String pageName(HttpServletRequest request,String pagename){
        if(pagename.equals("login")){
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

}
