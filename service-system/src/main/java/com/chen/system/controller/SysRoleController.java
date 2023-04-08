package com.chen.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.pojo.system.SysRole;
import com.chen.pojo.system.SysUserRole;
import com.chen.pojo.vo.AssginRoleVo;
import com.chen.pojo.vo.GetRoleVo;
import com.chen.pojo.vo.SysRoleQueryVo;
import com.chen.system.service.SysRoleService;
import com.chen.system.service.SysUserRoleService;
import com.chen.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 角色 前端控制器
 * </p>
 *
 * @author ccs
 * @since 2023-04-05
 */
@RestController
@Api(tags="角色管理")
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Autowired
    private SysUserRoleService userRoleService;
    @Autowired
    private SysRoleService sysRoleService;
    @GetMapping("/getAll")
    @ApiOperation("获取所有的角色")
    public List<SysRole> getAll(){
        return sysRoleService.list(null);
    }

    @PostMapping("/get/{currentPage}/{pageSize}")
    @ApiOperation("带条件分页获取角色")
    public Result getByCondition(@PathVariable Integer currentPage, @PathVariable Integer pageSize, @RequestBody(required = false) SysRoleQueryVo sysRoleQueryVo){
        Page<SysRole> page=new Page<>(currentPage,pageSize);
        LambdaQueryWrapper<SysRole> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasLength(sysRoleQueryVo.getRoleName()),SysRole::getRoleName,sysRoleQueryVo.getRoleName());
        sysRoleService.page(page,queryWrapper);
        List<SysRole> records = page.getRecords();
        long total = page.getTotal();
        Map<String,Object> map=new HashMap<>();
        map.put("roleLists",records);
        map.put("total",total);
        return Result.ok(map);
    }

    @GetMapping("/getOnly/{id}")
    @ApiOperation("根据id获取角色")
    public Result getOnly(@PathVariable String id){
        SysRole role = sysRoleService.getById(id);
        if(role==null){
            return Result.fail();
        }
        return Result.ok(role);
    }
    @DeleteMapping("/delete/{id}")
    @ApiOperation("逻辑删除角色")
    public Result delete(@PathVariable String id){
        boolean b = sysRoleService.removeById(id);
        return b?Result.ok().message("删除成功"):Result.fail();
    }
    @DeleteMapping("/deleteMore")
    @ApiOperation("批量删除")
    public Result deleteMore(@RequestBody List<String> ids){
        boolean b = sysRoleService.removeByIds(ids);
        return b?Result.ok().message("删除成功"):Result.fail();
    }
    @PostMapping("/edit/{id}")
    @ApiOperation("编辑角色")
    public Result editRole(@PathVariable String id,@RequestBody SysRole sysRole){
        sysRole.setId(id);
        boolean b = sysRoleService.updateById(sysRole);
        return b?Result.ok().message("修改成功"):Result.fail();
    }
    @PostMapping("/add")
    @ApiOperation("添加角色")
    public Result addRole(@RequestBody SysRole sysRole){
        boolean save = sysRoleService.save(sysRole);
        return save?Result.ok().message("增加成功"):Result.fail();
    }
    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable String userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId,userId);
        List<String> roleIdList = userRoleService.list(queryWrapper).stream().map((item) -> item.getRoleId()).collect(Collectors.toList());
        AssginRoleVo ownList=new AssginRoleVo();
        ownList.setUserId(userId);
        ownList.setRoleIdList(roleIdList);
        List<SysRole> list = sysRoleService.list(null);
        Map<String,Object> map=new HashMap<>();
        map.put("ownList",ownList);
        map.put("allList",list);
        return Result.ok(map);

    }
    @ApiOperation(value = "根据用户分配角色")
    @Transactional
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        //先删除已经分配的角色
        LambdaQueryWrapper<SysUserRole> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId,assginRoleVo.getUserId());
        userRoleService.remove(queryWrapper);
        //在重新分配角色
        List<String> roleIdList = assginRoleVo.getRoleIdList();
        String userId = assginRoleVo.getUserId();
        List<SysUserRole> collect = roleIdList.stream().map((item) -> {
            SysUserRole userRole=new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(item);
            return userRole;
        }).collect(Collectors.toList());
        boolean b = userRoleService.saveBatch(collect);
        return b?Result.ok().message("分配成功"):Result.fail();
    }


}

