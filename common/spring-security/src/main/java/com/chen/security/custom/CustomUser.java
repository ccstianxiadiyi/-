package com.chen.security.custom;

import com.chen.pojo.system.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 自定义user对象继承实现了userDetails接口的security中内置的对象
 */
public class CustomUser extends User {
    private SysUser sysUser;
    /**
     *
     *
     * @param authorities  身份集合
     */
    public CustomUser(SysUser sysUser, Collection<? extends GrantedAuthority> authorities) {
        super(sysUser.getUsername(), sysUser.getPassword(), authorities);
        this.sysUser=sysUser;
    }
    public SysUser sysUser(){
        return sysUser;
    }

    public SysUser getSysUser() {
        return sysUser;
    }
}
