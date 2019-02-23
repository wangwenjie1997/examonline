package com.wwj.examonline.service;

import com.github.pagehelper.Page;
import com.wwj.examonline.entity.PaperContent;
import com.wwj.examonline.entity.Question;

import java.util.List;

public interface PaperContentService {

    Page<PaperContent> getPagePaperContentQuestion(int paperId, int pageNum, int pageSize);

    List<PaperContent> getPaperContentByPaperId(int paperId);

    boolean editPaperContentMark(int paperCotentId,float questionMark);

    boolean deletePaperContent(int paperCotentId);

    int getQuestionNumByPaperIdAndQuestionId(int paperId,int questionId);

    boolean addQuestionToPaper(int questionId,int paperId);

    boolean deleteQuestionToPaper (int questionId,int paperId);

    List<PaperContent> getQuestionByPaperId(int paperId);

    int getQuestionNumByPaperId(int paperId);

    List<PaperContent> getPaperQuestionByPaperId(int paperId);

    PaperContent getPaperContentByContentId(int paperContentId);

    Float getPaperAllGrade(int paperId);

}
