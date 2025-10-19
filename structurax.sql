
-- =============================================
-- CORE TABLES
-- =============================================

-- User Table
CREATE TABLE users (
  user_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL,
  phone_number VARCHAR(20) NOT NULL,
  address VARCHAR(255) NOT NULL,
  type enum('Designer','Director','Senior_QS_Officer','QS_Officer','Project_Manager','Site_Supervisor','Legal_Officer','Financial_Officer','Client','Admin','Supplier') NOT NULL,
  joined_date DATE NOT NULL,
  password VARCHAR(255) NOT NULL,
  profile_image_url VARCHAR(255) NULL DEFAULT NULL,
  reset_token VARCHAR(255) NULL DEFAULT NULL,
  token_expiry DATETIME NULL DEFAULT NULL
);

-- --------------------------------------------------------

-- Client Table
CREATE TABLE client (
    client_id  VARCHAR(10) PRIMARY KEY,
    user_id INT(11),
    type ENUM('company', 'individual', 'government') NOT NULL,
    is_have_plan TINYINT(1) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- --------------------------------------------------------

CREATE TABLE client_id_sequence (
  id INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY(id)
);

-- --------------------------------------------------------

DELIMITER $$

CREATE TRIGGER before_insert_client
BEFORE INSERT ON client
FOR EACH ROW
BEGIN
  DECLARE next_id INT;

  -- Insert dummy value to increment and retrieve the next ID
  INSERT INTO client_id_sequence VALUES (NULL);
  SET next_id = LAST_INSERT_ID();

  -- Format the ID like CLI_001
  SET NEW.client_id = CONCAT('CLI_', LPAD(next_id, 3, '0'));
END$$

DELIMITER ;

-- --------------------------------------------------------

-- Employee Table
CREATE TABLE employee (
    employee_id VARCHAR(10) PRIMARY KEY,
    user_id INT(11),
    availability enum('Assigned','Available','Deactive') DEFAULT 'Available',
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

--
-- Triggers `employee`
--
DELIMITER $$
CREATE TRIGGER `before_insert_employee` BEFORE INSERT ON `employee` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    
    -- Get the next ID number
    SELECT IFNULL(MAX(CAST(SUBSTRING(employee_id, 5) AS UNSIGNED)), 0) + 1 
    INTO next_id 
    FROM employee;
    
    -- Set the employee_id with EMP_ prefix and zero-padded number
    SET NEW.employee_id = CONCAT('EMP_', LPAD(next_id, 3, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

-- Admin Table
CREATE TABLE admin (
    admin_id varchar(10) PRIMARY KEY,
    user_id INT(11),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- --------------------------------------------------------

CREATE TABLE admin_id_sequence (
  id INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY(id)
);

-- --------------------------------------------------------

DELIMITER $$

CREATE TRIGGER before_insert_admin
BEFORE INSERT ON admin
FOR EACH ROW
BEGIN
  DECLARE next_id INT;

  -- Insert a dummy row into the sequence table to get the next auto-increment ID
  INSERT INTO admin_id_sequence VALUES (NULL);
  SET next_id = LAST_INSERT_ID();

  -- Format the ID with leading zeros, e.g., ADM_001
  SET NEW.admin_id = CONCAT('ADM_', LPAD(next_id, 3, '0'));
END$$

DELIMITER ;

-- --------------------------------------------------------


-- Engineering Company Table
CREATE TABLE engineering_company (
    e_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20) NOT NULL
);

-- Supplier Table
CREATE TABLE supplier (
    supplier_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    user_id INT(11),
    status ENUM('active', 'inactive', 'blocked') DEFAULT 'active',
    type ENUM('building_materials','safety and PPE','MEP materials','office and admin','stuctural components','tools and equipments','other'),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =============================================
-- PROJECT MANAGEMENT TABLES
-- =============================================

-- Project Table
CREATE TABLE project (
    project_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status ENUM('ongoing','hold','completed','pending') NOT NULL,
    budget DECIMAL(15,2) NOT NULL,
    description TEXT,
    location VARCHAR(255) NOT NULL,
    estimated_value DECIMAL(15,2) NOT NULL,
    start_date DATE NOT NULL,
    due_date DATE NOT NULL,
    client_id VARCHAR(10),
    qs_id VARCHAR(10),
    pm_id VARCHAR(10),
    ss_id VARCHAR(10),
    category ENUM('residential', 'commercial', 'industrial', 'infrastructure', 'renovation', 'landscaping', 'civil_engineering', 'architectural') NOT NULL,
    FOREIGN KEY (client_id) REFERENCES client(client_id),
    FOREIGN KEY (qs_id) REFERENCES employee(employee_id),
    FOREIGN KEY (pm_id) REFERENCES employee(employee_id),
    FOREIGN KEY (ss_id) REFERENCES employee(employee_id)
);

-- --------------------------------------------------------

CREATE TABLE project_id_sequence (
  id INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY(id)
);

-- --------------------------------------------------------

DELIMITER $$

CREATE TRIGGER before_insert_project
BEFORE INSERT ON project
FOR EACH ROW
BEGIN
  DECLARE next_id INT;

  -- Generate the next auto-increment value
  INSERT INTO project_id_sequence VALUES (NULL);
  SET next_id = LAST_INSERT_ID();

  -- Format the project_id: PRJ_001
  SET NEW.project_id = CONCAT('PRJ_', LPAD(next_id, 3, '0'));
END$$

DELIMITER ;


-- --------------------------------------------------------

-- Project Site Supervisors (Multi-valued relationship)
CREATE TABLE project_site_supervisors (
    project_id VARCHAR(10) NOT NULL,
    site_supervisor_id VARCHAR(10) NOT NULL,
    PRIMARY KEY (project_id, site_supervisor_id),
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
    FOREIGN KEY (site_supervisor_id) REFERENCES employee(employee_id)
);

-- Project Materials
CREATE TABLE project_materials (
    materials_id INT NOT NULL,
    project_id VARCHAR(10) NOT NULL,
    tools TEXT,
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE
);


-- Employee Skills (Multi-valued relationship)
CREATE TABLE employee_skill (
    employee_id VARCHAR(10) NOT NULL,
    skill VARCHAR(255) NOT NULL,
    PRIMARY KEY (employee_id, skill),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE
);

-- Design Table
CREATE TABLE design (
    design_id VARCHAR(10) PRIMARY KEY,
    project_id VARCHAR(10) NOT NULL,
    name VARCHAR(255) NOT NULL,
    type ENUM('architectural','structural','electrical','plumbing','mechanical','landscape','interior','3d_modeling') NOT NULL,
    due_date DATE NOT NULL,
    priority ENUM('high', 'medium', 'low') NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    design_link VARCHAR(500),
    description TEXT,
    additional_note TEXT,
    status ENUM('completed', 'ongoing') NOT NULL,
    client_id VARCHAR(10) NOT NULL,
    employee_id VARCHAR(10) NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (client_id) REFERENCES client(client_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- --------------------------------------------------------

CREATE TABLE design_id_sequence (
  id INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY(id)
);

-- --------------------------------------------------------

DELIMITER $$

CREATE TRIGGER before_insert_design
BEFORE INSERT ON design
FOR EACH ROW
BEGIN
  DECLARE next_id INT;

  -- Generate the next sequence ID
  INSERT INTO design_id_sequence VALUES (NULL);
  SET next_id = LAST_INSERT_ID();

  -- Format: DSN_001, DSN_002, ...
  SET NEW.design_id = CONCAT('DSN_', LPAD(next_id, 3, '0'));
END$$

DELIMITER ;


-- --------------------------------------------------------

-- =============================================
-- DOCUMENT MANAGEMENT TABLES
-- =============================================

-- Project Documents Table
CREATE TABLE project_documents (
    document_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    document_url VARCHAR(500) NOT NULL,
    type ENUM('contract', 'permit', 'drawing', 'legal','specification', 'invoice', 'report', 'other') NOT NULL,
    description TEXT,
    upload_date TIMESTAMP DEFAULT current_timestamp(),
    FOREIGN KEY (project_id) REFERENCES project(project_id)
);

-- Project Images Table
CREATE TABLE project_images (
    image_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    description TEXT,
    upload_date TIMESTAMP DEFAULT current_timestamp(),
    FOREIGN KEY (project_id) REFERENCES project(project_id)
);

-- Legal Documents Table
CREATE TABLE legal_document (
    legal_document_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    document_url VARCHAR(500),
    date DATE NOT NULL,
    type ENUM('contract', 'agreement', 'permit', 'license', 'compliance', 'other') NOT NULL,
    description TEXT,
    FOREIGN KEY (project_id) REFERENCES project(project_id)
);

-- Legal Approval Table
CREATE TABLE legal_approval (
    id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    description TEXT,
    status ENUM('approved', 'pending', 'rejected') NOT NULL,
    approval_date DATE
);

-- =============================================
-- FINANCIAL MANAGEMENT TABLES
-- =============================================

-- Payment Plan Table
CREATE TABLE payment_plan (
    payment_plan_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    created_date DATE NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    no_of_installments INT(11) NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id)
);

-- Installment Table
CREATE TABLE installment (
    installment_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    payment_plan_id INT(11) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    due_date DATE NOT NULL,
    status ENUM('paid', 'upcoming', 'overdue', 'cancelled') NOT NULL,
    paid_date DATE,
    FOREIGN KEY (payment_plan_id) REFERENCES payment_plan(payment_plan_id)
);

-- Payment Table
CREATE TABLE payment (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT NOT NULL,
    due_date DATE NOT NULL,
    paid_date DATE,
    status ENUM('pending', 'paid', 'overdue', 'cancelled') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES project_documents(document_id)
);

-- Payment Confirmation Table
CREATE TABLE payment_confirmation (
    confirmation_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    payment_id INT(11) NOT NULL,
    project_id VARCHAR(10) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    document_id INT(11),
    status ENUM('confirmed', 'pending', 'rejected') NOT NULL,
    confirmation_date DATE,
    FOREIGN KEY (payment_id) REFERENCES payment(payment_id),
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (document_id) REFERENCES project_documents(document_id)
);

-- Labor Attendance Table
CREATE TABLE labor_attendance (
    attendance_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    date DATE NOT NULL,
    hiring_type ENUM('third_party', 'direct_workers') NOT NULL,
    labor_type VARCHAR(100) NOT NULL,
    count INT(11) NOT NULL,
    company_name VARCHAR(255),
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    UNIQUE KEY unique_attendance (project_id, date, hiring_type, labor_type)
);

-- Labor Payment Table
CREATE TABLE labor_payment (
    payment_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status ENUM('paid', 'pending', 'overdue') NOT NULL,
    date DATE NOT NULL,
    comment TEXT,
    receipt mediumblob,
    FOREIGN KEY (project_id) REFERENCES project(project_id)
);

-- Labor Salary Table
CREATE TABLE labor_salary (
    salary_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    attendance_id INT NOT NULL,
    labor_rate DECIMAL(8,2) NOT NULL,
    cost DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (attendance_id) REFERENCES labor_attendance(attendance_id)
);

-- Supplier Payment Table
CREATE TABLE supplier_payment (
    supplier_payment_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    invoice_id INT(11) NOT NULL,
    due_date DATE NOT NULL,
    payed_date DATE,
    amount DECIMAL(15,2) NOT NULL,
    status ENUM('paid', 'pending', 'overdue') NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (invoice_id) REFERENCES project_documents(document_id)
);

-- Petty Cash Table
CREATE TABLE petty_cash (
    petty_cash_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    employee_id VARCHAR(10) NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- Petty Cash Record Table
CREATE TABLE petty_cash_record (
    record_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    petty_cash_id INT(11) NOT NULL,
    expense_amount DECIMAL(10,2) NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    FOREIGN KEY (petty_cash_id) REFERENCES petty_cash(petty_cash_id)
);

-- =============================================
-- RESOURCE MANAGEMENT TABLES
-- =============================================

-- Request Site Resources Table
CREATE TABLE request_site_resources (
    request_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    pm_approval tinyint(1) DEFAULT 0,
    qs_approval tinyint(1) DEFAULT 0,
    request_type ENUM('material', 'tool', 'labor') NOT NULL,
    date DATE NOT NULL,
    project_id VARCHAR(10) NOT NULL,
    site_supervisor_id VARCHAR(10) NOT NULL,
    qs_id VARCHAR(10) NOT NULL,
    pm_id VARCHAR(10) NOT NULL,
    is_received ENUM('Pending','Approved','Rejected') NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (site_supervisor_id) REFERENCES employee(employee_id),
    FOREIGN KEY (qs_id) REFERENCES employee(employee_id),
    FOREIGN KEY (qs_id) REFERENCES employee(employee_id)
);

-- Site Resources Table
CREATE TABLE site_resources (
    id INT(11) PRIMARY KEY AUTO_INCREMENT,
    request_id INT(11) NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity INT(11) NOT NULL,
    priority ENUM('high', 'medium', 'low') NOT NULL,
    FOREIGN KEY (request_id) REFERENCES request_site_resources(request_id)
);

-- Catalog Table
CREATE TABLE catalog (
    item_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    rate DECIMAL(10,2) NOT NULL,
    availability TINYINT(1) DEFAULT 1,
    category VARCHAR(100)
);

CREATE TABLE supplier_inventory (
    product_id INT(11) AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    unit_price DECIMAL(10, 2),
    stock ENUM('in stock', 'out of stock'),
    status VARCHAR(50)
);

-- =============================================
-- PROCUREMENT TABLES
-- =============================================

-- Quotation Table
CREATE TABLE quotation (
    q_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    date DATE NOT NULL,
    deadline DATE NOT NULL,
    status ENUM('pending', 'sent', 'received', 'closed') NOT NULL,
    qs_id VARCHAR(10) NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id)
);

-- Quotation Supplier Table
CREATE TABLE quotation_supplier (
    q_id INT(11) NOT NULL,
    supplier_id INT(11) NOT NULL,
    PRIMARY KEY (q_id, supplier_id),
    FOREIGN KEY (q_id) REFERENCES quotation(q_id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id)
);

-- Quotation Item Table
CREATE TABLE quotation_item (
    item_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    q_id INT(11) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    amount DECIMAL(10,2) NOT NULL,
    quantity INT(11) NOT NULL,
    FOREIGN KEY (q_id) REFERENCES quotation(q_id)
);

-- Quotation Response Table
CREATE TABLE quotation_response (
    response_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    q_id INT(11) NOT NULL,
    supplier_id INT(11) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    delivery_date DATE NOT NULL,
    additional_note TEXT,
    respond_date DATE NOT NULL,
    status ENUM('pending', 'accepted', 'rejected') NOT NULL,
    FOREIGN KEY (q_id) REFERENCES quotation(q_id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id)
);

-- Purchase Order Table
CREATE TABLE purchase_order (
    order_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    supplier_id INT(11) NOT NULL,
    response_id INT(11) NOT NULL,
    payment_status ENUM('pending', 'partial', 'paid') NOT NULL,
    estimated_delivery_date DATE NOT NULL,
    order_date DATE DEFAULT curdate(),
    order_status TINYINT(1) DEFAULT 0,
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id),
    FOREIGN KEY (response_id) REFERENCES quotation_response(response_id)
);

-- Order Item Table
CREATE TABLE order_item (
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    description TEXT,
    unit_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (order_id, item_id),
    FOREIGN KEY (order_id) REFERENCES purchase_order(order_id),
    FOREIGN KEY (item_id) REFERENCES catalog(item_id)
);

-- Order Invoice Table
CREATE TABLE order_invoice (
    invoice_id INT NOT NULL,
    order_id INT NOT NULL,
    supplier_id INT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    description TEXT,
    file_path VARCHAR(500),
    date DATE NOT NULL,
    PRIMARY KEY (invoice_id, order_id),
    FOREIGN KEY (invoice_id) REFERENCES project_documents(document_id),
    FOREIGN KEY (order_id) REFERENCES purchase_order(order_id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id)
);

-- Supplier History Table
CREATE TABLE supplier_history (
    history_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    supplier_id INT(11) NOT NULL,
    order_id INT(11) NOT NULL,
    supply_date DATE NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id),
    FOREIGN KEY (order_id) REFERENCES purchase_order(order_id)
);

-- =============================================
-- BOQ TABLES
-- =============================================

-- BOQ Table
CREATE TABLE boq (
    boq_id INT PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    date DATE NOT NULL,
    qs_id VARCHAR(10) NOT NULL,
    status ENUM('draft', 'approved', 'final') DEFAULT 'draft',
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (qs_id) REFERENCES employee(employee_id)
);

-- BOQ Item Table
CREATE TABLE boq_item (
    item_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    boq_id INT(11) NOT NULL,
    item_description TEXT NOT NULL,
    rate DECIMAL(10,2) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (boq_id) REFERENCES boq(boq_id)
);

-- =============================================
-- TASK MANAGEMENT TABLES
-- =============================================

-- WBS Table
CREATE TABLE wbs (
    task_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    parent_id INT(11),
    name VARCHAR(255) NOT NULL,
    status ENUM('pending', 'in_progress', 'completed', 'on_hold') NOT NULL,
    milestone tinyint(1) DEFAULT 0,
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (parent_id) REFERENCES wbs(task_id)
);

-- To Do Table
CREATE TABLE to_do (
    task_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(10) NOT NULL,
    status ENUM('pending', 'in_progress', 'completed') NOT NULL,
    description TEXT NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- Project To Do Table
CREATE TABLE project_to_do (
    to_do_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    task TEXT NOT NULL,
    status ENUM('pending', 'in_progress', 'completed') NOT NULL,
    assigned_date DATE DEFAULT curdate(),
    FOREIGN KEY (project_id) REFERENCES project(project_id)
);

-- Daily Updates Table
CREATE TABLE daily_updates (
    update_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    date DATE NOT NULL,
    note TEXT NOT NULL,
    employee_id VARCHAR(10) NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    UNIQUE KEY unique_daily_update (project_id, date, employee_id)
);

-- =============================================
-- VISIT MANAGEMENT TABLES
-- =============================================

-- Site Visit Log Table
CREATE TABLE site_visit_log (
    visit_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    status ENUM('pending', 'completed', 'cancelled') NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id)
);

-- Visit Person Table
CREATE TABLE visit_person (
    visit_id INT(11) NOT NULL,
    employee_id VARCHAR(10) NOT NULL,
    PRIMARY KEY (visit_id, employee_id),
    FOREIGN KEY (visit_id) REFERENCES site_visit_log(visit_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- Visit Request Table
CREATE TABLE visit_request (
    request_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    purpose TEXT NOT NULL,
    pm_id VARCHAR(10) NOT NULL,
    status ENUM('pending', 'scheduled', 'completed', 'cancelled') NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (pm_id) REFERENCES employee(employee_id)
);

-- =============================================
-- COMMUNICATION TABLES
-- =============================================

-- Contact Support Table
CREATE TABLE contact_support (
    request_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(10) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status ENUM('open', 'resolved', 'closed') NOT NULL,
    response TEXT,
    created_date DATE DEFAULT curdate(),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- Notification Table
CREATE TABLE notification (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(10) NOT NULL,
    user_type ENUM('employee', 'client') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    is_seen BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT current_timestamp(),
    is_dismissed BOOLEAN DEFAULT FALSE
);

-- Chat Table
CREATE TABLE chat (
    chat_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    chat_name VARCHAR(255),
    sender_id VARCHAR(10) NOT NULL,
    receiver_id VARCHAR(10) NOT NULL,
    content TEXT NOT NULL,
    message_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    message_statement ENUM('sent', 'delivered', 'read') DEFAULT 'sent',
    attachment_url VARCHAR(500)
);

-- =============================================
-- PUBLISHED PROJECTS & REVIEWS
-- =============================================

-- Published Projects Table
CREATE TABLE published_projects (
    published_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    image_url VARCHAR(500),
    title VARCHAR(255) NOT NULL,
    category ENUM('residential', 'commercial', 'industrial', 'infrastructure', 'renovation', 'landscaping', 'civil_engineering', 'architectural') NOT NULL,
    description TEXT,
    publish_date DATE DEFAULT curdate(),
    FOREIGN KEY (project_id) REFERENCES project(project_id)
);

-- Reviews Table
CREATE TABLE reviews (
    review_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(10) NOT NULL,
    client_id VARCHAR(10) NOT NULL,
    rate INT(11) CHECK (rate >= 1 AND rate <= 5),
    review TEXT,
    review_date DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (client_id) REFERENCES client(client_id)
);

-- authentication trigger
DELIMITER $$

CREATE TRIGGER insert_users
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    IF NEW.type = 'Client' THEN
        INSERT INTO client(user_id) VALUES (NEW.user_id);
    ELSEIF NEW.type = 'Admin' THEN
        INSERT INTO admin(user_id) VALUES (NEW.user_id);
    ELSEIF NEW.type = 'Supplier' THEN
        INSERT INTO supplier(user_id) VALUES (NEW.user_id);
    ELSE
        INSERT INTO employee(user_id) VALUES (NEW.user_id);
    END IF;
END$$

DELIMITER ;

-- =============================================
-- INDEXES FOR PERFORMANCE
-- =============================================

-- Project related indexes
CREATE INDEX idx_project_status ON project(status);
CREATE INDEX idx_project_client ON project(client_id);
CREATE INDEX idx_project_dates ON project(start_date, due_date);

-- Employee related indexes
CREATE INDEX idx_employee_type ON users(type);
CREATE INDEX idx_employee_availability ON employee(availability);

-- Financial indexes
CREATE INDEX idx_payment_status ON payment(status);
CREATE INDEX idx_installment_due ON installment(due_date);

-- Document indexes
CREATE INDEX idx_document_project ON project_documents(project_id);
CREATE INDEX idx_document_type ON project_documents(type);

-- Notification indexes
CREATE INDEX idx_notification_user ON notification(user_id, user_type);
CREATE INDEX idx_notification_read ON notification(is_read);

-- Chat indexes
CREATE INDEX idx_chat_users ON chat(sender_id, receiver_id);
CREATE INDEX idx_chat_timestamp ON chat(message_timestamp);

-- Task indexes
CREATE INDEX idx_todo_employee ON to_do(employee_id);
CREATE INDEX idx_todo_status ON to_do(status);
CREATE INDEX idx_wbs_project ON wbs(project_id);
