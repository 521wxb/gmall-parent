package com.atguigu.gmall.common.exception;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice  // 声明当前这个类是一个全局异常处理器
public class GlobalExceptionHandler {

    /**
     * @ExceptionHandler指定当前这个方法就是一个异常处理方法，并且指定当前方法可以处理的异常类型
     * @return
     */
    @ExceptionHandler(value = GmallException.class)
    public Result gmallExceptionHandler(GmallException e) {
        e.printStackTrace();
        ResultCodeEnum resultCodeEnum = e.getResultCodeEnum();
        return Result.build(null , resultCodeEnum) ;
    }

    /**
     * 处理非业务异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Result systemExceptionHandler(Exception e) {
        e.printStackTrace();
        return Result.build(null , ResultCodeEnum.SYSTEM_EXCEPTION) ;
    }

    /**
     * 处理校验不通过时所产生的异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        e.printStackTrace();

        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        Map<String , String> map = new HashMap<>() ;
        for(FieldError error : fieldErrors) {
            String field = error.getField();                        // 获取校验不同的字段的名称
            String defaultMessage = error.getDefaultMessage();      // 获取校验不同的提示信息

            // 判断当前遍历的错误字段在map中是否存在
            if(!map.containsKey(field)) {
                map.put(field , defaultMessage) ;
            }else {
                String value = map.get(field);
                value =  value + ", " + defaultMessage ;
                map.put(field , value) ;
            }
        }

        // 构建Result响应数据
        Result result = new Result() ;
        result.setCode(60100);
        result.setMessage(JSON.toJSONString(map));

        return result;
    }

}
