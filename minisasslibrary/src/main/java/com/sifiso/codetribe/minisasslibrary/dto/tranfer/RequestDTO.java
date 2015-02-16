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

import java.util.List;

/**
 *
 * @author aubreyM
 */
public class RequestDTO {

    private int requestType;
    private String email, password;
    private Integer countryID, categoryID, commentID, conditionsID, evaluationID,  evaluationInsectID,
            evaluationCommentID, evaluationSiteID, insectID, provinceID, riverID, teamID, townID, teamMemberID, 
            evaluationImageID;

    public static final int REGISTER_TEAM = 1,
            GENERATE_TEAM = 100,
            GENERATE_RIVER = 101,
            REGISTER_TEAM_MEMBER = 2,
            SIGN_IN_MEMBER = 3,
            IMPORT_MEMBERS = 4;
    
    

    public static final int 
            ADD_RIVER = 10,
            ADD_RIVER_TOWN = 11,
            ADD_EVALUATION_SITE = 12,
            ADD_INSECT = 13,
            ADD_INSECT_IMAGE = 14,
            ADD_EVALUATION = 15,
            ADD_COMMENT = 16,
            
            ADD_EVALUATION_INSECT = 17;
                    
                    
    public static final int ADD_COUNTRY = 21,
            ADD_PROVINCE = 22,
            ADD_TOWN = 23;

    public static final int 
            UPDATE_RIVER = 30,
            UPDATE_RIVER_TOWN = 31,
            UPDATE_EVALUATION_SITE = 32,
            UPDATE_INSECT = 33,
            UPDATE_INSECT_IMAGE = 34,
            
            UPDATE_TOWN = 35,
            UPDATE_TEAM = 36,
            UPDATE_TEAM_MEMBER = 37,
            UPDATE_COMMENT = 38,
            UPDATE_CATEGORY = 39,
            UPDATE_CONDITIONS = 55,
            UPDATE_EVALUATION_IMAGE = 56;
                    
    public static final int
            LIST_RIVERS_IN_COUNTRY = 40,
            LIST_RIVER_TOWNS = 41,
            LIST_EVALUATION_SITES = 42,
            LIST_INSECTS = 43,
            LIST_TEAMS = 44,
            LIST_EVALUATION_SITE_BY_RIVER = 45,
            LIST_PROVINCE_BY_COUNTRY = 46,
            LIST_ALL_PROVINCES = 50,
            
            LIST_EVALUATION_BY_TEAM_MEMBER = 60,
            LIST_EVALUATION_BY_CONDITIONS = 61,
            LIST_EVALUATION_SITE_BY_CATEGORY = 62,
            LIST_EVALUATION_INSECT_BY_EVALUATION = 63,
            LIST_TEAMS_BY_TOWN = 64,
            LIST_TEAM_MEMBERS = 65,
            LIST_TOWN_BY_PROVINCE = 66,
            LIST_CATEGORY = 67,
            LIST_COMMENTS = 68,
            LIST_COUNTRYS = 69,
            LIST_EVALUATIONS = 70,
            LIST_RIVERS = 71;
                    
                    
    private EvaluationDTO evaluation;
    private ImagesDTO images;
    private CategoryDTO category;
    private ConditionsDTO conditions;
    private ErrorStoreAndroidDTO errorStoreAndroid;
    private ErrorStoreDTO errorStore;
    private EvaluationImageDTO evaluationImage;
    private EvaluationInsectDTO evaluationInsect;
    private GcmDeviceDTO gcmDevice;
    private TeamDTO team;
    private TeamMemberDTO teamMember;
    private RiverDTO river;
    private RiverTownDTO riverTown;
    private EvaluationSiteDTO evaluationSite;
    private CommentDTO comment;
    private InsectDTO insect;
    private InsectImageDTO insectImage;
    private CountryDTO country;
    private TownDTO town;
    private ProvinceDTO province;
    
    
    public static final String SAMPLE_DIR = "company";

    private List<TeamMemberDTO> members;

    public static final String EVALUATION_DIR = "evaluation";
    public static final String INSECTS_DIR = "insert";
    public static final String TEAM_DIR = "team";
    public static final String TEAM_MEMBER_DIR = "teamMember";

    public ImagesDTO getImages() {
        return images;
    }

    public void setImages(ImagesDTO images) {
        this.images = images;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public ConditionsDTO getConditions() {
        return conditions;
    }

    public void setConditions(ConditionsDTO conditions) {
        this.conditions = conditions;
    }

    public ErrorStoreAndroidDTO getErrorStoreAndroid() {
        return errorStoreAndroid;
    }

    public void setErrorStoreAndroid(ErrorStoreAndroidDTO errorStoreAndroid) {
        this.errorStoreAndroid = errorStoreAndroid;
    }

    public ErrorStoreDTO getErrorStore() {
        return errorStore;
    }

    public void setErrorStore(ErrorStoreDTO errorStore) {
        this.errorStore = errorStore;
    }

    public EvaluationImageDTO getEvaluationImage() {
        return evaluationImage;
    }

    public void setEvaluationImage(EvaluationImageDTO evaluationImage) {
        this.evaluationImage = evaluationImage;
    }

    public EvaluationInsectDTO getEvaluationInsect() {
        return evaluationInsect;
    }

    public void setEvaluationInsect(EvaluationInsectDTO evaluationInsect) {
        this.evaluationInsect = evaluationInsect;
    }

    public GcmDeviceDTO getGcmDevice() {
        return gcmDevice;
    }

    public void setGcmDevice(GcmDeviceDTO gcmDevice) {
        this.gcmDevice = gcmDevice;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public List<TeamMemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMemberDTO> members) {
        this.members = members;
    }

    public Integer getTeamID() {
        return teamID;
    }

    public void setTeamID(Integer teamID) {
        this.teamID = teamID;
    }

    public TownDTO getTown() {
        return town;
    }

    public void setTown(TownDTO town) {
        this.town = town;
    }

    public Integer getCountryID() {
        return countryID;
    }

    public void setCountryID(Integer countryID) {
        this.countryID = countryID;
    }

    public ProvinceDTO getProvince() {
        return province;
    }

    public void setProvince(ProvinceDTO province) {
        this.province = province;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getEmail() {
        return email;
    }

    public CommentDTO getComment() {
        return comment;
    }

    public void setComment(CommentDTO comment) {
        this.comment = comment;
    }

    public InsectDTO getInsect() {
        return insect;
    }

    public void setInsect(InsectDTO insect) {
        this.insect = insect;
    }

    public InsectImageDTO getInsectImage() {
        return insectImage;
    }

    public void setInsectImage(InsectImageDTO insectImage) {
        this.insectImage = insectImage;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EvaluationDTO getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
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

    public RiverDTO getRiver() {
        return river;
    }

    public void setRiver(RiverDTO river) {
        this.river = river;
    }

    public RiverTownDTO getRiverTown() {
        return riverTown;
    }

    public void setRiverTown(RiverTownDTO riverTown) {
        this.riverTown = riverTown;
    }

    public EvaluationSiteDTO getEvaluationSite() {
        return evaluationSite;
    }

    public void setEvaluationSite(EvaluationSiteDTO evaluationSite) {
        this.evaluationSite = evaluationSite;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public Integer getCommentID() {
        return commentID;
    }

    public void setCommentID(Integer commentID) {
        this.commentID = commentID;
    }

    public Integer getConditionsID() {
        return conditionsID;
    }

    public void setConditionsID(Integer conditionsID) {
        this.conditionsID = conditionsID;
    }

    public Integer getEvaluationID() {
        return evaluationID;
    }

    public void setEvaluationID(Integer evaluationID) {
        this.evaluationID = evaluationID;
    }

    public Integer getEvaluationInsectID() {
        return evaluationInsectID;
    }

    public void setEvaluationInsectID(Integer evaluationInsectID) {
        this.evaluationInsectID = evaluationInsectID;
    }

    public Integer getEvaluationCommentID() {
        return evaluationCommentID;
    }

    public void setEvaluationCommentID(Integer evaluationCommentID) {
        this.evaluationCommentID = evaluationCommentID;
    }

    public Integer getEvaluationSiteID() {
        return evaluationSiteID;
    }

    public void setEvaluationSiteID(Integer evaluationSiteID) {
        this.evaluationSiteID = evaluationSiteID;
    }

    public Integer getInsectID() {
        return insectID;
    }

    public void setInsectID(Integer insectID) {
        this.insectID = insectID;
    }

    public Integer getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(Integer provinceID) {
        this.provinceID = provinceID;
    }

    public Integer getRiverID() {
        return riverID;
    }

    public void setRiverID(Integer riverID) {
        this.riverID = riverID;
    }

    public Integer getTownID() {
        return townID;
    }

    public void setTownID(Integer townID) {
        this.townID = townID;
    }

    public Integer getTeamMemberID() {
        return teamMemberID;
    }

    public void setTeamMemberID(Integer teamMemberID) {
        this.teamMemberID = teamMemberID;
    }

    public Integer getEvaluationImageID() {
        return evaluationImageID;
    }

    public void setEvaluationImageID(Integer evaluationImageID) {
        this.evaluationImageID = evaluationImageID;
    }      

}
