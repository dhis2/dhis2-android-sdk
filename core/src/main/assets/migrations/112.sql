# Add title and subtitle to visualizations (ANDROSDK-1453)

ALTER TABLE Visualization ADD COLUMN title TEXT;
ALTER TABLE Visualization ADD COLUMN displayTitle TEXT;
ALTER TABLE Visualization ADD COLUMN subtitle TEXT;
ALTER TABLE Visualization ADD COLUMN displaySubtitle TEXT;

CREATE TABLE IndicatorLegendSetLink(_id INTEGER PRIMARY KEY AUTOINCREMENT, indicator TEXT NOT NULL, legendSet TEXT NOT NULL, FOREIGN KEY (indicator) REFERENCES Indicator (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (legendSet) REFERENCES LegendSet (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (indicator, legendSet));
