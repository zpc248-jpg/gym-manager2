SET NAMES utf8mb4;
USE gym_manager;

INSERT INTO member(name, phone, gender, age, card_type, expire_time, status) VALUES
('张三', '13800000000', '男', 22, '月卡', '2026-12-31', 1),
('李娜', '13900000001', '女', 28, '季卡', '2026-10-15', 1),
('赵明', '13700000002', '男', 35, '年卡', '2027-03-20', 0);

INSERT INTO sys_user(username, password, role, related_id, status) VALUES
('admin', '123456', 'admin', NULL, 1),
('zhangsan', '123456', 'member', 1, 1);

INSERT INTO coach(name, phone, specialty, entry_date, status) VALUES
('李教练', '13600000001', '燃脂塑形', '2024-03-01', 1),
('王教练', '13600000002', '普拉提', '2023-09-12', 1),
('陈教练', '13600000003', '力量训练', '2025-01-08', 1);

INSERT INTO course(name, type, coach_id, start_time, end_time, capacity, booked_count, status) VALUES
('燃脂团课', '有氧', 1, '2026-07-07 09:00:00', '2026-07-07 10:00:00', 20, 1, 1),
('普拉提塑形', '塑形', 2, '2026-07-07 14:30:00', '2026-07-07 15:30:00', 16, 1, 1),
('力量训练入门', '力量', 3, '2026-07-07 18:30:00', '2026-07-07 19:30:00', 20, 0, 1),
('瑜伽拉伸', '恢复', 2, '2026-07-08 19:00:00', '2026-07-08 20:00:00', 18, 0, 0);

INSERT INTO appointment(member_id, course_id, status) VALUES
(1, 1, 'reserved'),
(2, 2, 'reserved');
