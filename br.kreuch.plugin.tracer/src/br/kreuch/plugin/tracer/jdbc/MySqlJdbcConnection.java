package br.kreuch.plugin.tracer.jdbc;


public class MySqlJdbcConnection extends JdbcConnection {

	public MySqlJdbcConnection(String driverName, String connectionString,	String userName, String password) {
		super(driverName, connectionString, userName, password);
	}
	@Override
	protected String getNextConnectorIdCommand() {
		return "select Auto_increment from information_schema.tables where table_name='" + CONNECTOR_TABLE + "'";
	}
}
