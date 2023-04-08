package com.chen.annoation;

import com.chen.enums.BusinessType;
import com.chen.enums.OperatorType;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 模块
     */
    public String title() default "";
    /**
     * 功能
      */
    public BusinessType businessType() default BusinessType.OTHER;
    /**
     * 操作人类别
     */
    public OperatorType operatorType() default  OperatorType.MANAGE;
    /**
     * 是否保留请求的参数
     *
     */
    public boolean isSaveRequestData() default true;
    /**
     * 是否保留响应的参数
     *
     */
    public boolean isSaveResponseData() default true;
}
