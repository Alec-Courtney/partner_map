# 搭子地图 - 服务端架构设计

> 版本：v1.0  
> 日期：2026-04-14  
> 配套文档：[接口设计](./API.md) | [数据库设计](./DATABASE.md)

---

## 一、整体架构

```
┌──────────────────────────────────────────────────┐
│                    Android 客户端                  │
│         Retrofit + OkHttp + WebSocket             │
└──────────────────────┬───────────────────────────┘
                        │ HTTP / WS
                        ▼
┌──────────────────────────────────────────────────┐
│              Spring Boot 应用服务器                │
│  ┌───────────┐ ┌───────────┐ ┌───────────┐     │
│  │ Controller │ │  Service  │ │ Schedule   │     │
│  │    层      │ │    层     │ │  定时任务   │     │
│  └─────┬─────┘ └─────┬─────┘ └─────┬─────┘     │
│        └───────┬──────┘              │            │
│                ▼                     │            │
│  ┌──────────────────────────────────────┐        │
│  │           Repository 层              │        │
│  └────────────────┬─────────────────────┘        │
│                   ▼                              │
│           ┌──────────────┐                       │
│           │    MySQL     │                       │
│           └──────────────┘                       │
└──────────────────────────────────────────────────┘
```

---

## 二、技术选型

| 层级 | 技术 | 说明 |
|------|------|------|
| 语言 | Java 17 | 与客户端技术栈统一 |
| 框架 | Spring Boot 3.x | Web + WebSocket + Schedule |
| ORM | Spring Data JPA | 数据持久层 |
| 数据库 | MySQL 8.0 | 主数据存储 |
| 认证 | Spring Security + JWT | 无状态认证 |
| 接口文档 | Swagger / OpenAPI 3.0 | 接口自动文档 |
| 构建工具 | Maven | 项目构建 |
| 部署 | Docker | 容器化部署 |

---

## 三、项目结构

```
com.partnermap.server/
├── PartnerMapApplication.java           // 启动类
├── config/
│   ├── SecurityConfig.java              // 安全配置
│   ├── WebSocketConfig.java             // WebSocket 配置
│   └── CorsConfig.java                  // 跨域配置
│
├── controller/
│   ├── AuthController.java              // 认证接口
│   ├── UserController.java              // 用户接口
│   ├── RequestController.java           // 需求接口
│   ├── ParticipationController.java      // 参与接口
│   ├── ChatController.java             // 聊天接口
│   ├── EvaluationController.java        // 评价接口
│   └── SchoolController.java            // 学校接口
│
├── service/
│   ├── AuthService.java                 // 认证服务
│   ├── UserService.java                 // 用户服务
│   ├── RequestService.java             // 需求服务
│   ├── ParticipationService.java        // 参与服务
│   ├── ChatService.java                // 聊天服务
│   ├── EvaluationService.java           // 评价服务
│   ├── WebSocketService.java            // WebSocket 推送服务
│   └── ScheduleService.java             // 定时任务服务
│
├── repository/
│   ├── UserRepository.java
│   ├── RequestRepository.java
│   ├── ParticipationRepository.java
│   ├── ChatRoomRepository.java
│   ├── ChatMessageRepository.java
│   ├── EvaluationRepository.java
│   └── SchoolRepository.java
│
├── entity/
│   ├── User.java
│   ├── PartnerRequest.java
│   ├── RequestSnapshot.java
│   ├── ChatRoom.java
│   ├── ChatMessage.java
│   ├── Participation.java
│   ├── Evaluation.java
│   └── School.java
│
├── dto/
│   ├── request/                          // 请求 DTO
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── CreateRequestDTO.java
│   │   ├── UpdateRequestDTO.java
│   │   ├── CreateParticipationDTO.java
│   │   └── CreateEvaluationDTO.java
│   └── response/                          // 响应 DTO
│       ├── ApiResponse.java
│       ├── UserResponse.java
│       ├── RequestResponse.java
│       ├── ParticipationResponse.java
│       ├── ChatRoomResponse.java
│       ├── ChatMessageResponse.java
│       └── EvaluationResponse.java
│
├── handler/
│   └── WebSocketHandler.java             // WebSocket 消息处理器
│
├── security/
│   ├── JwtUtil.java                       // JWT 工具类
│   ├── JwtFilter.java                    // JWT 过滤器
│   └── UserDetailsServiceImpl.java       // 用户认证实现
│
└── util/
    ├── GeoUtil.java                       // 地理距离计算
    └── DateTimeUtil.java                  // 时间处理工具
```

---

## 四、分层职责

### 4.1 Controller 层
- 接收 HTTP 请求，参数校验
- 调用 Service 层处理业务
- 返回统一 `ApiResponse` 格式
- 不包含业务逻辑

### 4.2 Service 层
- 核心业务逻辑处理
- 事务管理（`@Transactional`）
- 调用 Repository 操作数据
- 调用 WebSocketService 推送消息

### 4.3 Repository 层
- 继承 `JpaRepository`
- 自定义查询方法（`@Query`）
- 纯数据访问，不包含业务逻辑

### 4.4 Entity / DTO 分离
- Entity 对应数据库表，不对外暴露
- DTO 负责请求/响应的数据传输
- Service 层负责 Entity 与 DTO 的转换

---

## 五、核心服务设计

### 5.1 AuthService

```
register(nickname, password, schoolId, avatar?)
  → 检查昵称唯一性
  → BCrypt 加密密码
  → 创建 User
  → 生成 JWT Token
  → 返回 token

login(nickname, password)
  → 查找用户
  → 验证密码
  → 生成 JWT Token
  → 返回 token
```

### 5.2 RequestService

```
createRequest(publisherId, requestDTO)
  → 创建 Request（status=0）
  → 返回 request 详情

getNearbyRequests(lat, lng, radius, category, schoolId, schoolFilter, timeRange, page, size)
  → 查询范围内的需求列表
  → 返回分页列表

updateRequest(requestId, requestDTO)
  → 校验发起者身份
  → 更新需求（不影响已有快照）

completeRequest(requestId, userId)
  → 校验发起者身份
  → 更新状态为已结束（status=2）
  → 为所有参与者创建待评价记录
  → 推送通知
```

### 5.3 ParticipationService

```
requestParticipation(requestId, userId)
  → 校验需求状态（招募中）
  → 校验性别要求
  → 检查是否已申请
  → 创建 ChatRoom
  → 创建 Participation（status=0 待审批）
  → 返回 chatRoomId

approveParticipation(participationId, publisherId)
  → 校验发起者身份
  → 更新 participation status=1
  → 创建 RequestSnapshot（保存快照）
  → 更新 currentParticipants
  → 若达到 maxParticipants → 更新 request status=1（已满员）
  → WebSocket 推送通知

rejectParticipation(participationId, publisherId)
  → 校验发起者身份
  → 更新 participation status=2
  → WebSocket 推送通知
```

### 5.4 ChatService

```
getChatRooms(userId)
  → 返回用户所有聊天室列表

getMessages(chatRoomId, page, size)
  → 返回聊天记录（分页）

sendMessage(chatRoomId, senderId, content)
  → 保存消息到数据库
  → WebSocket 推送给对方
  → 返回消息详情
```

### 5.5 EvaluationService

```
createEvaluation(requestId, fromUserId, toUserId, attended, praised)
  → 校验双方均参与该需求
  → 校验未重复评价
  → 创建 Evaluation 记录
  → 更新被评价者的 attend_count/attend_total 和 praise_count/praise_total
  → 检查该需求是否所有人已评价完毕
  → 若完毕 → 解散聊天室
```

### 5.6 ScheduleService

```
// 每5分钟执行
expireRequests()
  → 查找 status=0 且 scheduled_time - expire_before_min <= NOW()
  → 更新 status=2

// 每5分钟执行
autoCompleteRequests()
  → 查找 status=0/1 且 scheduled_time + 24h <= NOW()
  → 更新 status=2
  → 创建待评价记录

// 每天执行
autoEvaluateExpired()
  → 查找超过10天未评价的记录
  → 自动填写 attended=true, praised=true
  → 更新被评价者统计
```

---

## 六、WebSocket 设计

### 6.1 连接管理

- 客户端通过 `ws://{server}:8080/ws/chat?token=xxx` 建立 WebSocket 连接
- 服务端验证 JWT Token，建立用户与 WebSocket Session 的映射
- 维护内存中的 userId → Session 映射（ConcurrentHashMap）

### 6.2 消息类型

**客户端发送**：

| type | 说明 | 字段 |
|------|------|------|
| SEND_MESSAGE | 发送聊天消息 | chatRoomId, content |
| HEARTBEAT | 心跳 | - |

**服务端推送**：

| type | 说明 | 字段 |
|------|------|------|
| NEW_MESSAGE | 新消息 | chatRoomId, senderId, senderNickname, content, createdAt |
| PARTICIPATION_APPROVED | 参与批准 | chatRoomId, requestId |
| PARTICIPATION_REJECTED | 参与拒绝 | chatRoomId, requestId |
| REQUEST_COMPLETED | 需求已完成 | requestId |
| EVALUATION_REQUIRED | 需要评价 | requestId |
| ERROR | 错误 | message |

### 6.3 连接生命周期

```
connect → 认证通过 → 注册在线状态
  ├─ 接收消息 → 路由处理 → 推送目标用户
  ├─ 心跳保活（30s间隔）
  └─ disconnect → 移除在线状态
```

---

## 七、安全设计

### 7.1 认证
- JWT Token，过期时间 7 天
- 请求头 `Authorization: Bearer <token>`
- WebSocket 通过 URL 参数传递 Token

### 7.2 密码
- BCrypt 加密，rounds=10

### 7.3 接口权限
- `/api/v1/auth/**` — 公开
- 其他接口 — 需认证

### 7.4 参数校验
- 使用 `spring-boot-starter-validation`
- Controller 层 `@Valid` 校验请求参数
- Service 层二次校验业务规则

---

## 八、缓存策略

Demo 项目暂不引入分布式缓存，采用以下简单策略：
- 学校列表：应用启动时加载到内存，定时刷新
- WebSocket Session：ConcurrentHashMap 维护 userId → Session 映射
- 如需后续扩展，可引入 Redis

---

## 九、异常处理

### 9.1 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 9.2 错误码设计

| 范围 | 说明 |
|------|------|
| 200 | 成功 |
| 1001 | 参数校验失败 |
| 1002 | 未认证 |
| 1003 | 无权限 |
| 2001 | 用户不存在 |
| 2002 | 昵称已存在 |
| 2003 | 密码错误 |
| 3001 | 需求不存在 |
| 3002 | 需求已满员 |
| 3003 | 需求已结束 |
| 3004 | 不可编辑 |
| 3005 | 需求已关闭 |
| 4001 | 聊天室不存在 |
| 4002 | 不可进入聊天室 |
| 5001 | 已评价 |
| 5002 | 不可评价自己 |
| 6001 | 重复参与 |
| 6002 | 性别不符 |