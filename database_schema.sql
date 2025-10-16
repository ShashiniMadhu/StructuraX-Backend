-- Database schema for StructuraX quotation system

-- Create project table if it doesn't exist
CREATE TABLE IF NOT EXISTS project (
    project_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create quotation table if it doesn't exist
CREATE TABLE IF NOT EXISTS quotation (
    q_id INT PRIMARY KEY AUTO_INCREMENT,
    project_id VARCHAR(50) NOT NULL,
    qs_id VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    deadline DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'OPEN',
    description TEXT,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE
);

-- Create quotation_item table if needed
CREATE TABLE IF NOT EXISTS quotation_item (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    q_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    amount DECIMAL(15,2) NOT NULL,
    quantity INT NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (q_id) REFERENCES quotation(q_id) ON DELETE CASCADE
);

-- Create quotation_supplier table if needed
CREATE TABLE IF NOT EXISTS quotation_supplier (
    id INT PRIMARY KEY AUTO_INCREMENT,
    q_id INT NOT NULL,
    supplier_id INT NOT NULL,
    assigned_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (q_id) REFERENCES quotation(q_id) ON DELETE CASCADE
);

-- Create quotation_response table if needed
CREATE TABLE IF NOT EXISTS quotation_response (
    response_id INT PRIMARY KEY AUTO_INCREMENT,
    q_id INT NOT NULL,
    supplier_id INT NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    respond_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'SUBMITTED',
    remarks TEXT,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (q_id) REFERENCES quotation(q_id) ON DELETE CASCADE
);

-- Insert some sample data for testing
INSERT IGNORE INTO project (project_id, name, description, status) VALUES
('PROJ001', 'Sample Construction Project', 'A sample project for testing', 'ACTIVE'),
('PROJ002', 'Office Building Project', 'Modern office building construction', 'ACTIVE');

INSERT IGNORE INTO quotation (q_id, project_id, qs_id, date, deadline, status, description) VALUES
(1, 'PROJ001', 'QS001', '2025-09-15', '2025-09-30', 'OPEN', 'Construction materials quotation'),
(2, 'PROJ002', 'QS001', '2025-09-16', '2025-10-01', 'OPEN', 'Office furniture quotation');
