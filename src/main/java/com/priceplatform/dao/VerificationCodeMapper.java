package com.priceplatform.dao;

import com.priceplatform.entity.VerificationCode;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeMapper {

    // 插入验证码
    int insert(VerificationCode verificationCode);

    // 根据账号和类型查询最新的验证码
    VerificationCode selectLatestByAccountAndType(
            @Param("account") String account,
            @Param("type") Integer type
    );

    // 更新验证码状态 - 方法名应该是 updateIsUsed
    int updateIsUsed(@Param("id") Long id, @Param("isUsed") Integer isUsed);
}