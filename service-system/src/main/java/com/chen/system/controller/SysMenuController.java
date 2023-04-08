package com.chen.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chen.pojo.system.SysMenu;
import com.chen.pojo.system.SysRoleMenu;
import com.chen.pojo.vo.AssginMenuVo;
import com.chen.system.service.SysMenuService;
import com.chen.system.service.SysRoleMenuService;
import com.chen.utils.Result;
import com.chen.utils.exception.GuiguException;
import com.chen.utils.helper.MenuHelper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author ccs
 * @since 2023-04-06
 */
@RestController
@RequestMapping("/system/sysMenu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @GetMapping("/getAll")
    @ApiOperation("树形获取所有数据")
    public Result findAll(){
        List<SysMenu> list = sysMenuService.list(null);
        List<SysMenu> sysMenus = MenuHelper.buildTree(list);
        return Result.ok(sysMenus);
    }

    @PostMapping("/addMenu")
    @ApiOperation("新增菜单")
    public Result addMenu(@RequestBody SysMenu sysMenu){
        boolean save = sysMenuService.save(sysMenu);
        return  save? Result.ok("添加成功"):Result.fail();
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping("/update")
    public Result updateById(@RequestBody SysMenu permission) {
        sysMenuService.updateById(permission);
        return Result.ok();
    }

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable String id) {
        LambdaQueryWrapper<SysMenu> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getParentId,id);
        int count = sysMenuService.count(queryWrapper);
        if(count>0){
            throw new GuiguException(201,"有关联的下级菜单");
        }
        sysMenuService.removeById(id);
        return Result.ok();
    }
    @ApiOperation(value = "根据角色获取菜单")
    @GetMapping("/toAssign/{roleId}")
    public Result toAssign(@PathVariable String roleId) {
       // List<SysMenu> list = sysMenuService.findSysMenuByRoleId(roleId);
        List<SysMenu> allMenuList = sysMenuService.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus,1));

        LambdaQueryWrapper<SysRoleMenu> sysRoleMenuLambdaQueryWrapper=new LambdaQueryWrapper<>();
        sysRoleMenuLambdaQueryWrapper.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> ownList = sysRoleMenuService.list(sysRoleMenuLambdaQueryWrapper);
        List<String> idCollect = ownList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        for (SysMenu sysMenu : allMenuList) {
            sysMenu.setSelect(idCollect.contains(sysMenu.getId()));
        }
        List<SysMenu> sysMenus = MenuHelper.buildTree(allMenuList);
        return Result.ok(sysMenus);
    }

    @ApiOperation(value = "给角色分配菜单")
    @PostMapping("/doAssign")
    @Transactional
    public Result doAssign(@RequestBody AssginMenuVo assginMenuVo) {
      //  sysMenuService.doAssign(assginMenuVo);
        //获取已经分配的权限
        String roleId = assginMenuVo.getRoleId();
        LambdaQueryWrapper<SysRoleMenu> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId,roleId);
        sysRoleMenuService.remove(queryWrapper);
        List<String> menuIdList = assginMenuVo.getMenuIdList();
        menuIdList.stream().forEach(item->{
            SysRoleMenu sysRoleMenu=new SysRoleMenu();
            sysRoleMenu.setMenuId(item);
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenuService.save(sysRoleMenu);
        });
        return Result.ok("分配成功");
    }
}

