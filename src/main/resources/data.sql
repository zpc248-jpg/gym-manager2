SET NAMES utf8mb4;
USE gym_manager;

INSERT INTO member(name, phone, gender, age, card_type, expire_time, status) VALUES
('Zhang San', '13800000000', 'male', 22, 'Monthly', '2026-12-31', 1),
('Li Na', '13900000001', 'female', 28, 'Quarterly', '2026-10-15', 1),
('Zhao Ming', '13700000002', 'male', 35, 'Yearly', '2027-03-20', 0);

INSERT INTO sys_user(username, password, role, related_id, status) VALUES
('admin', '123456', 'admin', NULL, 1),
('zhangsan', '123456', 'member', 1, 1);

INSERT INTO coach(name, phone, specialty, entry_date, status) VALUES
('Coach Li', '13600000001', 'Fat Burning', '2024-03-01', 1),
('Coach Wang', '13600000002', 'Pilates', '2023-09-12', 1),
('Coach Chen', '13600000003', 'Strength Training', '2025-01-08', 1);

INSERT INTO course(name, type, coach_id, start_time, end_time, capacity, booked_count, status) VALUES
('Fat Burning Group Class', 'Cardio', 1, '2026-07-07 09:00:00', '2026-07-07 10:00:00', 20, 1, 1),
('Pilates Shaping', 'Shaping', 2, '2026-07-07 14:30:00', '2026-07-07 15:30:00', 16, 1, 1),
('Strength Training Intro', 'Strength', 3, '2026-07-07 18:30:00', '2026-07-07 19:30:00', 20, 0, 1),
('Yoga Stretch', 'Recovery', 2, '2026-07-08 19:00:00', '2026-07-08 20:00:00', 18, 0, 0);

INSERT INTO appointment(member_id, course_id, status) VALUES
(1, 1, 'reserved'),
(2, 2, 'reserved');
