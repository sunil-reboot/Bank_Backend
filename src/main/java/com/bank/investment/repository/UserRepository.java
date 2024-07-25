package com.bank.investment.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bank.investment.model.User;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String username);

    User findByPhoneNumber(String phoneNumber);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.phoneNumber = :phoneNumber")
    void updatePasswordForUser(@Param("password") String password, @Param("phoneNumber") String phoneNumber);


    @Query(value = "select * from entreaty.user where password=?1 and phone_number = ?2", nativeQuery = true)
    User oldPassword(String password, long phoneNumber);
}
