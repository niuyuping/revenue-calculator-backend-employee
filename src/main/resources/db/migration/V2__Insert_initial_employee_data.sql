-- Insert sample employee data
INSERT INTO employeeInfo (employee_number, name, furigana, birthday) VALUES
('EMP001', 'Tanaka Taro', 'tanaka taro', '1990-05-15'),
('EMP002', 'Sato Hanako', 'sato hanako', '1985-12-03'),
('EMP003', 'Suzuki Ichiro', 'suzuki ichiro', '1992-08-20'),
('EMP004', 'Takahashi Misaki', 'takahashi misaki', '1988-03-10'),
('EMP005', 'Yamada Jiro', 'yamada jiro', '1995-11-25')
ON CONFLICT (employee_number) DO NOTHING;
