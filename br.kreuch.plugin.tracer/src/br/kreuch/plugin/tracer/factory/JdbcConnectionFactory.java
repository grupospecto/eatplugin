package br.kreuch.plugin.tracer.factory;

import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import br.kreuch.plugin.tracer.jdbc.JdbcConnection;
import br.kreuch.plugin.tracer.jdbc.MySqlJdbcConnection;
import br.kreuch.plugin.tracer.jdbc.OracleJdbcConnection;
import br.kreuch.plugin.tracer.util.ResourceLoader;

public class JdbcConnectionFactory {
	private final static String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	private final static String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	
	private static JdbcConnection JDBC_CONNECTION;
	
	private static void createJdbcConnection(){
		JOptionPane.showMessageDialog(null, "Vou criar JDBC");
		Map<String, String> properties = ResourceLoader.getProperties("src/resources/jdbc.properties");
		
		for (Iterator<String> it = properties.keySet().iterator(); it.hasNext();){
			JOptionPane.showMessageDialog(null, it.next());
		}
		
		String driverClassName  = properties.get("datasource.jdbc.driverClassName");
		String databaseHost     = properties.get("datasource.jdbc.url");
		String databaseLogin    = properties.get("datasource.jdbc.usr");
		String databasePassword = properties.get("datasource.jdbc.passwd");
	
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
