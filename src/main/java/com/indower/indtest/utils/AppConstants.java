package com.indower.indtest.utils;

public interface AppConstants {

    public int PAGE_SIZE = 30;
    public int ANS_LIMIT = 5;
    public boolean IS_DEPLOYING = false;
    public String COUNT_ANS = "count_ans_";
    public String AVG_RATING = "avg_rating_";
    public String TOTAL_RATING = "total_ratings_";
    public String ANS_DID = "ans_did_";
    public String RATING_DID = "rating_did_";
    public String USER_TYPE = "user_type_";

    public String IMAGE_PREDICTION_URL = "https://neel692-nsfw-vs-sfw-image-classification.hf.space/run/predict";
    public String TEXT_PREDICTION_URL = "https://neel692-abusive-comment-detection.hf.space/run/predict";
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
            { "Tour Guide", "Hospitality & Tourism" },
            { "Others", "Others" }
    };

    public String BLURRY_IMAGE_BASE64_STRING = "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";
}
