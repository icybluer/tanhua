package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.mapper.QuestionMapper;
import com.tanhua.model.domain.Question;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class QuestionApiImpl implements QuestionApi {
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Question findByUserId(Long userId) {
        return questionMapper.selectOne(Wrappers.<Question>lambdaQuery().eq(Question::getUserId,userId));
    }

    @Override
    public void update(Question question) {
        questionMapper.updateById(question);
    }

    @Override
    public void save(Question question) {
        questionMapper.insert(question);
    }
}
