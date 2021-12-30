package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.RecommendUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayBest {
    private Long id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String[] tags;
    private Long fateValue;

    public static TodayBest init(UserInfo userInfo, RecommendUser recommendUser) {
        TodayBest vo = new TodayBest();
        BeanUtils.copyProperties(userInfo, vo);
        if (userInfo.getTags() != null) {
            vo.setTags(userInfo.getTags().split(","));
        }
        long fateValue = 0L;
        if (recommendUser != null) {
            fateValue = recommendUser.getScore().longValue();
        }
        vo.setFateValue(fateValue);
        return vo;
    }
}
