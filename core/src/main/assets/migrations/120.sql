# Add sortOrder to LegendSet Links,  (ANDROSDK-1470)

ALTER TABLE DataElementLegendSetLink ADD COLUMN sortOrder INTEGER;
ALTER TABLE IndicatorLegendSetLink ADD COLUMN sortOrder INTEGER;
ALTER TABLE ProgramIndicatorLegendSetLink ADD COLUMN sortOrder INTEGER;