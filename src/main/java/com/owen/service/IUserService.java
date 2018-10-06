package com.owen.service;


import com.owen.entity.User;
import com.owen.web.dto.UserDTO;

/**
 * 用户服务
 * Created by 瓦力.
 */
public interface IUserService {
    User findUserByName(String userName);

    ServiceResult<UserDTO> findById(Long userId);
}
