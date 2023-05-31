package com.indower.utils;

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

    public int envType() {
        for (String profile : profiles) {
            if (profile.equals("test")) {
                return 0;
            } else if (profile.equals("dev")) {
                return 1;
            } else if (profile.equals("prod")) {
                return 2;
            }
        }
        return 0;
    }


    public boolean isTest() {
        for (String profile : profiles) {
            if (profile.equals("test")) {
                return true;
            }
        }
        return false;
    }

    public boolean isDev() {
        for (String profile : profiles) {
            if (profile.equals("dev")) {
                return true;
            }
        }
        return false;
    }

    public boolean isProd() {
        for (String profile : profiles) {
            if (profile.equals("prod")) {
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
