-- ====================================================================
-- Student Hostel Management System - Phase 2 Database Schema
-- Database: hostel_db
-- ====================================================================

USE hostel_db;

-- 1. Students Profile Table
CREATE TABLE IF NOT EXISTS students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE,
    roll_number VARCHAR(50) NOT NULL UNIQUE,
    course VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    emergency_contact VARCHAR(20) NOT NULL,
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_students_users FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE
);

-- 2. Rooms Table
CREATE TABLE IF NOT EXISTS rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    block_wing VARCHAR(50) NOT NULL,
    capacity INT NOT NULL DEFAULT 2,
    current_occupancy INT NOT NULL DEFAULT 0,
    monthly_fee DECIMAL(10, 2) NOT NULL DEFAULT 5000.00,
    room_status ENUM('AVAILABLE', 'FULL', 'MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE'
);

-- 3. Room Allocations Table
CREATE TABLE IF NOT EXISTS allocations (
    allocation_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    room_id INT NOT NULL,
    allocation_date DATE NOT NULL,
    vacate_date DATE NULL,
    status ENUM('ACTIVE', 'VACATED', 'TRANSFERRED') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_allocations_students FOREIGN KEY (student_id) 
        REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_allocations_rooms FOREIGN KEY (room_id) 
        REFERENCES rooms(room_id) ON DELETE CASCADE
);

-- 4. Fee Management Table
CREATE TABLE IF NOT EXISTS fees (
    fee_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    amount_due DECIMAL(10, 2) NOT NULL,
    amount_paid DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    due_date DATE NOT NULL,
    payment_date DATE NULL,
    payment_status ENUM('PENDING', 'PAID', 'OVERDUE') NOT NULL DEFAULT 'PENDING',
    transaction_ref VARCHAR(100) NULL,
    CONSTRAINT fk_fees_students FOREIGN KEY (student_id) 
        REFERENCES students(student_id) ON DELETE CASCADE
);

-- 5. Complaints Table
CREATE TABLE IF NOT EXISTS complaints (
    complaint_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    category ENUM('ELECTRICAL', 'PLUMBING', 'CLEANLINESS', 'FOOD', 'OTHER') NOT NULL,
    description TEXT NOT NULL,
    status ENUM('PENDING', 'IN_PROGRESS', 'RESOLVED') NOT NULL DEFAULT 'PENDING',
    resolution_remarks TEXT NULL,
    filed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    CONSTRAINT fk_complaints_students FOREIGN KEY (student_id) 
        REFERENCES students(student_id) ON DELETE CASCADE
);

-- Sample initial rooms (if empty)
INSERT IGNORE INTO rooms (room_number, block_wing, capacity, current_occupancy, monthly_fee, room_status)
VALUES 
    ('A-101', 'Block A - 1st Floor', 2, 0, 5500.00, 'AVAILABLE'),
    ('A-102', 'Block A - 1st Floor', 2, 0, 5500.00, 'AVAILABLE'),
    ('A-201', 'Block A - 2nd Floor', 3, 0, 4800.00, 'AVAILABLE'),
    ('B-101', 'Block B - Ground Floor', 1, 0, 7500.00, 'AVAILABLE'),
    ('B-102', 'Block B - Ground Floor', 2, 0, 5200.00, 'AVAILABLE'),
    ('C-301', 'Block C - 3rd Floor', 4, 0, 4000.00, 'AVAILABLE');
