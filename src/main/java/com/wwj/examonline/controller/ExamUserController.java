package com.wwj.examonline.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.Page;
import com.wwj.examonline.entity.ExamUser;
import com.wwj.examonline.entity.User;
import com.wwj.examonline.globle.Constants;
import com.wwj.examonline.service.ExamUserService;
import com.wwj.examonline.service.ResultService;
import com.wwj.examonline.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Controller
public class ExamUserController {

    @Autowired
    private ExamUserService examUserServiceImpl;

    @Autowired
    private ResultService resultServiceImpl;

    @RequestMapping("/addoneexamuser")
    @ResponseBody
    public String addOneExamUser(String mail,String name,int paperId){
        String result=examUserServiceImpl.addOneExamUser(mail,name,paperId);
        Map<String,Object> root=new HashMap<String,Object>();
        root.put("result_msg",result);
        String JSON_root=new JSONObject().toJSONString(root);
        return JSON_root;
    }


    @RequestMapping("/addexcelexamuser")
    @ResponseBody
    public boolean addExcelExamUser(@RequestParam("file") MultipartFile file,String fileName,int paperId) throws IOException {
        String isEmail ="^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(isEmail);
        boolean notNull = false;
        boolean isExcel2003 = true;
        List<User> users=new ArrayList<User>();
        User user;
        if(file.getSize()/1000>1024)
            return false;
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            return false;
        }
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }
        InputStream is = file.getInputStream();
        Workbook wb = null;
        if (isExcel2003) {
            wb = new HSSFWorkbook(is);
        } else {
            wb = new XSSFWorkbook(is);
        }
        Sheet sheet = wb.getSheetAt(0);
        if(sheet!=null){
            notNull = true;
        }

        Row row = sheet.getRow(0);
        String mail=row.getCell(0).getStringCellValue();
        String name=row.getCell(1).getStringCellValue();
        if(!mail.equals("邮箱")||!name.equals("姓名"))
            return false;
        int num=sheet.getLastRowNum();
        for (int r = 1; r <= sheet.getLastRowNum(); r++){

            row = sheet.getRow(r);
            if (row == null){
                continue;
            }
            user = new User();
            if( row.getCell(0)==null) {
                continue;
            }
            mail = row.getCell(0).getStringCellValue();

            Matcher mat = pattern.matcher(mail);
//            boolean isempty=mail.isEmpty();
//            boolean result=mat.find();
            if(mail == null || mail.isEmpty()==true||mat.find()==false){
                continue;
            }
//            if(mail == null)
//                continue;
//            if(mail.isEmpty()==true)
//                continue;
//            if(mat.find()==false)
//                continue;
            if( row.getCell(1)==null) {
                continue;
            }
            name = row.getCell(1).getStringCellValue();
            if(name == null || name.isEmpty()){
                continue;
            }
            user.setMail(mail);
            user.setName(name);
            users.add(user);
        }
        return examUserServiceImpl.addExcelExamUser(users,paperId);
    }

    @RequestMapping("/downloadexamusermould")
    public void   downloadExamUserMould(HttpServletResponse response){
        String downloadFilePath = Constants.EXAMUSER_MOULD_PATH;//被下载的文件在服务器中的路径,
        String fileName = Constants.EXAMUSER_MOULD_FILENAME;//被下载文件的名称
        String filepath=downloadFilePath+File.separator+fileName;
        log.info(filepath);
        File file = new File(filepath);
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开            
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                log.info("下载成功");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (bis != null) {
                    try {
                        bis.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        log.info("下载失败");
    }

    @RequestMapping("/getpageexamuser")
    @ResponseBody
    public String getPageExamUser(int paperId,int pageNum,int onePageNum,String search){
        int examuser_page_all_num=1;//总页数
        int allExamUserNum=0;//考生总个数
        Map<String,Object> root=new HashMap<String,Object>();
        ArrayList<User> pageExamUser=new ArrayList<User>();
        Page<ExamUser> examUsers=null;
        if(search!=""){//搜索
            List<User> allExamUser=null;
            allExamUser=examUserServiceImpl.getAllExamUser(paperId,search);
            if(allExamUser!=null)
                allExamUserNum=allExamUser.size();
            else
                allExamUserNum=0;
            int start=(pageNum-1)*onePageNum;
            int end=pageNum*onePageNum;
            if(end>allExamUserNum)
                end=allExamUserNum;
            if(end==start&&pageNum>1){
                pageNum-=1;
                start=(pageNum-1)*onePageNum;
                end=pageNum*onePageNum;
            }
            for (int i=start;i<end;i++)
                pageExamUser.add(allExamUser.get(i));
        }
        else if(search==""){//不搜索
            allExamUserNum=examUserServiceImpl.getAllExamUserNum(paperId);
            if(allExamUserNum!=0){
                if(allExamUserNum%onePageNum==0)
                    examuser_page_all_num=allExamUserNum/onePageNum;
                else
                    examuser_page_all_num=allExamUserNum/onePageNum+1;
            }
            else if(allExamUserNum==0){
                examuser_page_all_num=1;
            }

            if(pageNum<=0)
                pageNum=1;
            if(pageNum>examuser_page_all_num)
                pageNum=examuser_page_all_num;

            examUsers=examUserServiceImpl.getPageExamUser(pageNum,onePageNum,paperId);
            if(pageNum>1&&examUsers.size()<=0){
                pageNum-=1;
                examUsers=examUserServiceImpl.getPageExamUser(pageNum,onePageNum,paperId);
            }

            for (ExamUser examUser:examUsers)
                pageExamUser.add(examUser.getUser());
        }
        root.put("one_page_examuser",pageExamUser);
        root.put("examuser_page_all_num",examuser_page_all_num);
        root.put("allExamUserNum",allExamUserNum);
        root.put("examuser_pageNum",pageNum);
        String JSON_root=new JSONObject().toJSONString(root);
        log.info("一页考生表user");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/deleteexamuser")
    @ResponseBody
    public boolean deleteExamUser(int userId,int paperId){
        return examUserServiceImpl.deleteExamUserByUserIdAndPaperId(userId,paperId);
    }

    @RequestMapping("/getexamgrade")
    @ResponseBody
    public String getExamGrade(int paperId, HttpSession session){
        Map<String,Object> root=new HashMap<String,Object>();
        User user= (User) session.getAttribute(Constants.USER_KEY);
        ExamUser examUser=examUserServiceImpl.getExamUserByUserIdAndPaperId(user.getUserId(),paperId);
        if(examUser.getJoinExam()==0){//未参加
            root.put("examGrade","未参加该场考试");
        }
        else if (examUser.getJoinExam()==1||examUser.getJoinExam()==2){//参加
            Float grade=examUserServiceImpl.getExamGrade(user.getUserId(),paperId);
            if(grade!=-1)
                root.put("examGrade",grade);
            else if(grade==-1)
                root.put("examGrade",resultServiceImpl.getAutoCheckGrade(user.getUserId(),paperId));
        }
        String JSON_root=new JSONObject().toJSONString(root);
        log.info("考试成绩");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/submitexam")
    @ResponseBody
    public boolean submitExam(int paperId,HttpSession session){
        User user= (User) session.getAttribute(Constants.USER_KEY);
        resultServiceImpl.getAutoCheckGrade(user.getUserId(),paperId);
        return examUserServiceImpl.submitExam(user.getUserId(),paperId);
    }

    @RequestMapping("/getpagefinishexamuser")
    @ResponseBody
    public String getPageFinishExamUser(String strPaperId,int pageNum,int onePageNum,String search){
        int examuser_page_all_num=1;//总页数
        int allExamUserNum=0;//考生总个数
        Map<String,Object> root=new HashMap<String,Object>();
        ArrayList<ExamUser> pageExamUser=new ArrayList<ExamUser>();
        Page<ExamUser> examUsers=null;
        if(strPaperId==""){
            root.put("one_page_examuser","{}");
        }
        else {
            int paperId=Integer.valueOf(strPaperId);
            if(search!=""){//搜索
                List<User> allExamUser=null;
                allExamUser=examUserServiceImpl.getAllExamUser(paperId,search);
                if(allExamUser!=null)
                    allExamUserNum=allExamUser.size();
                else
                    allExamUserNum=0;
                int start=(pageNum-1)*onePageNum;
                int end=pageNum*onePageNum;
                if(end>allExamUserNum)
                    end=allExamUserNum;
                if(end==start&&pageNum>1){
                    pageNum-=1;
                    start=(pageNum-1)*onePageNum;
                    end=pageNum*onePageNum;
                }
                for (int i=start;i<end;i++){
                    ExamUser examUser=examUserServiceImpl.getExamUserByUserIdAndPaperId(allExamUser.get(i).getUserId(),paperId);
                    examUser.setUser(allExamUser.get(i));
                    pageExamUser.add(examUser);
                }

            }
            else if(search==""){//不搜索
                allExamUserNum=examUserServiceImpl.getAllExamUserNum(paperId);
                if(allExamUserNum!=0){
                    if(allExamUserNum%onePageNum==0)
                        examuser_page_all_num=allExamUserNum/onePageNum;
                    else
                        examuser_page_all_num=allExamUserNum/onePageNum+1;
                }
                else if(allExamUserNum==0){
                    examuser_page_all_num=1;
                }

                if(pageNum<=0)
                    pageNum=1;
                if(pageNum>examuser_page_all_num)
                    pageNum=examuser_page_all_num;

                examUsers=examUserServiceImpl.getPageExamUser(pageNum,onePageNum,paperId);
                if(pageNum>1&&examUsers.size()<=0){
                    pageNum-=1;
                    examUsers=examUserServiceImpl.getPageExamUser(pageNum,onePageNum,paperId);
                }

                for (ExamUser examUser:examUsers)
                    pageExamUser.add(examUser);
            }
            if(pageExamUser.size()>0)
                root.put("one_page_examuser",pageExamUser);
            else if(pageExamUser.size()>0)
                root.put("one_page_examuser","{}");
        }

        root.put("examuser_page_all_num",examuser_page_all_num);
        root.put("allExamUserNum",allExamUserNum);
        root.put("examuser_pageNum",pageNum);

        String JSON_root=new JSONObject().toJSONString(root);
        log.info("一页考生表examuser");
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/setexamgrade")
    @ResponseBody
    public boolean setExamGrade(int paperId,int userId){
        return examUserServiceImpl.setExamGrade(paperId,userId);
    }

    @RequestMapping("/checkexamgrade")
    @ResponseBody
    public boolean checkExamGrade(int paperId,int userId){
        if(examUserServiceImpl.getExamGrade(userId,paperId)!=-1)
            return true;
        else
            return false;
    }

    @RequestMapping("/checkjoinexam")
    @ResponseBody
    public String checkjoinexam(int paperId,int userId){
        Map<String,Object> root=new HashMap<String, Object>();
        ExamUser examUser=examUserServiceImpl.getExamUserByUserIdAndPaperId(userId,paperId);
        boolean json_joinexam=false;
        boolean json_check=false;
        if(examUser.getJoinExam()!=0){//参加考试
            json_joinexam=true;
            if(examUserServiceImpl.getExamGrade(userId,paperId)!=-1)//已批改
                json_check=true;
            else//未批改
                json_check=false;
        }
        else {//未参加
            json_joinexam=false;
        }
        root.put("json_joinexam",json_joinexam);
        root.put("json_check",json_check);
        String JSON_root=new JSONObject().toJSONString(root);
        log.info(JSON_root);
        return JSON_root;
    }

    @RequestMapping("/editexamgrade")
    @ResponseBody
    public boolean editExamGrade(int paperId,int userId,Float examGrade){
        return examUserServiceImpl.editExamGrade(paperId,userId,examGrade);
    }

    @RequestMapping("/getcheckpapernum")
    @ResponseBody
    public int getCheckPaperNum(HttpSession session){
        User u=(User)session.getAttribute(Constants.USER_KEY);
        return  examUserServiceImpl.getCheckPaperNum(u);
    }

}
