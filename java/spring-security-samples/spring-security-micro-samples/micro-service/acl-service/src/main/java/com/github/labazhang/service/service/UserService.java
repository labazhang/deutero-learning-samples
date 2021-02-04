package com.github.labazhang.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.labazhang.common.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
public interface UserService extends IService<User> {

    // 从数据库中取出用户信息
    User selectByUsername(String username);
}
