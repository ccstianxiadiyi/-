package com.chen.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.pojo.system.SysLoginLog;
import com.chen.security.service.LoginLogService;
import com.chen.system.mapper.LoginLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper,SysLoginLog> implements LoginLogService {
    @Resource
    private LoginLogMapper loginLogMapper;
    @Override
    public void recordLoginLog(String username, Integer status, String ipaddr, String msg) {
        SysLoginLog sysLoginLog=new SysLoginLog();
        sysLoginLog.setUsername(username);
        sysLoginLog.setStatus(status);
        sysLoginLog.setIpaddr(ipaddr);
        sysLoginLog.setMsg(msg);
        loginLogMapper.insert(sysLoginLog);
    }
}
