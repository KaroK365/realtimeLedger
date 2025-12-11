CREATE TABLE reconciliation_reports (
  id BIGSERIAL PRIMARY KEY,
  run_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  ledger_sum BIGINT,
  accounts_sum BIGINT,
  mismatch BOOLEAN,
  details TEXT
);

CREATE TABLE anomalies (
  id BIGSERIAL PRIMARY KEY,
  detected_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  description TEXT
);

