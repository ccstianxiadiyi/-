package com.chen.system.mapper;

import com.chen.pojo.system.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author ccs
 * @since 2023-04-06
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {



    List<SysMenu> findMenuLists(String userId);


}
