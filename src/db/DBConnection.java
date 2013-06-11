package db;

import java.sql.*;
import snaq.db.ConnectionPool;

//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import org.apache.tomcat.jdbc.pool.DataSource;
//import org.apache.tomcat.jdbc.pool.PoolProperties;
//import java.sql.Statement;
//import javax.sql.DataSource;
//import org.apache.tomcat.jdbc.pool.ConnectionPool;

public class DBConnection {
	
	private static DBConnection dbConnectionSingleton = new DBConnection();
	
	Connection conn = null;
	ConnectionPool pool = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	
	/**
	 * Establishes a Connection Pool with database.
	 * 
	 * A private Constructor prevents any other class from instantiating.
	 */
	private DBConnection() {
		Class<?> c = null;
		try {
			c = Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Driver driver = null;
		try {
			driver = (Driver)c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		try {
			DriverManager.registerDriver(driver);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
        String name = "Local"; // Pool name.
        int minPool = 1; // Minimum number of pooled connections, or 0 for none.
        int maxPool = 3; // Maximum number of pooled connections, or 0 for none.
		int maxSize = 5; // Maximum number of possible connections, or 0 for no limit.
		long idleTimeout = 30; // Idle timeout (seconds) for idle pooled connections, or 0 for no timeout.
		String url = "jdbc:mysql://localhost:3309/db"; // JDBC connection URL.
		String username = "senecaBBB"; // Database username.                                    
        String password = "db"; // Password for the database username supplied.
        
        try {
        	pool = new ConnectionPool(name, minPool, maxPool, maxSize, idleTimeout, url, username, password);
        	conn = pool.getConnection(idleTimeout);
        } catch (SQLException e){
        	e.printStackTrace();
        } finally {
        	pool.registerShutdownHook();
        }
        
        
	}

/*
 	private DBConnection() {
     // Obtain our environment naming context
        Context initCtx = null;
		try {
			initCtx = new InitialContext();
		} catch (NamingException e) {
			e.printStackTrace();
		}
        Context envCtx = null;
		try {
			envCtx = (Context)initCtx.lookup("java:comp/env");
		} catch (NamingException e) {
			e.printStackTrace();
		}

        // Look up our data source
        DataSource ds = null;
		try {
			ds = (DataSource) envCtx.lookup("jdbc/TestDB");
		} catch (NamingException e) {
			e.printStackTrace();
		}

        // Allocate and use a connection from the pool
        try {
			Connection conn = ds.getConnection();
			
			PreparedStatement preparedStatemente = conn.prepareStatement("select * from non_ldap_user");
			ResultSet rs = preparedStatemente.executeQuery();
			int cnt = 1;
			while (rs.next()) {
				System.out.println((cnt++) + ". Host: " + rs.getString("nu_name") + " User: " + rs.getString("nu_lastname") + " Password: " + rs.getString("nu_salt"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
        
	}
*/
	
	/**
	 * Singleton static 'instance' method.
	 */
	public static DBConnection getInstance() {
	    return dbConnectionSingleton;
	  }

	/**
	 * Gets the connection.
	 *
	 * @return connection
	 */
	public Connection getConnection() {
		return conn;
	}
	
	/**
	 * Executes all queries.
	 *
	 * @return result set with database selected/affected rows
	 */
	public ResultSet queryDB(String queryString) {
		try {
			preparedStatement = conn.prepareStatement(queryString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}

	/**
	 * Closes objects and connection.
	 * 
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//pool.release();
		/*if (psdoLogin != null) {
			try {
				psdoLogin.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (resultSet != null) {
			try {
				psdoLogin.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}*/
	}

	/**
	 * Selects user's info.
	 */
	public ResultSet getUserInfo(String userID) {
		String sqlQuery = "SELECT bbb_user.*, non_ldap_user.*, user_role.pr_name, user_role.ur_rolemask "
				+ "FROM bbb_user "
				+ "INNER JOIN non_ldap_user "
				+ "ON bbb_user.bu_id = non_ldap_user.bu_id "
				+ "INNER JOIN user_role "
				+ "ON bbb_user.ur_id = user_role.ur_id "
				+ "WHERE bbb_user.bu_id = '" + userID + "'";
		return this.queryDB(sqlQuery);
	}

	/**
	 * Selects user's respective salt and hash.
	 */
	public ResultSet getSaltAndHash(String userID) {
		String sqlQuery = "SELECT non_ldap_user.nu_salt, non_ldap_user.nu_hash "
				+ "FROM non_ldap_user "
				+ "WHERE bu_id = '" + userID + "'";
		return this.queryDB(sqlQuery);
	}
	
	/**
	 * Selects all users.
	 */
	public ResultSet getUsers(String sValue) {		
		String sqlQuery = "SELECT bbb_user.*, non_ldap_user.*, user_role.pr_name, user_role.ur_rolemask "
				+ "FROM bbb_user "
				+ "INNER JOIN non_ldap_user "
				+ "ON bbb_user.bu_id = non_ldap_user.bu_id "
				+ "INNER JOIN user_role "
				+ "ON bbb_user.ur_id = user_role.ur_id "
				+ "WHERE bbb_user.bu_issuper = 'false' "
				+ "ORDER BY '" + sValue + "'";
		return this.queryDB(sqlQuery);
	}
	
	/**
	 * Gets number of active connections.
	 */
	public ResultSet getNumberOfConnections() {
		String sqlQuery = "SHOW PROCESSLIST";
		return this.queryDB(sqlQuery);
	}
}
