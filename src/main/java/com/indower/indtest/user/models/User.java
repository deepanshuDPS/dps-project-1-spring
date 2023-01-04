package com.indower.indtest.user.models;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.indower.indtest.models.documentModels.Profession;

import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "user")
public class User {

  private String _id;

  private List<String> oAuthIDs;

  @JsonProperty(value = "name", required = true)
  @NotNull(message = "Please enter the name")
  private String name;

  @JsonProperty(value = "imageUrl", required = true)
  @NotNull(message = "Please choose your image")
  private String imageUrl;


  @JsonProperty(value = "accountType", required = true)
  @NotNull(message = "Please select the account type")
  private Integer accountType;

  // only required in case of professional and self-improvement profile
  private Profession profession;

  // only required in case of professional and self-improvement profile
  private Integer gender;

  private String countryCode;

  private String mobile;

  private Address address;

  private String email;

  private Boolean onBoarded;

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

  public void setoAuthIDs(List<String> oAuthIDs) {
    this.oAuthIDs = oAuthIDs;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public User toIdOrStatus() {
    User objUser = new User();
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
  
}
