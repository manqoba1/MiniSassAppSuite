/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sifiso.codetribe.minisasslibrary.dto;


import java.io.Serializable;
import java.util.List;

/**
 * @author aubreyM
 */
public class RiverDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer riverID;
    private String riverName, originCountryName, endCountryName;
    private Double originLatitude;
    private Double originLongitude;
    private Double endLatitude;
    private Double endLongitude;
    private long dateRegistered;
    private List<RiverTownDTO> riverTownList;
    private List<EvaluationSiteDTO> evaluationSiteList;
    private List<ErrorStoreAndroidDTO> errorstoreandroidList;
    private Integer originCountryID;
    private Integer endCountryID;
    private CountryDTO originCountry;
    private CountryDTO endCountry;

    public RiverDTO() {
    }

    public List<ErrorStoreAndroidDTO> getErrorstoreandroidList() {
        return errorstoreandroidList;
    }

    public void setErrorstoreandroidList(List<ErrorStoreAndroidDTO> errorstoreandroidList) {
        this.errorstoreandroidList = errorstoreandroidList;
    }

    public CountryDTO getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(CountryDTO originCountry) {
        this.originCountry = originCountry;
    }

    public CountryDTO getEndCountry() {
        return endCountry;
    }

    public void setEndCountry(CountryDTO endCountry) {
        this.endCountry = endCountry;
    }

    public String getOriginCountryName() {
        return originCountryName;
    }

    public void setOriginCountryName(String originCountryName) {
        this.originCountryName = originCountryName;
    }

    public String getEndCountryName() {
        return endCountryName;
    }

    public void setEndCountryName(String endCountryName) {
        this.endCountryName = endCountryName;
    }

    public Integer getOriginCountryID() {
        return originCountryID;
    }

    public void setOriginCountryID(Integer originCountryID) {
        this.originCountryID = originCountryID;
    }

    public Integer getEndCountryID() {
        return endCountryID;
    }

    public void setEndCountryID(Integer endCountryID) {
        this.endCountryID = endCountryID;
    }

    public Integer getRiverID() {
        return riverID;
    }

    public void setRiverID(Integer riverID) {
        this.riverID = riverID;
    }

    public String getRiverName() {
        return riverName;
    }

    public void setRiverName(String riverName) {
        this.riverName = riverName;
    }

    public Double getOriginLatitude() {
        return originLatitude;
    }

    public void setOriginLatitude(Double originLatitude) {
        this.originLatitude = originLatitude;
    }

    public Double getOriginLongitude() {
        return originLongitude;
    }

    public void setOriginLongitude(Double originLongitude) {
        this.originLongitude = originLongitude;
    }

    public Double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(Double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public Double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(Double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public List<RiverTownDTO> getRiverTownList() {
        return riverTownList;
    }

    public void setRiverTownList(List<RiverTownDTO> riverTownList) {
        this.riverTownList = riverTownList;
    }

    public List<EvaluationSiteDTO> getEvaluationSiteList() {
        return evaluationSiteList;
    }

    public void setEvaluationSiteList(List<EvaluationSiteDTO> evaluationSiteList) {
        this.evaluationSiteList = evaluationSiteList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (riverID != null ? riverID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiverDTO)) {
            return false;
        }
        RiverDTO other = (RiverDTO) object;
        if ((this.riverID == null && other.riverID != null) || (this.riverID != null && !this.riverID.equals(other.riverID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.minisass.data.River[ riverID=" + riverID + " ]";
    }

}
