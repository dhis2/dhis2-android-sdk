# Add legendSet info to visualizations (ANDROSDK-1472)

ALTER TABLE Visualization ADD COLUMN legendShowKey TEXT;
ALTER TABLE Visualization ADD COLUMN legendStyle TEXT;
ALTER TABLE Visualization ADD COLUMN legendSetId TEXT;
ALTER TABLE Visualization ADD COLUMN legendStrategy TEXT;
