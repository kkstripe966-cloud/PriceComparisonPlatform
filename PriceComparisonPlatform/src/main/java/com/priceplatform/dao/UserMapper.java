package com.priceplatform.dao;

import com.priceplatform.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int insert(User user);
    User selectById(Long id);
    User selectByPhone(String phone);
    User selectByEmail(String email);
    User selectByPhoneOrEmail(@Param("account") String account);
    int update(User user);
    int updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") java.util.Date lastLoginTime);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
}