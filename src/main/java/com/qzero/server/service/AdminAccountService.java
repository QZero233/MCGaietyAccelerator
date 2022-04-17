package com.qzero.server.service;

import com.qzero.server.data.ServerAdmin;
import com.qzero.server.data.ServerAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class AdminAccountService {

    @Autowired
    private ServerAdminRepository adminRepository;

    public boolean checkAdminInfo(String minecraftId,String passwordHash){
        return adminRepository.existsByMinecraftIdAndPasswordHash(minecraftId,passwordHash);
    }

    public boolean hasAdmin(String minecraftId){
        return adminRepository.existsById(minecraftId);
    }

    public ServerAdmin getAdminInfo(String minecraftId){
        if(!adminRepository.existsById(minecraftId)){
            throw new IllegalArgumentException("Server admin with id "+minecraftId+" does not exist, can not get info");
        }

        return adminRepository.getById(minecraftId);
    }

    public List<String> getAdminNameList(){
        return adminRepository.getAdminNames();
    }

    public void removeAdmin(String minecraftId){
        if(!adminRepository.existsById(minecraftId)){
            throw new IllegalArgumentException("Server admin with id "+minecraftId+" does not exist, can not delete");
        }

        adminRepository.deleteById(minecraftId);
    }

    public void addAdmin(ServerAdmin admin){
        if(adminRepository.existsById(admin.getMinecraftId()))
            throw new IllegalArgumentException("Server admin with id "+admin.getMinecraftId()
                    +" exists, can not add one with the same id");
        adminRepository.save(admin);
    }

    public void updatePassword(String minecraftId,String newPasswordHash){
        if(!adminRepository.existsById(minecraftId)){
            throw new IllegalArgumentException("Server admin with id "+minecraftId+" does not exist, can not update password");
        }

        ServerAdmin admin=adminRepository.getById(minecraftId);
        admin.setPasswordHash(newPasswordHash);
        adminRepository.save(admin);
    }

}
