package com.yq.model;

import org.apache.shiro.authc.UsernamePasswordToken;

public class MultiLoginAuthenticationToken extends UsernamePasswordToken {
    private String realmName;

    public MultiLoginAuthenticationToken(String username, String password, String realmName){
        super(username, password);
        this.realmName = realmName;
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }
}
