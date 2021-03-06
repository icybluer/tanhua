# tanhua

本项目是一个陌生人的在线交友平台，在该平台中可以搜索附近的人，查看好友动态，平台还会通过大数据计算进行智能推荐，通过智能推荐可以找到更加匹配的好友，这样才能增进用户对产品的喜爱度。本项目还提供了在线即时通讯功能，可以实时的与好友进行沟通，让沟通随时随地的进行。

## 1 项目背景

本项目定位于 **陌生人交友市场**。

- 根据《2018社交领域投融资报告》中指出：虽然相比2017年，投融资事件减少29.5%，但是融资的总额却大幅增长，达到68%。
- 这些迹象说明：社交领域的发展规模正在扩大，而很多没有特色的产品也会被淘汰。而随着那些尾部产品的倒下，对我们来说就是机会，及时抓住不同社交需求的机会。以社交为核心向不同的细分领域衍生正在逐渐走向成熟化。
- 而我们按照娱乐形式和内容为主两个维度，将社交行业公司分类为：即时通信 内容社群 陌生人社交 泛娱乐社交以及兴趣社交几个领域。
- 而在2018年社交的各个细分领域下，均有备受资本所关注的项目，根据烯牛数据2018年的报告中，也同样指出：内容社交及陌生人社交为资本重要关注领域，合计融资占比达73%。

根据市场现状以及融资事件来看：陌生人社交、内容社群、兴趣社交在2019年仍然保持强劲的动力，占到近70%的比例，它们仍然是资本市场主要关注领域。从增长率来看陌生人社交的增长速度远远大于其他几类，因此我们从这个方向入手  

## 2 功能列表

|  功能   |           说明            |           备注           |
| :---: | :---------------------: | :--------------------: |
| 注册、登录 |  用户无需单独注册，直接通过手机号登录即可   |    首次登录成功后需要完善个人信息     |
|  交友   |  主要功能有测灵魂、桃花传音、搜附近、探花等  |           /            |
|  圈子   | 类似微信朋友圈，用户可以发动态、查看好友动态等 |           /            |
|  消息   |      通知类消息与即时通讯消息       |           /            |
|  小视频  |    类似抖音，用户可以发小视频、评论等    | 显示小视频列表需要进行推荐算法计算后进行展现 |
|  我的   |   我的动态、关注数、粉丝数、通用设置等    |           /            |

## 3 功能概述

### 3.1 用户登录

业务说明：

用户通过手机验证码进行登录，如果是第一次登录则需要完善个人信息，在上传图片时，需要对上传的图片做人像的校验，防止用户上传非人像的图片作为头像。流程完成后，则登录成功。 

### 3.2 交友

交友是本项目的核心功能之一，用户可以查看好友，添加好友，搜索好友等操作。

### 3.3 圈子

1. 推荐频道：根据用户问卷及喜好推荐的相似用户动态
2. 内容展示：

​	①  用户信息：头像、昵称、性别、年龄、标签

​	②  动态信息：文本内容、图片（不超过6张）或小视频

​	③  其他信息：距离、时间、点赞数、评论数、收藏量等

### 3.4 消息

消息包含通知类的消息和好友消息。

### 3.5 小视频

1. 发布/上传小视频
2. 支持点赞、评论等操作
3. 内部基于大数据算法，根据用户行为特征推荐不同的视频列表

## 4 技术方案

前端系统：

- 手机端：flutter + android + 环信SDK + redux + shared_preferences + connectivity + iconfont + webview + sqflite
- 管理后台：vue

后端：

- Spring Boot + SpringMVC + Mybatis + MybatisPlus + Dubbo
- 采用Dubbo作为微服务架构技术
- 采用RabbitMQ 作为消息服务中间件
- 采用Redis实现缓存的高可用
- 采用Mysql,MongoDB进行海量数据的存储
- 集成第三方应用组件（阿里云，短信，即时通信）
- 基于Spark Mllib推荐系统 

## 5 技术解决方案

- 使用MongoDBgeo实现附近的人的解决方案
- 使用Spark + Mllib实现智能推荐的解决方案
- 使用MongoDB进行海量数据的存储的解决方案
- 使用采用分布式文件系统存储小视频数据的解决方案
- 使用百度人脸识别的解决方案
- 使用阿里云进行短信验证码发送的解决方案

