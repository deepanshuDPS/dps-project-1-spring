package com.indower.indtest.utils;

public interface AppConstants {

    public int PAGE_SIZE = 30;
    public long ANS_LIMIT = 5;
    public boolean IS_DEPLOYING = false;
    public String COUNT_ANS = "count_ans_";
    public String AVG_RATING = "avg_rating_";
    public String TOTAL_RATING = "total_ratings_";
    public String ANS_DID = "ans_did_";
    public String RATING_DID = "rating_did_";
    public String USER_TYPE = "user_type_";

    public String IMAGE_PREDICTION_URL = "https://neel692-nsfw-vs-sfw-image-classification.hf.space/run/predict";
    public long ONE_HOUR = 60 * 60;
    public long THIRTY_SECS = 30;
    public long INFO_CACHE = 24 * 3600; // one day cache
    public String[][] PROFESSIONS = {
            { "Teacher", "Education" }, { "Professor", "Education" }, { "Tutor", "Education" },
            { "Web developer", "Technology" }, { "Programmer", "Technology" }, { "Software Developer", "Technology" },
            { "DevOps", "Technology" },
            { "Athelite", "Sports & Athletics" }, { "Coach", "Sports & Athletics" },
            { "Trainer", "Sports & Athletics" },
            { "Lawyer", "Legal" }, { "Judge", "Legal" },
            { "Accountant", "Finance" }, { "Banker", "Finance" },
            { "Doctor", "Health Care" }, { "Nurse", "Health Care" }, { "Dentist", "Health Care" },
            { "Pharmacist", "Health Care" }, { "Veterinarian", "Health Care" }, { "Pshycologist", "Health Care" },
            { "Police Officer", "Public Service" }, { "Firefighter", "Public Service" },
            { "Soldier", "Public Service" },
            { "Musician", "Arts & Media" }, { "Dancer", "Arts & Media" }, { "Actor", "Arts & Media" },
            { "Journalist", "Arts & Media" }, { "PhotoGrapher", "Arts & Media" },
            { "Engineer", "Architecture & Construction" }, { "Architect", "Architecture & Construction" },
            { "Contractor", "Architecture & Construction" },
            { "Salesperson", "Sales & Marketing" }, { "Consultant", "Sales & Marketing" },
            { "Marketing Manager", "Sales & Marketing" },
            { "Chef", "Hospitality & Tourism" }, { "Hotel Manager", "Hospitality & Tourism" },
            { "Tour Guide", "Hospitality & Tourism" }
    };
}
