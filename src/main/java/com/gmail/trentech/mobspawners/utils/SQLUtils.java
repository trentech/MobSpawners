package com.gmail.trentech.mobspawners.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

public abstract class SQLUtils {

	protected static SqlService sql;

	protected static DataSource getDataSource() throws SQLException {
		if (sql == null) {
			sql = Sponge.getServiceManager().provide(SqlService.class).get();
		}

		return sql.getDataSource("jdbc:mysql://localhost:3306/minecraft?user=root&password=Idontknow1");
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