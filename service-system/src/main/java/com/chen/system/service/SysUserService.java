package com.chen.system.service;

import com.chen.pojo.system.SysUser;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ccs
 * @since 2023-04-05
 */
public interface SysUserService extends IService<SysUser> {

    Map<String, Object> getUserInfo(String username);
}
