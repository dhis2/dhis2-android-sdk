# Add minimumLocationAccuracy and disableManualLocation to ProgramConfigurationSetting table (ANDROSDK-1897)

ALTER TABLE ProgramConfigurationSetting ADD COLUMN minimumLocationAccuracy INTEGER;
ALTER TABLE ProgramConfigurationSetting ADD COLUMN disableManualLocation INTEGER;