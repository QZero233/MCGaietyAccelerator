package com.qzero.server.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerAdminRepository extends JpaRepository<ServerAdmin,String> {

    boolean existsByMinecraftIdAndPasswordHash(String minecraftId,String passwordHash);

    ServerAdmin getByMinecraftIdAndPasswordHash(String minecraftId,String passwordHash);

    ServerAdmin getByMinecraftId(String minecraftId);

    @Query("SELECT minecraftId FROM ServerAdmin")
    List<String> getAdminNames();



}
