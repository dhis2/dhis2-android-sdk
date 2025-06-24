-- Create new tables for the split ItemFilter entities

-- EventDataFilter table
CREATE TABLE EventDataFilter (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    eventFilter TEXT,
    dataItem TEXT,
    le TEXT,
    ge TEXT,
    gt TEXT,
    lt TEXT,
    eq TEXT,
    inProperty TEXT,
    like TEXT,
    dateFilter TEXT,
    FOREIGN KEY (eventFilter) REFERENCES EventFilter (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
);

-- AttributeValueFilter table
CREATE TABLE AttributeValueFilter (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    trackedEntityInstanceFilter TEXT,
    attribute TEXT,
    sw TEXT,
    ew TEXT,
    le TEXT,
    ge TEXT,
    gt TEXT,
    lt TEXT,
    eq TEXT,
    inProperty TEXT,
    like TEXT,
    dateFilter TEXT,
    FOREIGN KEY (trackedEntityInstanceFilter) REFERENCES TrackedEntityInstanceFilter (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
);

-- ProgramStageWorkingListEventDataFilter table
CREATE TABLE ProgramStageWorkingListEventDataFilter (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    programStageWorkingList TEXT,
    dataItem TEXT,
    le TEXT,
    ge TEXT,
    gt TEXT,
    lt TEXT,
    eq TEXT,
    inProperty TEXT,
    like TEXT,
    dateFilter TEXT,
    FOREIGN KEY (programStageWorkingList) REFERENCES ProgramStageWorkingList (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
);

-- ProgramStageWorkingListAttributeValueFilter table
CREATE TABLE ProgramStageWorkingListAttributeValueFilter (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    programStageWorkingList TEXT,
    attribute TEXT,
    sw TEXT,
    ew TEXT,
    le TEXT,
    ge TEXT,
    gt TEXT,
    lt TEXT,
    eq TEXT,
    inProperty TEXT,
    like TEXT,
    dateFilter TEXT,
    FOREIGN KEY (programStageWorkingList) REFERENCES ProgramStageWorkingList (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
);

-- Copy data from ItemFilter to the new tables
-- EventDataFilter (when eventFilter is not null)
INSERT INTO EventDataFilter (_id, eventFilter, dataItem, le, ge, gt, lt, eq, inProperty, like, dateFilter)
SELECT _id, eventFilter, dataItem, le, ge, gt, lt, eq, inProperty, like, dateFilter
FROM ItemFilter
WHERE eventFilter IS NOT NULL AND dataItem IS NOT NULL;

-- AttributeValueFilter (when trackedEntityInstanceFilter is not null)
INSERT INTO AttributeValueFilter (_id, trackedEntityInstanceFilter, attribute, sw, ew, le, ge, gt, lt, eq, inProperty, like, dateFilter)
SELECT _id, trackedEntityInstanceFilter, attribute, sw, ew, le, ge, gt, lt, eq, inProperty, like, dateFilter
FROM ItemFilter
WHERE trackedEntityInstanceFilter IS NOT NULL AND attribute IS NOT NULL;

-- ProgramStageWorkingListEventDataFilter (when programStageWorkingList is not null and dataItem is not null)
INSERT INTO ProgramStageWorkingListEventDataFilter (_id, programStageWorkingList, dataItem, le, ge, gt, lt, eq, inProperty, like, dateFilter)
SELECT _id, programStageWorkingList, dataItem, le, ge, gt, lt, eq, inProperty, like, dateFilter
FROM ItemFilter
WHERE programStageWorkingList IS NOT NULL AND dataItem IS NOT NULL;

-- ProgramStageWorkingListAttributeValueFilter (when programStageWorkingList is not null and attribute is not null)
INSERT INTO ProgramStageWorkingListAttributeValueFilter (_id, programStageWorkingList, attribute, sw, ew, le, ge, gt, lt, eq, inProperty, like, dateFilter)
SELECT _id, programStageWorkingList, attribute, sw, ew, le, ge, gt, lt, eq, inProperty, like, dateFilter
FROM ItemFilter
WHERE programStageWorkingList IS NOT NULL AND attribute IS NOT NULL;

-- Drop the original ItemFilter table
DROP TABLE ItemFilter;