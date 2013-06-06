package db;

import java.sql.*;

public class DBConnection {

	PreparedStatement psdoLogin = null;
	ResultSet resultSet = null;
	Connection conn = null;

	public DBConnection() {
		// Establishes a connection with database
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3309/db", "senecaBBB", "db");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return conn;
	}

	public ResultSet queryDB(String queryString) {
		try {
			psdoLogin = conn.prepareStatement(queryString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			resultSet = psdoLogin.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}

	public void closeConnection() {
		// Closes objects and connection
		if (psdoLogin != null) {
			try {
				psdoLogin.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

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

	public ResultSet getSaltAndHash(String userID) {
		String sqlQuery = "SELECT non_ldap_user.nu_salt, non_ldap_user.nu_hash FROM non_ldap_user where bu_id = '" + userID + "'";
		return this.queryDB(sqlQuery);
	}

}