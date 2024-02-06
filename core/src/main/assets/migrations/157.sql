# Add tracker terminology (ANDROSDK-1806)

ALTER TABLE Program ADD COLUMN enrollmentLabel TEXT;
ALTER TABLE Program ADD COLUMN followUpLabel TEXT;
ALTER TABLE Program ADD COLUMN orgUnitLabel TEXT;
ALTER TABLE Program ADD COLUMN relationshipLabel TEXT;
ALTER TABLE Program ADD COLUMN noteLabel TEXT;
ALTER TABLE Program ADD COLUMN trackedEntityAttributeLabel TEXT;

ALTER TABLE ProgramStage ADD COLUMN programStageLabel TEXT;
ALTER TABLE ProgramStage ADD COLUMN eventLabel TEXT;