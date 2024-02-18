package com.fahad.mybills.API;

public class VersionResponse {
    private int versionCode;
    private String versionName;
    private String appLink;

    private String message;

    private boolean skipable;


    public VersionResponse(int versionCode, String versionName, String appLink, String message, boolean skipable) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.appLink = appLink;
        this.message = message;
        this.skipable = skipable;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getAppLink() {
        return appLink;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSkipable() {
        return skipable;
    }
}
