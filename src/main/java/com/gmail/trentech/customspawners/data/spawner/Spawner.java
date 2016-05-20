package com.gmail.trentech.customspawners.data.spawner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.customspawners.Main;
import com.gmail.trentech.customspawners.data.DataQueries;
import com.gmail.trentech.customspawners.utils.SQLUtils;
import com.gmail.trentech.customspawners.utils.Serializer;

public class Spawner extends SQLUtils implements DataSerializable {

	private static ConcurrentHashMap<String, Spawner> cache = new ConcurrentHashMap<>();
	
	protected String entity;
	protected String location;
	protected int amount;
	protected int time;
	protected int radius;
	protected boolean enable;
	
	public Spawner(String entity, String location, int amount, int time, int radius, boolean enable) {
		this.entity = entity;
		this.location = location;
		this.amount = amount;
		this.time = time;
		this.radius = radius;
		this.enable = enable;
	}

	public EntityType getEntity() {
		return Main.getGame().getRegistry().getType(EntityType.class, entity).get();
	}

	public void setEntity(EntityType entityType) {
		this.entity = entityType.getId();
	}

	public Optional<Location<World>> getLocation() {
		String[] split = location.split("\\.");
		
		Optional<World> optionalWorld = Main.getGame().getServer().getWorld(split[0]);
		
		if(!optionalWorld.isPresent()){
			return Optional.empty();
		}
		World world = optionalWorld.get();
		
		try{
			int x = Integer.parseInt(split[1]);
			int y = Integer.parseInt(split[2]);
			int z = Integer.parseInt(split[3]);
			
			return Optional.of(world.getLocation(x, y, z));
		}catch(Exception e) {
			return Optional.empty();
		}		
	}
	
	public void setLocation(Location<World> location) {
		this.location = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public boolean isEnabled() {
		return enable;
	}
	
	public void setEnabled(boolean enable) {
		this.enable = enable;
	}
	
	public static Optional<Spawner> get(String name) {
		if(cache.containsKey(name)) {
			return Optional.of(cache.get(name));
		}
		
		return Optional.empty();
	}

	public static ConcurrentHashMap<String, Spawner> all(){
		return cache;
	}

	public void create(String name) {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into Spawners (Name, Spawner) VALUES (?, ?)");	
			
		    statement.setString(1, name);
		    statement.setString(2, Serializer.serialize(this));

			statement.executeUpdate();
			
			connection.close();
			
			cache.put(name, this);
			
			Main.spawn(name, this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void update(String name) {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("UPDATE Spawners SET Spawner = ? WHERE Name = ?");

			statement.setString(1, Serializer.serialize(this));
			statement.setString(2, name);
			
			statement.executeUpdate();
			
			connection.close();
			
			cache.put(name, this);
			
			for(Task task : Main.getGame().getScheduler().getScheduledTasks()) {
				if(task.getName().equals(name)) {
					task.cancel();
					break;
				}
			}
			
			if(isEnabled()) {
				Main.spawn(name, this);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void remove(String name) {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("DELETE from Spawners WHERE Name = ?");
		    
			statement.setString(1, name);
			statement.executeUpdate();
			
			connection.close();
			
			cache.remove(name);
			
			for(Task task : Main.getGame().getScheduler().getScheduledTasks()) {
				if(task.getName().equals(name)) {
					task.cancel();
					break;
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void init() {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Spawners");
		    
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String name = result.getString("Name");
				Spawner spawner = Serializer.deserialize(result.getString("Spawner"));
				
				cache.put(name, spawner);
				
				if(spawner.isEnabled()) {
					Main.spawn(name, spawner);
				}			
			}
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int getContentVersion() {
		return 2;
	}

	@Override
    public DataContainer toContainer() {
        return new MemoryDataContainer().set(DataQueries.ENTITY, entity).set(DataQueries.LOCATION, location).set(DataQueries.AMOUNT, amount)
        		.set(DataQueries.TIME, time).set(DataQueries.RANGE, radius).set(DataQueries.ENABLE, enable);
    }
}
