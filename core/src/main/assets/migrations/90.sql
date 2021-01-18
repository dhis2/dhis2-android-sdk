# Adds decimals to Indicator
ALTER TABLE Indicator ADD COLUMN decimals INTEGER;
UPDATE Indicator SET decimals = 2;