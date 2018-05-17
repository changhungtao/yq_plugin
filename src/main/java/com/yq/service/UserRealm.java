package com.yq.service;

import com.yq.model.MultiLoginAuthenticationToken;
import com.yq.model.User;
import com.yq.service.inf.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    protected UserService userService;

    /**
     * 认证回调函数,登录时调用.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authToken;
        String username = token.getUsername();
        User user = userService.selUserByUsername(username);
        if (null == user) return null;
        return new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), this.getName());
    }

    /**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        User user = userService.selUserByUsername(username);
        info.addRole(user.getRole_id().toString());
        return info;
    }

    @PostConstruct
    public void initCredentialsMatcher() {
        setCredentialsMatcher(new CustomCredentialsMatcher());
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof MultiLoginAuthenticationToken) {
            return ((MultiLoginAuthenticationToken) token).getRealmName().equalsIgnoreCase("UserRealm");
        }
        return false;
    }

    @Override
    public boolean hasRole(PrincipalCollection principal, String roleIdentifier) {
        if (principal.fromRealm(getName()).isEmpty()) {
            return false;
        } else {
            return super.hasRole(principal, roleIdentifier);
        }
    }
}
