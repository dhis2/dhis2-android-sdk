ALTER TABLE DataElementOperand RENAME TO DataElementOperand_Old;
CREATE TABLE DataElementOperand (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, dataElement TEXT, categoryOptionCombo TEXT, FOREIGN KEY (dataElement) REFERENCES DataElement (uid) DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (categoryOptionCombo) REFERENCES CategoryOptionCombo (uid) DEFERRABLE INITIALLY DEFERRED);
INSERT INTO DataElementOperand (_id, uid, dataElement, categoryOptionCombo) SELECT _id, uid, dataElement, categoryOptionCombo FROM DataElementOperand_Old;
DROP TABLE DataElementOperand_Old;