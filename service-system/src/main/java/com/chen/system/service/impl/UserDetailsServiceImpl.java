package com.chen.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chen.pojo.system.SysUser;
import com.chen.security.custom.CustomUser;
import com.chen.system.service.SysMenuService;
import com.chen.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 根据用户名获取userDetails对象
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername,username);
        SysUser one = sysUserService.getOne(queryWrapper);
        if(one==null){
            throw new UsernameNotFoundException("用户不存在");
        }
        if(one.getStatus()==0){
            throw new RuntimeException("用户被禁用");
        }
        //查询用户的权限数据
        List<String> buttonList = sysMenuService.getButtonList(one.getId());
        //转换成security要求的格式
        List<SimpleGrantedAuthority> authorities=new ArrayList<>();
        buttonList.forEach(item-> authorities.add(new SimpleGrantedAuthority(item.trim())));
        return new CustomUser(one, authorities);
    }
}
