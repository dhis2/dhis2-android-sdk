# Creates the a table to save the number of the values to reserve for attribute
CREATE TABLE ReservedValueSetting (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT UNIQUE, numberOfValuesToReserve INTEGER, FOREIGN KEY (uid) REFERENCES TrackedEntityAttribute (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
