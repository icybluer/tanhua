package com.tanhua.dubbo.api;

import com.tanhua.model.domain.pojo.Question;

public interface QuestionApi {
    Question findByUserId(Long userId);

    void update(Question question);

    void save(Question question);
}
