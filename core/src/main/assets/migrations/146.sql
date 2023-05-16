# Add Calculation support (ANDROSDK-1687)

CREATE TABLE ExpressionDimensionItem (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, expression TEXT);