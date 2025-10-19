-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 18, 2025 at 07:48 AM
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

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `admin_id` varchar(10) NOT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `admin`
--
DELIMITER $$
CREATE TRIGGER `before_insert_admin` BEFORE INSERT ON `admin` FOR EACH ROW BEGIN
  DECLARE next_id INT;

  -- Insert a dummy row into the sequence table to get the next auto-increment ID
  INSERT INTO admin_id_sequence VALUES (NULL);
  SET next_id = LAST_INSERT_ID();

  -- Format the ID with leading zeros, e.g., ADM_001
  SET NEW.admin_id = CONCAT('ADM_', LPAD(next_id, 3, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `admin_id_sequence`
--

CREATE TABLE `admin_id_sequence` (
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `boq`
--

CREATE TABLE `boq` (
  `boq_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `date` date NOT NULL,
  `qs_id` varchar(10) NOT NULL,
  `status` enum('draft','approved','final') DEFAULT 'draft'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `boq_item`
--

CREATE TABLE `boq_item` (
  `item_id` int(11) NOT NULL,
  `boq_id` int(11) NOT NULL,
  `item_description` text NOT NULL,
  `rate` decimal(10,2) NOT NULL,
  `unit` varchar(50) NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `amount` decimal(15,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `catalog`
--

CREATE TABLE `catalog` (
  `item_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `rate` decimal(10,2) NOT NULL,
  `availability` tinyint(1) DEFAULT 1,
  `category` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `chat`
--

CREATE TABLE `chat` (
  `chat_id` int(11) NOT NULL,
  `chat_name` varchar(255) DEFAULT NULL,
  `sender_id` varchar(10) NOT NULL,
  `receiver_id` varchar(10) NOT NULL,
  `content` text NOT NULL,
  `message_timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  `message_statement` enum('sent','delivered','read') DEFAULT 'sent',
  `attachment_url` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `client`
--

CREATE TABLE `client` (
  `client_id` varchar(10) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `type` enum('company','individual','government') NOT NULL,
  `is_have_plan` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `client`
--

INSERT INTO `client` (`client_id`, `user_id`, `type`, `is_have_plan`) VALUES
('CLI_001', 3, 'company', 0),
('CLI_002', 3, 'individual', 1);

--
-- Triggers `client`
--
DELIMITER $$
CREATE TRIGGER `before_insert_client` BEFORE INSERT ON `client` FOR EACH ROW BEGIN
  DECLARE next_id INT;

  -- Insert dummy value to increment and retrieve the next ID
  INSERT INTO client_id_sequence VALUES (NULL);
  SET next_id = LAST_INSERT_ID();

  -- Format the ID like CLI_001
  SET NEW.client_id = CONCAT('CLI_', LPAD(next_id, 3, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `client_id_sequence`
--

CREATE TABLE `client_id_sequence` (
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `client_id_sequence`
--

INSERT INTO `client_id_sequence` (`id`) VALUES
(1),
(2);

-- --------------------------------------------------------

--
-- Table structure for table `contact_support`
--

CREATE TABLE `contact_support` (
  `request_id` int(11) NOT NULL,
  `employee_id` varchar(10) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `status` enum('open','resolved','closed') NOT NULL,
  `response` text DEFAULT NULL,
  `created_date` date DEFAULT curdate()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `daily_updates`
--

CREATE TABLE `daily_updates` (
  `update_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `date` date NOT NULL,
  `note` text NOT NULL,
  `employee_id` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `design`
--

CREATE TABLE `design` (
  `design_id` varchar(10) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` enum('architectural','structural','electrical','plumbing','mechanical','landscape','interior','3d_modeling') NOT NULL,
  `due_date` date NOT NULL,
  `priority` enum('high','medium','low') NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `design_link` varchar(500) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `additional_note` text DEFAULT NULL,
  `status` enum('completed','ongoing') NOT NULL,
  `client_id` varchar(10) NOT NULL,
  `employee_id` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `design`
--
DELIMITER $$
CREATE TRIGGER `before_insert_design` BEFORE INSERT ON `design` FOR EACH ROW BEGIN
  DECLARE next_id INT;

  -- Generate the next sequence ID
  INSERT INTO design_id_sequence VALUES (NULL);
  SET next_id = LAST_INSERT_ID();

  -- Format: DSN_001, DSN_002, ...
  SET NEW.design_id = CONCAT('DSN_', LPAD(next_id, 3, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `design_id_sequence`
--

CREATE TABLE `design_id_sequence` (
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `employee`
--

CREATE TABLE `employee` (
  `employee_id` varchar(10) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `availability` enum('Assigned','Available','Deactive') DEFAULT 'Available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `employee`
--

INSERT INTO `employee` (`employee_id`, `user_id`, `availability`) VALUES
('EMP_001', 1, 'Available'),
('EMP_002', 2, 'Available');

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

--
-- Table structure for table `employee_skill`
--

CREATE TABLE `employee_skill` (
  `employee_id` varchar(10) NOT NULL,
  `skill` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `engineering_company`
--

CREATE TABLE `engineering_company` (
  `e_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `contact_number` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `installment`
--

CREATE TABLE `installment` (
  `installment_id` int(11) NOT NULL,
  `payment_plan_id` int(11) NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `due_date` date NOT NULL,
  `status` enum('paid','upcoming','overdue','cancelled') NOT NULL,
  `paid_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `labor_attendance`
--

CREATE TABLE `labor_attendance` (
  `attendance_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `date` date NOT NULL,
  `hiring_type` enum('third_party','direct_workers') NOT NULL,
  `labor_type` varchar(100) NOT NULL,
  `count` int(11) NOT NULL,
  `company_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `labor_payment`
--

CREATE TABLE `labor_payment` (
  `payment_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `status` enum('paid','pending','overdue') NOT NULL,
  `date` date NOT NULL,
  `comment` text DEFAULT NULL,
  `receipt` mediumblob DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `labor_salary`
--

CREATE TABLE `labor_salary` (
  `salary_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `attendance_id` int(11) NOT NULL,
  `labor_rate` decimal(8,2) NOT NULL,
  `cost` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `legal_approval`
--

CREATE TABLE `legal_approval` (
  `id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `description` text DEFAULT NULL,
  `status` enum('approved','pending','rejected') NOT NULL,
  `approval_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `legal_document`
--

CREATE TABLE `legal_document` (
  `legal_document_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `document_url` varchar(500) DEFAULT NULL,
  `date` date NOT NULL,
  `type` enum('contract','agreement','permit','license','compliance','other') NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notification`
--

CREATE TABLE `notification` (
  `notification_id` int(11) NOT NULL,
  `user_id` varchar(10) NOT NULL,
  `user_type` enum('employee','client') NOT NULL,
  `title` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) DEFAULT 0,
  `is_seen` tinyint(1) DEFAULT 0,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `is_dismissed` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `order_invoice`
--

CREATE TABLE `order_invoice` (
  `invoice_id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `description` text DEFAULT NULL,
  `file_path` varchar(500) DEFAULT NULL,
  `date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `order_item`
--

CREATE TABLE `order_item` (
  `order_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `description` text DEFAULT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `payment_id` int(11) NOT NULL,
  `invoice_id` int(11) NOT NULL,
  `due_date` date NOT NULL,
  `paid_date` date DEFAULT NULL,
  `status` enum('pending','paid','overdue','cancelled') NOT NULL,
  `amount` decimal(15,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payment_confirmation`
--

CREATE TABLE `payment_confirmation` (
  `confirmation_id` int(11) NOT NULL,
  `payment_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `document_id` int(11) DEFAULT NULL,
  `status` enum('confirmed','pending','rejected') NOT NULL,
  `confirmation_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payment_plan`
--

CREATE TABLE `payment_plan` (
  `payment_plan_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `created_date` date NOT NULL,
  `total_amount` decimal(15,2) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `no_of_installments` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `petty_cash`
--

CREATE TABLE `petty_cash` (
  `petty_cash_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `date` date NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `employee_id` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `petty_cash_record`
--

CREATE TABLE `petty_cash_record` (
  `record_id` int(11) NOT NULL,
  `petty_cash_id` int(11) NOT NULL,
  `expense_amount` decimal(10,2) NOT NULL,
  `date` date NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `project`
--

CREATE TABLE `project` (
  `project_id` varchar(10) NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` enum('ongoing','hold','completed','pending') NOT NULL,
  `budget` decimal(15,2) NOT NULL,
  `description` text DEFAULT NULL,
  `location` varchar(255) NOT NULL,
  `estimated_value` decimal(15,2) NOT NULL,
  `start_date` date NOT NULL,
  `due_date` date NOT NULL,
  `client_id` varchar(10) DEFAULT NULL,
  `qs_id` varchar(10) DEFAULT NULL,
  `pm_id` varchar(10) DEFAULT NULL,
  `ss_id` varchar(10) DEFAULT NULL,
  `category` enum('residential','commercial','industrial','infrastructure','renovation','landscaping','civil_engineering','architectural') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `project`
--

INSERT INTO `project` (`project_id`, `name`, `status`, `budget`, `description`, `location`, `estimated_value`, `start_date`, `due_date`, `client_id`, `qs_id`, `pm_id`, `ss_id`, `category`) VALUES
('PRJ_003', 'Sunrise Apartment Complex', 'ongoing', 85000000.00, 'A 10-story luxury residential apartment project located in the city center.', 'Colombo 07, Sri Lanka', 90000000.00, '2025-01-15', '2026-06-30', 'CLI_001', 'EMP_001', NULL, NULL, 'residential');

--
-- Triggers `project`
--
DELIMITER $$
CREATE TRIGGER `before_insert_project` BEFORE INSERT ON `project` FOR EACH ROW BEGIN
  DECLARE next_id INT;

  -- Generate the next auto-increment value
  INSERT INTO project_id_sequence VALUES (NULL);
  SET next_id = LAST_INSERT_ID();

  -- Format the project_id: PRJ_001
  SET NEW.project_id = CONCAT('PRJ_', LPAD(next_id, 3, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `project_documents`
--

CREATE TABLE `project_documents` (
  `document_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `document_url` varchar(500) NOT NULL,
  `type` enum('contract','permit','drawing','legal','specification','invoice','report','other') NOT NULL,
  `description` text DEFAULT NULL,
  `upload_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `project_id_sequence`
--

CREATE TABLE `project_id_sequence` (
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `project_id_sequence`
--

INSERT INTO `project_id_sequence` (`id`) VALUES
(3);

-- --------------------------------------------------------

--
-- Table structure for table `project_images`
--

CREATE TABLE `project_images` (
  `image_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `image_url` varchar(500) NOT NULL,
  `description` text DEFAULT NULL,
  `upload_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `project_materials`
--

CREATE TABLE `project_materials` (
  `materials_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `tools` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `project_site_supervisors`
--

CREATE TABLE `project_site_supervisors` (
  `project_id` varchar(10) NOT NULL,
  `site_supervisor_id` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `project_to_do`
--

CREATE TABLE `project_to_do` (
  `to_do_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `task` text NOT NULL,
  `status` enum('pending','in_progress','completed') NOT NULL,
  `assigned_date` date DEFAULT curdate()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `published_projects`
--

CREATE TABLE `published_projects` (
  `published_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `category` enum('residential','commercial','industrial','infrastructure','renovation','landscaping','civil_engineering','architectural') NOT NULL,
  `description` text DEFAULT NULL,
  `publish_date` date DEFAULT curdate()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `purchase_order`
--

CREATE TABLE `purchase_order` (
  `order_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `response_id` int(11) NOT NULL,
  `payment_status` enum('pending','partial','paid') NOT NULL,
  `estimated_delivery_date` date NOT NULL,
  `order_date` date DEFAULT curdate(),
  `order_status` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `quotation`
--

CREATE TABLE `quotation` (
  `q_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `description` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `deadline` date NOT NULL,
  `status` enum('pending','sent','received','closed') NOT NULL,
  `qs_id` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `quotation_item`
--

CREATE TABLE `quotation_item` (
  `item_id` int(11) NOT NULL,
  `q_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `quotation_response`
--

CREATE TABLE `quotation_response` (
  `response_id` int(11) NOT NULL,
  `q_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `total_amount` decimal(15,2) NOT NULL,
  `delivery_date` date NOT NULL,
  `additional_note` text DEFAULT NULL,
  `respond_date` date NOT NULL,
  `status` enum('pending','accepted','rejected') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `quotation_supplier`
--

CREATE TABLE `quotation_supplier` (
  `q_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `request_site_resources`
--

CREATE TABLE `request_site_resources` (
  `request_id` int(11) NOT NULL,
  `pm_approval` tinyint(1) DEFAULT 0,
  `qs_approval` tinyint(1) DEFAULT 0,
  `request_type` enum('material','tool','labor') NOT NULL,
  `date` date NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `site_supervisor_id` varchar(10) NOT NULL,
  `qs_id` varchar(10) NOT NULL,
  `pm_id` varchar(10) NOT NULL,
  `is_received` enum('Pending','Approved','Rejected') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
  `review_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `client_id` varchar(10) NOT NULL,
  `rate` int(11) DEFAULT NULL CHECK (`rate` >= 1 and `rate` <= 5),
  `review` text DEFAULT NULL,
  `review_date` date DEFAULT curdate()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `site_resources`
--

CREATE TABLE `site_resources` (
  `id` int(11) NOT NULL,
  `request_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `quantity` int(11) NOT NULL,
  `priority` enum('high','medium','low') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `site_visit_log`
--

CREATE TABLE `site_visit_log` (
  `visit_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `date` date NOT NULL,
  `description` text DEFAULT NULL,
  `status` enum('pending','completed','cancelled') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `supplier`
--

CREATE TABLE `supplier` (
  `supplier_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `status` enum('active','inactive','blocked') DEFAULT 'active',
  `type` enum('building_materials','safety and PPE','MEP materials','office and admin','stuctural components','tools and equipments','other') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `supplier`
--

INSERT INTO `supplier` (`supplier_id`, `user_id`, `status`, `type`) VALUES
(2, 4, 'active', 'building_materials'),
(3, 101, 'active', 'building_materials');

-- --------------------------------------------------------

--
-- Table structure for table `supplier_history`
--

CREATE TABLE `supplier_history` (
  `history_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `supply_date` date NOT NULL,
  `amount` decimal(15,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `supplier_inventory`
--

CREATE TABLE `supplier_inventory` (
  `product_id` int(11) NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `stock` enum('in stock','out of stock') DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `supplier_payment`
--

CREATE TABLE `supplier_payment` (
  `supplier_payment_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `invoice_id` int(11) NOT NULL,
  `due_date` date NOT NULL,
  `payed_date` date DEFAULT NULL,
  `amount` decimal(15,2) NOT NULL,
  `status` enum('paid','pending','overdue') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `to_do`
--

CREATE TABLE `to_do` (
  `task_id` int(11) NOT NULL,
  `employee_id` varchar(10) NOT NULL,
  `status` enum('pending','in_progress','completed') NOT NULL,
  `description` text NOT NULL,
  `date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `address` varchar(255) NOT NULL,
  `type` enum('Designer','Director','Senior_QS_Officer','QS_Officer','Project_Manager','Site_Supervisor','Legal_Officer','Financial_Officer','Client','Aadmin','Supplier') NOT NULL,
  `joined_date` date NOT NULL,
  `password` varchar(255) NOT NULL,
  `profile_image_url` varchar(255) DEFAULT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `token_expiry` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `name`, `email`, `phone_number`, `address`, `type`, `joined_date`, `password`, `profile_image_url`, `reset_token`, `token_expiry`) VALUES
(1, 'Malith Sample QS', 'sandaruwanmalith9@gmail.com', '0768534750', 'Matara', 'QS_Officer', '2025-09-10', '$2a$12$X8vImkxhR0LZDj/4bFo7beTO5yTgSs60ykW8D6nnxKxDQUFqo1O4O', NULL, NULL, NULL),
(2, 'Rathnayaka Sample SQS', 'rathnayakamalithedu@gmail.com', '0768534750', 'Matara', 'Senior_QS_Officer', '2025-07-15', '$2a$12$chETLHuMeZ0TaLt5pDlopuZ/iPePaRiSKU22Nz2smxIV7/iAipo7O', NULL, NULL, NULL),
(3, 'Client', 'client@client.com', '1414141414', 'Client Address', 'Client', '0000-00-00', '$2a$12$h8/1elOrfgz.NL1iR1qQCu0CId08qqeVIPjEdtEyhJMG3CjcPJyuq', NULL, NULL, NULL),
(4, 'Supplier', 'sandaruwanmalith39@gmail.com', '0786452315', 'Colombo', 'Supplier', '2025-10-15', 'asasaswawawasas@#sa2', NULL, NULL, NULL),
(101, 'ABC Suppliers Ltd', 'abc@supplier.com', '+1234567890', '123 Supply Street, City', 'Supplier', '2025-10-18', '$2a$10$dummyHashedPassword123', NULL, NULL, NULL);

--
-- Triggers `users`
--
DELIMITER $$
CREATE TRIGGER `insert_users` AFTER INSERT ON `users` FOR EACH ROW BEGIN
    IF NEW.type = 'Client' THEN
        INSERT INTO client(user_id) VALUES (NEW.user_id);
    ELSEIF NEW.type = 'Admin' THEN
        INSERT INTO admin(user_id) VALUES (NEW.user_id);
    ELSEIF NEW.type = 'Supplier' THEN
        INSERT INTO supplier(user_id) VALUES (NEW.user_id);
    ELSE
        INSERT INTO employee(user_id) VALUES (NEW.user_id);
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `visit_person`
--

CREATE TABLE `visit_person` (
  `visit_id` int(11) NOT NULL,
  `employee_id` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `visit_request`
--

CREATE TABLE `visit_request` (
  `request_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `from_date` date NOT NULL,
  `to_date` date NOT NULL,
  `purpose` text NOT NULL,
  `pm_id` varchar(10) NOT NULL,
  `status` enum('pending','scheduled','completed','cancelled') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `wbs`
--

CREATE TABLE `wbs` (
  `task_id` int(11) NOT NULL,
  `project_id` varchar(10) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `status` enum('pending','in_progress','completed','on_hold') NOT NULL,
  `milestone` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`admin_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `admin_id_sequence`
--
ALTER TABLE `admin_id_sequence`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `boq`
--
ALTER TABLE `boq`
  ADD PRIMARY KEY (`boq_id`),
  ADD KEY `project_id` (`project_id`),
  ADD KEY `qs_id` (`qs_id`);

--
-- Indexes for table `boq_item`
--
ALTER TABLE `boq_item`
  ADD PRIMARY KEY (`item_id`),
  ADD KEY `boq_id` (`boq_id`);

--
-- Indexes for table `catalog`
--
ALTER TABLE `catalog`
  ADD PRIMARY KEY (`item_id`);

--
-- Indexes for table `chat`
--
ALTER TABLE `chat`
  ADD PRIMARY KEY (`chat_id`),
  ADD KEY `idx_chat_users` (`sender_id`,`receiver_id`),
  ADD KEY `idx_chat_timestamp` (`message_timestamp`);

--
-- Indexes for table `client`
--
ALTER TABLE `client`
  ADD PRIMARY KEY (`client_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `client_id_sequence`
--
ALTER TABLE `client_id_sequence`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `contact_support`
--
ALTER TABLE `contact_support`
  ADD PRIMARY KEY (`request_id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `daily_updates`
--
ALTER TABLE `daily_updates`
  ADD PRIMARY KEY (`update_id`),
  ADD UNIQUE KEY `unique_daily_update` (`project_id`,`date`,`employee_id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `design`
--
ALTER TABLE `design`
  ADD PRIMARY KEY (`design_id`),
  ADD KEY `project_id` (`project_id`),
  ADD KEY `client_id` (`client_id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `design_id_sequence`
--
ALTER TABLE `design_id_sequence`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `employee`
--
ALTER TABLE `employee`
  ADD PRIMARY KEY (`employee_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `idx_employee_availability` (`availability`);

--
-- Indexes for table `employee_skill`
--
ALTER TABLE `employee_skill`
  ADD PRIMARY KEY (`employee_id`,`skill`);

--
-- Indexes for table `engineering_company`
--
ALTER TABLE `engineering_company`
  ADD PRIMARY KEY (`e_id`);

--
-- Indexes for table `installment`
--
ALTER TABLE `installment`
  ADD PRIMARY KEY (`installment_id`),
  ADD KEY `payment_plan_id` (`payment_plan_id`),
  ADD KEY `idx_installment_due` (`due_date`);

--
-- Indexes for table `labor_attendance`
--
ALTER TABLE `labor_attendance`
  ADD PRIMARY KEY (`attendance_id`),
  ADD UNIQUE KEY `unique_attendance` (`project_id`,`date`,`hiring_type`,`labor_type`);

--
-- Indexes for table `labor_payment`
--
ALTER TABLE `labor_payment`
  ADD PRIMARY KEY (`payment_id`),
  ADD KEY `project_id` (`project_id`);

--
-- Indexes for table `labor_salary`
--
ALTER TABLE `labor_salary`
  ADD PRIMARY KEY (`salary_id`),
  ADD KEY `project_id` (`project_id`),
  ADD KEY `attendance_id` (`attendance_id`);

--
-- Indexes for table `legal_approval`
--
ALTER TABLE `legal_approval`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `legal_document`
--
ALTER TABLE `legal_document`
  ADD PRIMARY KEY (`legal_document_id`),
  ADD KEY `project_id` (`project_id`);

--
-- Indexes for table `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`notification_id`),
  ADD KEY `idx_notification_user` (`user_id`,`user_type`),
  ADD KEY `idx_notification_read` (`is_read`);

--
-- Indexes for table `order_invoice`
--
ALTER TABLE `order_invoice`
  ADD PRIMARY KEY (`invoice_id`,`order_id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `supplier_id` (`supplier_id`);

--
-- Indexes for table `order_item`
--
ALTER TABLE `order_item`
  ADD PRIMARY KEY (`order_id`,`item_id`),
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`payment_id`),
  ADD KEY `invoice_id` (`invoice_id`),
  ADD KEY `idx_payment_status` (`status`);

--
-- Indexes for table `payment_confirmation`
--
ALTER TABLE `payment_confirmation`
  ADD PRIMARY KEY (`confirmation_id`),
  ADD KEY `payment_id` (`payment_id`),
  ADD KEY `project_id` (`project_id`),
  ADD KEY `document_id` (`document_id`);

--
-- Indexes for table `payment_plan`
--
ALTER TABLE `payment_plan`
  ADD PRIMARY KEY (`payment_plan_id`),
  ADD KEY `project_id` (`project_id`);

--
-- Indexes for table `petty_cash`
--
ALTER TABLE `petty_cash`
  ADD PRIMARY KEY (`petty_cash_id`),
  ADD KEY `project_id` (`project_id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `petty_cash_record`
--
ALTER TABLE `petty_cash_record`
  ADD PRIMARY KEY (`record_id`),
  ADD KEY `petty_cash_id` (`petty_cash_id`);

--
-- Indexes for table `project`
--
ALTER TABLE `project`
  ADD PRIMARY KEY (`project_id`),
  ADD KEY `qs_id` (`qs_id`),
  ADD KEY `pm_id` (`pm_id`),
  ADD KEY `ss_id` (`ss_id`),
  ADD KEY `idx_project_status` (`status`),
  ADD KEY `idx_project_client` (`client_id`),
  ADD KEY `idx_project_dates` (`start_date`,`due_date`);

--
-- Indexes for table `project_documents`
--
ALTER TABLE `project_documents`
  ADD PRIMARY KEY (`document_id`),
  ADD KEY `idx_document_project` (`project_id`),
  ADD KEY `idx_document_type` (`type`);

--
-- Indexes for table `project_id_sequence`
--
ALTER TABLE `project_id_sequence`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `project_images`
--
ALTER TABLE `project_images`
  ADD PRIMARY KEY (`image_id`),
  ADD KEY `project_id` (`project_id`);

--
-- Indexes for table `project_materials`
--
ALTER TABLE `project_materials`
  ADD KEY `project_id` (`project_id`);

--
-- Indexes for table `project_site_supervisors`
--
ALTER TABLE `project_site_supervisors`
  ADD PRIMARY KEY (`project_id`,`site_supervisor_id`),
  ADD KEY `site_supervisor_id` (`site_supervisor_id`);

--
-- Indexes for table `project_to_do`
--
ALTER TABLE `project_to_do`
  ADD PRIMARY KEY (`to_do_id`),
  ADD KEY `project_id` (`project_id`);

--
-- Indexes for table `published_projects`
--
ALTER TABLE `published_projects`
  ADD PRIMARY KEY (`published_id`),
  ADD KEY `project_id` (`project_id`);

--
-- Indexes for table `purchase_order`
--
ALTER TABLE `purchase_order`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `project_id` (`project_id`),
  ADD KEY `supplier_id` (`supplier_id`),
  ADD KEY `response_id` (`response_id`);

--
-- Indexes for table `quotation`
--
ALTER TABLE `quotation`
  ADD PRIMARY KEY (`q_id`),
  ADD KEY `project_id` (`project_id`);

--
-- Indexes for table `quotation_item`
--
ALTER TABLE `quotation_item`
  ADD PRIMARY KEY (`item_id`),
  ADD KEY `q_id` (`q_id`);

--
-- Indexes for table `quotation_response`
--
ALTER TABLE `quotation_response`
  ADD PRIMARY KEY (`response_id`),
  ADD KEY `q_id` (`q_id`),
  ADD KEY `supplier_id` (`supplier_id`);

--
-- Indexes for table `quotation_supplier`
--
ALTER TABLE `quotation_supplier`
  ADD PRIMARY KEY (`q_id`,`supplier_id`),
  ADD KEY `supplier_id` (`supplier_id`);

--
-- Indexes for table `request_site_resources`
--
ALTER TABLE `request_site_resources`
  ADD PRIMARY KEY (`request_id`),
  ADD KEY `project_id` (`project_id`),
  ADD KEY `site_supervisor_id` (`site_supervisor_id`),
  ADD KEY `qs_id` (`qs_id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`review_id`),
  ADD KEY `project_id` (`project_id`),
  ADD KEY `client_id` (`client_id`);

--
-- Indexes for table `site_resources`
--
ALTER TABLE `site_resources`
  ADD PRIMARY KEY (`id`),
  ADD KEY `request_id` (`request_id`);

--
-- Indexes for table `site_visit_log`
--
ALTER TABLE `site_visit_log`
  ADD PRIMARY KEY (`visit_id`),
  ADD KEY `project_id` (`project_id`);

--
-- Indexes for table `supplier`
--
ALTER TABLE `supplier`
  ADD PRIMARY KEY (`supplier_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `supplier_history`
--
ALTER TABLE `supplier_history`
  ADD PRIMARY KEY (`history_id`),
  ADD KEY `supplier_id` (`supplier_id`),
  ADD KEY `order_id` (`order_id`);

--
-- Indexes for table `supplier_inventory`
--
ALTER TABLE `supplier_inventory`
  ADD PRIMARY KEY (`product_id`);

--
-- Indexes for table `supplier_payment`
--
ALTER TABLE `supplier_payment`
  ADD PRIMARY KEY (`supplier_payment_id`),
  ADD KEY `project_id` (`project_id`),
  ADD KEY `invoice_id` (`invoice_id`);

--
-- Indexes for table `to_do`
--
ALTER TABLE `to_do`
  ADD PRIMARY KEY (`task_id`),
  ADD KEY `idx_todo_employee` (`employee_id`),
  ADD KEY `idx_todo_status` (`status`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD KEY `idx_employee_type` (`type`);

--
-- Indexes for table `visit_person`
--
ALTER TABLE `visit_person`
  ADD PRIMARY KEY (`visit_id`,`employee_id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `visit_request`
--
ALTER TABLE `visit_request`
  ADD PRIMARY KEY (`request_id`),
  ADD KEY `project_id` (`project_id`),
  ADD KEY `pm_id` (`pm_id`);

--
-- Indexes for table `wbs`
--
ALTER TABLE `wbs`
  ADD PRIMARY KEY (`task_id`),
  ADD KEY `parent_id` (`parent_id`),
  ADD KEY `idx_wbs_project` (`project_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin_id_sequence`
--
ALTER TABLE `admin_id_sequence`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `boq`
--
ALTER TABLE `boq`
  MODIFY `boq_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `boq_item`
--
ALTER TABLE `boq_item`
  MODIFY `item_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `catalog`
--
ALTER TABLE `catalog`
  MODIFY `item_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `chat`
--
ALTER TABLE `chat`
  MODIFY `chat_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `client_id_sequence`
--
ALTER TABLE `client_id_sequence`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `contact_support`
--
ALTER TABLE `contact_support`
  MODIFY `request_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `daily_updates`
--
ALTER TABLE `daily_updates`
  MODIFY `update_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `design_id_sequence`
--
ALTER TABLE `design_id_sequence`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `engineering_company`
--
ALTER TABLE `engineering_company`
  MODIFY `e_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `installment`
--
ALTER TABLE `installment`
  MODIFY `installment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `labor_attendance`
--
ALTER TABLE `labor_attendance`
  MODIFY `attendance_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `labor_payment`
--
ALTER TABLE `labor_payment`
  MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `labor_salary`
--
ALTER TABLE `labor_salary`
  MODIFY `salary_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `legal_approval`
--
ALTER TABLE `legal_approval`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `legal_document`
--
ALTER TABLE `legal_document`
  MODIFY `legal_document_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notification`
--
ALTER TABLE `notification`
  MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
  MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payment_confirmation`
--
ALTER TABLE `payment_confirmation`
  MODIFY `confirmation_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payment_plan`
--
ALTER TABLE `payment_plan`
  MODIFY `payment_plan_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `petty_cash`
--
ALTER TABLE `petty_cash`
  MODIFY `petty_cash_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `petty_cash_record`
--
ALTER TABLE `petty_cash_record`
  MODIFY `record_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `project_documents`
--
ALTER TABLE `project_documents`
  MODIFY `document_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `project_id_sequence`
--
ALTER TABLE `project_id_sequence`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `project_images`
--
ALTER TABLE `project_images`
  MODIFY `image_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `project_to_do`
--
ALTER TABLE `project_to_do`
  MODIFY `to_do_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `published_projects`
--
ALTER TABLE `published_projects`
  MODIFY `published_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `purchase_order`
--
ALTER TABLE `purchase_order`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `quotation`
--
ALTER TABLE `quotation`
  MODIFY `q_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `quotation_item`
--
ALTER TABLE `quotation_item`
  MODIFY `item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `quotation_response`
--
ALTER TABLE `quotation_response`
  MODIFY `response_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `request_site_resources`
--
ALTER TABLE `request_site_resources`
  MODIFY `request_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `review_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `site_resources`
--
ALTER TABLE `site_resources`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `site_visit_log`
--
ALTER TABLE `site_visit_log`
  MODIFY `visit_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `supplier`
--
ALTER TABLE `supplier`
  MODIFY `supplier_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `supplier_history`
--
ALTER TABLE `supplier_history`
  MODIFY `history_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `supplier_inventory`
--
ALTER TABLE `supplier_inventory`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `supplier_payment`
--
ALTER TABLE `supplier_payment`
  MODIFY `supplier_payment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `to_do`
--
ALTER TABLE `to_do`
  MODIFY `task_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=102;

--
-- AUTO_INCREMENT for table `visit_request`
--
ALTER TABLE `visit_request`
  MODIFY `request_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `wbs`
--
ALTER TABLE `wbs`
  MODIFY `task_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `boq`
--
ALTER TABLE `boq`
  ADD CONSTRAINT `boq_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  ADD CONSTRAINT `boq_ibfk_2` FOREIGN KEY (`qs_id`) REFERENCES `employee` (`employee_id`);

--
-- Constraints for table `boq_item`
--
ALTER TABLE `boq_item`
  ADD CONSTRAINT `boq_item_ibfk_1` FOREIGN KEY (`boq_id`) REFERENCES `boq` (`boq_id`);

--
-- Constraints for table `client`
--
ALTER TABLE `client`
  ADD CONSTRAINT `client_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `contact_support`
--
ALTER TABLE `contact_support`
  ADD CONSTRAINT `contact_support_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`);

--
-- Constraints for table `daily_updates`
--
ALTER TABLE `daily_updates`
  ADD CONSTRAINT `daily_updates_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  ADD CONSTRAINT `daily_updates_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`);

--
-- Constraints for table `design`
--
ALTER TABLE `design`
  ADD CONSTRAINT `design_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  ADD CONSTRAINT `design_ibfk_2` FOREIGN KEY (`client_id`) REFERENCES `client` (`client_id`),
  ADD CONSTRAINT `design_ibfk_3` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`);

--
-- Constraints for table `employee`
--
ALTER TABLE `employee`
  ADD CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `employee_skill`
--
ALTER TABLE `employee_skill`
  ADD CONSTRAINT `employee_skill_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) ON DELETE CASCADE;

--
-- Constraints for table `installment`
--
ALTER TABLE `installment`
  ADD CONSTRAINT `installment_ibfk_1` FOREIGN KEY (`payment_plan_id`) REFERENCES `payment_plan` (`payment_plan_id`);

--
-- Constraints for table `labor_attendance`
--
ALTER TABLE `labor_attendance`
  ADD CONSTRAINT `labor_attendance_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`);

--
-- Constraints for table `labor_payment`
--
ALTER TABLE `labor_payment`
  ADD CONSTRAINT `labor_payment_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`);

--
-- Constraints for table `labor_salary`
--
ALTER TABLE `labor_salary`
  ADD CONSTRAINT `labor_salary_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  ADD CONSTRAINT `labor_salary_ibfk_2` FOREIGN KEY (`attendance_id`) REFERENCES `labor_attendance` (`attendance_id`);

--
-- Constraints for table `legal_document`
--
ALTER TABLE `legal_document`
  ADD CONSTRAINT `legal_document_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`);

--
-- Constraints for table `order_invoice`
--
ALTER TABLE `order_invoice`
  ADD CONSTRAINT `order_invoice_ibfk_1` FOREIGN KEY (`invoice_id`) REFERENCES `project_documents` (`document_id`),
  ADD CONSTRAINT `order_invoice_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `purchase_order` (`order_id`),
  ADD CONSTRAINT `order_invoice_ibfk_3` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`);

--
-- Constraints for table `order_item`
--
ALTER TABLE `order_item`
  ADD CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `purchase_order` (`order_id`),
  ADD CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `catalog` (`item_id`);

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`invoice_id`) REFERENCES `project_documents` (`document_id`);

--
-- Constraints for table `payment_confirmation`
--
ALTER TABLE `payment_confirmation`
  ADD CONSTRAINT `payment_confirmation_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`payment_id`),
  ADD CONSTRAINT `payment_confirmation_ibfk_2` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  ADD CONSTRAINT `payment_confirmation_ibfk_3` FOREIGN KEY (`document_id`) REFERENCES `project_documents` (`document_id`);

--
-- Constraints for table `payment_plan`
--
ALTER TABLE `payment_plan`
  ADD CONSTRAINT `payment_plan_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`);

--
-- Constraints for table `petty_cash`
--
ALTER TABLE `petty_cash`
  ADD CONSTRAINT `petty_cash_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  ADD CONSTRAINT `petty_cash_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`);

--
-- Constraints for table `petty_cash_record`
--
ALTER TABLE `petty_cash_record`
  ADD CONSTRAINT `petty_cash_record_ibfk_1` FOREIGN KEY (`petty_cash_id`) REFERENCES `petty_cash` (`petty_cash_id`);

--
-- Constraints for table `project`
--
ALTER TABLE `project`
  ADD CONSTRAINT `project_ibfk_1` FOREIGN KEY (`client_id`) REFERENCES `client` (`client_id`),
  ADD CONSTRAINT `project_ibfk_2` FOREIGN KEY (`qs_id`) REFERENCES `employee` (`employee_id`),
  ADD CONSTRAINT `project_ibfk_3` FOREIGN KEY (`pm_id`) REFERENCES `employee` (`employee_id`),
  ADD CONSTRAINT `project_ibfk_4` FOREIGN KEY (`ss_id`) REFERENCES `employee` (`employee_id`);

--
-- Constraints for table `project_documents`
--
ALTER TABLE `project_documents`
  ADD CONSTRAINT `project_documents_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`);

--
-- Constraints for table `project_images`
--
ALTER TABLE `project_images`
  ADD CONSTRAINT `project_images_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`);

--
-- Constraints for table `project_materials`
--
ALTER TABLE `project_materials`
  ADD CONSTRAINT `project_materials_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`) ON DELETE CASCADE;

--
-- Constraints for table `project_site_supervisors`
--
ALTER TABLE `project_site_supervisors`
  ADD CONSTRAINT `project_site_supervisors_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `project_site_supervisors_ibfk_2` FOREIGN KEY (`site_supervisor_id`) REFERENCES `employee` (`employee_id`);

--
-- Constraints for table `project_to_do`
--
ALTER TABLE `project_to_do`
  ADD CONSTRAINT `project_to_do_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`);

--
-- Constraints for table `published_projects`
--
ALTER TABLE `published_projects`
  ADD CONSTRAINT `published_projects_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`);

--
-- Constraints for table `purchase_order`
--
ALTER TABLE `purchase_order`
  ADD CONSTRAINT `purchase_order_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  ADD CONSTRAINT `purchase_order_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`),
  ADD CONSTRAINT `purchase_order_ibfk_3` FOREIGN KEY (`response_id`) REFERENCES `quotation_response` (`response_id`);

--
-- Constraints for table `quotation`
--
ALTER TABLE `quotation`
  ADD CONSTRAINT `quotation_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`);

--
-- Constraints for table `quotation_item`
--
ALTER TABLE `quotation_item`
  ADD CONSTRAINT `quotation_item_ibfk_1` FOREIGN KEY (`q_id`) REFERENCES `quotation` (`q_id`);

--
-- Constraints for table `quotation_response`
--
ALTER TABLE `quotation_response`
  ADD CONSTRAINT `quotation_response_ibfk_1` FOREIGN KEY (`q_id`) REFERENCES `quotation` (`q_id`),
  ADD CONSTRAINT `quotation_response_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`);

--
-- Constraints for table `quotation_supplier`
--
ALTER TABLE `quotation_supplier`
  ADD CONSTRAINT `quotation_supplier_ibfk_1` FOREIGN KEY (`q_id`) REFERENCES `quotation` (`q_id`),
  ADD CONSTRAINT `quotation_supplier_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`);

--
-- Constraints for table `request_site_resources`
--
ALTER TABLE `request_site_resources`
  ADD CONSTRAINT `request_site_resources_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  ADD CONSTRAINT `request_site_resources_ibfk_2` FOREIGN KEY (`site_supervisor_id`) REFERENCES `employee` (`employee_id`),
  ADD CONSTRAINT `request_site_resources_ibfk_3` FOREIGN KEY (`qs_id`) REFERENCES `employee` (`employee_id`),
  ADD CONSTRAINT `request_site_resources_ibfk_4` FOREIGN KEY (`qs_id`) REFERENCES `employee` (`employee_id`);

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  ADD CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`client_id`) REFERENCES `client` (`client_id`);

--
-- Constraints for table `site_resources`
--
ALTER TABLE `site_resources`
  ADD CONSTRAINT `site_resources_ibfk_1` FOREIGN KEY (`request_id`) REFERENCES `request_site_resources` (`request_id`);

--
-- Constraints for table `site_visit_log`
--
ALTER TABLE `site_visit_log`
  ADD CONSTRAINT `site_visit_log_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`);

--
-- Constraints for table `supplier`
--
ALTER TABLE `supplier`
  ADD CONSTRAINT `supplier_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `supplier_history`
--
ALTER TABLE `supplier_history`
  ADD CONSTRAINT `supplier_history_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`),
  ADD CONSTRAINT `supplier_history_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `purchase_order` (`order_id`);

--
-- Constraints for table `supplier_payment`
--
ALTER TABLE `supplier_payment`
  ADD CONSTRAINT `supplier_payment_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  ADD CONSTRAINT `supplier_payment_ibfk_2` FOREIGN KEY (`invoice_id`) REFERENCES `project_documents` (`document_id`);

--
-- Constraints for table `to_do`
--
ALTER TABLE `to_do`
  ADD CONSTRAINT `to_do_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`);

--
-- Constraints for table `visit_person`
--
ALTER TABLE `visit_person`
  ADD CONSTRAINT `visit_person_ibfk_1` FOREIGN KEY (`visit_id`) REFERENCES `site_visit_log` (`visit_id`),
  ADD CONSTRAINT `visit_person_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`);

--
-- Constraints for table `visit_request`
--
ALTER TABLE `visit_request`
  ADD CONSTRAINT `visit_request_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  ADD CONSTRAINT `visit_request_ibfk_2` FOREIGN KEY (`pm_id`) REFERENCES `employee` (`employee_id`);

--
-- Constraints for table `wbs`
--
ALTER TABLE `wbs`
  ADD CONSTRAINT `wbs_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`),
  ADD CONSTRAINT `wbs_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `wbs` (`task_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
