# Related to ANDROSDK-1147
ALTER TABLE ProgramSectionAttributeLink ADD COLUMN sortOrder INTEGER;
UPDATE ProgramSectionAttributeLink SET sortOrder=_id;