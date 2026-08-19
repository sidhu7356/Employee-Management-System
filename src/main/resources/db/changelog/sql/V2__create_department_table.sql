-- V2: Create department table
-- Note: department_head_id FK is added later in V4 after employee table is created
CREATE TABLE IF NOT EXISTS ems.department (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    creation_date DATE         NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_department_name UNIQUE (name)
);

COMMENT ON TABLE ems.department IS 'Stores department information';
COMMENT ON COLUMN ems.department.id IS 'Auto-generated primary key';
COMMENT ON COLUMN ems.department.name IS 'Unique department name';
COMMENT ON COLUMN ems.department.creation_date IS 'Date the department was officially created';
