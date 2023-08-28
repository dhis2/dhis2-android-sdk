# Add disableReferrals and collapsibleSections in ProgramConfigurationSetting table (ANDROSDK-1723 and ANDROSDK-1724)

ALTER TABLE ProgramConfigurationSetting ADD COLUMN disableReferrals INTEGER;
ALTER TABLE ProgramConfigurationSetting ADD COLUMN collapsibleSections INTEGER;
