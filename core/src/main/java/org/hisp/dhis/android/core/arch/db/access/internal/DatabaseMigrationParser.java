/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.arch.db.access.internal;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class DatabaseMigrationParser {

    private final AssetManager assetManager;

    DatabaseMigrationParser(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    List<List<String>> parseMigrations(int oldVersion, int newVersion) throws IOException {
        List<List<String>> scripts = new ArrayList<>();

        int startVersion = oldVersion + 1;
        for (int i = startVersion; i <= newVersion; i++) {
            scripts.add(this.parseMigration(i));
        }

        return scripts;
    }

    List<String> parseSnapshot(int version) throws IOException {
        return parseFile("snapshots", version);
    }

    private List<String> parseMigration(int version) throws IOException {
        return parseFile("migrations", version);
    }

    private List<String> parseFile(String directory, int newVersion) throws IOException {
        String fileName = directory + "/" + newVersion + ".sql";
        InputStream inputStream = assetManager.open(fileName);
        Scanner sc = new Scanner(inputStream, "UTF-8");
        List<String> lines = new ArrayList<>();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.length() > 1 && !line.contains("#")) {
                lines.add(line);
            }
        }
        sc.close();
        return lines;
    }
}