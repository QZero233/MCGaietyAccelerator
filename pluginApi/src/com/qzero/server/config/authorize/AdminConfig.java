package com.qzero.server.config.authorize;

public class AdminConfig {

    private String passwordHash;
    private String adminLevel;

    private int adminLevelInInt;

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public int getAdminLevelInInt() {
        return adminLevelInInt;
    }

    public void setAdminLevelInInt(int adminLevelInInt) {
        this.adminLevelInInt = adminLevelInInt;
    }

    @Override
    public String toString() {
        return "AdminConfig{" +
                "passwordHash='" + passwordHash + '\'' +
                ", adminLevel='" + adminLevel + '\'' +
                ", adminLevelInInt=" + adminLevelInInt +
                '}';
    }
}
