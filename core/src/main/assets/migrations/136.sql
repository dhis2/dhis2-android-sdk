# Rename to StockUseCases (ANDROSDK-1602);

CREATE TABLE StockUseCase (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, itemCode TEXT, itemDescription TEXT, programType TEXT, description TEXT, stockOnHand TEXT, FOREIGN KEY (uid) REFERENCES Program (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
CREATE TABLE StockUseCaseTransaction (_id INTEGER PRIMARY KEY AUTOINCREMENT, programUid TEXT NOT NULL, sortOrder INTEGER, transactionType TEXT, distributedTo TEXT, stockDistributed TEXT, stockDiscarded TEXT, stockCorrected TEXT, FOREIGN KEY (programUid) REFERENCES StockUseCase (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
