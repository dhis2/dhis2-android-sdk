# Missing Visualization (ANDROSDK-1692)

DELETE FROM AnalyticsDhisVisualization where uid NOT IN (SELECT uid FROM Visualization);
