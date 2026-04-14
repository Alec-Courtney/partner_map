# 搭子地图 - 客户端架构设计

> 版本：v1.0  
> 日期：2026-04-14  
> 配套文档：[项目结构](./PROJECT_STRUCTURE.md) | [页面设计](./PAGES.md)

---

## 一、架构模式

采用 **MVVM（Model-View-ViewModel）** 架构，结合 Repository 模式进行数据层抽象。

```
┌─────────────────────────────────────────────┐
│                   View Layer                │
│  Activity / Fragment / Adapter / XML Layout │
├─────────────────────────────────────────────┤
│                ViewModel Layer              │
│  ViewModel / LiveData                       │
├─────────────────────────────────────────────┤
│               Repository Layer              │
│  Repository（统一数据源，协调本地与远程）      │
├──────────────┬──────────────────────────────┤
│  Local Data  │       Remote Data            │
│  SharedPreferences │  Retrofit + OkHttp      │
└──────────────┴──────────────────────────────┘
```

### 各层职责

| 层级 | 职责 | 核心组件 |
|------|------|---------|
| View | UI展示、用户交互 | Activity, Fragment, Adapter, XML |
| ViewModel | 持有UI状态、处理业务逻辑 | ViewModel, LiveData |
| Repository | 数据源协调 | Repository |
| Local | 登录态、简单偏好 | SharedPreferences |
| Remote | 网络请求、实时通信 | Retrofit, OkHttp WebSocket |

### 数据流向

```
View → ViewModel（用户事件）
ViewModel → Repository（请求数据）
Repository → Remote（网络请求）或 Local（偏好读取）
Repository → ViewModel（返回数据）
ViewModel → LiveData → View（UI更新）
```

---

## 二、技术选型

| 层级 | 技术 | 说明 |
|------|------|------|
| 语言 | Java 11 | Android 原生开发 |
| UI | Android XML Layout | 原生视图 |
| 导航 | Intent 显式跳转 | Activity 间导航 |
| 异步 | ExecutorService + Handler | 子线程网络与本地操作 |
| 数据观察 | LiveData | 生命周期感知的数据观察 |
| 网络请求 | Retrofit + OkHttp | RESTful API 通信 |
| JSON解析 | Gson | 请求/响应序列化 |
| 偏好存储 | SharedPreferences | 登录态、用户设置 |
| 地图 | 高德地图 SDK (AMap) | 地图展示、选点、定位 |
| 即时通讯 | OkHttp WebSocket | 聊天室实时消息 |
| 图片加载 | Glide | 头像与图片加载 |

---

## 三、核心依赖

```gradle
// Android 基础
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'

// 网络
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:okhttp:4.12.0'

// 高德地图
implementation 'com.amap.api:map2d:latest.release'
implementation 'com.amap.api:location:latest.release'

// 图片
implementation 'com.github.bumptech.glide:glide:4.16.0'

// WebSocket（OkHttp 已包含）
```

---

## 四、数据缓存策略

Demo 项目采用轻量缓存策略：
- **学校列表**：首次从 API 拉取后缓存到 SharedPreferences（JSON），后续启动先读缓存再刷新
- **当前用户信息**：登录后缓存到 SharedPreferences，修改后更新
- **Token**：存入 SharedPreferences，自动携带于请求头
- 其余数据直接从 API 获取，不做本地持久化

---

## 五、地图模块设计

### 5.1 高德地图集成要点

- 初始化：Application 中调用 `MapsInitializer.updatePrivacySettings()`
- 权限声明：`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`
- 定位：使用 AMapLocationClient 获取用户当前位置
- 编码：Geocoder 将经纬度转换为地址描述

### 5.2 PIN点渲染策略

- 按分类 item 状态渲染不同颜色的 Marker
- 大量标记时启用点聚合（ClusterOverlay）
- 可视区域变化时按新边界重新请求数据

### 5.3 分类图标映射

| Category枚举值 | 分类名称 | Marker颜色 | 资源图标 |
|----------------|---------|-----------|---------|
| 0 | 学习 | 蓝色 #2196F3 | ic_category_study |
| 1 | 运动 | 绿色 #4CAF50 | ic_category_sport |
| 2 | 美食 | 橙色 #FF9800 | ic_category_food |
| 3 | 出行 | 紫色 #9C27B0 | ic_category_travel |
| 4 | 娱乐 | 粉色 #E91E63 | ic_category_fun |
| 5 | 购物 | 黄色 #FFC107 | ic_category_shopping |

---

## 六、聊天模块设计

### 6.1 WebSocket 连接管理

```
WebSocketManager（单例）
  ├─ connect(token)          → 建立连接
  ├─ disconnect()            → 断开连接
  ├─ sendMessage(msg)        → 发送消息
  ├─ onMessage(callback)     → 收到消息回调
  ├─ onConnectionChange(cb)  → 连接状态回调
  └─ reconnect()             → 自动重连（固定5s间隔，最多3次）
```

### 6.2 消息收发流程

```
发送消息：
  用户输入 → ViewModel.sendMessage()
    → WebSocketManager.sendMessage()
      → 本地先存 Room + 更新UI（乐观更新）
      → 服务端确认后标记已发送

接收消息：
  WebSocketManager.onMessage()
    → 本地存 Room
    → LiveData 通知 UI 更新
```

---

## 七、认证与状态管理

### 7.1 登录态管理

- 登录成功后 Token 存入 SharedPreferences
- 每次启动检查 Token 有效性
- Token 过期后跳转登录页

### 7.2 全局状态

```
PartnerMapApplication
  ├─ 当前登录用户信息（User对象）
  ├─ 当前定位（LatLng）
  ├─ 关联学校信息
  └─ WebSocket连接状态
```

---

## 八、错误处理策略

| 场景 | 处理方式 |
|------|---------|
| 网络不可用 | Toast提示"网络不可用" |
| 服务器错误 | Toast提示错误信息 |
| Token过期 | 跳转登录页 |
| 地图加载失败 | 展示列表模式 + 提示 |
| 定位失败 | 提示手动选择位置 |
| WebSocket断连 | 自动重连 + 聊天页提示"重连中" |
| 发布失败 | Toast提示 + 保留表单内容 |