package com.atnjupt.sqyxgo.common.result;

import lombok.Data;

/**
 * ClassName:Result
 * Package: com.atnjupt.sqyxgo.common.result
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 11:56
 * @Version 1.0
 * 这样格式是统一的，每个接口返回的具体信息虽然不一样，但是肯定都包含状态码和提示信息
 * 每个接口特有的数据就放到data里面，一般是json，json两种格式，{}对应对象，[]对象数据和集合，常见是两者混合
 */
@Data
public class Result<T>{
    private Integer code;//状态码
    private String message;//提示信息
    private T data;//数据体
    //私有化构造方法
    private Result() {
    }

    /**
     * 设置数据,返回对象的方法
     * @param data
     * @param resultCodeEnum
     * @return
     * @param <T>
     */
    public static<T>  Result<T>  build(T data,ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<>();
        if(data !=null){
            result.setData(data);
        }
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return  result;
    }
    public static <T> Result<T> build(T data,String message,Integer code){
        Result<T> result = new Result<>();
        if(data !=null){
            result.setData(data);
        }
        result.setCode(code);
        result.setMessage(message);
        return  result;
    }

    //成功的方法
    public static<T> Result<T> ok(T data) {
        Result<T> result = build(data, ResultCodeEnum.SUCCESS);
        return result;
    }

    //失败的方法
    public static<T> Result<T> fail(T data) {
        return build(data,ResultCodeEnum.FAIL);
    }
}