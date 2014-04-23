package br.com.specto.plugin.tracer.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class JdbcConnection {
	final protected static String OBJECT_ID_COLUMN = "object_id";
	final protected static String USE_CASE_DESCRIPTION_COLUMN = "name";
	final protected static String NEXT_ID_COLUMN_ALIAS = "next_id";
	final protected static String CONNECTOR_TABLE= "t_connector";
	
	Connection connection = null;
	Statement statement = null;
	
	private String driverName;
	private String connectionString;
	private String userName;
	private String password;
	
	public JdbcConnection(final String driverName, final String connectionString, final String userName, final String password){
		this.driverName = driverName;
		this.connectionString = connectionString;
		this.userName = userName;
		this.password = password;
		
		this.createConnection();
	}
	
	private final void createConnection(){
		try {			 
			Class.forName(this.driverName); 
		} catch (ClassNotFoundException e) {

		}
 
		try { 
			this.connection = DriverManager.getConnection(this.connectionString, this.userName, this.password);
			this.statement = this.connection.createStatement();
		} catch (SQLException e) {

		}
	}
	
	public final void closeConnection(){	
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public final Map<Long, String> getUseCases(){
		Map<Long, String> result = new HashMap<Long, String>();
		try {
			ResultSet rs = this.statement.executeQuery(this.getUseCaseListCommand());
			
			while (rs.next()){
				long code = rs.getLong(OBJECT_ID_COLUMN);
				String description = rs.getString(USE_CASE_DESCRIPTION_COLUMN); 
				result.put(code, description);
			}
			
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public final Long getObjectIdForGenFile(String fileName){
		try {
			String command = this.getObjectIdCommand(fileName);
			ResultSet rs = this.statement.executeQuery(command);
			
			Long result = 0L;
			if (rs.next()){
				result = rs.getLong(OBJECT_ID_COLUMN);
			}
			
			return result;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public final boolean createConnector(long useCaseId, long classId){
		try {
			this.statement.execute(this.getCreateConnectorCommand(useCaseId, classId));
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public final boolean deleteExistingConnectors(List<Long> useCaseIds, long classId){
		try {
			this.statement.execute(this.getDeleteExistingConnectorsCommand(useCaseIds, classId));
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected final Long getNextConnectorId(){
		try {
			ResultSet rs = this.statement.executeQuery(this.getNextConnectorIdCommand());
			
			Long result = null;
			if (rs.next()){
				result = rs.getLong(NEXT_ID_COLUMN_ALIAS);
			}
			
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	protected String getUseCaseListCommand(){
		return "select " + OBJECT_ID_COLUMN + ", name from t_object where object_type = 'UseCase' and stereotype is null order by name";
	};
	
	protected String getObjectIdCommand(String fileName){
		return "select " + OBJECT_ID_COLUMN + " from t_object where genfile like '%" + fileName + "'";
	};
	
	protected String getDeleteExistingConnectorsCommand(List<Long> useCaseIds, long classId){
		return "delete from t_connector where start_object_id = " + classId;
	};
	
	protected String getCreateConnectorCommand(long useCaseId, long classId){
		StringBuffer result = new StringBuffer()
		.append(" insert into " + CONNECTOR_TABLE + " ( ")
		
		.append(" connector_id, ")
		.append(" direction, ")
		.append(" connector_type, ")
		.append(" sourceaccess, ")
		.append(" destaccess, ")
		.append(" sourcecontainment, ")
		.append(" sourceisaggregate, ")
		.append(" sourceisordered, ")
		.append(" destcontainment, ")
		.append(" destisaggregate, ")
		.append(" destisordered, ")
		.append(" start_object_id, ")
		.append(" end_object_id, ")
		.append(" start_edge, ")
		.append(" end_edge, ")
		.append(" ptstartx, ")
		.append(" ptstarty, ")
		.append(" ptendx, ")
		.append(" ptendy, ")
		.append(" seqno, ")
		.append(" headstyle, ")
		.append(" linestyle, ")
		.append(" routestyle, ")
		.append(" isbold, ")
		.append(" linecolor, ")
		.append(" virtualinheritance, ")
		.append(" diagramid, ")
		.append(" ea_guid, ")
		.append(" sourceisnavigable, ")
		.append(" destisnavigable, ")
		.append(" isroot, ")
		.append(" isleaf, ")
		.append(" sourcechangeable, ")
		.append(" destchangeable, ")
		.append(" sourcets, ")
		.append(" destts, ")
		.append(" sourcestyle, ")
		.append(" deststyle")
		
		.append(" ) values (")
		
		.append(" " + this.getNextConnectorId() + ", ") 		/*CONNECTOR_ID*/
		.append(" 'Source -> Destination', ")					//DIRECTION
		.append(" 'Realisation', ")								//CONNECTOR_TYPE
		.append(" 'Public', ")									//SOURCEACCESS
		.append(" 'Public', ")									//DESTACCESS
		.append(" 'Unspecified', ")								//SOURCECONTAINMENT
		.append(" 0, ")											//SOURCEISAGGREGATE
		.append(" 0, ")											//SOURCEISORDERED
		.append(" 'Unspecified', ")								//DESTCONTAINMENT
		.append(" 0, ")											//DESTISAGGREGATE
		.append(" 0, ")											//DESTISORDERED
		.append(" " + classId + ", ")							//START_OBJECT_ID
		.append(" " + useCaseId + ", ")							//END_OBJECT_ID
		.append(" 0, ")											//START_EDGE
		.append(" 0, ")											//END_EDGE
		.append(" 0, ")											//PTSTARTX
		.append(" 0, ")											//PTSTARTY
		.append(" 0, ")											//PTENDX
		.append(" 0, ")											//PTENDY
		.append(" 0, ")											//SEQNO
		.append(" 0, ")											//HEADSTYLE
		.append(" 0, ")											//LINESTYLE
		.append(" 3, ")											//ROUTESTYLE
		.append(" 0, ")											//ISBOLD
		.append(" -1, ")										//LINECOLOR
		.append(" 0, ")											//VIRTUALINHERITANCE
		.append(" 0, ")											//DIAGRAMID
		.append(" '{" + UUID.randomUUID().toString() + "}', ")	//EA_GUID
		.append(" 0, ")											//SOURCEISNAVIGABLE
		.append(" 0, ")											//DESTISNAVIGABLE
		.append(" 0, ")											//ISROOT
		.append(" 0, ")											//ISLEAF
		.append(" 'none', ")									//SOURCECHANGEABLE
		.append(" 'none', ")									//DESTCHANGEABLE
		.append(" 'instance', ")								//SOURCETS
		.append(" 'instance', ")								//DESTTS
		.append(" 'Union=0;Derived=0;AllowDuplicates=0;Owned=0;Navigable=Non-Navigable;', ") //SOURCESTYLE
		.append(" 'Union=0;Derived=0;AllowDuplicates=0;Owned=0;Navigable=Navigable;' ") 	 //DESTSTYLE
		
		.append(" )");

		return result.toString();
	};
	
	//No SQL ANSI available, nor a comon way between databases:
	protected abstract String getNextConnectorIdCommand();
}
