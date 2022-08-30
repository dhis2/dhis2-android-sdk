# Add title and subtitle to visualizations (ANDROSDK-1453)

ALTER TABLE Visualization ADD COLUMN title TEXT;
ALTER TABLE Visualization ADD COLUMN displayTitle TEXT;
ALTER TABLE Visualization ADD COLUMN subtitle TEXT;
ALTER TABLE Visualization ADD COLUMN displaySubtitle TEXT;