# 搭子地图 - 客户端项目结构

> 版本：v1.0  
> 日期：2026-04-14  
> 配套文档：[架构设计](./ARCHITECTURE.md) | [页面设计](./PAGES.md)

---

## 包结构

```
com.androidcourse.partner_map/
│
├── app/
│   ├── PartnerMapApplication.java        // Application 入口
│   └── Constants.java                    // 全局常量（服务器地址、分类枚举等）
│
├── data/
│   ├── remote/
│   │   ├── ApiClient.java               // Retrofit 客户端（单例）
│   │   ├── ApiService.java              // API 接口定义
│   │   └── WebSocketManager.java         // WebSocket 连接管理（单例）
│   │
│   └── repository/
│       ├── UserRepository.java           // 用户数据仓库
│       ├── RequestRepository.java        // 需求数据仓库
│       ├── ChatRepository.java           // 聊天数据仓库
│       └── EvaluationRepository.java     // 评价数据仓库
│
├── model/
│   ├── User.java                         // 用户模型
│   ├── PartnerRequest.java               // 需求模型
│   ├── RequestSnapshot.java              // 需求快照模型
│   ├── ChatRoom.java                     // 聊天室模型
│   ├── ChatMessage.java                  // 聊天消息模型
│   ├── Participation.java                // 参与记录模型
│   ├── Evaluation.java                   // 评价模型
│   ├── School.java                       // 学校模型
│   └── Category.java                    // 分类枚举模型
│
├── viewmodel/
│   ├── LoginViewModel.java               // 登录/注册
│   ├── MapViewModel.java                // 首页地图
│   ├── RequestListViewModel.java         // 需求列表
│   ├── CreateRequestViewModel.java       // 发布需求
│   ├── RequestDetailViewModel.java       // 需求详情
│   ├── ChatViewModel.java               // 聊天
│   ├── ProfileViewModel.java             // 个人中心
│   ├── MyRequestsViewModel.java          // 我的需求
│   ├── MyParticipationsViewModel.java    // 我的参与
│   └── EvaluationViewModel.java          // 评价
│
├── view/
│   ├── activity/
│   │   ├── LoginActivity.java            // 登录页
│   │   ├── RegisterActivity.java         // 注册页
│   │   ├── MainActivity.java             // 首页（地图+列表）
│   │   ├── CreateRequestActivity.java    // 发布需求页
│   │   ├── RequestDetailActivity.java    // 需求详情页
│   │   ├── ChatActivity.java            // 聊天页
│   │   ├── ProfileActivity.java          // 个人中心页
│   │   ├── MyRequestsActivity.java       // 我的需求页
│   │   ├── MyParticipationsActivity.java // 我的参与页
│   │   └── EvaluationActivity.java       // 评价页
│   │
│   ├── adapter/
│   │   ├── RequestListAdapter.java       // 需求列表适配器
│   │   ├── ChatMessageAdapter.java       // 聊天消息适配器
│   │   ├── ParticipationAdapter.java     // 参与者列表适配器
│   │   └── HistoryAdapter.java          // 历史（发布/参与）列表适配器
│   │
│   ├── fragment/
│   │   ├── MapFragment.java              // 地图 Fragment
│   │   └── RequestListFragment.java      // 列表 Fragment
│   │
│   └── widget/
│       ├── CategoryPicker.java           // 分类选择控件
│       └── RatingView.java               // 赴约率/好评率展示控件
│
└── util/
    ├── LocationHelper.java               // 高德定位工具类
    ├── TimeUtil.java                      // 时间格式化工具
    ├── CategoryHelper.java                // 分类图标/颜色映射
    └── SharedPreferencesUtil.java          // 偏好存储工具
```

---

## 模块依赖关系

```
view/activity  ──→  viewmodel  ──→  repository  ──→  data/remote
      │                │                 │                │
      │                │                 │                ↓
      │                │                 │          ApiService
      │                │                 │          WebSocketManager
      │                │                 │
      │                └──→  model       │
      │                                         │
      ├──→  view/adapter                      │
      ├──→  view/fragment                      │
      └──→  view/widget                        │
```

---

## 关键类说明

### PartnerMapApplication
- 初始化高德地图 SDK
- 初始化 Retrofit 客户端
- 管理 WebSocket 生命周期
- 持有全局用户状态

### Constants
```java
public class Constants {
    public static final String BASE_URL = "http://{server}:8080/api/v1";
    public static final String WS_URL = "ws://{server}:8080/ws/chat";
    public static final int PAGE_SIZE = 20;
    public static final int DEFAULT_RADIUS_KM = 10;
    public static final int EVALUATION_TIMEOUT_DAYS = 10;
    public static final int DEFAULT_EXPIRE_BEFORE_MIN = 60;
}
```

### Category 枚举
```java
public enum Category {
    STUDY(0, "学习", "#2196F3", R.drawable.ic_category_study),
    SPORT(1, "运动", "#4CAF50", R.drawable.ic_category_sport),
    FOOD(2, "美食", "#FF9800", R.drawable.ic_category_food),
    TRAVEL(3, "出行", "#9C27B0", R.drawable.ic_category_travel),
    FUN(4, "娱乐", "#E91E63", R.drawable.ic_category_fun),
    SHOPPING(5, "购物", "#FFC107", R.drawable.ic_category_shopping);

    public final int code;
    public final String label;
    public final String color;
    public final int iconRes;
}
```

### WebSocketManager
- 单例模式
- 登录成功后建立连接，登出后断开
- 固定间隔重连（5s，最多3次）
- 消息类型：SEND_MESSAGE, NEW_MESSAGE, PARTICIPATION_APPROVED, PARTICIPATION_REJECTED

### Repository 数据流
```java
// 以 RequestRepository 为例
public class RequestRepository {
    private final ApiService apiService;

    public LiveData<Resource<List<PartnerRequest>>> getNearbyRequests(double lat, double lng, int radius) {
        // 1. 直接发起网络请求
        // 2. LiveData 通知 UI 更新
        // 3. 错误时通过 Resource.error 通知
    }
}
```