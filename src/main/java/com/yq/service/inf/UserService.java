package com.yq.service.inf;

import com.yq.model.User;

import java.util.Map;

public interface UserService {
    User selUserByUsername(String username);

    User selUserByUserId(int user_id);

    void updateUser(Map map);

    void insertUser(User user);
}
