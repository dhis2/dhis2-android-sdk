/*
 *  Copyright (c) 2004-2025, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import java.io.File
import java.io.OutputStream

class MigrationProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    private val outputPackage = "org.hisp.dhis.android.persistence.db.migrations"
    private val migrationDir = File(options["migrationDir"]!!).canonicalFile
    var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }
        if (!migrationDir.exists()) {
            logger.error("Migration directory not found: $migrationDir")
            return emptyList()
        }

        val files = migrationDir.listFiles { f -> f.extension == "sql" }?.sortedBy {
            it.nameWithoutExtension.toIntOrNull() ?: Int.MAX_VALUE
        } ?: emptyList()

        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = false),
            packageName = outputPackage,
            fileName = "RoomGeneratedMigrations"
        )

        file += "package $outputPackage\n\n"
        file += "import androidx.room.migration.Migration\n"
        file += "import androidx.sqlite.SQLiteConnection\n"
        file += "import androidx.sqlite.db.SupportSQLiteDatabase\n"
        file += "import androidx.sqlite.execSQL\n\n"
        file += "internal object RoomGeneratedMigrations {\n"

        val migrationNames = mutableListOf<String>()

        for (f in files) {
            val next = f.nameWithoutExtension.toIntOrNull() ?: continue
            val version = next - 1
            val name = "MIGRATION_${version}_${next}"
            migrationNames += name

            val lines = f.readLines()
                .map { it.trim() }
                .filter { !it.startsWith("#") }
                .filter { it.isNotBlank() }

            file += "    val $name = object : Migration($version, $next) {\n"
            file += "        override fun migrate(db: SupportSQLiteDatabase) {\n"
            lines.forEach { line ->
                file += "            db.execSQL(\"${escapeSqlLine(line)}\")\n"
            }
            file += "        }\n"
            file += "        override fun migrate(connection: SQLiteConnection) {\n"
            lines.forEach { line ->
                file += "            connection.execSQL(\"${escapeSqlLine(line)}\")\n"
            }
            file += "        }\n"
            file += "    }\n\n"
        }

        file += "    val ALL_MIGRATIONS = listOf(\n"
        file += migrationNames.joinToString(",\n") { "        $it" } + "\n"
        file += "    )\n"

        file += "}\n"

        file.close()
        invoked = true
        return emptyList()
    }

    private operator fun OutputStream.plusAssign(str: String) {
        write(str.toByteArray())
    }

    private fun escapeSqlLine(line: String): String {
        return line.replace("\"", "\\\"")
    }
}
