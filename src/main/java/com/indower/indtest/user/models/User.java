package com.indower.indtest.user.models;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class User{

  private String _id;

  private List<String> oAuthIDs;

  private String name;

  private String countryCode;

  private String mobile;

  private Address address;
  
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

}
