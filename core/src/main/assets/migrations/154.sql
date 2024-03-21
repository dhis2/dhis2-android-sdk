# Make dimensionItem not mandatory (ANDROSDK-1790)

ALTER TABLE VisualizationDimensionItem RENAME TO VisualizationDimensionItem_Old;
CREATE TABLE VisualizationDimensionItem(_id INTEGER PRIMARY KEY AUTOINCREMENT, visualization TEXT NOT NULL, position TEXT NOT NULL, dimension TEXT NOT NULL, dimensionItem TEXT, dimensionItemType TEXT, FOREIGN KEY (visualization) REFERENCES Visualization (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO VisualizationDimensionItem(_id, visualization, position, dimension, dimensionItem, dimensionItemType) SELECT _id, visualization, position, dimension, dimensionItem, dimensionItemType FROM VisualizationDimensionItem_Old;
DROP TABLE IF EXISTS VisualizationDimensionItem_Old;
