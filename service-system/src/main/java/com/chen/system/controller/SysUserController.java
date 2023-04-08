package com.chen.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.pojo.system.SysUser;
import com.chen.pojo.vo.SysUserQueryVo;
import com.chen.system.service.SysUserService;
import com.chen.utils.MD5;
import com.chen.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ccs
 * @since 2023-04-05
 */
@RestController
@Api(tags="用户管理")
@RequestMapping("/system/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/getCondition/{currentPage}/{pageSize}")
    @ApiOperation("分页获取用户")
    public Result getUsers(@PathVariable Integer currentPage, @PathVariable Integer pageSize, @RequestBody(required = false) SysUserQueryVo userQueryVo){
        Page<SysUser> page=new Page<>(currentPage,pageSize);
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        String createTimeBegin = userQueryVo.getCreateTimeBegin();
        String keyword = userQueryVo.getKeyword();
        String createTimeEnd = userQueryVo.getCreateTimeEnd();
        queryWrapper.like(StringUtils.hasLength(keyword),SysUser::getName,keyword);
        queryWrapper.lt(StringUtils.hasLength(createTimeEnd),SysUser::getCreateTime,createTimeEnd);
        queryWrapper.gt(StringUtils.hasLength(createTimeBegin),SysUser::getCreateTime,createTimeBegin);
      //  queryWrapper.eq(SysUser::getStatus,0);
        queryWrapper.orderByAsc(SysUser::getId);
        sysUserService.page(page,queryWrapper);
        long total = page.getTotal();
        List<SysUser> records = page.getRecords();
        Map<String,Object> map=new HashMap<>();
        map.put("userList",records);
        map.put("total",total);
        return Result.ok(map);
    }

    @GetMapping("/getOne/{id}")
    @ApiOperation("根据id获取用户")
    public Result getOne(@PathVariable String id){
        SysUser user =  sysUserService.getById(id);
        if(user==null){
            return Result.fail();
        }
        return Result.ok(user);
    }

    @PostMapping("/add")
    @ApiOperation("新增用户")
    public Result addUser(@RequestBody SysUser sysUser){
        sysUser.setStatus(0);
        sysUser.setPassword(MD5.encrypt(sysUser.getPassword()));
        boolean b = sysUserService.save(sysUser);
        return b?Result.ok("添加成功"):Result.fail();
    }
    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除用户")
    public Result delete(@PathVariable String id){
        boolean b = sysUserService.removeById(id);
        return b?Result.ok("删除成功"):Result.fail();
    }
    @DeleteMapping("/edit/{id}")
    @ApiOperation("更新用户")
    public Result edit(@PathVariable String id,@RequestBody SysUser sysUser){
       sysUser.setId(id);
        boolean b = sysUserService.updateById(sysUser);
        return b?Result.ok("更新成功"):Result.fail();
    }
    @ApiOperation(value = "更新状态")
    @GetMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        SysUser user = sysUserService.getById(id);
        user.setStatus(status);
        sysUserService.updateById(user);
        return Result.ok("修改成功");
    }


}

