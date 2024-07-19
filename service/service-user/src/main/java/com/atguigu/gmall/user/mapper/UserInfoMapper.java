package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.user.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【user_info(用户表)】的数据库操作Mapper
* @createDate 2023-02-22 10:56:53
* @Entity com.atguigu.gmall.user.entity.UserInfo
*/
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}




