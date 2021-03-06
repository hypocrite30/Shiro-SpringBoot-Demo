package com.kuang.config;

import com.kuang.pojo.User;
import com.kuang.service.UserServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

//自定义的 UserRealm
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserServiceImpl userService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了 => doGetAuthorizationInfo");

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 拿到当前登录的这个对象
        Subject subject = SecurityUtils.getSubject();
        // 拿到User对象
        User currentUser = (User) subject.getPrincipal();
        // 设置当前用户的权限
        System.out.println(currentUser.getName() + "的权限为 " + currentUser.getPerms());
        info.addStringPermission(currentUser.getPerms());

        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行了 => doGetAuthenticationInfo");

        UsernamePasswordToken userToken = (UsernamePasswordToken) authenticationToken;
        //连接真实的数据库
        User user = userService.queryUserByName(userToken.getUsername());
        if (user == null) {
            //没有这个人
            return null; //抛出异常 UnknownAccountException
        }

        // 登录成功 将用户信息存入session
        Subject currentSubject = SecurityUtils.getSubject();
        Session session = currentSubject.getSession();
        session.setAttribute("loginUser", user.getName());

        // 密码认证，shiro做
        return new SimpleAuthenticationInfo(user, user.getPassword(), "");
    }
}
