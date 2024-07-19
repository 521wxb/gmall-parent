package com.atguigu.gmall.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.user.dto.UserLoginDTO;
import com.atguigu.gmall.user.vo.UserLoginSuccessVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.user.entity.UserInfo;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author Administrator
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2023-02-22 10:56:53
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper ;

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Override
    public UserLoginSuccessVo login(UserLoginDTO userLoginDTO) {

        // 根据用户名查询数据库，获取用户数据
        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(UserInfo::getLoginName , userLoginDTO.getLoginName()) ;
        UserInfo userInfo = userInfoMapper.selectOne(lambdaQueryWrapper);

        // 如果查询不到，抛出异常，给出提示：用户名或者密码错误
        if(userInfo == null) {
            throw new GmallException(ResultCodeEnum.USER_LOGIN_ERROR) ;
        }

        // 判断密码是否正确(使用用户所输入的密码和数据库中查询到的密码进行比对)，如果不相同,抛出异常，给出提示：用户名或者密码错误
        // 对用户所输入的密码进行md5加密
        String passwdMD5 = DigestUtils.md5DigestAsHex(userLoginDTO.getPasswd().getBytes(StandardCharsets.UTF_8));
        if(!passwdMD5.equals(userInfo.getPasswd())) {
            throw new GmallException(ResultCodeEnum.USER_LOGIN_ERROR) ;
        }

        // 生成token
        String token = UUID.randomUUID().toString().replace("-", "");

        // 把token和用户的信息保存到Redis中
        /**
         * 登录成功以后，如果用户一直操作我们的系统，那么此时更新它过期时间为30分钟，思考问题？
         * 登录成功以后，如果没有操作系统，30分钟让用户重新登录。
         */
        redisTemplate.opsForValue().set(GmallConstant.REDIS_USER_LOGIN_PREFIX + token , JSON.toJSONString(userInfo) ,
                30 , TimeUnit.DAYS);

        // 构建响应数据进行返回
        UserLoginSuccessVo userLoginSuccessVo = new UserLoginSuccessVo() ;
        userLoginSuccessVo.setToken(token);
        userLoginSuccessVo.setNickName(userInfo.getNickName());

        // 返回
        return userLoginSuccessVo;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(GmallConstant.REDIS_USER_LOGIN_PREFIX + token) ;
    }

}




