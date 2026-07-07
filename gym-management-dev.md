# 健身房管理系统开发文档

## 1. 项目定位

本项目用于练习 Spring Boot + Vue 前后端分离开发，目标是做一个轻量版健身房管理系统。

系统分为两个角色：

- 管理员：管理会员、教练、课程、预约记录，查看统计数据。
- 会员：登录系统，修改个人信息，查看课程，预约课程，取消自己的预约。

本项目不追求复杂业务，重点练习：

- 登录认证
- 角色权限
- CRUD 增删改查
- 表关联查询
- 分页查询
- 前后端接口联调
- Vue 路由权限控制
- 简单数据统计

## 2. 技术栈

### 后端

- Java 17，或 Java 8 也可以
- Spring Boot
- MyBatis-Plus
- MySQL
- JWT
- Lombok
- Knife4j / Swagger，可选

### 前端

- Vue 3
- Vite
- Vue Router
- Pinia
- Axios
- Element Plus
- ECharts

## 3. 功能模块

### 3.1 登录模块

功能：

- 管理员登录
- 会员登录
- 登录成功后返回 token、用户信息、角色
- 前端根据角色跳转不同页面
- 未登录不能访问系统页面

角色：

```text
admin   管理员
member  会员
```

### 3.2 管理员端

管理员功能：

- 首页统计
- 会员管理
- 教练管理
- 课程管理
- 预约管理

页面建议：

```text
/admin/dashboard
/admin/member
/admin/coach
/admin/course
/admin/appointment
```

### 3.3 会员端

会员功能：

- 查看个人信息
- 修改个人信息
- 修改密码
- 查看课程列表
- 预约课程
- 取消预约
- 查看我的预约

页面建议：

```text
/member/profile
/member/course
/member/appointment
```

## 4. 数据库设计

数据库名：

```sql
CREATE DATABASE gym_manager DEFAULT CHARACTER SET utf8mb4;
USE gym_manager;
```

### 4.1 用户表 sys_user

用于登录认证。

```sql
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  role VARCHAR(20) NOT NULL COMMENT '角色：admin/member',
  related_id BIGINT NULL COMMENT '关联会员ID，管理员为空',
  status TINYINT DEFAULT 1 COMMENT '状态：1正常，0禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 4.2 会员表 member

```sql
CREATE TABLE member (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL COMMENT '姓名',
  phone VARCHAR(20) COMMENT '手机号',
  gender VARCHAR(10) COMMENT '性别',
  age INT COMMENT '年龄',
  card_type VARCHAR(30) COMMENT '会员卡类型',
  expire_time DATE COMMENT '会员到期时间',
  status TINYINT DEFAULT 1 COMMENT '状态：1正常，0禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 4.3 教练表 coach

```sql
CREATE TABLE coach (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL COMMENT '姓名',
  phone VARCHAR(20) COMMENT '手机号',
  specialty VARCHAR(100) COMMENT '擅长项目',
  entry_date DATE COMMENT '入职日期',
  status TINYINT DEFAULT 1 COMMENT '状态：1在职，0离职',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 4.4 课程表 course

```sql
CREATE TABLE course (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL COMMENT '课程名称',
  type VARCHAR(50) COMMENT '课程类型',
  coach_id BIGINT COMMENT '教练ID',
  start_time DATETIME COMMENT '开始时间',
  end_time DATETIME COMMENT '结束时间',
  capacity INT DEFAULT 20 COMMENT '人数上限',
  booked_count INT DEFAULT 0 COMMENT '已预约人数',
  status TINYINT DEFAULT 1 COMMENT '状态：1可预约，0停课',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 4.5 预约表 appointment

```sql
CREATE TABLE appointment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_id BIGINT NOT NULL COMMENT '会员ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  status VARCHAR(20) DEFAULT 'reserved' COMMENT '状态：reserved/canceled',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_member_course (member_id, course_id)
);
```

### 4.6 初始化数据

```sql
INSERT INTO sys_user(username, password, role, related_id, status)
VALUES ('admin', '123456', 'admin', NULL, 1);

INSERT INTO member(name, phone, gender, age, card_type, expire_time, status)
VALUES ('张三', '13800000000', '男', 22, '月卡', '2026-12-31', 1);

INSERT INTO sys_user(username, password, role, related_id, status)
VALUES ('zhangsan', '123456', 'member', 1, 1);
```

练手阶段可以先用明文密码，后续再改成 BCrypt 加密。

## 5. 后端接口设计

统一返回格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 5.1 登录接口

```text
POST /api/auth/login
```

请求：

```json
{
  "username": "admin",
  "password": "123456"
}
```

返回：

```json
{
  "token": "xxx",
  "userId": 1,
  "username": "admin",
  "role": "admin"
}
```

### 5.2 管理员接口

会员管理：

```text
GET    /api/admin/members
GET    /api/admin/members/{id}
POST   /api/admin/members
PUT    /api/admin/members/{id}
DELETE /api/admin/members/{id}
```

教练管理：

```text
GET    /api/admin/coaches
GET    /api/admin/coaches/{id}
POST   /api/admin/coaches
PUT    /api/admin/coaches/{id}
DELETE /api/admin/coaches/{id}
```

课程管理：

```text
GET    /api/admin/courses
GET    /api/admin/courses/{id}
POST   /api/admin/courses
PUT    /api/admin/courses/{id}
DELETE /api/admin/courses/{id}
```

预约管理：

```text
GET    /api/admin/appointments
PUT    /api/admin/appointments/{id}/cancel
```

首页统计：

```text
GET /api/admin/dashboard
```

返回示例：

```json
{
  "memberCount": 20,
  "coachCount": 5,
  "courseCount": 8,
  "appointmentCount": 31
}
```

### 5.3 会员接口

个人信息：

```text
GET /api/member/profile
PUT /api/member/profile
PUT /api/member/password
```

课程列表：

```text
GET /api/member/courses
```

我的预约：

```text
GET  /api/member/appointments
POST /api/member/appointments
PUT  /api/member/appointments/{id}/cancel
```

预约课程请求：

```json
{
  "courseId": 1
}
```

预约课程校验：

- 当前用户必须是会员
- 课程必须存在
- 课程状态必须是可预约
- 课程人数不能超过上限
- 同一会员不能重复预约同一课程

## 6. 权限规则

后端接口权限：

```text
/api/auth/**       不需要登录
/api/admin/**      只允许 admin
/api/member/**     只允许 member
```

JWT 中建议保存：

```json
{
  "userId": 1,
  "username": "admin",
  "role": "admin",
  "relatedId": null
}
```

前端权限：

- 登录后保存 token、role、username
- 请求接口时自动携带 token
- 路由跳转前判断是否登录
- admin 只能进入 `/admin/**`
- member 只能进入 `/member/**`

## 7. 后端包结构建议

```text
src/main/java/com/example/gym
  ├─ GymApplication.java
  ├─ common
  │   ├─ Result.java
  │   ├─ PageResult.java
  │   └─ BusinessException.java
  ├─ config
  │   ├─ WebConfig.java
  │   └─ JwtInterceptor.java
  ├─ controller
  │   ├─ AuthController.java
  │   ├─ admin
  │   │   ├─ AdminMemberController.java
  │   │   ├─ AdminCoachController.java
  │   │   ├─ AdminCourseController.java
  │   │   ├─ AdminAppointmentController.java
  │   │   └─ AdminDashboardController.java
  │   └─ member
  │       ├─ MemberProfileController.java
  │       ├─ MemberCourseController.java
  │       └─ MemberAppointmentController.java
  ├─ entity
  │   ├─ SysUser.java
  │   ├─ Member.java
  │   ├─ Coach.java
  │   ├─ Course.java
  │   └─ Appointment.java
  ├─ mapper
  ├─ service
  ├─ service/impl
  └─ util
      └─ JwtUtil.java
```

## 8. 前端目录建议

```text
src
  ├─ api
  │   ├─ auth.js
  │   ├─ member.js
  │   ├─ coach.js
  │   ├─ course.js
  │   └─ appointment.js
  ├─ router
  │   └─ index.js
  ├─ stores
  │   └─ user.js
  ├─ utils
  │   └─ request.js
  ├─ layouts
  │   ├─ AdminLayout.vue
  │   └─ MemberLayout.vue
  ├─ views
  │   ├─ Login.vue
  │   ├─ admin
  │   │   ├─ Dashboard.vue
  │   │   ├─ MemberList.vue
  │   │   ├─ CoachList.vue
  │   │   ├─ CourseList.vue
  │   │   └─ AppointmentList.vue
  │   └─ member
  │       ├─ Profile.vue
  │       ├─ CourseList.vue
  │       └─ MyAppointment.vue
  ├─ App.vue
  └─ main.js
```

## 9. 推荐开发顺序

### 第一步：搭建后端项目

完成内容：

- 创建 Spring Boot 项目
- 配置 MySQL
- 集成 MyBatis-Plus
- 创建数据库和表
- 写统一返回类 `Result`

验收标准：

- 项目能启动
- 能连接数据库
- 能写一个测试接口返回数据

### 第二步：完成登录

完成内容：

- 编写 `SysUser` 实体
- 编写登录接口
- 登录成功返回 token
- 编写 JWT 工具类
- 编写登录拦截器

验收标准：

- 正确账号密码可以登录
- 错误账号密码不能登录
- 未登录不能访问业务接口

### 第三步：完成管理员 CRUD

完成内容：

- 会员管理
- 教练管理
- 课程管理
- 分页查询
- 条件查询

验收标准：

- 管理员可以新增、修改、删除、查询数据
- 列表支持分页
- 课程可以关联教练

### 第四步：完成会员端

完成内容：

- 查看个人信息
- 修改个人信息
- 查看课程列表
- 预约课程
- 取消预约
- 查看我的预约

验收标准：

- 会员只能看到自己的信息和预约
- 会员不能访问管理员接口
- 课程满员后不能继续预约
- 重复预约会被拦截

### 第五步：搭建前端项目

完成内容：

- 创建 Vue3 + Vite 项目
- 安装 Element Plus、Axios、Pinia、Vue Router
- 封装 Axios 请求
- 配置登录页
- 配置路由守卫

验收标准：

- 可以登录
- 登录后根据角色跳转
- 请求自动携带 token
- token 失效后回到登录页

### 第六步：完成后台页面

完成内容：

- 管理员布局
- 首页统计
- 会员管理页面
- 教练管理页面
- 课程管理页面
- 预约管理页面

验收标准：

- 表格可以展示数据
- 表单可以新增和编辑
- 删除前有确认提示
- 页面操作后自动刷新列表

### 第七步：完成会员页面

完成内容：

- 会员布局
- 个人信息页面
- 课程列表页面
- 我的预约页面

验收标准：

- 可以修改个人信息
- 可以预约课程
- 可以取消自己的预约
- 不能取消别人的预约

### 第八步：完善和测试

完成内容：

- 接口异常处理
- 表单校验
- 空数据提示
- 加载状态
- 简单统计图

验收标准：

- 页面无明显报错
- 常见错误有提示
- 管理员和会员权限互不混乱

## 10. 页面功能清单

### 登录页

- 用户名输入框
- 密码输入框
- 登录按钮
- 登录失败提示

### 管理员首页

- 会员总数
- 教练总数
- 课程总数
- 预约总数
- 简单柱状图

### 会员管理

- 会员列表
- 姓名/手机号查询
- 新增会员
- 编辑会员
- 删除会员

### 教练管理

- 教练列表
- 姓名查询
- 新增教练
- 编辑教练
- 删除教练

### 课程管理

- 课程列表
- 课程名查询
- 新增课程
- 编辑课程
- 删除课程
- 选择授课教练

### 预约管理

- 预约列表
- 按会员名查询
- 按课程名查询
- 取消预约

### 会员个人中心

- 查看个人资料
- 修改手机号、年龄、性别
- 修改密码

### 会员课程列表

- 查看所有可预约课程
- 显示课程时间、教练、容量、已预约人数
- 点击预约

### 我的预约

- 查看已预约课程
- 取消预约

## 11. 核心业务逻辑

### 11.1 登录逻辑

```text
1. 前端提交用户名和密码
2. 后端查询 sys_user
3. 判断账号是否存在
4. 判断密码是否正确
5. 判断账号状态是否正常
6. 生成 JWT
7. 返回 token 和用户信息
```

### 11.2 预约课程逻辑

```text
1. 从 token 中获取当前会员 ID
2. 查询课程是否存在
3. 判断课程是否可预约
4. 判断课程人数是否已满
5. 判断当前会员是否已经预约过
6. 新增预约记录
7. 课程 booked_count 加 1
```

### 11.3 取消预约逻辑

```text
1. 查询预约记录是否存在
2. 判断预约是否属于当前会员
3. 判断预约状态是否为 reserved
4. 修改预约状态为 canceled
5. 课程 booked_count 减 1
```

管理员取消预约时，不需要判断是否属于当前会员。

## 12. 前端路由示例

```js
const routes = [
  { path: '/login', component: () => import('@/views/Login.vue') },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { role: 'admin' },
    children: [
      { path: 'dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'member', component: () => import('@/views/admin/MemberList.vue') },
      { path: 'coach', component: () => import('@/views/admin/CoachList.vue') },
      { path: 'course', component: () => import('@/views/admin/CourseList.vue') },
      { path: 'appointment', component: () => import('@/views/admin/AppointmentList.vue') }
    ]
  },
  {
    path: '/member',
    component: () => import('@/layouts/MemberLayout.vue'),
    meta: { role: 'member' },
    children: [
      { path: 'profile', component: () => import('@/views/member/Profile.vue') },
      { path: 'course', component: () => import('@/views/member/CourseList.vue') },
      { path: 'appointment', component: () => import('@/views/member/MyAppointment.vue') }
    ]
  }
]
```

## 13. Axios 封装要点

请求拦截：

```text
从 Pinia 或 localStorage 中读取 token
如果有 token，添加到请求头 Authorization
```

响应拦截：

```text
如果 code 是 401，清空登录信息，跳转登录页
如果 code 不是 200，弹出错误提示
```

## 14. 简化建议

如果时间不够，可以先不做这些：

- Spring Security
- Redis
- 文件上传
- 支付功能
- 会员卡复杂规则
- 课程签到
- 操作日志
- 复杂图表

先把下面闭环做出来：

```text
登录 -> 管理员建课程 -> 会员看课程 -> 会员预约 -> 管理员看预约
```

这个闭环完成后，项目就已经可以作为一个完整练手项目展示。

## 15. 最小完成版本

最小版本只需要完成：

- 管理员登录
- 会员登录
- 管理员管理会员
- 管理员管理教练
- 管理员管理课程
- 会员查看课程
- 会员预约课程
- 会员查看我的预约

完成这个版本后，再补：

- 管理员预约管理
- 首页统计
- 修改密码
- 表单校验
- ECharts 图表

