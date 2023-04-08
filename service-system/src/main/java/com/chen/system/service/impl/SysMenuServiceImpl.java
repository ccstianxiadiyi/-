package com.chen.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chen.pojo.system.SysMenu;
import com.chen.pojo.vo.RouterVo;
import com.chen.system.mapper.SysMenuMapper;
import com.chen.system.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.utils.helper.MenuHelper;
import com.chen.utils.helper.RouterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author ccs
 * @since 2023-04-06
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {


    @Override
    public List<RouterVo> getRouterList(String userId) {
        List<SysMenu> list=null;
        if("1".equals(userId)){
            LambdaQueryWrapper<SysMenu> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SysMenu::getStatus,1);
            lambdaQueryWrapper.orderByDesc(SysMenu::getSortValue);
            list=baseMapper.selectList(lambdaQueryWrapper);
        }
        else {
            list=baseMapper.findMenuLists(userId);
        }
        List<SysMenu> sysMenus = MenuHelper.buildTree(list);
        List<RouterVo> voList = RouterHelper.buildRouters(sysMenus);

        return voList;
    }

    @Override
    public List<String> getButtonList(String userId) {
        //超级管理员admin账号id为：1
        List<SysMenu> sysMenuList = null;
        if ("1".equals(userId)) {
            sysMenuList = baseMapper.selectList(new QueryWrapper<SysMenu>().eq("status", 1));
        } else {
            sysMenuList = baseMapper.findMenuLists(userId);
        }
        //创建返回的集合
        List<String> permissionList = new ArrayList<>();
        for (SysMenu sysMenu : sysMenuList) {
            if(sysMenu.getType() == 2){
                permissionList.add(sysMenu.getPerms());
            }
        }
        return permissionList;
    }
}
