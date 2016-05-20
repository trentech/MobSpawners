package com.gmail.trentech.customspawners.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.spongepowered.api.service.sql.SqlService;

import com.gmail.trentech.customspawners.Main;

public abstract class SQLUtils {

    protected static SqlService sql;

    protected static DataSource getDataSource() throws SQLException {
	    if (sql == null) {
	        sql = Main.getGame().getServiceManager().provide(SqlService.class).get();
	    }
	    
	    return sql.getDataSource("jdbc:h2:./config/customspawners/data");
	}

	public static void createTables() {		
		try {
			Connection connection = getDataSource().getConnection();
			
			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Spawners (Name TEXT, Spawner TEXT)");
			
			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}