-- V5: Seed department data (without department heads — employees haven't been inserted yet)
INSERT INTO ems.department (id, name, creation_date, created_at, updated_at)
VALUES
    (1, 'Engineering',      '2020-01-15', NOW(), NOW()),
    (2, 'Human Resources',  '2020-01-15', NOW(), NOW()),
    (3, 'Finance',          '2020-02-01', NOW(), NOW());

-- Advance the sequence past the inserted IDs
SELECT setval('ems.department_id_seq', 3, true);
