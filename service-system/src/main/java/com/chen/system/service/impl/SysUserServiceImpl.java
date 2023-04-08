package com.chen.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.pojo.system.SysUser;
import com.chen.pojo.vo.RouterVo;
import com.chen.system.mapper.SysUserMapper;
import com.chen.system.service.SysMenuService;
import com.chen.system.service.SysUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ccs
 * @since 2023-04-05
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public Map<String, Object> getUserInfo(String username) {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername,username);
        SysUser one = sysUserService.getOne(queryWrapper);
        String userId = one.getId();
        Map<String, Object> map=new HashMap<>();
        List<RouterVo> voList=sysMenuService.getRouterList(userId);
        List<String> buttonList=sysMenuService.getButtonList(userId);
        SysUser user = getById(userId);
        map.put("name",user.getUsername());
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("roles","[\"admin\"]");
        map.put("buttons", buttonList);
        map.put("routers", voList);
        return map;
    }
}
