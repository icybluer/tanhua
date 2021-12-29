package com.tanhua.server.service;

import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.constant.Constants;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.dto.CommentDTO;
import com.tanhua.model.dto.MovementDTO;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVO;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.mongo.Movement;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MovementService {
    @Autowired
    private OssTemplate ossTemplate;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    // 发布动态
    public void publishMovement(MovementDTO dto, MultipartFile[] imageContent) throws IOException {
        // 校验动态内容是否为空
        if (dto.getTextContent() == null) {
            throw new BusinessException(ErrorResult.contentError());
        }
        Movement movement = new Movement();
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        movement.setUserId(userId);
        // 上传媒体对象到oss
        if (imageContent != null) {
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : imageContent) {
                String url = ossTemplate.upload(file.getOriginalFilename(), file.getInputStream());
                urls.add(url);
            }
            movement.setMedias(urls);
        }
        // 封装到movement对象
        movement.setTextContent(dto.getTextContent());
        movement.setLocationName(dto.getLocation());
        movement.setLongitude(dto.getLongitude());
        movement.setLatitude(dto.getLatitude());
        // 保存到movement表
        movementApi.publish(movement);
    }

    // 查询我的动态
    public PageResult findAll(Long userId, Integer page, Integer pagesize) {
        PageResult pageInfo = movementApi.findByUserId(userId, page, pagesize);
        List<Movement> items = (List<Movement>) pageInfo.getItems();
        if (items == null || items.size() == 0) {
            return pageInfo;
        }
        UserInfo userInfo = userInfoApi.findById(userId);
        List<MovementsVO> vos = items.stream()
                .map(movement -> MovementsVO.init(userInfo, movement))
                .collect(Collectors.toList());
        pageInfo.setItems(vos);
        return pageInfo;
    }

    // 查询好友动态
    public PageResult findFriendsMovements(Integer page, Integer pagesize) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 根据用户id查询可查看的动态
        List<Movement> movements = movementApi.getFriendsMovements(userId, page, pagesize);
        // 封装分页对象
        return getPageResult(page, pagesize, movements);
    }

    // 查询推荐动态
    public PageResult getRecommendMovements(Integer page, Integer pagesize) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 获取当前用户的推荐列表
        List<Movement> movementList = movementApi.randomMovements(pagesize);
        // 封装分页对象
        return getPageResult(page, pagesize, movementList);
    }

    // 查询单条动态
    public MovementsVO findById(String id) {
        Movement movement = movementApi.findById(id);
        if (movement != null) {
            UserInfo userInfo = userInfoApi.findById(movement.getUserId());
            return MovementsVO.init(userInfo, movement);
        }
        return null;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    private PageResult getPageResult(Integer page, Integer pagesize, List<Movement> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new PageResult();
        }
        PageResult pageInfo = new PageResult();
        pageInfo.setPage(page);
        pageInfo.setPagesize(pagesize);
        // 根据动态列表中的用户id查询动态发布者的用户信息
        List<Long> userIDs = list.stream().map(Movement::getUserId).collect(Collectors.toList());
        List<UserInfo> userInfoList = userInfoApi.findByIds(userIDs);
        // 封装为VO对象列表
        Map<Long, UserInfo> userInfoMap = userInfoList.stream()
                .collect(Collectors.toMap(UserInfo::getId, Function.identity()));
        List<MovementsVO> vos = list.stream()
                .filter(movement -> userInfoMap.get(movement.getUserId()) != null)
                .map(movement -> {
                    MovementsVO vo = MovementsVO.init(userInfoMap.get(movement.getUserId()), movement);
                    String key = Constants.MOVEMENTS_INTERACT_KEY + vo.getId();
                    // 设置是否点过赞
                    String likeHashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();
                    if (redisTemplate.opsForHash().hasKey(key, likeHashKey)) {
                        vo.setHasLiked(1);
                    }
                    // 设置是否喜欢
                    String loveHashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
                    if (redisTemplate.opsForHash().hasKey(key, loveHashKey)) {
                        vo.setHasLoved(1);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        pageInfo.setItems(vos);
        return pageInfo;
    }

    @DubboReference
    private CommentApi commentApi;

    //动态-点赞
    public Integer likeComment(String movementId) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 查询用户是否已点赞
        Comment comment = commentApi.hasComment(userId, movementId, CommentType.LIKE.getType());
        if (comment != null) {
            return movementApi.findById(movementId).getLikeCount();
        }
        // 构建dto对象
        CommentDTO dto = new CommentDTO();
        dto.setUserId(userId);
        dto.setMovementId(movementId);
        dto.setCommentType(CommentType.LIKE.getType());
        // 调用api保存点赞数据
        Integer likeCount = commentApi.save(dto);
        // 拼接redis的key，将用户的点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + userId;
        redisTemplate.opsForHash().put(key, hashKey, "1");
        return likeCount;
    }

    //动态-取消点赞
    public Integer dislikeComment(String movementId) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 查询用户是否已点赞
        Comment comment = commentApi.hasComment(userId, movementId, CommentType.LIKE.getType());
        if (comment == null) {
            return movementApi.findById(movementId).getLikeCount();
        }
        // 删除点赞的comment数据
        Integer likeCount = commentApi.delete(userId, movementId, CommentType.LIKE.getType());
        // 移除redis中相应数据
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + userId;
        redisTemplate.opsForHash().delete(key, hashKey);
        // 返回点赞数
        return likeCount;
    }

    //动态-喜欢
    public Integer loveComment(String movementId) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 查询用户是否已喜欢
        Comment comment = commentApi.hasComment(userId, movementId, CommentType.LOVE.getType());
        if (comment != null){
            return movementApi.findById(movementId).getLoveCount();
        }
        // 封装dto对象
        CommentDTO dto = new CommentDTO();
        dto.setUserId(userId);
        dto.setMovementId(movementId);
        dto.setCommentType(CommentType.LOVE.getType());
        // 调用api保存喜欢数据
        Integer loveCount = commentApi.save(dto);
        // 拼接redis的key，将用户的喜欢状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + userId;
        redisTemplate.opsForHash().put(key, hashKey, "1");
        // 返回喜欢数
        return loveCount;
    }

    //动态-取消喜欢
    public Integer unloveComment(String movementId) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 查询用户是否已点赞
        Comment comment = commentApi.hasComment(userId, movementId, CommentType.LOVE.getType());
        if (comment == null) {
            return movementApi.findById(movementId).getLoveCount();
        }
        // 删除点赞的comment数据
        Integer likeCount = commentApi.delete(userId, movementId, CommentType.LOVE.getType());
        // 移除redis中相应数据
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + userId;
        redisTemplate.opsForHash().delete(key, hashKey);
        // 返回点赞数
        return likeCount;
    }
}
