-- V6: Seed 25 employees with a full reporting hierarchy
-- Hierarchy: CEO → VP/CFO/HR Director → Directors → Managers → Staff
-- Employees must be inserted top-down so reporting_manager_id references already-exist

INSERT INTO ems.employee (
    id, employee_code, name, date_of_birth, salary, department_id,
    address, role_title, joining_date, yearly_bonus_percentage,
    reporting_manager_id, created_at, updated_at
) VALUES
-- Level 0: CEO (no reporting manager)
(1,  'EMP-00001', 'Alice Johnson',    '1975-03-15', 180000.00, 1,
 '100 Executive Blvd, New York, NY 10001',       'Chief Executive Officer',   '2015-01-01', 25.00, NULL, NOW(), NOW()),

-- Level 1: Direct reports to CEO
(2,  'EMP-00002', 'Bob Williams',     '1978-07-22', 140000.00, 1,
 '200 Tech Ave, New York, NY 10002',             'VP of Engineering',         '2015-03-15', 20.00, 1,    NOW(), NOW()),
(3,  'EMP-00003', 'Carol Davis',      '1977-11-08', 145000.00, 3,
 '300 Finance St, New York, NY 10003',           'Chief Financial Officer',   '2015-02-01', 20.00, 1,    NOW(), NOW()),
(4,  'EMP-00004', 'David Wilson',     '1980-05-30', 120000.00, 2,
 '400 HR Lane, New York, NY 10004',              'HR Director',               '2015-04-01', 18.00, 1,    NOW(), NOW()),

-- Level 2: Directors / Senior Leaders
(5,  'EMP-00005', 'Eve Martinez',     '1982-09-14', 125000.00, 1,
 '500 Dev Dr, Brooklyn, NY 11201',               'Engineering Director',      '2016-06-01', 18.00, 2,    NOW(), NOW()),
(11, 'EMP-00011', 'Katherine White',  '1979-02-14', 115000.00, 3,
 '111 CFO Row, Manhattan, NY 10005',             'Finance Director',          '2016-01-20', 18.00, 3,    NOW(), NOW()),
(16, 'EMP-00016', 'Patrick Martinez', '1983-04-22',  98000.00, 2,
 '666 HR Hub, Brooklyn, NY 11203',               'HR Manager',                '2017-11-01', 14.00, 4,    NOW(), NOW()),
(22, 'EMP-00022', 'Victor Walker',    '1986-12-19', 108000.00, 1,
 '1212 Deploy Dr, Manhattan, NY 10008',          'DevOps Engineer',           '2018-01-08', 15.00, 2,    NOW(), NOW()),
(24, 'EMP-00024', 'Xavier Young',     '1985-07-28', 112000.00, 1,
 '1414 Data Dr, Brooklyn, NY 11206',             'Data Engineer',             '2017-06-12', 16.00, 2,    NOW(), NOW()),

-- Level 3: Senior Engineers / Specialists / Accountants
(6,  'EMP-00006', 'Frank Anderson',   '1985-01-25', 105000.00, 1,
 '600 Code St, Brooklyn, NY 11202',              'Senior Software Engineer',  '2017-03-01', 15.00, 5,    NOW(), NOW()),
(7,  'EMP-00007', 'Grace Taylor',     '1986-04-18', 102000.00, 1,
 '700 Stack Ave, Queens, NY 11354',              'Senior Software Engineer',  '2017-05-15', 15.00, 5,    NOW(), NOW()),
(12, 'EMP-00012', 'Liam Harris',      '1984-10-11',  95000.00, 3,
 '222 Ledger Lane, Manhattan, NY 10006',         'Senior Accountant',         '2017-09-01', 14.00, 11,   NOW(), NOW()),
(17, 'EMP-00017', 'Quinn Robinson',   '1988-08-30',  68000.00, 2,
 '777 People Pl, Brooklyn, NY 11204',            'HR Specialist',             '2019-04-01', 10.00, 16,   NOW(), NOW()),
(18, 'EMP-00018', 'Rachel Clark',     '1989-11-17',  67000.00, 2,
 '888 Talent Terr, Queens, NY 11356',            'HR Specialist',             '2019-09-16', 10.00, 16,   NOW(), NOW()),
(19, 'EMP-00019', 'Samuel Rodriguez', '1992-02-14',  60000.00, 2,
 '999 Recruit Rd, Queens, NY 11357',             'Recruiter',                 '2020-05-11',  8.00, 16,   NOW(), NOW()),
(20, 'EMP-00020', 'Taylor Lewis',     '1993-09-09',  58000.00, 2,
 '1010 Hire Hwy, Bronx, NY 10453',               'Recruiter',                 '2021-01-04',  8.00, 16,   NOW(), NOW()),
(21, 'EMP-00021', 'Uma Lee',          '1987-06-05',  90000.00, 1,
 '1111 Test Trail, Manhattan, NY 10007',          'QA Engineer',               '2018-07-23', 12.00, 5,    NOW(), NOW()),
(23, 'EMP-00023', 'Wendy Hall',       '1988-03-11',  95000.00, 1,
 '1313 Pipeline Pkwy, Brooklyn, NY 11205',       'Senior DevOps Engineer',    '2018-09-03', 13.00, 22,   NOW(), NOW()),
(25, 'EMP-00025', 'Yvonne King',      '1990-10-02',  80000.00, 1,
 '1515 Analytics Ave, Queens, NY 11358',         'Data Analyst',              '2020-10-19', 11.00, 24,   NOW(), NOW()),

-- Level 4: Engineers / Juniors / Analysts
(8,  'EMP-00008', 'Henry Thomas',     '1990-08-07',  85000.00, 1,
 '800 Byte Blvd, Queens, NY 11355',              'Software Engineer',         '2019-01-07', 12.00, 6,    NOW(), NOW()),
(9,  'EMP-00009', 'Isabella Moore',   '1991-12-03',  82000.00, 1,
 '900 Java Rd, Bronx, NY 10451',                 'Software Engineer',         '2019-07-01', 12.00, 6,    NOW(), NOW()),
(10, 'EMP-00010', 'Jack Jackson',     '1993-06-20',  78000.00, 1,
 '1000 Python Path, Bronx, NY 10452',            'Junior Software Engineer',  '2020-08-17', 10.00, 7,    NOW(), NOW()),
(13, 'EMP-00013', 'Mia Martin',       '1989-03-29',  72000.00, 3,
 '333 Balance Blvd, Staten Island, NY 10301',    'Accountant',                '2019-11-01', 10.00, 12,   NOW(), NOW()),
(14, 'EMP-00014', 'Noah Thompson',    '1990-07-15',  70000.00, 3,
 '444 Audit Ave, Staten Island, NY 10302',       'Accountant',                '2020-02-03', 10.00, 12,   NOW(), NOW()),
(15, 'EMP-00015', 'Olivia Garcia',    '1994-01-08',  65000.00, 3,
 '555 Credit Ct, Staten Island, NY 10303',       'Finance Analyst',           '2021-06-14',  8.00, 12,   NOW(), NOW());

-- Advance the employee sequence past the max inserted ID
SELECT setval('ems.employee_id_seq', 25, true);
