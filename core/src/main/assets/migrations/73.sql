# Related to ANDROSDK-1151
UPDATE DataSetCompleteRegistration SET date = date() || 'T00:00:00.000' WHERE date IS NULL;