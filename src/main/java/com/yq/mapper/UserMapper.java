package com.yq.mapper;

import com.yq.model.User;

import java.util.Map;

public interface UserMapper {
    User selectUser(Map map);

    void updateUser(Map map);

    void insertUser(User user);
}
