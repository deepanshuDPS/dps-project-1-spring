package com.indower.indtest.utils;

public class EnvironmentSetup {

    private String profiles[];

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

    public static EnvironmentSetup getInstance(String[] profiles) {
        if (instance == null) {
            instance = new EnvironmentSetup();
            instance.profiles = profiles;
        }
        return instance;
    }

    private EnvironmentSetup() {

    }

}
