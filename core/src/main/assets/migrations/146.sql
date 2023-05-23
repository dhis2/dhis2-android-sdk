# Add Calculation support (ANDROSDK-1687)

CREATE TABLE ExpressionDimensionItem (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, expression TEXT);

DROP TABLE IF EXISTS Visualization;
CREATE TABLE Visualization (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, description TEXT, displayDescription TEXT, displayFormName TEXT, title TEXT, displayTitle TEXT, subtitle TEXT, displaySubtitle TEXT, type TEXT, hideTitle INTEGER, hideSubtitle INTEGER, hideEmptyColumns INTEGER, hideEmptyRows INTEGER, hideEmptyRowItems TEXT, hideLegend INTEGER, showHierarchy INTEGER, rowTotals INTEGER, rowSubTotals INTEGER, colTotals INTEGER, colSubTotals INTEGER, showDimensionLabels INTEGER, percentStackedValues INTEGER, noSpaceBetweenColumns INTEGER, skipRounding INTEGER, displayDensity TEXT, digitGroupSeparator TEXT, legendShowKey TEXT, legendStyle TEXT, legendSetId TEXT, legendStrategy TEXT, aggregationType TEXT);

CREATE TABLE VisualizationDimensionItem(_id INTEGER PRIMARY KEY AUTOINCREMENT, visualization TEXT NOT NULL, position TEXT NOT NULL, dimension TEXT NOT NULL, dimensionItem TEXT NOT NULL, dimensionItemType TEXT, FOREIGN KEY (visualization) REFERENCES Visualization (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
