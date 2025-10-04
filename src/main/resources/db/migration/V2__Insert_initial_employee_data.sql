-- 插入示例员工数据
INSERT INTO employees (employee_number, name, furigana, birthday) VALUES
('EMP001', '田中太郎', 'タナカタロウ', '1990-05-15'),
('EMP002', '佐藤花子', 'サトウハナコ', '1985-12-03'),
('EMP003', '鈴木一郎', 'スズキイチロウ', '1992-08-20'),
('EMP004', '高橋美咲', 'タカハシミサキ', '1988-03-10'),
('EMP005', '山田次郎', 'ヤマダジロウ', '1995-11-25')
ON CONFLICT (employee_number) DO NOTHING;
