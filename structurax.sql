-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 20, 2025
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `structurax`
--

-- =============================================
-- CORE TABLES
-- =============================================

-- User Table
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `address` varchar(255) NOT NULL,
  `type` enum('Designer','Director','Senior_QS_Officer','QS_Officer','Project_Manager','Site_Supervisor','Legal_Officer','Financial_Officer','Client','Admin','Supplier') NOT NULL,
  `joined_date` date NOT NULL,
  `password` varchar(255) NOT NULL,
  `profile_image_url` varchar(255) DEFAULT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `token_expiry` datetime DEFAULT NULL
);

-- --------------------------------------------------------

-- Client Table
CREATE TABLE `client` (
  `client_id` varchar(10) PRIMARY KEY,
  `user_id` int(11) DEFAULT NULL,
  `type` enum('company','individual','government') NOT NULL,
  `is_have_plan` tinyint(1) DEFAULT 0,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
);

-- --------------------------------------------------------

CREATE TABLE `client_id_sequence` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY
);

-- --------------------------------------------------------

DELIMITER $$
CREATE TRIGGER `before_insert_client` BEFORE INSERT ON `client` FOR EACH ROW
BEGIN
  DECLARE next_id INT;
  INSERT INTO client_id_sequence VALUES (NULL);
  SET next_id = LAST_INSERT_ID();
  SET NEW.client_id = CONCAT('CLI_', LPAD(next_id, 3, '0'));
END$$
DELIMITER ;

-- --------------------------------------------------------

-- Employee Table
CREATE TABLE `employee` (
  `employee_id` varchar(10) PRIMARY KEY,
  `user_id` int(11) DEFAULT NULL,
  `availability` enum('Assigned','Available','Deactive') DEFAULT 'Available',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
);

-- --------------------------------------------------------

DELIMITER $$
CREATE TRIGGER `before_insert_employee` BEFORE INSERT ON `employee` FOR EACH ROW
BEGIN
    DECLARE next_id INT;
    SELECT IFNULL(MAX(CAST(SUBSTRING(employee_id, 5) AS UNSIGNED)), 0) + 1
    INTO next_id
    FROM employee;
    SET NEW.employee_id = CONCAT('EMP_', LPAD(next_id, 3, '0'));
END$$
DELIMITER ;

-- --------------------------------------------------------

-- Admin Table
CREATE TABLE `admin` (
  `admin_id` varchar(10) PRIMARY KEY,
  `user_id` int(11) DEFAULT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
);

-- --------------------------------------------------------

CREATE TABLE `admin_id_sequence` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY
);

-- --------------------------------------------------------

DELIMITER $$
CREATE TRIGGER `before_insert_admin` BEFORE INSERT ON `admin` FOR EACH ROW
BEGIN
  DECLARE next_id INT;
  INSERT INTO admin_id_sequence VALUES (NULL);
  SET next_id = LAST_INSERT_ID();
  SET NEW.admin_id = CONCAT('ADM_', LPAD(next_id, 3, '0'));
END$$
DELIMITER ;

-- --------------------------------------------------------

-- Engineering Company Table
CREATE TABLE `engineering_company` (
  `e_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `contact_number` varchar(20) NOT NULL
);

-- --------------------------------------------------------

-- Supplier Table
CREATE TABLE `supplier` (
  `supplier_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `status` enum('active','inactive','blocked') DEFAULT 'active',
  `type` enum('building_materials','safety and PPE','MEP materials','office and admin','stuctural components','tools and equipments','other'),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
);

-- =============================================
-- PROJECT MANAGEMENT TABLES
-- =============================================

-- Project Table
CREATE TABLE `project` (
  `project_id` varchar(10) PRIMARY KEY,
  `name` varchar(255) NOT NULL,
  `status` enum('ongoing','hold','completed','pending') NOT NULL,
  `budget` decimal(15,2) NOT NULL,
  `description` text,
  `location` varchar(255) NOT NULL,
  `estimated_value` decimal(15,2) NOT NULL,
  `start_date` date NOT NULL,
  `due_date` date NOT NULL,
  `client_id` varchar(10) DEFAULT NULL,
  `qs_id` varchar(10) DEFAULT NULL,
  `pm_id` varchar(10) DEFAULT NULL,
  `ss_id` varchar(10) DEFAULT NULL,
  `category` enum('residential','commercial','industrial','infrastructure','renovation','landscaping','civil_engineering','architectural') NOT NULL,
  FOREIGN KEY (`client_id`) REFERENCES `client`(`client_id`),
  FOREIGN KEY (`qs_id`) REFERENCES `employee`(`employee_id`),
  FOREIGN KEY (`pm_id`) REFERENCES `employee`(`employee_id`),
  FOREIGN KEY (`ss_id`) REFERENCES `employee`(`employee_id`)
);

-- --------------------------------------------------------

CREATE TABLE `project_id_sequence` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY
);

-- --------------------------------------------------------

DELIMITER $$
CREATE TRIGGER `before_insert_project` BEFORE INSERT ON `project` FOR EACH ROW
BEGIN
  DECLARE next_id INT;
  INSERT INTO project_id_sequence VALUES (NULL);
  SET next_id = LAST_INSERT_ID();
  SET NEW.project_id = CONCAT('PRJ_', LPAD(next_id, 3, '0'));
END$$
DELIMITER ;

-- --------------------------------------------------------

-- Project Site Supervisors (Multi-valued relationship)
CREATE TABLE `project_site_supervisors` (
  `project_id` varchar(10) NOT NULL,
  `site_supervisor_id` varchar(10) NOT NULL,
  PRIMARY KEY (`project_id`, `site_supervisor_id`),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`) ON DELETE CASCADE,
  FOREIGN KEY (`site_supervisor_id`) REFERENCES `employee`(`employee_id`)
);

-- --------------------------------------------------------

-- Project Materials
CREATE TABLE `project_materials` (
  `materials_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `tools` text,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`) ON DELETE CASCADE
);

-- --------------------------------------------------------

-- Employee Skills (Multi-valued relationship)
CREATE TABLE `employee_skill` (
  `employee_id` varchar(10) NOT NULL,
  `skill` varchar(255) NOT NULL,
  PRIMARY KEY (`employee_id`, `skill`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee`(`employee_id`) ON DELETE CASCADE
);

-- --------------------------------------------------------

-- Design Table
CREATE TABLE `design` (
  `design_id` varchar(10) PRIMARY KEY,
  `project_id` varchar(10) NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` enum('architectural','structural','electrical','plumbing','mechanical','landscape','interior','3d_modeling') NOT NULL,
  `due_date` date NOT NULL,
  `priority` enum('high','medium','low') NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `design_link` varchar(500) DEFAULT NULL,
  `description` text,
  `additional_note` text,
  `status` enum('completed','ongoing') NOT NULL,
  `client_id` varchar(10) NOT NULL,
  `employee_id` varchar(10) NOT NULL,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  FOREIGN KEY (`client_id`) REFERENCES `client`(`client_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee`(`employee_id`)
);

-- --------------------------------------------------------

CREATE TABLE `design_id_sequence` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY
);

-- --------------------------------------------------------

DELIMITER $$
CREATE TRIGGER `before_insert_design` BEFORE INSERT ON `design` FOR EACH ROW
BEGIN
  DECLARE next_id INT;
  INSERT INTO design_id_sequence VALUES (NULL);
  SET next_id = LAST_INSERT_ID();
  SET NEW.design_id = CONCAT('DSN_', LPAD(next_id, 3, '0'));
END$$
DELIMITER ;

-- =============================================
-- DOCUMENT MANAGEMENT TABLES
-- =============================================

-- Project Documents Table
CREATE TABLE `project_documents` (
  `document_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `document_url` varchar(500) NOT NULL,
  `type` enum('contract','permit','drawing','legal','specification','invoice','report','other') NOT NULL,
  `description` text,
  `upload_date` timestamp NULL DEFAULT current_timestamp(),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`)
);

-- --------------------------------------------------------

-- Project Images Table
CREATE TABLE `project_images` (
  `image_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `image_url` varchar(500) NOT NULL,
  `description` text,
  `upload_date` timestamp NULL DEFAULT current_timestamp(),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`)
);

-- --------------------------------------------------------

-- Legal Documents Table
CREATE TABLE `legal_document` (
  `legal_document_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `document_url` varchar(500) DEFAULT NULL,
  `date` date NOT NULL,
  `type` enum('contract','agreement','permit','license','compliance','other') NOT NULL,
  `description` text,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`)
);

-- --------------------------------------------------------

-- Legal Approval Table
CREATE TABLE `legal_approval` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `description` text,
  `status` enum('approved','pending','rejected') NOT NULL,
  `approval_date` date DEFAULT NULL
);

-- =============================================
-- FINANCIAL MANAGEMENT TABLES
-- =============================================

-- Payment Plan Table
CREATE TABLE `payment_plan` (
  `payment_plan_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `created_date` date NOT NULL,
  `total_amount` decimal(15,2) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `no_of_installments` int(11) NOT NULL,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`)
);

-- --------------------------------------------------------

-- Installment Table
CREATE TABLE `installment` (
  `installment_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `payment_plan_id` int(11) NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `due_date` date NOT NULL,
  `status` enum('paid','upcoming','overdue','cancelled') NOT NULL,
  `paid_date` date DEFAULT NULL,
  FOREIGN KEY (`payment_plan_id`) REFERENCES `payment_plan`(`payment_plan_id`)
);

-- --------------------------------------------------------

-- Payment Table
CREATE TABLE `payment` (
  `payment_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `invoice_id` int(11) NOT NULL,
  `due_date` date NOT NULL,
  `paid_date` date DEFAULT NULL,
  `status` enum('pending','paid','overdue','cancelled') NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  FOREIGN KEY (`invoice_id`) REFERENCES `project_documents`(`document_id`)
);

-- --------------------------------------------------------

-- Payment Confirmation Table
CREATE TABLE `payment_confirmation` (
  `confirmation_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `payment_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `document_id` int(11) DEFAULT NULL,
  `status` enum('confirmed','pending','rejected') NOT NULL,
  `confirmation_date` date DEFAULT NULL,
  FOREIGN KEY (`payment_id`) REFERENCES `payment`(`payment_id`),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  FOREIGN KEY (`document_id`) REFERENCES `project_documents`(`document_id`)
);

-- --------------------------------------------------------

-- Labor Attendance Table
CREATE TABLE `labor_attendance` (
  `attendance_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `date` date NOT NULL,
  `hiring_type` enum('third_party','direct_workers') NOT NULL,
  `labor_type` varchar(100) NOT NULL,
  `count` int(11) NOT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  UNIQUE KEY `unique_attendance` (`project_id`,`date`,`hiring_type`,`labor_type`)
);

-- --------------------------------------------------------

-- Labor Payment Table
CREATE TABLE `labor_payment` (
  `payment_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `status` enum('paid','pending','overdue') NOT NULL,
  `date` date NOT NULL,
  `comment` text,
  `receipt` mediumblob,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`)
);

-- --------------------------------------------------------

-- Labor Salary Table
CREATE TABLE `labor_salary` (
  `salary_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `attendance_id` int(11) NOT NULL,
  `labor_rate` decimal(8,2) NOT NULL,
  `cost` decimal(10,2) NOT NULL,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  FOREIGN KEY (`attendance_id`) REFERENCES `labor_attendance`(`attendance_id`)
);

-- --------------------------------------------------------

-- Supplier Payment Table
CREATE TABLE `supplier_payment` (
  `supplier_payment_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `invoice_id` int(11) NOT NULL,
  `due_date` date NOT NULL,
  `payed_date` date DEFAULT NULL,
  `amount` decimal(15,2) NOT NULL,
  `status` enum('paid','pending','overdue') NOT NULL,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  FOREIGN KEY (`invoice_id`) REFERENCES `project_documents`(`document_id`)
);

-- --------------------------------------------------------

-- Petty Cash Table
CREATE TABLE `petty_cash` (
  `petty_cash_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `date` date NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `employee_id` varchar(10) NOT NULL,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee`(`employee_id`)
);

-- --------------------------------------------------------

-- Petty Cash Record Table
CREATE TABLE `petty_cash_record` (
  `record_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `petty_cash_id` int(11) NOT NULL,
  `expense_amount` decimal(10,2) NOT NULL,
  `date` date NOT NULL,
  `description` text,
  FOREIGN KEY (`petty_cash_id`) REFERENCES `petty_cash`(`petty_cash_id`)
);

-- =============================================
-- RESOURCE MANAGEMENT TABLES
-- =============================================

-- Request Site Resources Table
CREATE TABLE `request_site_resources` (
  `request_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `pm_approval` tinyint(1) DEFAULT 0,
  `qs_approval` tinyint(1) DEFAULT 0,
  `request_type` enum('material','tool','labor') NOT NULL,
  `date` date NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `site_supervisor_id` varchar(10) NOT NULL,
  `qs_id` varchar(10) NOT NULL,
  `pm_id` varchar(10) NOT NULL,
  `is_received` enum('Pending','Approved','Rejected') NOT NULL,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  FOREIGN KEY (`site_supervisor_id`) REFERENCES `employee`(`employee_id`),
  FOREIGN KEY (`qs_id`) REFERENCES `employee`(`employee_id`),
  FOREIGN KEY (`pm_id`) REFERENCES `employee`(`employee_id`)
);

-- --------------------------------------------------------

-- Site Resources Table
CREATE TABLE `site_resources` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `request_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `quantity` int(11) NOT NULL,
  `priority` enum('high','medium','low') NOT NULL,
  FOREIGN KEY (`request_id`) REFERENCES `request_site_resources`(`request_id`)
);

-- --------------------------------------------------------

-- Catalog Table
CREATE TABLE `catalog` (
  `item_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text,
  `rate` decimal(10,2) NOT NULL,
  `availability` tinyint(1) DEFAULT 1,
  `category` varchar(100) DEFAULT NULL
);

-- --------------------------------------------------------

CREATE TABLE `supplier_inventory` (
  `product_id` int(11) AUTO_INCREMENT PRIMARY KEY,
  `product_name` varchar(255) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `stock` enum('in stock','out of stock') DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL
);

-- =============================================
-- PROCUREMENT TABLES
-- =============================================

-- Quotation Table
CREATE TABLE `quotation` (
  `q_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `description` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `deadline` date NOT NULL,
  `status` enum('pending','sent','received','closed') NOT NULL,
  `qs_id` varchar(10) NOT NULL,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`)
);

-- --------------------------------------------------------

-- Quotation Supplier Table
CREATE TABLE `quotation_supplier` (
  `q_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  PRIMARY KEY (`q_id`, `supplier_id`),
  FOREIGN KEY (`q_id`) REFERENCES `quotation`(`q_id`),
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier`(`supplier_id`)
);

-- --------------------------------------------------------

-- Quotation Item Table
CREATE TABLE `quotation_item` (
  `item_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `q_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text,
  `amount` decimal(10,2) NOT NULL,
  `quantity` int(11) NOT NULL,
  FOREIGN KEY (`q_id`) REFERENCES `quotation`(`q_id`)
);

-- --------------------------------------------------------

-- Quotation Response Table
CREATE TABLE `quotation_response` (
  `response_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `q_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `total_amount` decimal(15,2) NOT NULL,
  `delivery_date` date NOT NULL,
  `additional_note` text,
  `respond_date` date NOT NULL,
  `status` enum('pending','accepted','rejected') NOT NULL,
  FOREIGN KEY (`q_id`) REFERENCES `quotation`(`q_id`),
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier`(`supplier_id`)
);

-- --------------------------------------------------------

-- Purchase Order Table
CREATE TABLE `purchase_order` (
  `order_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `response_id` int(11) NOT NULL,
  `payment_status` enum('pending','partial','paid') NOT NULL,
  `estimated_delivery_date` date NOT NULL,
  `order_date` date DEFAULT curdate(),
  `order_status` tinyint(1) DEFAULT 0,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier`(`supplier_id`),
  FOREIGN KEY (`response_id`) REFERENCES `quotation_response`(`response_id`)
);

-- --------------------------------------------------------

-- Order Item Table
CREATE TABLE `order_item` (
  `order_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `description` text,
  `unit_price` decimal(10,2) NOT NULL,
  `quantity` int(11) NOT NULL,
  PRIMARY KEY (`order_id`, `item_id`),
  FOREIGN KEY (`order_id`) REFERENCES `purchase_order`(`order_id`),
  FOREIGN KEY (`item_id`) REFERENCES `catalog`(`item_id`)
);

-- --------------------------------------------------------

-- Order Invoice Table
CREATE TABLE `order_invoice` (
  `invoice_id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `description` text,
  `file_path` varchar(500) DEFAULT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`invoice_id`, `order_id`),
  FOREIGN KEY (`invoice_id`) REFERENCES `project_documents`(`document_id`),
  FOREIGN KEY (`order_id`) REFERENCES `purchase_order`(`order_id`),
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier`(`supplier_id`)
);

-- --------------------------------------------------------

-- Supplier History Table
CREATE TABLE `supplier_history` (
  `history_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `supplier_id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `supply_date` date NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier`(`supplier_id`),
  FOREIGN KEY (`order_id`) REFERENCES `purchase_order`(`order_id`)
);

-- =============================================
-- BOQ TABLES
-- =============================================

-- BOQ Table
CREATE TABLE `boq` (
  `boq_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `date` date NOT NULL,
  `qs_id` varchar(10) NOT NULL,
  `status` enum('draft','approved','final') DEFAULT 'draft',
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  FOREIGN KEY (`qs_id`) REFERENCES `employee`(`employee_id`)
);

-- --------------------------------------------------------

-- BOQ Item Table
CREATE TABLE `boq_item` (
  `item_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `boq_id` int(11) NOT NULL,
  `item_description` text NOT NULL,
  `rate` decimal(10,2) NOT NULL,
  `unit` varchar(50) NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  FOREIGN KEY (`boq_id`) REFERENCES `boq`(`boq_id`)
);

-- =============================================
-- TASK MANAGEMENT TABLES
-- =============================================

-- WBS Table
CREATE TABLE `wbs` (
  `task_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `status` enum('pending','in_progress','completed','on_hold') NOT NULL,
  `milestone` tinyint(1) DEFAULT 0,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  FOREIGN KEY (`parent_id`) REFERENCES `wbs`(`task_id`)
);

-- --------------------------------------------------------

-- To Do Table
CREATE TABLE `to_do` (
  `task_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `employee_id` varchar(10) NOT NULL,
  `status` enum('pending','in_progress','completed') NOT NULL,
  `description` text NOT NULL,
  `date` date NOT NULL,
  FOREIGN KEY (`employee_id`) REFERENCES `employee`(`employee_id`)
);

-- --------------------------------------------------------

-- Project To Do Table
CREATE TABLE `project_to_do` (
  `to_do_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `task` text NOT NULL,
  `status` enum('pending','in_progress','completed') NOT NULL,
  `assigned_date` date DEFAULT curdate(),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`)
);

-- --------------------------------------------------------

-- Daily Updates Table
CREATE TABLE `daily_updates` (
  `update_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `date` date NOT NULL,
  `note` text NOT NULL,
  `employee_id` varchar(10) NOT NULL,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee`(`employee_id`),
  UNIQUE KEY `unique_daily_update` (`project_id`,`date`,`employee_id`)
);

-- =============================================
-- VISIT MANAGEMENT TABLES
-- =============================================

-- Site Visit Log Table
CREATE TABLE `site_visit_log` (
  `visit_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `date` date NOT NULL,
  `description` text,
  `status` enum('pending','completed','cancelled') NOT NULL,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`)
);

-- --------------------------------------------------------

-- Visit Person Table
CREATE TABLE `visit_person` (
  `visit_id` int(11) NOT NULL,
  `employee_id` varchar(10) NOT NULL,
  PRIMARY KEY (`visit_id`, `employee_id`),
  FOREIGN KEY (`visit_id`) REFERENCES `site_visit_log`(`visit_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee`(`employee_id`)
);

-- --------------------------------------------------------

-- Visit Request Table
CREATE TABLE `visit_request` (
  `request_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `from_date` date NOT NULL,
  `to_date` date NOT NULL,
  `purpose` text NOT NULL,
  `pm_id` varchar(10) NOT NULL,
  `status` enum('pending','scheduled','completed','cancelled') NOT NULL,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  FOREIGN KEY (`pm_id`) REFERENCES `employee`(`employee_id`)
);

-- =============================================
-- COMMUNICATION TABLES
-- =============================================

-- Contact Support Table
CREATE TABLE `contact_support` (
  `request_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `employee_id` varchar(10) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `status` enum('open','resolved','closed') NOT NULL,
  `response` text,
  `created_date` date DEFAULT curdate(),
  FOREIGN KEY (`employee_id`) REFERENCES `employee`(`employee_id`)
);

-- --------------------------------------------------------

-- Notification Table
CREATE TABLE `notification` (
  `notification_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `user_id` varchar(10) NOT NULL,
  `user_type` enum('employee','client') NOT NULL,
  `title` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) DEFAULT 0,
  `is_seen` tinyint(1) DEFAULT 0,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `is_dismissed` tinyint(1) DEFAULT 0
);

-- --------------------------------------------------------

-- Chat Table
CREATE TABLE `chat` (
  `chat_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `chat_name` varchar(255) DEFAULT NULL,
  `sender_id` varchar(10) NOT NULL,
  `receiver_id` varchar(10) NOT NULL,
  `content` text NOT NULL,
  `message_timestamp` timestamp NULL DEFAULT current_timestamp(),
  `message_statement` enum('sent','delivered','read') DEFAULT 'sent',
  `attachment_url` varchar(500) DEFAULT NULL
);

-- =============================================
-- PUBLISHED PROJECTS & REVIEWS
-- =============================================

-- Published Projects Table
CREATE TABLE `published_projects` (
  `published_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `category` enum('residential','commercial','industrial','infrastructure','renovation','landscaping','civil_engineering','architectural') NOT NULL,
  `description` text,
  `publish_date` date DEFAULT curdate(),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`)
);

-- --------------------------------------------------------

-- Reviews Table
CREATE TABLE `reviews` (
  `review_id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `project_id` varchar(10) NOT NULL,
  `client_id` varchar(10) NOT NULL,
  `rate` int(11) DEFAULT NULL CHECK (`rate` >= 1 and `rate` <= 5),
  `review` text,
  `review_date` date DEFAULT curdate(),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`project_id`),
  FOREIGN KEY (`client_id`) REFERENCES `client`(`client_id`)
);

-- =============================================
-- AUTHENTICATION TRIGGER
-- =============================================

DELIMITER $$
CREATE TRIGGER `insert_users` AFTER INSERT ON `users` FOR EACH ROW
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

CREATE INDEX `idx_project_status` ON `project`(`status`);
CREATE INDEX `idx_project_client` ON `project`(`client_id`);
CREATE INDEX `idx_project_dates` ON `project`(`start_date`, `due_date`);

CREATE INDEX `idx_employee_type` ON `users`(`type`);
CREATE INDEX `idx_employee_availability` ON `employee`(`availability`);

CREATE INDEX `idx_payment_status` ON `payment`(`status`);
CREATE INDEX `idx_installment_due` ON `installment`(`due_date`);

CREATE INDEX `idx_document_project` ON `project_documents`(`project_id`);
CREATE INDEX `idx_document_type` ON `project_documents`(`type`);

CREATE INDEX `idx_notification_user` ON `notification`(`user_id`, `user_type`);
CREATE INDEX `idx_notification_read` ON `notification`(`is_read`);

CREATE INDEX `idx_chat_users` ON `chat`(`sender_id`, `receiver_id`);
CREATE INDEX `idx_chat_timestamp` ON `chat`(`message_timestamp`);

CREATE INDEX `idx_todo_employee` ON `to_do`(`employee_id`);
CREATE INDEX `idx_todo_status` ON `to_do`(`status`);
CREATE INDEX `idx_wbs_project` ON `wbs`(`project_id`);

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;