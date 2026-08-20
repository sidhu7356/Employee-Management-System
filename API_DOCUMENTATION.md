# Employee Management System (EMS) - API Documentation

This document contains a complete reference of all REST API endpoints available in the Employee Management System, including request methods, URLs, path variables, query parameters, request body schemas, sample requests (cURL and JSON), and sample responses.

---

## Table of Contents
1. [Base URL & General Info](#base-url--general-info)
2. [Employee APIs](#employee-apis)
   - [1. Create Employee (POST /api/v1/employees)](#1-create-employee)
   - [2. Update Employee (PUT /api/v1/employees/{id})](#2-update-employee)
   - [3. Get Employee by ID (GET /api/v1/employees/{id})](#3-get-employee-by-id)
   - [4. Get All Employees (GET /api/v1/employees)](#4-get-all-employees)
   - [5. Update Employee Department (PATCH /api/v1/employees/{id}/department)](#5-update-employee-department)
   - [6. Get Reporting Chain (GET /api/v1/employees/{id}/reporting-chain)](#6-get-reporting-chain)
3. [Department APIs](#department-apis)
   - [1. Create Department (POST /api/v1/departments)](#1-create-department)
   - [2. Update Department (PUT /api/v1/departments/{id})](#2-update-department)
   - [3. Delete Department (DELETE /api/v1/departments/{id})](#3-delete-department)
   - [4. Get Department by ID (GET /api/v1/departments/{id})](#4-get-department-by-id)
   - [5. Get All Departments (GET /api/v1/departments)](#5-get-all-departments)
   - [6. Get Department Analytics (GET /api/v1/departments/{id}/analytics)](#6-get-department-analytics)
4. [Standard Error Responses](#standard-error-responses)

---

## Base URL & General Info

- **Base URL:** `http://localhost:8080`
- **Content-Type Header:** `application/json` (for POST, PUT, PATCH)
- **Swagger / OpenAPI Documentation:** `http://localhost:8080/swagger-ui.html`

---

## Employee APIs

### 1. Create Employee

Creates a new employee record. The `employeeCode` is auto-generated (e.g. `EMP-00001`).

- **Method:** `POST`
- **URL:** `/api/v1/employees`
- **Query Parameters:** None
- **Request Body Parameters:**
  | Parameter | Type | Required | Description / Constraints |
  | :--- | :--- | :--- | :--- |
  | `name` | String | Yes | Full name of the employee (max 255 chars) |
  | `dateOfBirth` | String (ISO Date) | Yes | Format: `YYYY-MM-DD` (must be in past) |
  | `salary` | Number (Decimal) | Yes | Greater than `0.00` |
  | `departmentId` | Long | Yes | Valid existing department ID |
  | `address` | String | No | Address details (max 1000 chars) |
  | `roleTitle` | String | Yes | Job title (max 255 chars) |
  | `joiningDate` | String (ISO Date) | Yes | Format: `YYYY-MM-DD` (past or present) |
  | `yearlyBonusPercentage` | Number (Decimal) | Yes | Value between `0.00` and `100.00` |
  | `reportingManagerId` | Long | No | Optional ID of manager. Null for top-level role (e.g. CEO) |

#### Sample Request (cURL)
```bash
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "dateOfBirth": "1992-05-15",
    "salary": 95000.00,
    "departmentId": 1,
    "address": "123 Tech Park, Suite 400, New York, NY",
    "roleTitle": "Senior Software Engineer",
    "joiningDate": "2023-01-10",
    "yearlyBonusPercentage": 12.50,
    "reportingManagerId": 1
  }'
```

#### Sample Request Body (JSON)
```json
{
  "name": "Jane Smith",
  "dateOfBirth": "1992-05-15",
  "salary": 95000.00,
  "departmentId": 1,
  "address": "123 Tech Park, Suite 400, New York, NY",
  "roleTitle": "Senior Software Engineer",
  "joiningDate": "2023-01-10",
  "yearlyBonusPercentage": 12.50,
  "reportingManagerId": 1
}
```

#### Sample Response (`201 Created`)
```json
{
  "id": 10,
  "employeeCode": "EMP-00010",
  "name": "Jane Smith",
  "dateOfBirth": "1992-05-15",
  "salary": 95000.00,
  "department": {
    "id": 1,
    "name": "Engineering"
  },
  "address": "123 Tech Park, Suite 400, New York, NY",
  "roleTitle": "Senior Software Engineer",
  "joiningDate": "2023-01-10",
  "yearlyBonusPercentage": 12.50,
  "reportingManager": {
    "id": 1,
    "name": "John Doe",
    "roleTitle": "VP of Engineering"
  },
  "createdAt": "2026-08-20T21:30:00",
  "updatedAt": "2026-08-20T21:30:00"
}
```

---

### 2. Update Employee

Updates all fields of an existing employee record.

- **Method:** `PUT`
- **URL:** `/api/v1/employees/{id}`
- **Path Parameters:**
  - `id` (Long, required): ID of the employee to update.
- **Query Parameters:** None
- **Request Body Parameters:** Same schema as Create Employee.

#### Sample Request (cURL)
```bash
curl -X PUT http://localhost:8080/api/v1/employees/10 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "dateOfBirth": "1992-05-15",
    "salary": 105000.00,
    "departmentId": 1,
    "address": "456 Innovation Way, New York, NY",
    "roleTitle": "Lead Software Engineer",
    "joiningDate": "2023-01-10",
    "yearlyBonusPercentage": 15.00,
    "reportingManagerId": 1
  }'
```

#### Sample Response (`200 OK`)
```json
{
  "id": 10,
  "employeeCode": "EMP-00010",
  "name": "Jane Smith",
  "dateOfBirth": "1992-05-15",
  "salary": 105000.00,
  "department": {
    "id": 1,
    "name": "Engineering"
  },
  "address": "456 Innovation Way, New York, NY",
  "roleTitle": "Lead Software Engineer",
  "joiningDate": "2023-01-10",
  "yearlyBonusPercentage": 15.00,
  "reportingManager": {
    "id": 1,
    "name": "John Doe",
    "roleTitle": "VP of Engineering"
  },
  "createdAt": "2026-08-20T21:30:00",
  "updatedAt": "2026-08-20T21:32:00"
}
```

---

### 3. Get Employee by ID

Returns full details for a single employee.

- **Method:** `GET`
- **URL:** `/api/v1/employees/{id}`
- **Path Parameters:**
  - `id` (Long, required): Employee ID.
- **Query Parameters:** None

#### Sample Request (cURL)
```bash
curl -X GET http://localhost:8080/api/v1/employees/10
```

#### Sample Response (`200 OK`)
```json
{
  "id": 10,
  "employeeCode": "EMP-00010",
  "name": "Jane Smith",
  "dateOfBirth": "1992-05-15",
  "salary": 105000.00,
  "department": {
    "id": 1,
    "name": "Engineering"
  },
  "address": "456 Innovation Way, New York, NY",
  "roleTitle": "Lead Software Engineer",
  "joiningDate": "2023-01-10",
  "yearlyBonusPercentage": 15.00,
  "reportingManager": {
    "id": 1,
    "name": "John Doe",
    "roleTitle": "VP of Engineering"
  },
  "createdAt": "2026-08-20T21:30:00",
  "updatedAt": "2026-08-20T21:32:00"
}
```

---

### 4. Get All Employees

Fetches a paginated list of employees. Supports lookup mode for lightweight dropdowns/selectors.

- **Method:** `GET`
- **URL:** `/api/v1/employees`
- **Query Parameters:**
  - `lookup` (boolean, optional, default: `false`): If `true`, returns lightweight `id` + `name` pairs only.
  - `page` (int, optional, default: `0`): Page index (0-indexed).
  - `size` (int, optional, default: `20`): Page size.

#### Mode A: Full Details (`GET /api/v1/employees?page=0&size=20`)

##### Sample Request (cURL)
```bash
curl -X GET "http://localhost:8080/api/v1/employees?page=0&size=2"
```

##### Sample Response (`200 OK`)
```json
{
  "content": [
    {
      "id": 1,
      "employeeCode": "EMP-00001",
      "name": "John Doe",
      "dateOfBirth": "1980-01-01",
      "salary": 200000.00,
      "department": {
        "id": 1,
        "name": "Engineering"
      },
      "address": "100 Corporate Blvd",
      "roleTitle": "VP of Engineering",
      "joiningDate": "2020-01-01",
      "yearlyBonusPercentage": 20.00,
      "reportingManager": null,
      "createdAt": "2026-08-20T20:00:00",
      "updatedAt": "2026-08-20T20:00:00"
    },
    {
      "id": 10,
      "employeeCode": "EMP-00010",
      "name": "Jane Smith",
      "dateOfBirth": "1992-05-15",
      "salary": 105000.00,
      "department": {
        "id": 1,
        "name": "Engineering"
      },
      "address": "456 Innovation Way, New York, NY",
      "roleTitle": "Lead Software Engineer",
      "joiningDate": "2023-01-10",
      "yearlyBonusPercentage": 15.00,
      "reportingManager": {
        "id": 1,
        "name": "John Doe",
        "roleTitle": "VP of Engineering"
      },
      "createdAt": "2026-08-20T21:30:00",
      "updatedAt": "2026-08-20T21:32:00"
    }
  ],
  "page": 0,
  "size": 2,
  "totalElements": 2,
  "totalPages": 1
}
```

#### Mode B: Lookup Mode (`GET /api/v1/employees?lookup=true`)

##### Sample Request (cURL)
```bash
curl -X GET "http://localhost:8080/api/v1/employees?lookup=true&page=0&size=2"
```

##### Sample Response (`200 OK`)
```json
{
  "content": [
    {
      "id": 1,
      "name": "John Doe"
    },
    {
      "id": 10,
      "name": "Jane Smith"
    }
  ],
  "page": 0,
  "size": 2,
  "totalElements": 2,
  "totalPages": 1
}
```

---

### 5. Update Employee Department

Partially updates an employee's department assignment.

- **Method:** `PATCH`
- **URL:** `/api/v1/employees/{id}/department`
- **Path Parameters:**
  - `id` (Long, required): Employee ID.
- **Query Parameters:** None
- **Request Body Parameters:**
  | Parameter | Type | Required | Description |
  | :--- | :--- | :--- | :--- |
  | `departmentId` | Long | Yes | New department ID |

#### Sample Request (cURL)
```bash
curl -X PATCH http://localhost:8080/api/v1/employees/10/department \
  -H "Content-Type: application/json" \
  -d '{
    "departmentId": 2
  }'
```

#### Sample Request Body (JSON)
```json
{
  "departmentId": 2
}
```

#### Sample Response (`200 OK`)
```json
{
  "id": 10,
  "employeeCode": "EMP-00010",
  "name": "Jane Smith",
  "dateOfBirth": "1992-05-15",
  "salary": 105000.00,
  "department": {
    "id": 2,
    "name": "Product Management"
  },
  "address": "456 Innovation Way, New York, NY",
  "roleTitle": "Lead Software Engineer",
  "joiningDate": "2023-01-10",
  "yearlyBonusPercentage": 15.00,
  "reportingManager": {
    "id": 1,
    "name": "John Doe",
    "roleTitle": "VP of Engineering"
  },
  "createdAt": "2026-08-20T21:30:00",
  "updatedAt": "2026-08-20T21:33:00"
}
```

---

### 6. Get Reporting Chain

Traverses up the management hierarchy starting from the given employee up to the top-level executive (e.g. Employee → Manager → Director → CEO).

- **Method:** `GET`
- **URL:** `/api/v1/employees/{id}/reporting-chain`
- **Path Parameters:**
  - `id` (Long, required): Target Employee ID.
- **Query Parameters:** None

#### Sample Request (cURL)
```bash
curl -X GET http://localhost:8080/api/v1/employees/10/reporting-chain
```

#### Sample Response (`200 OK`)
```json
[
  {
    "id": 10,
    "name": "Jane Smith",
    "roleTitle": "Lead Software Engineer"
  },
  {
    "id": 1,
    "name": "John Doe",
    "roleTitle": "VP of Engineering"
  }
]
```

---

## Department APIs

### 1. Create Department

Creates a new department record.

- **Method:** `POST`
- **URL:** `/api/v1/departments`
- **Query Parameters:** None
- **Request Body Parameters:**
  | Parameter | Type | Required | Description / Constraints |
  | :--- | :--- | :--- | :--- |
  | `name` | String | Yes | Department name (max 255 chars) |
  | `creationDate` | String (ISO Date) | Yes | Format: `YYYY-MM-DD` (past or present) |
  | `departmentHeadId` | Long | No | Optional employee ID of department head |

#### Sample Request (cURL)
```bash
curl -X POST http://localhost:8080/api/v1/departments \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Product Management",
    "creationDate": "2024-01-01",
    "departmentHeadId": 1
  }'
```

#### Sample Request Body (JSON)
```json
{
  "name": "Product Management",
  "creationDate": "2024-01-01",
  "departmentHeadId": 1
}
```

#### Sample Response (`201 Created`)
```json
{
  "id": 2,
  "name": "Product Management",
  "creationDate": "2024-01-01",
  "departmentHead": {
    "id": 1,
    "name": "John Doe",
    "roleTitle": "VP of Engineering"
  },
  "createdAt": "2026-08-20T21:30:00",
  "updatedAt": "2026-08-20T21:30:00"
}
```

---

### 2. Update Department

Updates an existing department's details.

- **Method:** `PUT`
- **URL:** `/api/v1/departments/{id}`
- **Path Parameters:**
  - `id` (Long, required): Department ID.
- **Query Parameters:** None
- **Request Body Parameters:** Same schema as Create Department.

#### Sample Request (cURL)
```bash
curl -X PUT http://localhost:8080/api/v1/departments/2 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Product & Growth",
    "creationDate": "2024-01-01",
    "departmentHeadId": 10
  }'
```

#### Sample Response (`200 OK`)
```json
{
  "id": 2,
  "name": "Product & Growth",
  "creationDate": "2024-01-01",
  "departmentHead": {
    "id": 10,
    "name": "Jane Smith",
    "roleTitle": "Lead Software Engineer"
  },
  "createdAt": "2026-08-20T21:30:00",
  "updatedAt": "2026-08-20T21:34:00"
}
```

---

### 3. Delete Department

Deletes a department. 

> **Note:** Fails with `422 Unprocessable Entity` if any employees are currently assigned to the department.

- **Method:** `DELETE`
- **URL:** `/api/v1/departments/{id}`
- **Path Parameters:**
  - `id` (Long, required): Department ID.
- **Query Parameters:** None

#### Sample Request (cURL)
```bash
curl -X DELETE http://localhost:8080/api/v1/departments/2
```

#### Sample Response (`204 No Content`)
*(Empty response body)*

---

### 4. Get Department by ID

Retrieves department details. Supports expanding embedded employee list.

- **Method:** `GET`
- **URL:** `/api/v1/departments/{id}`
- **Path Parameters:**
  - `id` (Long, required): Department ID.
- **Query Parameters:**
  - `expand` (string, optional): Pass `employee` to include paginated assigned employees.
  - `page` (int, optional, default: `0`): Page index (used only when `expand=employee`).
  - `size` (int, optional, default: `20`): Page size (used only when `expand=employee`).

#### Mode A: Standard Department Info (`GET /api/v1/departments/1`)

##### Sample Request (cURL)
```bash
curl -X GET http://localhost:8080/api/v1/departments/1
```

##### Sample Response (`200 OK`)
```json
{
  "id": 1,
  "name": "Engineering",
  "creationDate": "2020-01-01",
  "departmentHead": {
    "id": 1,
    "name": "John Doe",
    "roleTitle": "VP of Engineering"
  },
  "createdAt": "2026-08-20T20:00:00",
  "updatedAt": "2026-08-20T20:00:00"
}
```

#### Mode B: Expanded Employee Details (`GET /api/v1/departments/1?expand=employee`)

##### Sample Request (cURL)
```bash
curl -X GET "http://localhost:8080/api/v1/departments/1?expand=employee&page=0&size=10"
```

##### Sample Response (`200 OK`)
```json
{
  "department": {
    "id": 1,
    "name": "Engineering",
    "creationDate": "2020-01-01",
    "departmentHead": {
      "id": 1,
      "name": "John Doe",
      "roleTitle": "VP of Engineering"
    },
    "createdAt": "2026-08-20T20:00:00",
    "updatedAt": "2026-08-20T20:00:00"
  },
  "employees": [
    {
      "id": 1,
      "employeeCode": "EMP-00001",
      "name": "John Doe",
      "dateOfBirth": "1980-01-01",
      "salary": 200000.00,
      "department": {
        "id": 1,
        "name": "Engineering"
      },
      "address": "100 Corporate Blvd",
      "roleTitle": "VP of Engineering",
      "joiningDate": "2020-01-01",
      "yearlyBonusPercentage": 20.00,
      "reportingManager": null,
      "createdAt": "2026-08-20T20:00:00",
      "updatedAt": "2026-08-20T20:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

---

### 5. Get All Departments

Returns a paginated list of all departments.

- **Method:** `GET`
- **URL:** `/api/v1/departments`
- **Query Parameters:**
  - `page` (int, optional, default: `0`): Page index (0-indexed).
  - `size` (int, optional, default: `20`): Page size.

#### Sample Request (cURL)
```bash
curl -X GET "http://localhost:8080/api/v1/departments?page=0&size=20"
```

#### Sample Response (`200 OK`)
```json
{
  "content": [
    {
      "id": 1,
      "name": "Engineering",
      "creationDate": "2020-01-01",
      "departmentHead": {
        "id": 1,
        "name": "John Doe",
        "roleTitle": "VP of Engineering"
      },
      "createdAt": "2026-08-20T20:00:00",
      "updatedAt": "2026-08-20T20:00:00"
    },
    {
      "id": 2,
      "name": "Product & Growth",
      "creationDate": "2024-01-01",
      "departmentHead": {
        "id": 10,
        "name": "Jane Smith",
        "roleTitle": "Lead Software Engineer"
      },
      "createdAt": "2026-08-20T21:30:00",
      "updatedAt": "2026-08-20T21:34:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2,
  "totalPages": 1
}
```

---

### 6. Get Department Analytics

Retrieves department statistics including total employee count, average salary, and total salary expenditure.

- **Method:** `GET`
- **URL:** `/api/v1/departments/{id}/analytics`
- **Path Parameters:**
  - `id` (Long, required): Department ID.
- **Query Parameters:** None

#### Sample Request (cURL)
```bash
curl -X GET http://localhost:8080/api/v1/departments/1/analytics
```

#### Sample Response (`200 OK`)
```json
{
  "departmentId": 1,
  "employeeCount": 15,
  "averageSalary": 112500.00,
  "totalSalary": 1687500.00
}
```

---

## Standard Error Responses

Whenever an API call results in an error, a standard error response structure is returned:

```json
{
  "timestamp": "2026-08-20T21:35:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 999",
  "path": "/api/v1/employees/999"
}
```

### Common Status Codes

- `400 Bad Request`: Validation failure on input fields or malformed JSON payload.
- `404 Not Found`: Requested Employee or Department resource does not exist.
- `422 Unprocessable Entity`: Business logic violation (e.g. attempting to delete a department with active employees, or invalid reporting hierarchy).
- `500 Internal Server Error`: Unexpected server-side failure.
