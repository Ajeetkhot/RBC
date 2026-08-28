USE corporate_banking;

INSERT INTO accounts (account_number, account_type, currency, balance, available_balance, status, created_at, user_id)
VALUES ('10000012345', 'CURRENT', 'INR', 100000.00, 95000.00, 'ACTIVE', NOW(), 1);

INSERT INTO beneficiaries (beneficiary_name, account_number, bank_name, ifsc_code, created_by, status, created_at)
VALUES ('John', '1234567890', 'ABC Bank', 'ABC0001234', 1, 'ACTIVE', NOW());

INSERT INTO beneficiaries (beneficiary_name, account_number, bank_name, ifsc_code, created_by, status, created_at)
VALUES ('Jane', '0987654321', 'XYZ Bank', 'XYZ0004321', 1, 'ACTIVE', NOW());

INSERT INTO fund_transfers (transaction_id, maker_id, debit_account_id, beneficiary_id, amount, transaction_type, remarks, status, created_at, updated_at, approved_by, approved_at, rejection_reason)
VALUES ('TXN100001', 1, 1, 1, 5000.00, 'FUND_TRANSFER', 'Payment', 'PENDING_APPROVAL', NOW(), NULL, NULL, NULL, NULL);

INSERT INTO approval_history (transaction_id, checker_id, action, remarks, created_at)
VALUES ('TXN100001', 201, 'APPROVED', 'Looks good', NOW());

INSERT INTO approval_history (transaction_id, checker_id, action, remarks, created_at)
VALUES ('TXN100001', 201, 'REJECTED', 'Invalid supporting information', NOW());
