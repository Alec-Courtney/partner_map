# 搭子地图 - 接口设计

> 版本：v1.0  
> 日期：2026-04-14  
> 配套文档：[架构设计](./ARCHITECTURE.md) | [数据库设计](./DATABASE.md)

---

## 一、通用约定

### 1.1 Base URL

```
http://{server}:8080/api/v1
```

### 1.2 认证方式

- JWT Token，请求头：`Authorization: Bearer <token>`
- WebSocket 通过 URL 参数：`ws://{server}:8080/ws/chat?token=<token>`
- Token 有效期：7天

### 1.3 统一响应格式

**成功**：
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

**分页响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 42,
    "page": 1,
    "size": 20,
    "items": [ ... ]
  }
}
```

**失败**：
```json
{
  "code": 3002,
  "message": "需求已满员",
  "data": null
}
```

### 1.4 时间格式

- 请求/响应中所有时间字段使用 ISO 8601 格式：`yyyy-MM-dd'T'HH:mm:ss`
- 时间戳使用毫秒级 Unix 时间戳

---

## 二、认证模块

### 2.1 注册

```
POST /auth/register
```

**Request**：
```json
{
  "nickname": "小张",
  "password": "password123",
  "gender": 1,
  "schoolId": "uuid-school-1",
  "avatar": "base64string or null"
}
```

**Response**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": "uuid-user-1",
    "nickname": "小张",
    "token": "jwt_token_string"
  }
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| nickname | String | 是 | 昵称，2-20字符，唯一 |
| password | String | 是 | 密码，6-20字符 |
| gender | Int | 否 | 性别：0未知/1男/2女，默认0 |
| schoolId | String | 是 | 关联学校ID |
| avatar | String | 否 | 头像 Base64 编码 |

---

### 2.2 登录

```
POST /auth/login
```

**Request**：
```json
{
  "nickname": "小张",
  "password": "password123"
}
```

**Response**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": "uuid-user-1",
    "nickname": "小张",
    "avatar": "https://xxx/avatar.jpg",
    "gender": 1,
    "schoolId": "uuid-school-1",
    "schoolName": "XX大学",
    "token": "jwt_token_string"
  }
}
```

### 2.3 获取当前用户信息

```
GET /auth/me
```

**Response**：
```json
{
  "code": 200,
  "data": {
    "userId": "uuid-user-1",
    "nickname": "小张",
    "avatar": "https://xxx/avatar.jpg",
    "gender": 1,
    "schoolId": "uuid-school-1",
    "schoolName": "XX大学",
    "attendRate": 0.92,
    "praiseRate": 0.95
  }
}
```

---

## 三、用户模块

### 3.1 获取用户信息

```
GET /users/{userId}
```

**Response**：
```json
{
  "code": 200,
  "data": {
    "userId": "uuid-user-1",
    "nickname": "小张",
    "avatar": "https://xxx/avatar.jpg",
    "gender": 1,
    "genderName": "男",
    "schoolId": "uuid-school-1",
    "schoolName": "XX大学",
    "attendRate": 0.92,
    "praiseRate": 0.95,
    "publishCount": 15,
    "participateCount": 23
  }
}
```

### 3.2 更新用户信息

```
PUT /users/{userId}
```

**Request**：
```json
{
  "nickname": "小张新名字",
  "avatar": "base64string"
}
```

### 3.3 获取用户统计数据

```
GET /users/{userId}/stats
```

**Response**：
```json
{
  "code": 200,
  "data": {
    "userId": "uuid-user-1",
    "attendCount": 23,
    "attendTotal": 25,
    "attendRate": 0.92,
    "praiseCount": 22,
    "praiseTotal": 25,
    "praiseRate": 0.88,
    "publishCount": 15,
    "participateCount": 23
  }
}
```

---

## 四、学校模块

### 4.1 获取学校列表

```
GET /schools?city=武汉
```

**Response**：
```json
{
  "code": 200,
  "data": [
    {
      "schoolId": "uuid-school-1",
      "name": "武汉大学",
      "lat": 30.5428,
      "lng": 114.3665,
      "city": "武汉"
    }
  ]
}
```

参数 `city` 可选，不传则返回全部学校。

### 4.2 获取学校详情

```
GET /schools/{schoolId}
```

---

## 五、需求模块

### 5.1 发布需求

```
POST /requests
```

**Request**：
```json
{
  "title": "周末羽毛球约起来",
  "description": "周末下午打球，有球拍最好，新手也欢迎",
  "category": 1,
  "requestLat": 30.5428,
  "requestLng": 114.3665,
  "requestAddress": "XX体育中心",
  "publishLat": 30.5400,
  "publishLng": 114.3700,
  "maxParticipants": 4,
  "scheduledTime": "2026-04-15T16:00:00",
  "expireBeforeMin": 60,
  "genderRequirement": "不限",
  "costDescription": "AA制，预计30元/人"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | 是 | 标题，1-100字符 |
| description | String | 否 | 描述 |
| category | Int | 是 | 分类：0学习/1运动/2美食/3出行/4娱乐/5购物 |
| requestLat | Double | 是 | 需求位置纬度 |
| requestLng | Double | 是 | 需求位置经度 |
| requestAddress | String | 否 | 需求位置地址描述 |
| publishLat | Double | 否 | 发布位置纬度 |
| publishLng | Double | 否 | 发布位置经度 |
| maxParticipants | Int | 是 | 人数上限，最小2 |
| scheduledTime | String | 是 | 预定时间 |
| expireBeforeMin | Int | 是 | 预定时间前N分钟失效，最小0 |
| genderRequirement | Int | 否 | 性别要求：0不限/1仅男/2仅女，默认0 |
| costDescription | String | 否 | 消费说明 |

**Response**：
```json
{
  "code": 200,
  "data": {
    "requestId": "uuid-request-1",
    "publisherId": "uuid-user-1",
    "publisherNickname": "小张",
    "publisherAvatar": "https://xxx/avatar.jpg",
    "title": "周末羽毛球约起来",
    "description": "周末下午打球，有球拍最好，新手也欢迎",
    "category": 1,
    "categoryName": "运动",
    "requestLat": 30.5428,
    "requestLng": 114.3665,
    "requestAddress": "XX体育中心",
    "publishLat": 30.5400,
    "publishLng": 114.3700,
    "maxParticipants": 4,
    "currentParticipants": 0,
    "scheduledTime": "2026-04-15T16:00:00",
    "expireBeforeMin": 60,
"genderRequirement": 0,
    "genderRequirementName": "不限",
    "costDescription": "AA制，预计30元/人",
    "status": 0,
    "statusName": "招募中",
    "createdAt": "2026-04-14T10:00:00"
  }
}
```

---

### 5.2 获取需求列表

```
GET /requests?lat=30.5&lng=114.3&radius=10&category=0,1
    &schoolId=uuid-school-1&schoolFilter=all&timeRange=today
    &page=1&size=20
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| lat | Double | 是 | 用户当前纬度 |
| lng | Double | 是 | 用户当前经度 |
| radius | Int | 否 | 搜索半径(km)，默认10 |
| category | String | 否 | 分类过滤，逗号分隔 |
| schoolId | String | 否 | 学校ID（用于仅本校过滤） |
| schoolFilter | String | 否 | all/my，默认all |
| timeRange | String | 否 | today/week/month/all，默认all |
| page | Int | 否 | 页码，默认1 |
| size | Int | 否 | 每页条数，默认20 |

**Response**：
```json
{
  "code": 200,
  "data": {
    "total": 42,
    "page": 1,
    "size": 20,
    "items": [
      {
        "requestId": "uuid-request-1",
        "publisherId": "uuid-user-1",
        "publisherNickname": "小张",
        "publisherAvatar": "https://xxx/avatar.jpg",
        "title": "周末羽毛球约起来",
        "category": 1,
        "categoryName": "运动",
        "requestLat": 30.5428,
        "requestLng": 114.3665,
        "requestAddress": "XX体育中心",
        "maxParticipants": 4,
        "currentParticipants": 2,
        "scheduledTime": "2026-04-15T16:00:00",
"genderRequirement": 0,
        "costDescription": "AA制，预计30元/人",
        "status": 0,
        "statusName": "招募中",
        "createdAt": "2026-04-14T10:00:00"
      }
    ]
  }
}
```

---

### 5.3 获取需求详情

```
GET /requests/{requestId}
```

**Response**：在列表项基础上增加以下字段：

```json
{
  "code": 200,
  "data": {
    "...列表所有字段...",
    "description": "完整描述",
    "expireBeforeMin": 60,
    "publishLat": 30.5400,
    "publishLng": 114.3700,
    "participants": [
      {
        "userId": "uuid-user-2",
        "nickname": "小李",
        "avatar": "https://xxx/avatar2.jpg",
        "schoolName": "YY大学",
        "praiseRate": 0.88,
        "joinedAt": "2026-04-14T11:00:00"
      }
    ],
    "isPublisher": false,
    "myParticipationStatus": 1,
    "mySnapshotData": null
  }
}
```

| 新增字段 | 说明 |
|---------|------|
| participants | 已加入的参与者列表 |
| isPublisher | 当前用户是否为发起者 |
| myParticipationStatus | 当前用户参与状态：null未参与/0待审批/1已加入/2已拒绝 |
| mySnapshotData | 当前用户参与时的快照数据，未参与为null |

---

### 5.4 编辑需求

```
PUT /requests/{requestId}
```

**Request**：与发布需求相同字段（可部分更新）

**业务规则**：
- 仅发起者可编辑
- 已有参与者时仍可编辑，但不影响已有快照

---

### 5.5 关闭需求

```
DELETE /requests/{requestId}
```

**业务规则**：
- 仅发起者可关闭
- 状态变为已结束（status=2）
- 通知所有已参与/待审批的用户

---

### 5.6 标记需求已完成

```
PUT /requests/{requestId}/complete
```

**业务规则**：
- 仅发起者可标记
- 需求状态变为已结束（status=2）
- 为所有参与者创建待评价记录
- 通过 WebSocket 推送通知

---

---

## 六、参与模块

### 6.1 申请参与

```
POST /requests/{requestId}/participate
```

**Response**：
```json
{
  "code": 200,
  "data": {
    "participationId": "uuid-part-1",
    "requestId": "uuid-request-1",
    "userId": "uuid-user-2",
    "status": 0,
    "chatRoomId": "uuid-chat-1"
  }
}
```

**业务规则**：
- 需求必须为招募中状态
- 不能重复参与
- 性别要求校验
- 自动创建聊天室并返回 chatRoomId

---

### 6.2 获取需求的参与列表

```
GET /requests/{requestId}/participations?status=1
```

**Response**：
```json
{
  "code": 200,
  "data": [
    {
      "participationId": "uuid-part-1",
      "userId": "uuid-user-2",
      "nickname": "小李",
      "avatar": "https://xxx/avatar2.jpg",
      "schoolName": "YY大学",
      "praiseRate": 0.88,
      "status": 1,
      "joinedAt": "2026-04-14T11:00:00"
    }
  ]
}
```

参数 `status` 可选，过滤参与状态。

---

### 6.3 审批通过

```
PUT /participations/{participationId}/approve
```

**业务规则**：
- 仅发起者可审批
- 创建 RequestSnapshot 保存快照
- 更新 currentParticipants
- 达到上限时更新需求状态为已满员
- WebSocket 通知申请者

---

### 6.4 审批拒绝

```
PUT /participations/{participationId}/reject
```

**业务规则**：
- 仅发起者可审批
- WebSocket 通知申请者

---

### 6.5 获取用户的参与列表

```
GET /users/{userId}/participations?page=1&size=20
```

**Response**：
```json
{
  "code": 200,
  "data": {
    "total": 5,
    "page": 1,
    "size": 20,
    "items": [
      {
        "participationId": "uuid-part-1",
        "requestId": "uuid-request-1",
        "title": "周末羽毛球约起来",
        "category": 1,
        "categoryName": "运动",
        "status": 1,
        "requestStatus": 0,
        "scheduledTime": "2026-04-15T16:00:00",
        "joinedAt": "2026-04-14T11:00:00"
      }
    ]
  }
}
```

---

### 6.6 获取用户发布的需求列表

```
GET /users/{userId}/requests?page=1&size=20
```

**Response**：
```json
{
  "code": 200,
  "data": {
    "total": 15,
    "page": 1,
    "size": 20,
    "items": [
      {
        "requestId": "uuid-request-1",
        "title": "周末羽毛球约起来",
        "category": 1,
        "status": 0,
        "statusName": "招募中",
        "currentParticipants": 2,
        "maxParticipants": 4,
        "scheduledTime": "2026-04-15T16:00:00",
        "createdAt": "2026-04-14T10:00:00"
      }
    ]
  }
}
```

---

## 七、聊天模块

### 7.1 WebSocket 连接

```
ws://{server}:8080/ws/chat?token=jwt_token_string
```

### 7.2 客户端发送消息

```json
{
  "type": "SEND_MESSAGE",
  "chatRoomId": "uuid-chat-1",
  "content": "你好，我想参加"
}
```

### 7.3 服务端推送消息

**新消息**：
```json
{
  "type": "NEW_MESSAGE",
  "chatRoomId": "uuid-chat-1",
  "senderId": "uuid-user-2",
  "senderNickname": "小李",
  "content": "你好，我想参加",
  "createdAt": "2026-04-14T10:05:00"
}
```

**参与批准**：
```json
{
  "type": "PARTICIPATION_APPROVED",
  "chatRoomId": "uuid-chat-1",
  "requestId": "uuid-request-1"
}
```

**参与拒绝**：
```json
{
  "type": "PARTICIPATION_REJECTED",
  "chatRoomId": "uuid-chat-1",
  "requestId": "uuid-request-1"
}
```

**需求已完成**：
```json
{
  "type": "REQUEST_COMPLETED",
  "requestId": "uuid-request-1"
}
```

**需要评价**：
```json
{
  "type": "EVALUATION_REQUIRED",
  "requestId": "uuid-request-1"
}
```

---

### 7.4 获取我的聊天室列表

```
GET /chat-rooms
```

**Response**：
```json
{
  "code": 200,
  "data": [
    {
      "chatRoomId": "uuid-chat-1",
      "requestId": "uuid-request-1",
      "requestTitle": "周末羽毛球约起来",
      "otherUserId": "uuid-user-1",
      "otherNickname": "小张",
      "otherAvatar": "https://xxx/avatar.jpg",
      "lastMessage": "你好，我想参加",
      "lastMessageAt": "2026-04-14T10:05:00",
      "status": 0
    }
  ]
}
```

**说明**：
- 直接通过 Token 识别当前用户，无需传 userId
- `otherUserId` 为对话对方的用户ID（若当前用户是申请者则为发起者，反之亦然）

---

### 7.5 获取聊天历史消息

```
GET /chat-rooms/{chatRoomId}/messages?page=1&size=50
```

**Response**：
```json
{
  "code": 200,
  "data": {
    "total": 12,
    "page": 1,
    "size": 50,
    "items": [
      {
        "messageId": "uuid-msg-1",
        "chatRoomId": "uuid-chat-1",
        "senderId": "uuid-user-2",
        "senderNickname": "小李",
        "content": "你好，我想参加",
        "createdAt": "2026-04-14T10:05:00"
      }
    ]
  }
}
```

---

## 八、评价模块

### 8.1 提交评价

```
POST /evaluations
```

**Request**：
```json
{
  "requestId": "uuid-request-1",
  "toUserId": "uuid-user-2",
  "attended": true,
  "praised": true
}
```

**业务规则**：
- 不能评价自己
- 不能重复评价同一个人
- 双方都必须是该需求的参与者

---

### 8.2 获取待评价列表

```
GET /users/{userId}/pending-evaluations
```

**Response**：
```json
{
  "code": 200,
  "data": [
    {
      "requestId": "uuid-request-1",
      "title": "周末羽毛球约起来",
      "scheduledTime": "2026-04-15T16:00:00",
      "usersToEvaluate": [
        {
          "userId": "uuid-user-2",
          "nickname": "小李",
          "avatar": "https://xxx/avatar2.jpg"
        }
      ],
      "deadline": "2026-04-25T16:00:00"
    }
  ]
}
```

`deadline` 为预定时间 + 24h + 10天。

---

### 8.3 获取需求的评价列表

```
GET /requests/{requestId}/evaluations
```

**Response**：
```json
{
  "code": 200,
  "data": [
    {
      "evaluationId": "uuid-eval-1",
      "fromUserId": "uuid-user-1",
      "toUserId": "uuid-user-2",
      "attended": true,
      "praised": true,
      "createdAt": "2026-04-16T10:00:00"
    }
  ]
}
```

> 注意：此接口仅对需求发起者可见，不对外暴露评价者身份详情。

---

## 九、错误码汇总

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 1001 | 参数校验失败 |
| 1002 | 未认证（Token 无效或过期） |
| 1003 | 无权限（操作非自己的资源） |
| 2001 | 用户不存在 |
| 2002 | 昵称已存在 |
| 2003 | 密码错误 |
| 3001 | 需求不存在 |
| 3002 | 需求已满员 |
| 3003 | 需求已结束 |
| 3004 | 不可编辑（仅发起者可编辑） |
| 3005 | 需求已关闭 |
| 4001 | 聊天室不存在 |
| 4002 | 不可进入聊天室（已满员） |
| 4003 | 已创建聊天室 |
| 5001 | 已评价过该用户 |
| 5002 | 不可评价自己 |
| 5003 | 双方非同一需求参与者 |
| 6001 | 重复参与 |
| 6002 | 性别不符 |