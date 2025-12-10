CREATE TABLE transactions (
  id BIGSERIAL PRIMARY KEY,
  transaction_id UUID NOT NULL UNIQUE,
  from_account UUID NOT NULL,
  to_account UUID NOT NULL,
  amount BIGINT NOT NULL,  -- in paise
  currency VARCHAR(3) NOT NULL DEFAULT 'INR',
  status VARCHAR(20) NOT NULL, -- PENDING, SUCCESS, FAILED
  idempotency_key VARCHAR(200),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE ledger_entries (
  id BIGSERIAL PRIMARY KEY,
  entry_id UUID NOT NULL UNIQUE,
  transaction_id UUID NOT NULL,
  account_id UUID NOT NULL,
  delta BIGINT NOT NULL, -- credit: +, debit: -
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);
