#  (ANDROSDK-XXX)

DELETE FROM AnalyticsDhisVisualization where uid NOT IN (SELECT uid FROM Visualization);
