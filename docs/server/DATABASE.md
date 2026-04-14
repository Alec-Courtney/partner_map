# 搭子地图 - 数据库设计

> 版本：v1.0  
> 日期：2026-04-14  
> 配套文档：[架构设计](./ARCHITECTURE.md) | [接口设计](./API.md)

---

## 一、数据库概览

**数据库**：MySQL 8.0  
**字符集**：utf8mb4  
**排序规则**：utf8mb4_unicode_ci  
**表数量**：7张

### ER 关系图

```
┌──────────┐     ┌─────────────────┐     ┌───────────────────┐
│  school  │     │  partner_request │     │ request_snapshot  │
│          │◄────┤  publisher_id    │─────┤  request_id       │
└──────────┘     │  school_id(FK)   │     │  user_id          │
                 │                  │     └───────────────────┘
┌──────────┐     │                  │
│   user   │◄────┤                  │     ┌───────────────────┐
│          │     └────────┬─────────┘     │   participation   │
└────┬─────┘              │               │                   │
     │                    │               │  request_id(FK)   │
     │                    │               │  user_id(FK)       │
     │         ┌──────────┴──────┐        └───────────────────┘
     │         │                 │
     │    ┌────┴─────┐    ┌────┴─────┐
     │    │chat_room  │    │evaluation │
     │    │           │    │           │
     │    │ request_id│    │request_id │
     │    │requester_id│   │from_user  │
     │    │publisher_id │   │to_user   │
     │    └────┬──────┘    └──────────┘
     │         │
     │    ┌────┴──────┐
     │    │chat_message│
     │    │            │
     │    │chat_room_id│
     │    └────────────┘
     │
     └──→ (FK references throughout)
```

---

## 二、表结构定义

### 2.1 user（用户表）

```sql
CREATE TABLE `user` (
  `user_id`       VARCHAR(36)   PRIMARY KEY COMMENT '用户ID',
  `nickname`      VARCHAR(50)   NOT NULL COMMENT '昵称',
  `password_hash` VARCHAR(255)  NOT NULL COMMENT '密码(BCrypt)',
  `avatar_url`    VARCHAR(500)  DEFAULT NULL COMMENT '头像URL',
  `gender`        TINYINT       NOT NULL DEFAULT 0 COMMENT '性别：0未知/1男/2女',
  `school_id`     VARCHAR(36)   NOT NULL COMMENT '关联学校ID',
  `attend_count`  INT           NOT NULL DEFAULT 0 COMMENT '实际赴约次数',
  `attend_total`  INT           NOT NULL DEFAULT 0 COMMENT '应赴约总次数',
  `praise_count`  INT           NOT NULL DEFAULT 0 COMMENT '好评次数',
  `praise_total`  INT           NOT NULL DEFAULT 0 COMMENT '被评价总次数',
  `created_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  UNIQUE INDEX `uk_nickname` (`nickname`),
  INDEX `idx_school` (`school_id`),
  CONSTRAINT `fk_user_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

**字段说明**：

| 字段 | 说明 |
|------|------|
| attend_count / attend_total | attend_rate = attend_count / attend_total，应用层计算 |
| praise_count / praise_total | praise_rate = praise_count / praise_total，应用层计算 |
| gender | 性别枚举，用于需求性别要求校验 |
| nickname | 唯一索引，用于登录 |
| password_hash | BCrypt 哈希，rounds=10 |

---

### 2.2 school（学校表）

```sql
CREATE TABLE `school` (
  `school_id`  VARCHAR(36)   PRIMARY KEY COMMENT '学校ID',
  `name`       VARCHAR(100)  NOT NULL COMMENT '学校名称',
  `lat`        DOUBLE        NOT NULL COMMENT '纬度',
  `lng`        DOUBLE        NOT NULL COMMENT '经度',
  `city`       VARCHAR(50)   DEFAULT NULL COMMENT '城市',

  INDEX `idx_city` (`city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学校表';
```

**初始化数据**：

预先录入同城各大学数据，包含学校名称和经纬度。

---

### 2.3 partner_request（搭子需求表）

```sql
CREATE TABLE `partner_request` (
  `request_id`          VARCHAR(36)   PRIMARY KEY COMMENT '需求ID',
  `publisher_id`        VARCHAR(36)   NOT NULL COMMENT '发起者ID',
  `school_id`           VARCHAR(36)   NOT NULL COMMENT '归属学校ID（来自发起者）',
  `title`               VARCHAR(100)  NOT NULL COMMENT '标题',
  `description`         TEXT           DEFAULT NULL COMMENT '描述',
  `category`            TINYINT        NOT NULL COMMENT '分类：0学习/1运动/2美食/3出行/4娱乐/5购物',
  `request_lat`         DOUBLE         NOT NULL COMMENT '需求位置纬度',
  `request_lng`         DOUBLE         NOT NULL COMMENT '需求位置经度',
  `request_address`     VARCHAR(255)   DEFAULT NULL COMMENT '需求位置地址描述',
  `publish_lat`          DOUBLE         DEFAULT NULL COMMENT '发布位置纬度',
  `publish_lng`          DOUBLE         DEFAULT NULL COMMENT '发布位置经度',
  `max_participants`    INT            NOT NULL DEFAULT 2 COMMENT '人数上限',
  `current_participants` INT           NOT NULL DEFAULT 0 COMMENT '当前参与人数',
  `scheduled_time`      DATETIME       NOT NULL COMMENT '预定时间',
  `expire_before_min`   INT            NOT NULL DEFAULT 60 COMMENT '预定时间前N分钟失效',
  `gender_requirement`  TINYINT        NOT NULL DEFAULT 0 COMMENT '性别要求：0不限/1仅男/2仅女',
  `cost_description`    VARCHAR(255)   DEFAULT NULL COMMENT '消费说明',
  `status`              TINYINT        NOT NULL DEFAULT 0 COMMENT '状态：0招募中/1已满员/2已结束',
  `created_at`          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  INDEX `idx_publisher` (`publisher_id`),
  INDEX `idx_school_status` (`school_id`, `status`),
  INDEX `idx_status` (`status`),
  INDEX `idx_category` (`category`),
  INDEX `idx_scheduled` (`scheduled_time`),
  INDEX `idx_location` (`request_lat`, `request_lng`),
  CONSTRAINT `fk_request_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `fk_request_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搭子需求表';
```

**索引说明**：

| 索引 | 用途 |
|------|------|
| idx_publisher | 查询用户发布的需求 |
| idx_school_status | 按学校+状态组合查询 |
| idx_status | 按状态过滤 |
| idx_category | 按分类过滤 |
| idx_scheduled | 定时任务查询即将失效的需求 |
| idx_location | 地图按区域检索 |

**状态枚举**：

| 值 | 含义 |
|----|------|
| 0 | 招募中 |
| 1 | 已满员 |
| 2 | 已结束 |

**分类枚举**：

| 值 | 含义 |
|----|------|
| 0 | 学习 |
| 1 | 运动 |
| 2 | 美食 |
| 3 | 出行 |
| 4 | 娱乐 |
| 5 | 购物 |

---

### 2.4 request_snapshot（需求快照表）

```sql
CREATE TABLE `request_snapshot` (
  `snapshot_id`    VARCHAR(36)  PRIMARY KEY COMMENT '快照ID',
  `request_id`     VARCHAR(36)  NOT NULL COMMENT '原需求ID',
  `user_id`        VARCHAR(36)  NOT NULL COMMENT '参与者ID',
  `snapshot_data`  JSON         NOT NULL COMMENT '参与时刻的需求详情JSON',
  `joined_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '参与时间',

  UNIQUE INDEX `uk_request_user` (`request_id`, `user_id`),
  INDEX `idx_user` (`user_id`),
  CONSTRAINT `fk_snapshot_request` FOREIGN KEY (`request_id`) REFERENCES `partner_request` (`request_id`),
  CONSTRAINT `fk_snapshot_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='需求快照表';
```

**snapshot_data 示例**：
```json
{
  "title": "周末羽毛球约起来",
  "description": "周末下午打球，有球拍最好",
  "category": 1,
  "requestLat": 30.5428,
  "requestLng": 114.3665,
  "maxParticipants": 4,
  "scheduledTime": "2026-04-15T16:00:00",
  "genderRequirement": "不限",
  "costDescription": "AA制，预计30元/人"
}
```

---

### 2.5 chat_room（聊天室表）

```sql
CREATE TABLE `chat_room` (
  `chat_room_id`  VARCHAR(36)  PRIMARY KEY COMMENT '聊天室ID',
  `request_id`    VARCHAR(36)  NOT NULL COMMENT '关联需求ID',
  `requester_id`  VARCHAR(36)  NOT NULL COMMENT '申请者ID',
  `publisher_id`  VARCHAR(36)  NOT NULL COMMENT '发起者ID',
  `last_message`   TEXT         DEFAULT NULL COMMENT '最后一条消息内容',
  `last_message_at` DATETIME   DEFAULT NULL COMMENT '最后消息时间',
  `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0进行中/1已解散',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

  INDEX `idx_request` (`request_id`),
  INDEX `idx_requester` (`requester_id`),
  INDEX `idx_publisher` (`publisher_id`),
  UNIQUE INDEX `uk_request_requester` (`request_id`, `requester_id`),
  CONSTRAINT `fk_chatroom_request` FOREIGN KEY (`request_id`) REFERENCES `partner_request` (`request_id`),
  CONSTRAINT `fk_chatroom_requester` FOREIGN KEY (`requester_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `fk_chatroom_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天室表';
```

**设计说明**：
- 每个需求中，一个申请者与发起者之间至多一个聊天室（`uk_request_requester` 唯一索引）
- `requester_id` 为申请者，`publisher_id` 为发起者
- `last_message` 和 `last_message_at` 冗余存储最后消息，避免列表查询 JOIN
- 评价完成后 status 变为 1（已解散）

---

### 2.6 chat_message（聊天消息表）

```sql
CREATE TABLE `chat_message` (
  `message_id`    VARCHAR(36)  PRIMARY KEY COMMENT '消息ID',
  `chat_room_id`  VARCHAR(36)  NOT NULL COMMENT '聊天室ID',
  `sender_id`     VARCHAR(36)  NOT NULL COMMENT '发送者ID',
  `content`       TEXT         NOT NULL COMMENT '消息内容',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

  INDEX `idx_room_created` (`chat_room_id`, `created_at`),
  INDEX `idx_sender` (`sender_id`),
  CONSTRAINT `fk_message_room` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_room` (`chat_room_id`),
  CONSTRAINT `fk_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';
```

**设计说明**：
- 聊天室解散后消息不再保留（随聊天室状态标记）
- 按时间分页查询（`idx_room_created` 复合索引）

---

### 2.7 participation（参与记录表）

```sql
CREATE TABLE `participation` (
  `participation_id` VARCHAR(36)  PRIMARY KEY COMMENT '参与ID',
  `request_id`       VARCHAR(36)  NOT NULL COMMENT '需求ID',
  `user_id`           VARCHAR(36)  NOT NULL COMMENT '参与者ID',
  `status`           TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0待审批/1已加入/2已拒绝/3已退出',
  `joined_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '参与时间',

  UNIQUE INDEX `uk_request_user` (`request_id`, `user_id`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_status` (`status`),
  CONSTRAINT `fk_participation_request` FOREIGN KEY (`request_id`) REFERENCES `partner_request` (`request_id`),
  CONSTRAINT `fk_participation_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='参与记录表';
```

**状态枚举**：

| 值 | 含义 |
|----|------|
| 0 | 待审批 |
| 1 | 已加入 |
| 2 | 已拒绝 |
| 3 | 已退出 |

---

### 2.8 evaluation（评价记录表）

```sql
CREATE TABLE `evaluation` (
  `evaluation_id`  VARCHAR(36)  PRIMARY KEY COMMENT '评价ID',
  `request_id`     VARCHAR(36)  NOT NULL COMMENT '需求ID',
  `from_user_id`   VARCHAR(36)  NOT NULL COMMENT '评价者ID',
  `to_user_id`     VARCHAR(36)  NOT NULL COMMENT '被评价者ID',
  `attended`       BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '是否赴约',
  `praised`        BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '是否好评',
  `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',

  UNIQUE INDEX `uk_request_from_to` (`request_id`, `from_user_id`, `to_user_id`),
  INDEX `idx_from` (`from_user_id`),
  INDEX `idx_to` (`to_user_id`),
  INDEX `idx_request` (`request_id`),
  CONSTRAINT `fk_eval_request` FOREIGN KEY (`request_id`) REFERENCES `partner_request` (`request_id`),
  CONSTRAINT `fk_eval_from` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `fk_eval_to` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价记录表';
```

**设计说明**：
- `uk_request_from_to` 唯一索引确保同一需求中同一对用户只能评价一次
- 评价完成后需更新 `user` 表的计数字段：

```sql
-- 更新被评价者的赴约统计
UPDATE user 
SET attend_total = attend_total + 1,
    attend_count = attend_count + CASE WHEN {attended} THEN 1 ELSE 0 END
WHERE user_id = {toUserId};

-- 更新被评价者的好评统计
UPDATE user 
SET praise_total = praise_total + 1,
    praise_count = praise_count + CASE WHEN {praised} THEN 1 ELSE 0 END
WHERE user_id = {toUserId};
```

---

## 三、索引设计总结

| 表 | 索引名 | 字段 | 类型 | 用途 |
|----|--------|------|------|------|
| user | uk_nickname | nickname | UNIQUE | 登录查询 |
| user | idx_school | school_id | NORMAL | 按学校筛选 |
| partner_request | idx_publisher | publisher_id | NORMAL | 查询用户发布的需求 |
| partner_request | idx_school_status | school_id, status | NORMAL | 按学校+状态筛选 |
| partner_request | idx_status | status | NORMAL | 按状态过滤 |
| partner_request | idx_category | category | NORMAL | 按分类过滤 |
| partner_request | idx_scheduled | scheduled_time | NORMAL | 定时任务查询 |
| partner_request | idx_location | request_lat, request_lng | NORMAL | 地图区域检索 |
| request_snapshot | uk_request_user | request_id, user_id | UNIQUE | 防重复快照 |
| chat_room | uk_request_requester | request_id, requester_id | UNIQUE | 防重复聊天室 |
| chat_room | idx_request | request_id | NORMAL | 按需求查聊天室 |
| chat_message | idx_room_created | chat_room_id, created_at | NORMAL | 聊天记录分页 |
| participation | uk_request_user | request_id, user_id | UNIQUE | 防重复参与 |
| evaluation | uk_request_from_to | request_id, from_user_id, to_user_id | UNIQUE | 防重复评价 |

---

## 四、数据量估算

### 4.1 预估数据量

| 表 | 预估月增量 | 预估年增量 |
|----|-----------|-----------|
| user | 5,000 | 60,000 |
| partner_request | 20,000 | 240,000 |
| request_snapshot | 60,000 | 720,000 |
| participation | 60,000 | 720,000 |
| chat_room | 60,000 | 720,000 |
| chat_message | 600,000 | 7,200,000 |
| evaluation | 180,000 | 2,160,000 |

### 4.2 优化建议

- **chat_message** 表数据量增长最快，如需可按月归档历史消息
- 地理位置查询优先使用 `(request_lat, request_lng)` 索引 + 应用层矩形过滤
- `school_id` 冗余到 `partner_request` 表，避免 JOIN 查询
- `chat_room` 表冗余 `last_message`/`last_message_at` 字段，避免列表查询时 JOIN 聚合