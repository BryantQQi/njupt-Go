package com.atnjupt.sqyxgo.common.exception;

import com.atnjupt.sqyxgo.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ClassName:GlobalExceptionHandler
 * Package: com.atnjupt.sqyxgo.common.exception
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 12:06
 * @Version 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return  Result.fail(null);
    }
    @ExceptionHandler(SqyxgoException.class)
    @ResponseBody
    public Result error(SqyxgoException e){

        return Result.build(null,e.getMessage(),e.getCode());
    }

}