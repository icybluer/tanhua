package com.tanhua.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.model.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    @Select("select * from tb_user_info where id in (" +
            "  select black_user_id from tb_black_list where user_id = #{userId}" +
            ")")
    IPage<UserInfo> findBlackList(@Param("pages") Page pages, @Param("userId") Long userId);
}
