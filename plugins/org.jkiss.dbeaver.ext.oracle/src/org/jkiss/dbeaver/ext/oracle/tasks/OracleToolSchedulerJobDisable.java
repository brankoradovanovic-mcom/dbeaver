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
import org.jkiss.dbeaver.model.edit.DBEPersistAction;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.DBCSession;
import org.jkiss.dbeaver.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.dbeaver.model.sql.task.SQLToolExecuteHandler;

import java.util.List;

public class OracleToolSchedulerJobDisable extends SQLToolExecuteHandler<OracleSchedulerJob, OracleToolSchedulerJobSettings> {

    @NotNull
    @Override
    public OracleToolSchedulerJobSettings createToolSettings() {
        return new OracleToolSchedulerJobSettings();
    }

    @Override
    public void generateObjectQueries(
        DBCSession session,
        OracleToolSchedulerJobSettings settings,
        List<DBEPersistAction> queries,
        OracleSchedulerJob object
    ) throws DBCException {
        // END; is required for execution (getScript is run as-is). generateScript() may show END;; in preview.
        String sql = "BEGIN\n" +
            "  DBMS_SCHEDULER.DISABLE(name=>" + OracleToolSchedulerJobUtils.getJobNameLiteral(object) + ", force => TRUE);\n" +
            "END;";
        queries.add(new SQLDatabasePersistAction(sql));
    }

    @Override
    public boolean needsRefreshOnFinish() {
        return true;
    }
}
