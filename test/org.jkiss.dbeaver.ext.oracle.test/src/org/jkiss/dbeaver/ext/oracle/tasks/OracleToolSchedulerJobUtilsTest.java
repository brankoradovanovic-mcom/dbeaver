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

import org.jkiss.dbeaver.ext.oracle.model.OracleDataSource;
import org.jkiss.dbeaver.ext.oracle.model.OracleSchedulerJob;
import org.jkiss.dbeaver.ext.oracle.model.OracleSchema;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.edit.DBEPersistAction;
import org.jkiss.dbeaver.model.exec.DBCSession;
import org.jkiss.junit.DBeaverUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

public class OracleToolSchedulerJobUtilsTest extends DBeaverUnitTest {

    private OracleDataSource dataSource;

    @Mock
    private DBCSession session;

    @BeforeEach
    public void setUp() {
        DBPDataSourceContainer container = configureTestContainer("oracle");
        dataSource = new OracleDataSource(container);
    }

    @Test
    public void getJobNameLiteral_quotesMixedCaseIdentifiers() {
        OracleSchedulerJob job = createJob("HrApp", "NightlySync");

        Assertions.assertEquals("'\"HrApp\".\"NightlySync\"'", OracleToolSchedulerJobUtils.getJobNameLiteral(job));
    }

    @Test
    public void getJobNameLiteral_quotesNamesWithSpaces() {
        OracleSchedulerJob job = createJob("MY SCHEMA", "my job");

        Assertions.assertEquals("'\"MY SCHEMA\".\"my job\"'", OracleToolSchedulerJobUtils.getJobNameLiteral(job));
    }

    @Test
    public void getJobNameLiteral_escapesDoubleQuotesInsideIdentifiers() {
        OracleSchedulerJob job = createJob("OWN\"ER", "JOB\"NAME");

        Assertions.assertEquals("'\"OWN\"\"ER\".\"JOB\"\"NAME\"'", OracleToolSchedulerJobUtils.getJobNameLiteral(job));
    }

    @Test
    public void getJobNameLiteral_escapesSingleQuotesInsideIdentifiers() {
        OracleSchedulerJob job = createJob("O'NEIL", "JOB'S");

        Assertions.assertEquals("'\"O''NEIL\".\"JOB''S\"'", OracleToolSchedulerJobUtils.getJobNameLiteral(job));
    }

    @Test
    public void enableTool_embedsEscapedJobNameLiteral() throws Exception {
        OracleSchedulerJob job = createJob("O'NEIL", "JOB'S");
        List<DBEPersistAction> queries = new ArrayList<>();

        new OracleToolSchedulerJobEnable().generateObjectQueries(
            session,
            new OracleToolSchedulerJobSettings(),
            queries,
            job
        );

        Assertions.assertEquals(1, queries.size());
        Assertions.assertEquals(
            "BEGIN\n  DBMS_SCHEDULER.ENABLE(name=>'\"O''NEIL\".\"JOB''S\"');\nEND;",
            queries.get(0).getScript()
        );
    }

    @Test
    public void disableTool_embedsEscapedJobNameLiteral() throws Exception {
        OracleSchedulerJob job = createJob("O'NEIL", "JOB'S");
        List<DBEPersistAction> queries = new ArrayList<>();

        new OracleToolSchedulerJobDisable().generateObjectQueries(
            session,
            new OracleToolSchedulerJobSettings(),
            queries,
            job
        );

        Assertions.assertEquals(1, queries.size());
        Assertions.assertEquals(
            "BEGIN\n  DBMS_SCHEDULER.DISABLE(name=>'\"O''NEIL\".\"JOB''S\"', force => TRUE);\nEND;",
            queries.get(0).getScript()
        );
    }

    private OracleSchedulerJob createJob(String schemaName, String jobName) {
        OracleSchema schema = new OracleSchema(dataSource, -1, schemaName);
        return new OracleSchedulerJob(schema, jobName, "SCHEDULED", null);
    }
}
