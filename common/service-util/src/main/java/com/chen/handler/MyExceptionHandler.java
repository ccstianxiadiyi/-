package com.chen.handler;

import com.chen.utils.Result;
import com.chen.utils.exception.GuiguException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {


    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result msg(Exception e){
        e.printStackTrace();
        return Result.fail().message("网络异常");
    }

    @ResponseBody
    @ExceptionHandler(GuiguException.class)
    public Result error(GuiguException e){
        e.printStackTrace();
        return Result.fail().message(e.getMessage()).code(e.getCode());
    }
}
