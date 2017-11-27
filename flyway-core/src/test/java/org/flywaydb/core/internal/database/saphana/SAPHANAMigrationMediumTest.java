/*
 * Copyright 2010-2017 Boxfuse GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flywaydb.core.internal.database.saphana;

import org.flywaydb.core.DbCategory;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.internal.util.jdbc.DriverDataSource;
import org.flywaydb.core.migration.ConcurrentMigrationTestCase;
import org.flywaydb.core.migration.MigrationTestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.sql.DataSource;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

/**
 * Test to demonstrate the migration functionality using SAP HANA.
 *
 * Testing with SAP HANA 1
 * -----------------------
 * 1. Launch SAP HANA One on SUSE Linux Enterprise Server 11 SP 4 from AWS Marketplace (ami-28b25547) with known Keypair
 * 2. Open all ports in newly created security group
 * 3. Log in at https://instanceip
 * 4. Fill in AWS Access Key and Secret Key
 * 5. Pick SAPhana1 for hdbadm and SYSTEM passwords
 * 6. Wait for HANA instance to start (40-50 minutes!)
 * 7. Add license (and fill in credentials!)
 * 8. Connect to jdbc:sap://instanceip:30015/ with SYSTEM / SAPhana1
 * 9. Execute CREATE USER flyway PASSWORD SAPhana1 NO FORCE_FIRST_PASSWORD_CHANGE
 * 10. Connect to jdbc:sap://instanceip:30015/ with flyway / SAPhana1
 * 11. Run tests
 * 12. Terminate instance in AWS Console
 * 13. Delete remaining volumes in AWS Console
 */
@Category(DbCategory.SAPHANA.class)
public class SAPHANAMigrationMediumTest extends MigrationTestCase {
    private static final String JDBC_URL = "jdbc:sap://localhost:39013/?databaseName=HXE";
    private static final String JDBC_USER = "SYSTEM";
    private static final String JDBC_PASSWORD = "HXEHana1";

    @Override
    protected void ensureTestEnabled() {
        //assumeTrue(Boolean.valueOf(System.getProperty("flyway.test.saphana")));
    }

    @Override
    protected DataSource createDataSource(Properties customProperties) {
        return new DriverDataSource(Thread.currentThread().getContextClassLoader(), null,
                JDBC_URL, JDBC_USER, JDBC_PASSWORD, null
                //, "CREATE USER flywaydb1 PASSWORD SAPhana1 NO FORCE_FIRST_PASSWORD_CHANGE"
                );
    }

    protected String getBasedir() {
        return "migration/database/saphana/sql/base";
    }

    @Override
    protected String getQuoteLocation() {
        return "migration/quote";
    }

    @Test
    public void concurrent() throws Exception {
        ConcurrentMigrationTestCase testCase = new ConcurrentMigrationTestCase() {
            @Override
            protected DataSource createDataSource(Properties customProperties) {
                return new DriverDataSource(Thread.currentThread().getContextClassLoader(), null,
                        JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            }

            @Override
            protected String getSchemaName() {
                return JDBC_USER.toUpperCase();
            }

            @Override
            protected String getBasedir() {
                return "migration/database/saphana/sql/base";
            }
        };

        testCase.setUp();
        testCase.migrateConcurrently();
    }

    @Test
    public void sequence() throws Exception {
        flyway.setLocations("migration/database/saphana/sql/sequence");
        flyway.migrate();

        MigrationVersion version = flyway.info().current().getVersion();
        assertEquals("1", version.toString());
        assertEquals("Sequence", flyway.info().current().getDescription());

        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void trigger() throws Exception {
        flyway.setLocations("migration/database/saphana/sql/trigger");
        flyway.migrate();

        MigrationVersion version = flyway.info().current().getVersion();
        assertEquals("1", version.toString());
        assertEquals("Trigger", flyway.info().current().getDescription());

        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void index() throws Exception {
        flyway.setLocations("migration/database/saphana/sql/index");
        flyway.migrate();

        MigrationVersion version = flyway.info().current().getVersion();
        assertEquals("1", version.toString());
        assertEquals("Index", flyway.info().current().getDescription());

        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void block() throws Exception {
        flyway.setLocations("migration/database/saphana/sql/block");
        assertEquals(1, flyway.migrate());
    }

    @Test
    public void statistics() throws Exception {
        flyway.setLocations("migration/database/saphana/sql/statistics");
        flyway.migrate();

        MigrationVersion version = flyway.info().current().getVersion();
        assertEquals("1", version.toString());
        assertEquals("Statistics", flyway.info().current().getDescription());

        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void synonym() throws Exception {
        flyway.setLocations("migration/database/saphana/sql/synonym");
        flyway.migrate();

        MigrationVersion version = flyway.info().current().getVersion();
        assertEquals("1", version.toString());
        assertEquals("Synonym", flyway.info().current().getDescription());

        flyway.clean();
        flyway.migrate();
    }

    @Test
    @Ignore("Ignored as SAP HANA demo lacks create schema permissions")
    public void migrateMultipleSchemas() throws Exception {
    }

    @Test
    @Ignore("Ignored as SAP HANA demo lacks create schema permissions")
    public void setCurrentSchema() throws Exception {
    }

    @Test
    @Ignore("Ignored due SAP HANA syntax compatibility issues")
    public void subDir() {
    }

    @Test
    @Ignore("Ignored due SAP HANA syntax compatibility issues")
    public void outOfOrderMultipleRankIncrease() {
    }
}