# Change DataInputPeriod primary key from composite (dataSet, period, openingDate, closingDate) to auto-incremental _id

ALTER TABLE DataInputPeriod RENAME TO DataInputPeriod_Old;

CREATE TABLE DataInputPeriod(
    _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    dataSet TEXT NOT NULL,
    period TEXT NOT NULL,
    openingDate TEXT,
    closingDate TEXT,
    FOREIGN KEY(dataSet) REFERENCES DataSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    FOREIGN KEY(period) REFERENCES Period(periodId) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
);

INSERT INTO DataInputPeriod(_id, dataSet, period, openingDate, closingDate)
SELECT NULL, dataSet, period, openingDate, closingDate FROM DataInputPeriod_Old;

DROP TABLE DataInputPeriod_Old;
