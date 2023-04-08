package com.chen.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.pojo.system.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;

@Mapper
public interface LoginLogMapper extends BaseMapper<SysLoginLog> {
}
