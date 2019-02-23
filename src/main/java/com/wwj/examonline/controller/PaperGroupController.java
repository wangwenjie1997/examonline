package com.wwj.examonline.controller;

import com.alibaba.fastjson.JSON;
import com.wwj.examonline.entity.PaperGroup;
import com.wwj.examonline.entity.PaperInfo;
import com.wwj.examonline.entity.User;
import com.wwj.examonline.globle.Constants;
import com.wwj.examonline.service.PaperGroupService;
import com.wwj.examonline.service.PaperInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Controller
public class PaperGroupController {

    @Autowired
    private PaperGroupService paperGroupServiceImpl;

    @Autowired
    private PaperInfoService paperInfoServiceImpl;

    @RequestMapping("/checkgroupname")
    @ResponseBody
    public boolean checkGroupName(String groupname, HttpSession session){
        User u= (User) session.getAttribute(Constants.USER_KEY);
        if(paperGroupServiceImpl.checkPaperGroup(u.getUserId(),groupname)!=null)
            return false;
        else
            return true;
    }

    @RequestMapping("/addpapergroup")
    @ResponseBody
    public boolean addPaperGroup(String groupName,String search, HttpSession session){
        User u= (User) session.getAttribute(Constants.USER_KEY);
        return paperGroupServiceImpl.addPaperGroup(u.getUserId(),groupName);
    }

    @RequestMapping("/getpapergroup")
    @ResponseBody
    public String getPaperGroup(HttpSession session,String search,String addGroupName,String editGroupName){
        User u= (User) session.getAttribute(Constants.USER_KEY);
        List<PaperGroup> paperGroups=null;
        String JSON_group="{}";
        if(search==""){
            paperGroups=paperGroupServiceImpl.getPaperGroupByUserId(u);
        }
        else if(search!=""){
            paperGroups=paperGroupServiceImpl.searchPaperGroup(search,u.getUserId());
        }
        JSON_group= JSON.toJSONString(paperGroups);
        log.info("试卷分组");
        return JSON_group;
    }

    @RequestMapping("/editpapergroup")
    @ResponseBody
    public boolean editPaperGroup(HttpSession session,int groupId,String groupName,String search){
        return paperGroupServiceImpl.editPaperGroupName(groupName,groupId);
    }

    @RequestMapping("/deletepapergroup")
    @ResponseBody
    public boolean deletePaperGroup(int groupId){
        boolean flag=true;
        List<PaperInfo> paperInfos=paperInfoServiceImpl.getPaperInfoByPaperGroupId(groupId);
        for (PaperInfo paperInfo:paperInfos){
            paperInfoServiceImpl.deletePaper(paperInfo.getPaperId());
        }
        paperGroupServiceImpl.deletePaperGroup(groupId);
        return flag;
    }


}
