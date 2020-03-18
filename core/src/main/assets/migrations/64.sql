# DataElement
ALTER TABLE DataElement ADD COLUMN color TEXT;
ALTER TABLE DataElement ADD COLUMN icon TEXT;
UPDATE DataElement SET color = (SELECT color FROM ObjectStyle WHERE Dataelement.uid = ObjectStyle.uid), icon = (SELECT icon FROM ObjectStyle WHERE Dataelement.uid = ObjectStyle.uid);

  # DataSet
ALTER TABLE DataSet ADD COLUMN color TEXT;
ALTER TABLE DataSet ADD COLUMN icon TEXT;
UPDATE DataSet SET color = (SELECT color FROM ObjectStyle WHERE DataSet.uid = ObjectStyle.uid), icon = (SELECT icon FROM ObjectStyle WHERE DataSet.uid = ObjectStyle.uid);

  # Option
ALTER TABLE Option ADD COLUMN color TEXT;
ALTER TABLE Option ADD COLUMN icon TEXT;
UPDATE Option SET color = (SELECT color FROM ObjectStyle WHERE Option.uid = ObjectStyle.uid), icon = (SELECT icon FROM ObjectStyle WHERE Option.uid = ObjectStyle.uid);

  # Program
ALTER TABLE Program ADD COLUMN color TEXT;
ALTER TABLE Program ADD COLUMN icon TEXT;
UPDATE Program SET color = (SELECT color FROM ObjectStyle WHERE Program.uid = ObjectStyle.uid), icon = (SELECT icon FROM ObjectStyle WHERE Program.uid = ObjectStyle.uid);

  # ProgramSection
ALTER TABLE ProgramSection ADD COLUMN color TEXT;
ALTER TABLE ProgramSection ADD COLUMN icon TEXT;
UPDATE ProgramSection SET color = (SELECT color FROM ObjectStyle WHERE ProgramSection.uid = ObjectStyle.uid), icon = (SELECT icon FROM ObjectStyle WHERE ProgramSection.uid = ObjectStyle.uid);

  # ProgramStage
ALTER TABLE ProgramStage ADD COLUMN color TEXT;
ALTER TABLE ProgramStage ADD COLUMN icon TEXT;
UPDATE ProgramStage SET color = (SELECT color FROM ObjectStyle WHERE ProgramStage.uid = ObjectStyle.uid), icon = (SELECT icon FROM ObjectStyle WHERE ProgramStage.uid = ObjectStyle.uid);

  # TrackedEntityAttribute
ALTER TABLE TrackedEntityAttribute ADD COLUMN color TEXT;
ALTER TABLE TrackedEntityAttribute ADD COLUMN icon TEXT;
UPDATE TrackedEntityAttribute SET color = (SELECT color FROM ObjectStyle WHERE TrackedEntityAttribute.uid = ObjectStyle.uid), icon = (SELECT icon FROM ObjectStyle WHERE TrackedEntityAttribute.uid = ObjectStyle.uid);

  # TrackedEntityType
ALTER TABLE TrackedEntityType ADD COLUMN color TEXT;
ALTER TABLE TrackedEntityType ADD COLUMN icon TEXT;
UPDATE TrackedEntityType SET color = (SELECT color FROM ObjectStyle WHERE TrackedEntityType.uid = ObjectStyle.uid), icon = (SELECT icon FROM ObjectStyle WHERE TrackedEntityType.uid = ObjectStyle.uid);

  # ObjectStyle
DROP TABLE IF EXISTS ObjectStyle;
