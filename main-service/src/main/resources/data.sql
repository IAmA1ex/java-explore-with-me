INSERT INTO participants_statuses (name)
SELECT 'PENDING'
WHERE NOT EXISTS (SELECT 1 FROM participants_statuses WHERE name = 'PENDING');

INSERT INTO participants_statuses (name)
SELECT 'CONFIRMED'
WHERE NOT EXISTS (SELECT 1 FROM participants_statuses WHERE name = 'CONFIRMED');

INSERT INTO participants_statuses (name)
SELECT 'REJECTED'
WHERE NOT EXISTS (SELECT 1 FROM participants_statuses WHERE name = 'REJECTED');

INSERT INTO participants_statuses (name)
SELECT 'CANCELED'
WHERE NOT EXISTS (SELECT 1 FROM participants_statuses WHERE name = 'CANCELED');



INSERT INTO events_states (name)
SELECT 'PENDING'
WHERE NOT EXISTS (SELECT 1 FROM events_states WHERE name = '');

INSERT INTO events_states (name)
SELECT 'PUBLISHED'
WHERE NOT EXISTS (SELECT 1 FROM events_states WHERE name = '');

INSERT INTO events_states (name)
SELECT 'CANCELED'
WHERE NOT EXISTS (SELECT 1 FROM events_states WHERE name = '');



