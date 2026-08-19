-- V7: Assign department heads now that employees have been inserted
-- Engineering → Bob Williams (VP of Engineering, id=2)
UPDATE ems.department
SET department_head_id = 2, updated_at = NOW()
WHERE id = 1;

-- Human Resources → David Wilson (HR Director, id=4)
UPDATE ems.department
SET department_head_id = 4, updated_at = NOW()
WHERE id = 2;

-- Finance → Carol Davis (CFO, id=3)
UPDATE ems.department
SET department_head_id = 3, updated_at = NOW()
WHERE id = 3;
