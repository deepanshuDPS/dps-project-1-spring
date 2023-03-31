package com.indower.indtest.user.models.documentModels;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.indower.indtest.models.documentModels.Profession;
import com.indower.indtest.user.models.Address;

import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "user")
public class UserDoc {

  private String _id;

  private List<String> oAuthIDs;

  @JsonProperty(value = "name", required = true)
  @NotNull(message = "Please enter the name")
  private String name;

  // @JsonProperty(value = "imageUrl", required = true)
  // @NotNull(message = "Please choose your image")
  private String imageUrl;


  // 0 reveiwer, 1 professional, 2 self development
  @JsonProperty(value = "accountType", required = true)
  @NotNull(message = "Please select the account type")
  private Integer accountType;

  // only required in case of professional and self-improvement profile
  private Profession profession;

  @JsonProperty(value = "professionDesc", required = true)
  @NotNull(message = "Please enter prefession Description")
  private String professionDesc;

  // only required in case of professional and self-improvement profile
  private Integer gender;

  private String countryCode;

  private String mobile;

  private Address address;

  private String email;

  private Boolean onBoarded;

  private Boolean isAnonymous;

  private List<String> socialLinks;

  private String description;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Date createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Date updatedAt;

  public Integer getAccountType() {
    return accountType;
  }

  public void setAccountType(Integer accountType) {
    this.accountType = accountType;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getOnBoarded() {
    return onBoarded;
  }

  public void setOnBoarded(Boolean onBoarded) {
    this.onBoarded = onBoarded;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public String get_id() {
    return _id;
  }

  public void set_id(String _id) {
    this._id = _id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  // return null to deserilize no need of auth ids in frontend
  public List<String> getoAuthIDs() {
    return null;
  }

  public List<String> gSecretAuthIds() {
    return oAuthIDs;
  }

  public void setoAuthIDs(List<String> oAuthIDs) {
    this.oAuthIDs = oAuthIDs;
  }

  public String getImageUrl() {
    if (imageUrl != null && imageUrl.contains("http://files.dpskreations.com/")) {
      imageUrl = imageUrl.replace("http://files.dpskreations.com/",
          "https://s3.ap-south-1.amazonaws.com/files.dpskreations.com/");
    }
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public UserDoc toIdOrStatus() {
    UserDoc objUser = new UserDoc();
    objUser.set_id(_id);
    objUser.setOnBoarded(onBoarded);
    return objUser;
  }

  public Integer getGender() {
    return gender;
  }

  public void setGender(Integer gender) {
    this.gender = gender;
  }

  public Profession getProfession() {
    return profession;
  }

  public void setProfession(Profession profession) {
    this.profession = profession;
  }

  public List<String> getSocialLinks() {
    if (socialLinks != null && socialLinks.size() > 5)
      return socialLinks.subList(0, 5);
    else
      return socialLinks;
  }

  public void setSocialLinks(List<String> socialLinks) {
    this.socialLinks = socialLinks;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  @JsonProperty(value = "languages", required = true)
  @NotNull(message = "Please select languages you know")
  private List<String> languages;

  public List<String> getLanguages() {
    return languages;
  }

  public void setLanguages(List<String> languages) {
    this.languages = languages;
  }

  public void setAnonymous(boolean isAnonymous) {
    this.isAnonymous = isAnonymous;
  }

  public Boolean isAnonymous() {
    return isAnonymous;
  }

  public String getProfessionDesc() {
    return professionDesc;
  }

  public void setProfessionDesc(String professionDesc) {
    this.professionDesc = professionDesc;
  }

}
