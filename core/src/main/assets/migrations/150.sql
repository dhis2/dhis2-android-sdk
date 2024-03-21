# Add description and displayDescription properties in ProgramStageSection table (ANDROSDK-1725)

ALTER TABLE ProgramStageSection ADD COLUMN description TEXT;
ALTER TABLE ProgramStageSection ADD COLUMN displayDescription TEXT;