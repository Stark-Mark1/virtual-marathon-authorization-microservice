package com.virtualmarathon.Authorization.repository;

import com.virtualmarathon.Authorization.data.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,String> {
    UserEntity findByEmail(String email);
}
