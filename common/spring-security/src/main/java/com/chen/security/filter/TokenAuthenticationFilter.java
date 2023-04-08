package com.chen.security.filter;

import com.alibaba.fastjson.JSON;
import com.chen.security.service.LoginLogService;
import com.chen.security.utils.ResponseUtil;
import com.chen.utils.Result;
import com.chen.utils.ResultCodeEnum;
import com.chen.utils.helper.JwtHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

//认证解析token过滤器
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(RedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if("/admin/system/index/login".equals(request.getRequestURI())){
            filterChain.doFilter(request,httpServletResponse);
            return;
        }
        UsernamePasswordAuthenticationToken authenticationToken=getAuthentication(request);
        if(null!=authenticationToken){
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request,httpServletResponse);
        }else {
            ResponseUtil.out(httpServletResponse, Result.build(null, ResultCodeEnum.PERMISSION));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token= request.getHeader("token");
        if(StringUtils.hasLength(token)){
            String username = JwtHelper.getUsername(token);
            if(StringUtils.hasLength(username)){
                String auths = (String)redisTemplate.opsForValue().get(username);
                List<Map> maps = JSON.parseArray(auths, Map.class);
                List<SimpleGrantedAuthority> lists=new ArrayList<>();
                maps.forEach(item->lists.add(new SimpleGrantedAuthority((String) item.get("authority"))));
                return new UsernamePasswordAuthenticationToken(username,null, lists);
            }

        }

        return null;
    }
}
