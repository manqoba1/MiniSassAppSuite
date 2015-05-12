/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sifiso.codetribe.minisasslibrary.dto;

import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @author aubreyM
 */
public class EvaluationDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer evaluationID, teamMemberID, conditionsID, evaluationSiteID;
    private long evaluationDate;
    private String comment, conditionName, teamName;
    private Double score;
    private Double pH;
    private String remarks;
    private Double waterTemperature;
    private Double oxygen;
    private Double waterClarity;
    private Double electricityConductivity;
    private List<EvaluationImageDTO> evaluationImageList;
    private TeamMemberDTO teamMember;
    private List<ImagesDTO> imagesList;
    private EvaluationSiteDTO evaluationSite;
    private ConditionsDTO conditions;
    private List<EvaluationInsectDTO> evaluationInsectList;
    private List<EvaluationCommentDTO> evaluationCommentList;

    public List<ImagesDTO> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<ImagesDTO> imagesList) {
        this.imagesList = imagesList;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public EvaluationDTO() {
    }

    public Double getElectricityConductivity() {
        return electricityConductivity;
    }

    public void setElectricityConductivity(Double electricityConductivity) {
        this.electricityConductivity = electricityConductivity;
    }

    public Integer getEvaluationID() {
        return evaluationID;
    }

    public void setEvaluationID(Integer evaluationID) {
        this.evaluationID = evaluationID;
    }

    public Integer getTeamMemberID() {
        return teamMemberID;
    }

    public void setTeamMemberID(Integer teamMemberID) {
        this.teamMemberID = teamMemberID;
    }

    public Integer getConditionsID() {
        return conditionsID;
    }

    public void setConditionsID(Integer conditionsID) {
        this.conditionsID = conditionsID;
    }

    public Integer getEvaluationSiteID() {
        return evaluationSiteID;
    }

    public void setEvaluationSiteID(Integer evaluationSiteID) {
        this.evaluationSiteID = evaluationSiteID;
    }

    public long getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(long evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getpH() {
        return pH;
    }

    public void setpH(Double pH) {
        this.pH = pH;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Double getWaterTemperature() {
        return waterTemperature;
    }

    public void setWaterTemperature(Double waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public Double getOxygen() {
        return oxygen;
    }

    public void setOxygen(Double oxygen) {
        this.oxygen = oxygen;
    }

    public Double getWaterClarity() {
        return waterClarity;
    }

    public void setWaterClarity(Double waterClarity) {
        this.waterClarity = waterClarity;
    }

    public List<EvaluationImageDTO> getEvaluationImageList() {
        return evaluationImageList;
    }

    public void setEvaluationImageList(List<EvaluationImageDTO> evaluationImageList) {
        this.evaluationImageList = evaluationImageList;
    }

    public TeamMemberDTO getTeamMember() {
        return teamMember;
    }

    public void setTeamMember(TeamMemberDTO teamMember) {
        this.teamMember = teamMember;
    }

    public EvaluationSiteDTO getEvaluationSite() {
        return evaluationSite;
    }

    public void setEvaluationSite(EvaluationSiteDTO evaluationSite) {
        this.evaluationSite = evaluationSite;
    }

    public ConditionsDTO getConditions() {
        return conditions;
    }

    public void setConditions(ConditionsDTO conditions) {
        this.conditions = conditions;
    }

    public List<EvaluationInsectDTO> getEvaluationInsectList() {
        return evaluationInsectList;
    }

    public void setEvaluationInsectList(List<EvaluationInsectDTO> evaluationInsectList) {
        this.evaluationInsectList = evaluationInsectList;
    }

    public List<EvaluationCommentDTO> getEvaluationCommentList() {
        return evaluationCommentList;
    }

    public void setEvaluationCommentList(List<EvaluationCommentDTO> evaluationCommentList) {
        this.evaluationCommentList = evaluationCommentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (evaluationID != null ? evaluationID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EvaluationDTO)) {
            return false;
        }
        EvaluationDTO other = (EvaluationDTO) object;
        if ((this.evaluationID == null && other.evaluationID != null) || (this.evaluationID != null && !this.evaluationID.equals(other.evaluationID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.minisass.data.Evaluation[ evaluationID=" + evaluationID + " ]";
    }

}
