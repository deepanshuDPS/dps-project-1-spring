package com.indower.indtest.user.models;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "user")
public class User{

  private String _id;

  private List<String> oAuthIDs;

  private String name;

  private String countryCode;

  private String mobile;

  private Address address;

  private String email;

  private Boolean onBoarded;
  
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

  public List<String> getoAuthIDs() {
    return oAuthIDs;
  }
  public void setoAuthIDs(List<String> oAuthIDs) {
    this.oAuthIDs = oAuthIDs;
  }

  
 public User toIdOrStatus(){
    User objUser = new User();
    objUser.set_id(_id);
    objUser.setOnBoarded(onBoarded);
    return objUser;
  }
}
