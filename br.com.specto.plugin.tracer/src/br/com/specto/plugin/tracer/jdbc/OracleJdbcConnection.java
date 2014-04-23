package br.com.specto.plugin.tracer.jdbc;


public class OracleJdbcConnection extends JdbcConnection {

	public OracleJdbcConnection(String driverName, String connectionString,	String userName, String password) {
		super(driverName, connectionString, userName, password);
	}
	@Override
	protected String getNextConnectorIdCommand() {		
		return "select connector_id_seq.nextval as " + JdbcConnection.NEXT_ID_COLUMN_ALIAS + " from dual";
	}
}
