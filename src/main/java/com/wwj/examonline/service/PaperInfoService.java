package com.wwj.examonline.service;

import com.github.pagehelper.Page;
import com.wwj.examonline.entity.PaperInfo;
import com.wwj.examonline.entity.User;

import java.util.List;
import java.util.Map;

public interface PaperInfoService {

    List<PaperInfo> getPaperInfoByPaperGroupId(int paperGroupId);

    int getAllSearchPaperNum(String paperName,int paperGroupId);

    int getAllPaperNum(int paperGroupId);

    Page<PaperInfo> searchPagePaper(int pageNum,int pageSize,String search,int groupId);

    Page<PaperInfo> getPagePaper(int pageNum, int pageSize,int paperGroupId);

    boolean getPaperByGroupIdAndPaperName(int paperGroupId,String paperName);

    PaperInfo addPaper(String paperName,int paperGroupId);

    boolean settingPaper(String paperName,int oneOptionCheck,int moreOptionCheck,int trueOrFalseCheck,int fillInBlankCheck
            ,float oneOptionMark,float moreOptionMark,float trueOrFalseMark,float fillInBlankMark
            ,float shortAnswerQuestionMark,int paperId);

    PaperInfo getPaperByPaperId(int paperId);

    boolean deletePaper(int paperId);

    boolean releasePaper(String paperStart,String paperEnd,int paperId);

    PaperInfo getExamPaper(int paperId);

    Map<String,Object> getPapersInfo(User user);

}
