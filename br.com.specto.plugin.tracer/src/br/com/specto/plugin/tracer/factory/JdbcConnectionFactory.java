package br.com.specto.plugin.tracer.factory;

import java.util.Properties;

import br.com.specto.plugin.tracer.jdbc.JdbcConnection;
import br.com.specto.plugin.tracer.jdbc.MySqlJdbcConnection;
import br.com.specto.plugin.tracer.jdbc.OracleJdbcConnection;
import br.com.specto.plugin.tracer.util.ResourceLoader;

public class JdbcConnectionFactory {
	private final static String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	private final static String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	
	private static JdbcConnection JDBC_CONNECTION;
	
	private static void createJdbcConnection(){
		Properties properties = ResourceLoader.getProperties("resources/jdbc.properties");
		
		String driverClassName  = (String) properties.get("datasource.jdbc.driverClassName");
		String databaseHost     = (String) properties.get("datasource.jdbc.url");
		String databaseLogin    = (String) properties.get("datasource.jdbc.usr");
		String databasePassword = (String) properties.get("datasource.jdbc.passwd");

		if (driverClassName.equals(ORACLE_DRIVER)){
			JDBC_CONNECTION = new OracleJdbcConnection(driverClassName, databaseHost, databaseLogin, databasePassword);
		} else if (driverClassName.equals(MYSQL_DRIVER)){
			JDBC_CONNECTION = new MySqlJdbcConnection(driverClassName, databaseHost, databaseLogin, databasePassword);
		}
	}
	
	public static JdbcConnection getJdbcConnection(){
		if (JDBC_CONNECTION == null){
			JdbcConnectionFactory.createJdbcConnection();
		}

		return JdbcConnectionFactory.JDBC_CONNECTION;
	}
}
