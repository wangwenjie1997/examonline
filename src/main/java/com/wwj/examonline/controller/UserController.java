package com.wwj.examonline.controller;

import com.wwj.examonline.entity.User;
import com.wwj.examonline.globle.Constants;
import com.wwj.examonline.service.UserService;
import com.wwj.examonline.util.CookieUtil;
import com.wwj.examonline.util.FileUtils;
import com.wwj.examonline.util.VerificationCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Controller
public class UserController {

    @Autowired
    private UserService userServiceImpl;

    @RequestMapping("/login")
    public String login(@RequestParam(value = "mail", required = true) String mail,
                        @RequestParam(value = "password", required = true) String password,
                        @RequestParam(value = "remember_password", required = false) String remember_password,
                        HttpSession session, HttpServletRequest request,HttpServletResponse response){
        User u=userServiceImpl.login(mail,password);
        if(u!=null){
            session.setAttribute(Constants.USER_KEY,u);
            Cookie [] cookies=request.getCookies();
            if(remember_password!=null) {//记住密码
                log.info("记住密码");
                int maxAge=30*24*60*60;//存活期为一个月 30*24*60*60
                CookieUtil.addCookie(response,Constants.MAIL_KEY,mail,maxAge);
                CookieUtil.addCookie(response,Constants.PASSWORD_KEY,password,maxAge);
                CookieUtil.addCookie(response,Constants.CHECKBOX_STATUS_KEY,"checked",maxAge);
            }
            else {//不记住密码
                log.info("不记住密码");
                CookieUtil.removeCookie(response,Constants.MAIL_KEY);
                CookieUtil.removeCookie(response,Constants.PASSWORD_KEY);
                CookieUtil.removeCookie(response,Constants.CHECKBOX_STATUS_KEY);
            }
            return "redirect:/gotopage?pagename=index";
        }
        else{
            request.setAttribute(Constants.ERROR_KEY,"账号密码错误");
            return "login";
        }
    }

    @RequestMapping("/regist")
    public String regist(User user,HttpSession session, HttpServletRequest request){
        User u=userServiceImpl.regist(user);
        if(u!=null) {
            session.setAttribute(Constants.USER_KEY,u);
            return "redirect:/gotopage?pagename=index";
        }
        else{
            request.setAttribute(Constants.ERROR_KEY,"注册失败");
            return "regist";
        }
    }

    @RequestMapping("/checkmail")
    @ResponseBody
    public boolean checkMail(String mail){
        if(userServiceImpl.checkMail(mail)!=null)
            return false;
        else
            return true;
    }

    @RequestMapping("/editpassword")
    public String editPassword(String oldpassword,String password,HttpSession session
            ,HttpServletRequest request,HttpServletResponse response){
        User u=(User)session.getAttribute(Constants.USER_KEY);
        if(oldpassword.equals(u.getPassword())){
            if (oldpassword.equals(password)){
                request.setAttribute(Constants.ERROR_KEY,"新密码不能与原密码相同");
            }
            else {
                if(userServiceImpl.editPassword(password,u.getUserId())){
                    u.setPassword(password);
                    session.setAttribute(Constants.USER_KEY,u);

                    CookieUtil.removeCookie(response,Constants.MAIL_KEY);
                    CookieUtil.removeCookie(response,Constants.PASSWORD_KEY);
                    CookieUtil.removeCookie(response,Constants.CHECKBOX_STATUS_KEY);

                    request.setAttribute(Constants.ERROR_KEY,"修改成功");
                }
                else
                    request.setAttribute(Constants.ERROR_KEY,"修改失败");
            }
        }
        else {
            request.setAttribute(Constants.ERROR_KEY,"原密码错误");
        }
        return "editpassword";
    }

    @RequestMapping("/editmail")
    public String editmail(String mail,HttpSession session
            ,HttpServletRequest request,HttpServletResponse response){
        User u=(User)session.getAttribute(Constants.USER_KEY);
        if(userServiceImpl.editMail(mail,u.getUserId())){
            u.setMail(mail);
            session.setAttribute(Constants.USER_KEY,u);

            CookieUtil.removeCookie(response,Constants.MAIL_KEY);
            CookieUtil.removeCookie(response,Constants.PASSWORD_KEY);
            CookieUtil.removeCookie(response,Constants.CHECKBOX_STATUS_KEY);

            request.setAttribute(Constants.ERROR_KEY,"修改成功");
        }
        else
            request.setAttribute(Constants.ERROR_KEY,"修改失败");
        return "editmail";
    }

    @RequestMapping("/editname")
    public String editName(String name,HttpSession session,HttpServletRequest request){
        User u=(User)session.getAttribute(Constants.USER_KEY);
        if(userServiceImpl.editName(name,u.getUserId())){
            u.setName(name);
            session.setAttribute(Constants.USER_KEY,u);
            request.setAttribute(Constants.ERROR_KEY,"修改成功");
        }
        else
            request.setAttribute(Constants.ERROR_KEY,"修改失败");
        return "editname";
    }

    /**
     * 生成验证码
     */
    @RequestMapping(value = "/getverify")
    public void getVerify(HttpSession session,HttpServletRequest request, HttpServletResponse response) {
        //利用图片工具生成图片
        //第一个参数是生成的验证码，第二个参数是生成的图片
        Object[] objs = VerificationCodeUtil.createImage();
        //将验证码存入Session
        session.setAttribute(Constants.VERIFY_KEY,objs[0]);

        //将图片输出给浏览器
        BufferedImage image = (BufferedImage) objs[1];
        response.setContentType("image/png");
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            log.info("获取验证码失败");
            session.removeAttribute(Constants.VERIFY_KEY);
            e.printStackTrace();
        }
    }

    @RequestMapping("/forgetpassword")
    public String forgetPassword(String mail,String verificationcode,HttpSession session,HttpServletRequest request){
        log.info("---------------------------------forgetpassword");
        String checkcode= (String) session.getAttribute(Constants.VERIFY_KEY);
        User u=userServiceImpl.checkMail(mail);
        if(u!=null){
            if(checkcode.equalsIgnoreCase(verificationcode)) {
                userServiceImpl.sendMail(Constants.MAIL_SENDER_KEY,u.getMail()
                        ,Constants.MAIL_TITLE_KEY,u.getName()+Constants.MAIL_CONTENT_KEY+u.getPassword());
                request.setAttribute(Constants.ERROR_KEY, "发送成功");
            }
            else
                request.setAttribute(Constants.ERROR_KEY,"验证码错误");
        }
        else
            request.setAttribute(Constants.ERROR_KEY,"该邮箱还未注册");
        return "forgetpassword";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/gotopage?pagename=login";
    }

    @RequestMapping("/editphoto")
    public String upload(@RequestParam("fileName") MultipartFile file,String edittype, HttpServletRequest request,HttpSession session){
        //重置头像
        if(edittype.equals("reset_avatar")){
            User u= (User) session.getAttribute(Constants.USER_KEY);
            User user=userServiceImpl.getUser(u.getMail(),u.getPassword());
            String picPath=user.getPicPath();
            u.setPicPath(picPath);
            session.setAttribute(Constants.USER_KEY,u);
            request.setAttribute(Constants.ERROR_KEY,"取消预览成功");
        }
        else {
            //上传成功或者失败的提示
            String msg = "";
            if(file.getSize()/1000>100){
                msg="图片大小不能超过100KB";
            }
            else {
                // 要上传的目标文件存放路径
                String localPath = Constants.PIC_SAVE_FULL_PATH;

                //判断上传文件格式
                String fileType=file.getContentType();
                if(fileType.equals("image/jpeg")||fileType.equals("image/png")||fileType.equals("image/jpeg")){
                    if (FileUtils.upload(file, localPath, file.getOriginalFilename())){
                        // 上传成功，给出页面提示
                        User u= (User) session.getAttribute(Constants.USER_KEY);
                        String fileName=file.getOriginalFilename();
                        String filePath=Constants.PIC_SAVE_PATH+fileName;
                        u.setPicPath(filePath);
                        session.setAttribute(Constants.USER_KEY,u);
                        if(edittype.equals("change_avatar")&&userServiceImpl.editPicPath(filePath,u.getUserId())){
                            msg = "更换成功！";
                        }
                        else if(edittype.equals("show_avatar")) {
                            msg = "预览成功！";
                        }
                    }else{
                        if(edittype.equals("change_avatar")) {
                            msg = "更换失败！";
                        }
                        else if(edittype.equals("show_avatar")) {
                            msg = "预览失败！";
                        }
                    }
                }
                else
                    msg="请选择jpg或png格式图片";
            }
            request.setAttribute(Constants.ERROR_KEY,msg);
        }

        // 显示图片
        return "editphoto";
    }
}
