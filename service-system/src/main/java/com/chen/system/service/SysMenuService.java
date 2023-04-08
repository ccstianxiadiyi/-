package com.chen.system.service;

import com.chen.pojo.system.SysMenu;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.pojo.vo.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author ccs
 * @since 2023-04-06
 */
public interface SysMenuService extends IService<SysMenu> {

    List<RouterVo> getRouterList(String userId);

    List<String> getButtonList(String userId);
}
