package com.chen.security.config;

import com.chen.security.custom.CustomMd5Password;
import com.chen.security.filter.TokenAuthenticationFilter;
import com.chen.security.filter.TokenLoginFilter;
import com.chen.security.service.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/*
--------------------------------------------------------------------------------------------------
用户认证流程
1.用户提交用户名/密码
2.将请求信息封装到Authentication 实现类为UsernamePasswordAuthenticationToken
3.认证authenticate()
4.委托认证authenticate()
5.获取用户信息 loadUserByUsername()
6.返回UserDetails
7.通过PasswordEncoder对比UserDetails中的密码与Authentication中的密码是否一致
8.填充Authentication
9.返回Authentication对象
10.通过SecurityContextHolder.getContext().setAuthentication(value)方法将Authentication保存到上下文
---------------------------------------------------------------------------------------------------

* 使用security 需要这几步
* 1.自定义对前端提交的密码进行加密的组件  需要实现 PasswordEncoder 接口 其中会重写两个方法
*   （1） encode 对前端提交的密码进行加密
*   （2）matches 判断前端提交的密码与数据库的密码是否一致  这里获取也要实现
* 2.自定义用户对象  需要继承 springSecurity中的 User对象（这个对象实现了userDetails接口）
*   （1） 我们需要将自己的定义的实体类对象作为该类的成员变量 提供该类的构造方法（有三个参数）
*    一. username
*    二. password  这里可以直接 将一二封装成 sysuser
*    三 . 身份的集合
* 3.创建根据用户名查询用户信息的方法 需要实现userDetailsService
* 重写loadUserByUsername 方法 （根据自己的业务逻辑进行编写）
* 4.自定义认证过滤器 继承UsernamePasswordAuthenticationFilter
* 5.返回信息工具类
* 6.认证解析过滤器  继承 OncePerRequestFilter
* 7.配置用户认证全局信息（在本类中）
-------------------------------------------------------------
授权
1.修改loadUserByUsername
2.配置redis ,存储权限数据
3.修改过滤器
（1）认证过滤器
（2）解析过滤器
4.在项目中配置redis，完成控制层代码

* */

/**
 * security配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity//开启使用Security的注解 @PreAuthorize("hasAuthority('bnt.sysRole.list')")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private CustomMd5Password customMd5Password;
    @Autowired
    private LoginLogService loginLogService;
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()//关闭csrf认证
                .cors().and() //开启跨域给前端调用接口
                .authorizeRequests()  //对接口进行认证
                        .antMatchers("/admin/system/index/login").permitAll() //对登录不需要认证
                .anyRequest().authenticated()//其他的接口都需呀认证才能访问
                .and()//TokenAuthenticationFilter放到UsernamePasswordAuthenticationFilter的前面
                .addFilterBefore(new TokenAuthenticationFilter(redisTemplate), UsernamePasswordAuthenticationFilter.class)
        .addFilter(new TokenLoginFilter(authenticationManager(),redisTemplate,loginLogService));
        //禁用session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //指定UserDetailsService和加密器
        auth.userDetailsService(userDetailsService).passwordEncoder(customMd5Password);
    }

    /**
     * 配置那些资源请求不拦截
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
      web.ignoring().antMatchers("favicon.ico",
              "/swagger-resources/**","webjars/**","/v2/**","/swagger-ui.html/**","/doc.html");
    }
}
