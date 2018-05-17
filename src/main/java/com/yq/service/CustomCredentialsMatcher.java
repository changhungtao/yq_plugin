package com.yq.service;

import com.yq.config.SpringContextHolder;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

public class CustomCredentialsMatcher extends SimpleCredentialsMatcher {
    private MessageDigestPasswordEncoder passwordEncoder;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authToken, AuthenticationInfo info) {
        passwordEncoder = (MessageDigestPasswordEncoder)SpringContextHolder.getBean("passwordEncoder");
        UsernamePasswordToken token = (UsernamePasswordToken)authToken;
        Object tokenCredentials = encrypt(String.valueOf(token.getPassword()));
        Object accountCredentials = getCredentials(info);
        return equals(tokenCredentials, accountCredentials);
    }

    private String encrypt(String psw){
        String md5_psw = passwordEncoder.encodePassword(psw, "");
        return md5_psw;
    }
}
