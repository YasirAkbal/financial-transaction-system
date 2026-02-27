SELECT 'CREATE DATABASE account_service' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'account_service')\gexec
SELECT 'CREATE DATABASE customer_service' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'customer_service')\gexec
SELECT 'CREATE DATABASE ledger_service' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'ledger_service')\gexec
SELECT 'CREATE DATABASE money_transfer_service' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'money_transfer_service')\gexec
SELECT 'CREATE DATABASE notification_service' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'notification_service')\gexec

GRANT ALL PRIVILEGES ON DATABASE account_service TO username;
GRANT ALL PRIVILEGES ON DATABASE customer_service TO username;
GRANT ALL PRIVILEGES ON DATABASE ledger_service TO username;
GRANT ALL PRIVILEGES ON DATABASE money_transfer_service TO username;
GRANT ALL PRIVILEGES ON DATABASE notification_service TO username;