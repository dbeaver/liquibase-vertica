package liquibase.ext.vertica.snapshot;

import liquibase.database.Database;
import liquibase.ext.vertica.database.VerticaDatabase;
import liquibase.snapshot.SnapshotGenerator;
import liquibase.snapshot.jvm.SequenceSnapshotGenerator;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Schema;

public class SequenceSnapshotGeneratorVertica extends SequenceSnapshotGenerator {
	
	@Override
    public int getPriority(Class<? extends DatabaseObject> objectType, Database database) {
        if (database instanceof VerticaDatabase) {
        	int priority = super.getPriority(objectType, database);
            return priority += PRIORITY_DATABASE;
        }
        return PRIORITY_NONE;
    }

    @Override
    public Class<? extends SnapshotGenerator>[] replaces() {
        return new Class[] { SequenceSnapshotGenerator.class };
    }

    @Override
    protected String getSelectSequenceSql(Schema schema, Database database) {
    	if (database instanceof VerticaDatabase) {
    		return "SELECT\n" +
                    "SEQUENCE_NAME,\n" +
                    "minimum AS MIN_VALUE, " +
                    "maximum AS MAX_VALUE, " +
                    "INCREMENT_BY,\n" +
                    "allow_cycle AS WILL_CYCLE\n" +
                    "FROM v_catalog.sequences s\n" +
                    "WHERE\n" +
                    "s.sequence_schema ='" + schema.getName() + "'";
        }
        return super.getSelectSequenceSql(schema, database);
    }

}
