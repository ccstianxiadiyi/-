package com.chen.security.custom;

import com.chen.utils.MD5;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/*
* 自定义密码处理
* */
@Component
public class CustomMd5Password implements PasswordEncoder {
    /**
     * 对前端提交的密码进行加密
     * @param charSequence
     * @return
     */
    @Override
    public String encode(CharSequence charSequence) {
        return MD5.encrypt(charSequence.toString());
    }

    /**
     * 对前端提交的密码加密后进行检验
     * @param charSequence
     * @param s
     * @return
     */
    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return s.equals(MD5.encrypt(charSequence.toString()));
    }
}
