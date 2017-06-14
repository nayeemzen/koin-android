CREATE TABLE Payment (
    transaction_token TEXT PRIMARY KEY NOT NULL,
    amount INTEGER NOT NULL,
    created_at INTEGER NOT NULL,
    state TEXT NOT NULL,
    merchant_name TEXT NOT NULL,
    merchant_type TEXT NOT NULL
);