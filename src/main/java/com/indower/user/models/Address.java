package com.indower.user.models;

public class Address {
    
    private String houseDetails;

    private String city;

    private String state;

    private Integer pinCode;

    public String getHouseDetails() {
        return houseDetails;
    }

    public void setHouseDetails(String houseDetails) {
        this.houseDetails = houseDetails;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getPinCode() {
        return pinCode;
    }

    public void setPinCode(Integer pinCode) {
        this.pinCode = pinCode;
    }


}
