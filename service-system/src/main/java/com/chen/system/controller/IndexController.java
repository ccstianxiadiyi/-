package com.chen.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chen.pojo.system.SysUser;
import com.chen.pojo.vo.LoginVo;
import com.chen.system.service.SysUserService;
import com.chen.utils.MD5;
import com.chen.utils.Result;
import com.chen.utils.ResultCodeEnum;
import com.chen.utils.exception.GuiguException;
import com.chen.utils.helper.JwtHelper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 后台登录登出
 * </p>
 */
@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService sysUserService;


    /**
     * 登录
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo) {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername,loginVo.getUsername());
        SysUser one = sysUserService.getOne(queryWrapper);
        if(one==null){
            throw new GuiguException(20001,"用户不存在");
        }
        if(!MD5.encrypt(loginVo.getPassword()).equals(one.getPassword())) {
            throw new GuiguException(20001,"密码错误");
        }
        if(one.getStatus() == 0) {
            throw new GuiguException(201,"用户被禁用");
        }
        String token = JwtHelper.createToken(one.getId(), one.getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }
    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/info")
    public Result info(HttpServletRequest request) {
        String token = request.getHeader("token");
        String username = JwtHelper.getUsername(token);

        Map<String, Object> map = sysUserService.getUserInfo(username);
//        map.put("roles","[admin]");
//        map.put("name","admin");
//        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return Result.ok(map);
    }
    /**
     * 退出
     * @return
     */
    @PostMapping("/logout")
    public Result logout(){
        return Result.ok();
    }

}
