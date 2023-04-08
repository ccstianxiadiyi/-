package com.chen.security.service;

public interface LoginLogService {
    public void recordLoginLog(String username ,Integer status,String ipaddr,String msg);
}
