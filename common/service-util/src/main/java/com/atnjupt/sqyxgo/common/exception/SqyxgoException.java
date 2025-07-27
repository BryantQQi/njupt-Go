package com.atnjupt.sqyxgo.common.exception;

import com.atnjupt.sqyxgo.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * ClassName:SqyxgoException
 * Package: com.atnjupt.sqyxgo.common.exception
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 12:08
 * @Version 1.0
 */
@Data
public class SqyxgoException extends  RuntimeException{
    private  Integer code;
    public SqyxgoException(String message,Integer code) {
        super(message);
        this.code = code;
    }
    public SqyxgoException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }



}
