package com.indower.indtest.utils;

public class EnvironmentSetup {

    private String profiles[];
    private String eKey;
    
    public String geteKey() {
        return eKey;
    }

    private String eCountry;

    public String geteCountry() {
        return eCountry;
    }

    public String[] getProfiles() {
        return profiles;
    }

    public boolean isProd() {
        for (String profile : profiles) {
            if (profile.equals("dev")) {
                return false;
            } else if (profile.equals("prod")) {
                return true;
            }
        }
        return false;
    }

    private static EnvironmentSetup instance = null;

    public static EnvironmentSetup getInstance(String[] profiles, String encryptionKey, String encryptionCountry) {
        if (instance == null) {
            instance = new EnvironmentSetup();
            instance.profiles = profiles;
            instance.eKey = encryptionKey;
            instance.eCountry = encryptionCountry;
        }
        return instance;
    }

    private EnvironmentSetup() {

    }

}
