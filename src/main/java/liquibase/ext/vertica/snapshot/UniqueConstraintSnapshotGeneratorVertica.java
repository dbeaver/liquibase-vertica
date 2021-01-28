package liquibase.ext.vertica.snapshot;

import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.ext.vertica.database.VerticaDatabase;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.jvm.UniqueConstraintSnapshotGenerator;
import liquibase.structure.DatabaseObject;

public class UniqueConstraintSnapshotGeneratorVertica extends UniqueConstraintSnapshotGenerator{
    @Override
    public int getPriority(Class<? extends DatabaseObject> objectType, Database database) {
        if (database instanceof VerticaDatabase)
            return PRIORITY_DATABASE;
        return PRIORITY_NONE;
    }

    @Override
    protected DatabaseObject snapshotObject(DatabaseObject example, DatabaseSnapshot snapshot) throws DatabaseException {
        return null;
    }

    @Override
    protected void addTo(DatabaseObject foundObject, DatabaseSnapshot snapshot) throws DatabaseException {
    }
}
