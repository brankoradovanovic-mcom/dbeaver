/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2026 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ext.oracle.tasks;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.ext.oracle.model.OracleSchedulerJob;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.sql.SQLUtils;

/**
 * Shared helpers for Oracle scheduler job SQL tools.
 */
final class OracleToolSchedulerJobUtils {

    private OracleToolSchedulerJobUtils() {
    }

    /**
     * Builds a PL/SQL string literal for {@code DBMS_SCHEDULER} {@code name=>} arguments.
     * Always double-quotes owner and job (SQL Developer style), then escapes the result as a
     * single-quoted string literal so apostrophes in identifiers cannot break the statement.
     */
    @NotNull
    static String getJobNameLiteral(@NotNull OracleSchedulerJob job) {
        String qualifiedName = DBUtils.getQuotedIdentifier(job.getDataSource(), job.getSchema().getName(), true, true)
            + "."
            + DBUtils.getQuotedIdentifier(job.getDataSource(), job.getName(), true, true);
        return SQLUtils.quoteString(job.getDataSource(), qualifiedName);
    }
}
