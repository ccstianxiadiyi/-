package com.chen.system.controller;


import com.chen.system.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户角色 前端控制器
 * </p>
 *
 * @author ccs
 * @since 2023-04-05
 */
@RestController
@RequestMapping("/system/sysUserRole")
public class SysUserRoleController {
    @Autowired
    private SysUserRoleService roleService;


}

