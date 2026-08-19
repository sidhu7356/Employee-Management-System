-- V3: Create employee table
CREATE TABLE IF NOT EXISTS ems.employee (
    id                       BIGSERIAL PRIMARY KEY,
    employee_code            VARCHAR(50)  NOT NULL,
    name                     VARCHAR(255) NOT NULL,
    date_of_birth            DATE,
    salary                   NUMERIC(15, 2) NOT NULL,
    department_id            BIGINT NOT NULL,
    address                  TEXT,
    role_title               VARCHAR(255),
    joining_date             DATE NOT NULL,
    yearly_bonus_percentage  NUMERIC(5, 2),
    reporting_manager_id     BIGINT,
    created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_employee_code UNIQUE (employee_code),
    CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id) REFERENCES ems.department(id),
    CONSTRAINT fk_employee_reporting_manager
        FOREIGN KEY (reporting_manager_id) REFERENCES ems.employee(id),
    CONSTRAINT chk_employee_salary
        CHECK (salary > 0),
    CONSTRAINT chk_bonus_percentage
        CHECK (yearly_bonus_percentage IS NULL OR (yearly_bonus_percentage >= 0 AND yearly_bonus_percentage <= 100))
);

-- Indexes for frequent query paths
CREATE INDEX IF NOT EXISTS idx_employee_department_id
    ON ems.employee(department_id);

CREATE INDEX IF NOT EXISTS idx_employee_reporting_manager_id
    ON ems.employee(reporting_manager_id);

CREATE INDEX IF NOT EXISTS idx_employee_name
    ON ems.employee(name);

CREATE INDEX IF NOT EXISTS idx_employee_role_title
    ON ems.employee(role_title);

COMMENT ON TABLE ems.employee IS 'Stores employee information';
COMMENT ON COLUMN ems.employee.employee_code IS 'Unique, auto-generated employee identifier (e.g. EMP-XXXXXXXX)';
COMMENT ON COLUMN ems.employee.reporting_manager_id IS 'Self-reference: the direct manager of this employee. NULL for top-level employees.';
COMMENT ON COLUMN ems.employee.yearly_bonus_percentage IS 'Annual bonus as a percentage of salary (0-100)';
