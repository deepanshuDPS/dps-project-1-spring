package com.indower.indtest.models.documentModels;

public class Profession {

    private String _id;
    private String profession;
    private String profDept;
    private String profIcon;

    public Profession() {
    }

    public Profession(String profession, String profDept) {
        this.profession = profession;
        this.profDept = profDept.toLowerCase().replaceAll("\\s+", "-");
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getProfDept() {
        return profDept;
    }

    public void setProfDept(String profDept) {
        this.profDept = profDept;
    }

    public String getProfIcon() {
        return profIcon;
    }

    public void setProfIcon(String profIcon) {
        this.profIcon = profIcon;
    }

}
