-- Database initialization script for Mauriexchange
-- This script will be executed when the MySQL container starts

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS mauriexchange_db;
USE mauriexchange_db;

-- Grant privileges to root user
GRANT ALL PRIVILEGES ON mauriexchange_db.* TO 'root'@'%';
FLUSH PRIVILEGES;

-- Note: The actual schema will be created by Flyway migrations
-- This script is just for initial database setup
