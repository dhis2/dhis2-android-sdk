# Add renderType to ProgramSection (ANDROSDK-1492)

ALTER TABLE ProgramSection ADD COLUMN desktopRenderType TEXT;
ALTER TABLE ProgramSection ADD COLUMN mobileRenderType TEXT;
