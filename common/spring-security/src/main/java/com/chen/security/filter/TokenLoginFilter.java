package com.chen.security.filter;

import com.alibaba.fastjson.JSON;
import com.chen.pojo.system.SysUser;
import com.chen.pojo.vo.LoginVo;
import com.chen.security.custom.CustomUser;
import com.chen.security.service.LoginLogService;
import com.chen.security.utils.ResponseUtil;
import com.chen.utils.IpUtil;
import com.chen.utils.Result;
import com.chen.utils.ResultCodeEnum;
import com.chen.utils.helper.JwtHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//自定义认证过滤器
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {
    private RedisTemplate redisTemplate;
    private LoginLogService loginLogService;
    public TokenLoginFilter(AuthenticationManager authenticationManager,RedisTemplate redisTemplate,LoginLogService loginLogService) {
        this.setAuthenticationManager(authenticationManager);
        this.setPostOnly(false);
        //指定登录接口和提交方式
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/system/index/login","POST"));
        this.redisTemplate=redisTemplate;
        this.loginLogService=loginLogService;
    }
    //获取认证信息方法
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            LoginVo loginVo = new ObjectMapper().readValue(request.getInputStream(), LoginVo.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginVo.getUsername(), loginVo.getPassword());
            return this.getAuthenticationManager().authenticate(authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    //认证成功

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //获取认证对象
        CustomUser principal = (CustomUser) authResult.getPrincipal();
        //保存权限数据
        redisTemplate.opsForValue().set(principal.getSysUser().getUsername(), JSON.toJSONString(principal.getAuthorities()));
        //生成token
        String token = JwtHelper.createToken(principal.getSysUser().getId(), principal.getSysUser().getUsername());
        //记录日志
        loginLogService.recordLoginLog(principal.getSysUser().getUsername(),1, IpUtil.getIpAddress(request),"登陆了");
        //返回
        Map<String,Object> map=new HashMap<>();
        map.put("token",token);
        ResponseUtil.out(response, Result.ok(map));

    }

    //认证失败

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        if(e.getCause() instanceof RuntimeException){
            ResponseUtil.out(response,Result.build(null,204,e.getMessage()));
        }else{
            ResponseUtil.out(response,Result.build(null, ResultCodeEnum.LOGIN_MOBLE_ERROR));
        }

    }
}
