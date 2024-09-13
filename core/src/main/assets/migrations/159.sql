# Add external map layers (ANDROSDK-1800) and migrate map layer keys (ANDROSDK-1803)

ALTER TABLE MapLayer ADD COLUMN code TEXT;
ALTER TABLE MapLayer ADD COLUMN mapService TEXT;
ALTER TABLE MapLayer ADD COLUMN imageFormat TEXT;
ALTER TABLE MapLayer ADD COLUMN layers TEXT;

UPDATE MapLayer SET uid = 'osmLight' WHERE uid = 'l7rimUxoQu4';
UPDATE MapLayerImageryProvider SET mapLayer = 'osmLight' WHERE mapLayer = 'l7rimUxoQu4';

UPDATE MapLayer SET uid = 'openStreetMap' WHERE uid = 'k6QEWMytadd';
UPDATE MapLayerImageryProvider SET mapLayer = 'openStreetMap' WHERE mapLayer = 'k6QEWMytadd';

UPDATE MapLayer SET uid = 'bingLight' WHERE uid = 'ql5jVkAL1iy';
UPDATE MapLayerImageryProvider SET mapLayer = 'bingLight' WHERE mapLayer = 'ql5jVkAL1iy';

UPDATE MapLayer SET uid = 'bingDark' WHERE uid = 'PwJ1fQoTthh';
UPDATE MapLayerImageryProvider SET mapLayer = 'bingDark' WHERE mapLayer = 'PwJ1fQoTthh';

UPDATE MapLayer SET uid = 'bingAerial' WHERE uid = 'kKJNmY2yYtM';
UPDATE MapLayerImageryProvider SET mapLayer = 'bingAerial' WHERE mapLayer = 'kKJNmY2yYtM';

UPDATE MapLayer SET uid = 'bingHybrid' WHERE uid = 'TfK2zM71AHJ';
UPDATE MapLayerImageryProvider SET mapLayer = 'bingHybrid' WHERE mapLayer = 'TfK2zM71AHJ';