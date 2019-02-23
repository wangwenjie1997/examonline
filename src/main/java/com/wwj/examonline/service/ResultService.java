package com.wwj.examonline.service;

import com.wwj.examonline.entity.Result;

public interface ResultService {

    int updateResult(String resultAnsewer,int userId,int paperContentId,int paperId);

    boolean joinExam(int userId,int paperId);

    Result getResultByUserIdAndContentIdAndPaperId(int userId,int paperContentId,int paperId);

    String getAutoCheckGrade(int userId,int paperId);

    Result getResultByResultId(int resultId);

    boolean updateResultGrade(int resultId,Float resultMark);

}
