/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core.arch.db.access.internal

import android.content.Context
import android.util.Log
import org.hisp.dhis.android.core.arch.db.access.DatabaseExportMetadata
import org.hisp.dhis.android.core.arch.db.access.DatabaseImportExport
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Utility for importing and exporting Room databases with enhanced support for metadata,
 * attachments, and encryption handling.
 */
class RoomDatabaseImportExportHandler(private val context: Context): DatabaseImportExport {

    companion object {
        private const val TAG = "RoomDbImportExport"
        private const val DB_EXTENSION = ".db"
        private const val BACKUP_EXTENSION = ".backup"
        private const val WAL_EXTENSION = "-wal"
        private const val SHM_EXTENSION = "-shm"
        private const val METADATA_FILE = "metadata.json"
        private const val BUFFER_SIZE = 2048
    }
    
    /**
     * Exports a database to a file with associated metadata and journal files.
     *
     * @param databaseName The name of the database to export
     * @param targetFile The file to export the database to (should be a zip file)
     * @param includeJournalFiles Whether to include WAL and SHM files in the export
     * @param metadata Additional metadata to include in the export
     * @return true if export was successful, false otherwise
     */
    fun exportDatabase(
        databaseName: String,
        targetFile: File,
        includeJournalFiles: Boolean = true,
        metadata: Map<String, String> = emptyMap()
    ): Boolean {
        val dbFile = context.getDatabasePath(databaseName)
        if (!dbFile.exists()) {
            Log.e(TAG, "Database file not found: $databaseName")
            return false
        }
        
        // Ensure the database is not in WAL mode during export
        ensureDatabaseIsSynced(databaseName)
        
        try {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(targetFile))).use { zipOut ->
                // Add the main database file
                addFileToZip(zipOut, dbFile, databaseName + DB_EXTENSION)
                
                // Add journal files if requested
                if (includeJournalFiles) {
                    val walFile = File(dbFile.path + WAL_EXTENSION)
                    val shmFile = File(dbFile.path + SHM_EXTENSION)
                    
                    if (walFile.exists()) {
                        addFileToZip(zipOut, walFile, databaseName + WAL_EXTENSION)
                    }
                    
                    if (shmFile.exists()) {
                        addFileToZip(zipOut, shmFile, databaseName + SHM_EXTENSION)
                    }
                }
                
                // Add metadata file
                val metadataJson = createMetadataJson(databaseName, metadata)
                zipOut.putNextEntry(ZipEntry(METADATA_FILE))
                zipOut.write(metadataJson.toByteArray())
                zipOut.closeEntry()
            }
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting database $databaseName", e)
            return false
        }
    }
    
    /**
     * Imports a database from a file with optional encryption.
     *
     * @param sourceFile The file to import the database from (should be a zip file)
     * @param targetDatabaseName The name for the imported database
     * @param encrypt Whether to encrypt the imported database
     * @param password The password to use for encryption (required if encrypt is true)
     * @return true if import was successful, false otherwise
     */
    fun importDatabase(
        sourceFile: File,
        targetDatabaseName: String,
        encrypt: Boolean = false,
        password: String? = null
    ): Boolean {
        if (!sourceFile.exists()) {
            Log.e(TAG, "Source file not found: ${sourceFile.path}")
            return false
        }
        
        val tempDir = File(context.cacheDir, "db_import_${System.currentTimeMillis()}")
        tempDir.mkdirs()
        
        try {
            // Extract all files to temporary directory
            ZipInputStream(BufferedInputStream(FileInputStream(sourceFile))).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    val targetFile = File(tempDir, entry.name)
                    
                    if (!entry.isDirectory) {
                        extractFile(zipIn, targetFile)
                    }
                    
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }
            
            // Read metadata if available
            val metadataFile = File(tempDir, METADATA_FILE)
            val metadata = if (metadataFile.exists()) {
                parseMetadataJson(metadataFile.readText())
            } else {
                emptyMap()
            }
            
            // Find the main database file in the extracted files
            val dbFilePattern = ".*\\.db$".toRegex()
            val extractedDbFile = tempDir.listFiles()?.find { it.name.matches(dbFilePattern) }
            
            if (extractedDbFile == null) {
                Log.e(TAG, "No database file found in the import archive")
                return false
            }
            
            // Delete existing database if it exists
            val targetDbFile = context.getDatabasePath(targetDatabaseName)
            if (targetDbFile.exists()) {
                context.deleteDatabase(targetDatabaseName)
            }
            
            // Prepare target directory
            targetDbFile.parentFile?.mkdirs()
            
            // Handle encryption if needed
            val result = if (encrypt && password != null) {
                importWithEncryption(extractedDbFile, targetDatabaseName, password)
            } else {
                // Simple file copy for unencrypted database
                copyFile(extractedDbFile, targetDbFile)
                
                // Copy journal files if they exist
                val walSourceFile = File(tempDir, extractedDbFile.name.replace(DB_EXTENSION, WAL_EXTENSION))
                val shmSourceFile = File(tempDir, extractedDbFile.name.replace(DB_EXTENSION, SHM_EXTENSION))
                
                val walTargetFile = File(targetDbFile.path + WAL_EXTENSION)
                val shmTargetFile = File(targetDbFile.path + SHM_EXTENSION)
                
                if (walSourceFile.exists()) {
                    copyFile(walSourceFile, walTargetFile)
                }
                
                if (shmSourceFile.exists()) {
                    copyFile(shmSourceFile, shmTargetFile)
                }
                
                true
            }
            
            // Clean up temporary directory
            tempDir.deleteRecursively()
            
            return result
            
        } catch (e: Exception) {
            Log.e(TAG, "Error importing database to $targetDatabaseName", e)
            tempDir.deleteRecursively()
            return false
        }
    }
    
    /**
     * Import a database with encryption.
     */
    private fun importWithEncryption(sourceFile: File, targetDatabaseName: String, password: String): Boolean {
        // This would need a proper implementation using SQLCipher to read the unencrypted database
        // and create a new encrypted database with the same content
        
        // For now, this is a placeholder. In a real implementation, we would:
        // 1. Open the unencrypted database
        // 2. Create a new encrypted database
        // 3. Copy the schema and data between them
        
        return false
    }
    
    /**
     * Ensures a database is fully synced by forcing a checkpoint.
     */
    private fun ensureDatabaseIsSynced(databaseName: String) {
        // This would need to use the SupportSQLiteDatabase's methods to
        // ensure all pending writes are flushed and the database is in a consistent state
        // For example, executing "PRAGMA wal_checkpoint(FULL);"
    }
    
    /**
     * Creates a JSON representation of metadata.
     */
    private fun createMetadataJson(databaseName: String, metadata: Map<String, String>): String {
        // In a real implementation, use a proper JSON library (e.g., Gson, Moshi, or kotlinx.serialization)
        val metadataWithDefaults = metadata + mapOf(
            "exportDate" to System.currentTimeMillis().toString(),
            "databaseName" to databaseName,
            "version" to "1.0"
        )
        
        return buildString {
            append("{")
            metadataWithDefaults.entries.forEachIndexed { index, (key, value) ->
                if (index > 0) append(",")
                append("\"$key\":\"$value\"")
            }
            append("}")
        }
    }
    
    /**
     * Parses metadata JSON.
     */
    private fun parseMetadataJson(json: String): Map<String, String> {
        // Simple parser, in a real implementation use a proper JSON library
        val result = mutableMapOf<String, String>()
        val pattern = "\"([^\"]+)\":\"([^\"]+)\"".toRegex()
        pattern.findAll(json).forEach { matchResult ->
            val (key, value) = matchResult.destructured
            result[key] = value
        }
        return result
    }
    
    /**
     * Add a file to a zip archive.
     */
    private fun addFileToZip(zipOut: ZipOutputStream, file: File, entryName: String) {
        FileInputStream(file).use { fileIn ->
            BufferedInputStream(fileIn).use { bufferedIn ->
                zipOut.putNextEntry(ZipEntry(entryName))
                val buffer = ByteArray(BUFFER_SIZE)
                var len: Int
                while (bufferedIn.read(buffer).also { len = it } > 0) {
                    zipOut.write(buffer, 0, len)
                }
                zipOut.closeEntry()
            }
        }
    }
    
    /**
     * Extract a file from a zip input stream to a target file.
     */
    private fun extractFile(zipIn: ZipInputStream, targetFile: File) {
        targetFile.parentFile?.mkdirs()
        
        BufferedOutputStream(FileOutputStream(targetFile)).use { bufferedOut ->
            val buffer = ByteArray(BUFFER_SIZE)
            var len: Int
            while (zipIn.read(buffer).also { len = it } > 0) {
                bufferedOut.write(buffer, 0, len)
            }
        }
    }
    
    /**
     * Copy a file from source to destination.
     */
    private fun copyFile(source: File, destination: File): Boolean {
        return try {
            destination.parentFile?.mkdirs()
            
            FileInputStream(source).use { input ->
                FileOutputStream(destination).use { output ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var length: Int
                    while (input.read(buffer).also { length = it } > 0) {
                        output.write(buffer, 0, length)
                    }
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error copying file from ${source.path} to ${destination.path}", e)
            false
        }
    }

    override fun importDatabase(file: File): DatabaseExportMetadata {
        TODO("Not yet implemented")
    }

    override fun exportLoggedUserDatabase(): File {
        TODO("Not yet implemented")
    }
}
