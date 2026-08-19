-- V4: Add department_head_id to department table
-- This is done after employee table creation to avoid circular FK dependency
ALTER TABLE ems.department
    ADD COLUMN IF NOT EXISTS department_head_id BIGINT;

ALTER TABLE ems.department
    ADD CONSTRAINT fk_department_head
        FOREIGN KEY (department_head_id) REFERENCES ems.employee(id);

CREATE INDEX IF NOT EXISTS idx_department_head_id
    ON ems.department(department_head_id);

COMMENT ON COLUMN ems.department.department_head_id IS 'FK to employee who heads this department';
