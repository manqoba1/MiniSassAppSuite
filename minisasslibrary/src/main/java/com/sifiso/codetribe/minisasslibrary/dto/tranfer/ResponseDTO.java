/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sifiso.codetribe.minisasslibrary.dto.tranfer;


import com.sifiso.codetribe.minisasslibrary.dto.CategoryDTO;
import com.sifiso.codetribe.minisasslibrary.dto.CommentDTO;
import com.sifiso.codetribe.minisasslibrary.dto.ConditionsDTO;
import com.sifiso.codetribe.minisasslibrary.dto.CountryDTO;
import com.sifiso.codetribe.minisasslibrary.dto.ErrorStoreAndroidDTO;
import com.sifiso.codetribe.minisasslibrary.dto.ErrorStoreDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationInsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.GcmDeviceDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.ProvinceDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TownDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author aubreyM
 */
public class ResponseDTO implements Serializable {

    private Integer statusCode;
    private String message;
    private Integer statusCountInPeriod, goodCount, badCount;
    private String sessionID, GCMRegistrationID, fileString;
    private double elapsedRequestTimeInSeconds;
    private Date lastCacheDate, startdate, endDate, dateTaken;

    private TeamDTO team;
    private TeamMemberDTO teamMember;
    private EvaluationDTO evaluation;
    private List<ImagesDTO> imagesList;
    private List<EvaluationImageDTO> evaluationImageList;
    private List<GcmDeviceDTO> gcmDeviceList;
    private List<TeamDTO> teamList = new ArrayList<>();
    private List<TeamMemberDTO> teamMemberList = new ArrayList<>();
    private List<RiverDTO> riverList = new ArrayList<>();
    private List<RiverTownDTO> riverTownList = new ArrayList<>();
    private List<InsectDTO> insectList = new ArrayList<>();
    private List<CommentDTO> commentList = new ArrayList<>();
    private List<EvaluationDTO> evaluationList = new ArrayList<>();
    private List<EvaluationSiteDTO> evaluationSiteList = new ArrayList<>();
    private List<CategoryDTO> categoryList = new ArrayList<>();
    private List<CountryDTO> countryList = new ArrayList<>();
    private List<ProvinceDTO> provinceList = new ArrayList<>();
    private List<TownDTO> townList = new ArrayList<>();
    private List<EvaluationInsectDTO> evaluationInsectList = new ArrayList<>();
    private List<ConditionsDTO> conditionsList;
    private List<InsectImageDTO> insectImageList = new ArrayList<>();
    private List<String> evaluationImageFileName;
    private List<ErrorStoreDTO> errorStoreList = new ArrayList<>();
    private List<ErrorStoreAndroidDTO> errorStoreAndroidList = new ArrayList<>();


    public List<InsectImageDTO> getInsectImageList() {
        return insectImageList;
    }

    public void setInsectImageList(List<InsectImageDTO> insectImageList) {
        this.insectImageList = insectImageList;
    }

    public List<String> getEvaluationImageFileName() {
        return evaluationImageFileName;
    }

    public void setEvaluationImageFileName(List<String> evaluationImageFileName) {
        this.evaluationImageFileName = evaluationImageFileName;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    public Date getLastCacheDate() {
        return lastCacheDate;
    }

    public void setLastCacheDate(Date lastCacheDate) {
        this.lastCacheDate = lastCacheDate;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public EvaluationDTO getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    public List<ImagesDTO> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<ImagesDTO> imagesList) {
        this.imagesList = imagesList;
    }

    public List<EvaluationImageDTO> getEvaluationImageList() {
        return evaluationImageList;
    }

    public void setEvaluationImageList(List<EvaluationImageDTO> evaluationImageList) {
        this.evaluationImageList = evaluationImageList;
    }

    public List<GcmDeviceDTO> getGcmDeviceList() {
        return gcmDeviceList;
    }

    public void setGcmDeviceList(List<GcmDeviceDTO> gcmDeviceList) {
        this.gcmDeviceList = gcmDeviceList;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    public TeamMemberDTO getTeamMember() {
        return teamMember;
    }

    public void setTeamMember(TeamMemberDTO teamMember) {
        this.teamMember = teamMember;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TeamDTO> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<TeamDTO> teamList) {
        this.teamList = teamList;
    }

    public List<TeamMemberDTO> getTeamMemberList() {
        return teamMemberList;
    }

    public void setTeamMemberList(List<TeamMemberDTO> teamMemberList) {
        this.teamMemberList = teamMemberList;
    }

    public List<RiverDTO> getRiverList() {
        return riverList;
    }

    public void setRiverList(List<RiverDTO> riverList) {
        this.riverList = riverList;
    }

    public List<RiverTownDTO> getRiverTownList() {
        return riverTownList;
    }

    public void setRiverTownList(List<RiverTownDTO> riverTownList) {
        this.riverTownList = riverTownList;
    }

    public List<InsectDTO> getInsectList() {
        return insectList;
    }

    public void setInsectList(List<InsectDTO> insectList) {
        this.insectList = insectList;
    }

    public List<EvaluationInsectDTO> getEvaluationInsectList() {
        return evaluationInsectList;
    }

    public void setEvaluationInsectList(List<EvaluationInsectDTO> evaluationInsectList) {
        this.evaluationInsectList = evaluationInsectList;
    }

    public List<CommentDTO> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentDTO> commentList) {
        this.commentList = commentList;
    }

    public List<EvaluationDTO> getEvaluationList() {
        return evaluationList;
    }

    public void setEvaluationList(List<EvaluationDTO> evaluationList) {
        this.evaluationList = evaluationList;
    }

    public List<EvaluationSiteDTO> getEvaluationSiteList() {
        return evaluationSiteList;
    }

    public void setEvaluationSiteList(List<EvaluationSiteDTO> evaluationSiteList) {
        this.evaluationSiteList = evaluationSiteList;
    }

    public List<CategoryDTO> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryDTO> categoryList) {
        this.categoryList = categoryList;
    }

    public List<CountryDTO> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<CountryDTO> countryList) {
        this.countryList = countryList;
    }

    public List<ProvinceDTO> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<ProvinceDTO> provinceList) {
        this.provinceList = provinceList;
    }

    public List<TownDTO> getTownList() {
        return townList;
    }

    public void setTownList(List<TownDTO> townList) {
        this.townList = townList;
    }

    public List<ConditionsDTO> getConditionsList() {
        return conditionsList;
    }

    public void setConditionsList(List<ConditionsDTO> conditionsList) {
        this.conditionsList = conditionsList;
    }

    public Integer getStatusCountInPeriod() {
        return statusCountInPeriod;
    }

    public void setStatusCountInPeriod(Integer statusCountInPeriod) {
        this.statusCountInPeriod = statusCountInPeriod;
    }

    public Integer getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(Integer goodCount) {
        this.goodCount = goodCount;
    }

    public Integer getBadCount() {
        return badCount;
    }

    public void setBadCount(Integer badCount) {
        this.badCount = badCount;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getGCMRegistrationID() {
        return GCMRegistrationID;
    }

    public void setGCMRegistrationID(String GCMRegistrationID) {
        this.GCMRegistrationID = GCMRegistrationID;
    }

    public String getFileString() {
        return fileString;
    }

    public void setFileString(String fileString) {
        this.fileString = fileString;
    }

    public double getElapsedRequestTimeInSeconds() {
        return elapsedRequestTimeInSeconds;
    }

    public void setElapsedRequestTimeInSeconds(double elapsedRequestTimeInSeconds) {
        this.elapsedRequestTimeInSeconds = elapsedRequestTimeInSeconds;
    }

    public List<ErrorStoreDTO> getErrorStoreList() {
        return errorStoreList;
    }

    public void setErrorStoreList(List<ErrorStoreDTO> errorStoreList) {
        this.errorStoreList = errorStoreList;
    }

    public List<ErrorStoreAndroidDTO> getErrorStoreAndroidList() {
        return errorStoreAndroidList;
    }

    public void setErrorStoreAndroidList(List<ErrorStoreAndroidDTO> errorStoreAndroidList) {
        this.errorStoreAndroidList = errorStoreAndroidList;
    }

}
