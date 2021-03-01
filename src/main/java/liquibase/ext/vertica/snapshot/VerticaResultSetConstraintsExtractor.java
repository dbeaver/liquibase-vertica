package liquibase.ext.vertica.snapshot;

import java.sql.SQLException;
import java.util.List;

import liquibase.CatalogAndSchema;
import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.snapshot.CachedRow;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.VerticaResultSetCache;
import liquibase.snapshot.VerticaResultSetCache.RowData;
import liquibase.snapshot.VerticaResultSetCache.SingleResultSetExtractor;
import liquibase.structure.core.Schema;

public class VerticaResultSetConstraintsExtractor extends SingleResultSetExtractor {
	
    private Database database;
    private String catalogName;
    private String schemaName;
    private String tableName;

	public VerticaResultSetConstraintsExtractor(DatabaseSnapshot databaseSnapshot, String catalogName, String schemaName,
            String tableName) {
		super(databaseSnapshot.getDatabase());
        this.database = databaseSnapshot.getDatabase();
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.tableName = tableName;
	}

	@Override
	public List<CachedRow> fastFetchQuery() throws SQLException, DatabaseException {
		 CatalogAndSchema catalogAndSchema = new CatalogAndSchema(this.catalogName, this.schemaName)
	                .customize(this.database);

	        return executeAndExtract(
	                createSql(((AbstractJdbcDatabase) this.database).getJdbcCatalogName(catalogAndSchema),
	                        ((AbstractJdbcDatabase) this.database).getJdbcSchemaName(catalogAndSchema), this.tableName),
	                this.database, false);
	}

	@Override
	public List<CachedRow> bulkFetchQuery() throws SQLException, DatabaseException {
		CatalogAndSchema catalogAndSchema = new CatalogAndSchema(this.catalogName, this.schemaName)
                .customize(this.database);

        return executeAndExtract(
                createSql(((AbstractJdbcDatabase) this.database).getJdbcCatalogName(catalogAndSchema),
                        ((AbstractJdbcDatabase) this.database).getJdbcSchemaName(catalogAndSchema), null),
                this.database);
	}
	
	private String createSql(String catalog, String schema, String table) {
        CatalogAndSchema catalogAndSchema = new CatalogAndSchema(catalog, schema).customize(this.database);

        String jdbcSchemaName = this.database.correctObjectName(
                ((AbstractJdbcDatabase) this.database).getJdbcSchemaName(catalogAndSchema), Schema.class);

        String sql = "SELECT CONSTRAINT_NAME, COLUMN_NAME FROM v_catalog.constraint_columns cc " +
                "where table_schema='" + jdbcSchemaName
                + "' and constraint_type='u'";
        if (table != null) {
            sql += " and table_name='" + table + "'";
        }

        return sql;
    }

	@Override
	public boolean bulkContainsSchema(String schemaKey) {
		return false;
	}

	@Override
	public RowData rowKeyParameters(CachedRow row) {
		return new VerticaResultSetCache.RowData(this.catalogName, this.schemaName, this.database,
                row.getString("table_name"));
	}

	@Override
	public RowData wantedKeyParameters() {
		return new VerticaResultSetCache.RowData(this.catalogName, this.schemaName, this.database, this.tableName);
	}

}
