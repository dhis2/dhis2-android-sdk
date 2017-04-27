package org.hisp.dhis.android.core.program;


import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.program.ProgramStageSectionDataElementLinkModel.Columns;

import static org.hisp.dhis.android.core.utils.StoreUtils.nonNull;
import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public class ProgramStageSectionDataElementLinkStoreImpl implements ProgramStageSectionDataElementLinkStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " +
            ProgramStageSectionDataElementLinkModel.TABLE + " (" +
            Columns.PROGRAM_STAGE_SECTION + ", " + Columns.DATA_ELEMENT + ") " +
            "VALUES(?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " +
            ProgramStageSectionDataElementLinkModel.TABLE + " SET " +
            Columns.PROGRAM_STAGE_SECTION + "=?, " + Columns.DATA_ELEMENT + "=? " +
            " WHERE " + Columns.PROGRAM_STAGE_SECTION + "=? " + " AND " + Columns.DATA_ELEMENT + "=?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;

    public ProgramStageSectionDataElementLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
    }

    @Override
    public Long insert(@NonNull String programStageSection, @NonNull String dataElement) {

        nonNull(programStageSection);
        nonNull(dataElement);
        sqLiteBind(insertStatement, 1, programStageSection);
        sqLiteBind(insertStatement, 2, dataElement);

        Long result = insertStatement.executeInsert();
        insertStatement.clearBindings();
        return result;
    }


    @Override
    public int update(@NonNull String programStageSection, @NonNull String dataElement,
                      @NonNull String whereProgramStageSection, @NonNull String whereDataElement) {

        nonNull(programStageSection);
        nonNull(dataElement);
        nonNull(whereProgramStageSection);
        nonNull(whereDataElement);

        sqLiteBind(updateStatement, 1, programStageSection);
        sqLiteBind(updateStatement, 2, dataElement);
        sqLiteBind(updateStatement, 3, whereProgramStageSection);
        sqLiteBind(updateStatement, 4, whereDataElement);

        int result = updateStatement.executeUpdateDelete();
        updateStatement.clearBindings();
        return result;
    }
}
