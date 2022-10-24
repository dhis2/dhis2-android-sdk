# Remove NOT NULL constraint in categoryOption (ANDROSDK-1548);

ALTER TABLE VisualizationCategoryDimensionLink RENAME TO VisualizationCategoryDimensionLink_Old;
CREATE TABLE VisualizationCategoryDimensionLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, visualization TEXT NOT NULL, category TEXT NOT NULL, categoryOption TEXT, FOREIGN KEY (category) REFERENCES Category (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (visualization) REFERENCES Visualization (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (categoryOption) REFERENCES CategoryOption (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO VisualizationCategoryDimensionLink (_id, visualization, category, categoryOption) SELECT _id, visualization, category, categoryOption FROM VisualizationCategoryDimensionLink_Old;
DROP TABLE IF EXISTS VisualizationCategoryDimensionLink_Old;