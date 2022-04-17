package com.qzero.server.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ServerAdmin {

    @Id
    private String minecraftId;
    private String passwordHash;
    private int adminLevel;

    public static final int LEVEL_NORMAL=1;
    public static final int LEVEL_SUPER=2;//Super admin can modify admin accounts
    public static final int LEVEL_FULL=3;//Full admin can execute server commands

    public ServerAdmin() {
    }

    public ServerAdmin(String minecraftId, String passwordHash, int adminLevel) {
        this.minecraftId = minecraftId;
        this.passwordHash = passwordHash;
        this.adminLevel = adminLevel;
    }

    public String getMinecraftId() {
        return minecraftId;
    }

    public void setMinecraftId(String minecraftId) {
        this.minecraftId = minecraftId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }

    @Override
    public String toString() {
        return "ServerAdmin{" +
                "minecraftId='" + minecraftId + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", adminLevel=" + adminLevel +
                '}';
    }
}
