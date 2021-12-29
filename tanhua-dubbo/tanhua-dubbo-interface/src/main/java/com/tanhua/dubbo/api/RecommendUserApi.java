package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface RecommendUserApi {
	//查询今日佳人数据
    RecommendUser queryWithMaxScore(Long toUserId);

    List<RecommendUser> queryRecommendUserList(Long toUserId);

    Map<Long, RecommendUser> queryRecommendUserMap(Long toUserId);

    PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId);
}