# Refactor stockCorrected by stockCount (ANDROSDK-1638);

DELETE FROM StockUseCaseTransaction;
DELETE FROM StockUseCase;

DROP TABLE IF EXISTS StockUseCaseTransaction;
CREATE TABLE StockUseCaseTransaction (_id INTEGER PRIMARY KEY AUTOINCREMENT, programUid TEXT NOT NULL, sortOrder INTEGER, transactionType TEXT, distributedTo TEXT, stockDistributed TEXT, stockDiscarded TEXT, stockCount TEXT, FOREIGN KEY (programUid) REFERENCES StockUseCase (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);