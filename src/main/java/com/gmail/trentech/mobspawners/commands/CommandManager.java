package com.gmail.trentech.mobspawners.commands;

import org.spongepowered.api.command.spec.CommandSpec;

import com.gmail.trentech.mobspawners.commands.module.CMDEntity;
import com.gmail.trentech.mobspawners.commands.module.CMDQuantity;
import com.gmail.trentech.mobspawners.commands.module.CMDSpeed;

public class CommandManager {
	
	private CommandSpec cmdEntity = CommandSpec.builder()
		    .permission("mobspawners.cmd.spawner.module.entity")
		    .executor(new CMDEntity())
		    .build();
	
	private CommandSpec cmdSpeed = CommandSpec.builder()
		    .permission("mobspawners.cmd.spawner.module.speed")
		    .executor(new CMDSpeed())
		    .build();
	
	private CommandSpec cmdQuantity = CommandSpec.builder()
		    .permission("mobspawners.cmd.spawner.module.quantity")
		    .executor(new CMDQuantity())
		    .build();
	
	private CommandSpec cmdModule = CommandSpec.builder()
		    .permission("mobspawners.cmd.spawner.module")
		    .child(cmdEntity, "entity", "e")
		    .child(cmdQuantity, "quantity", "q")
		    .child(cmdSpeed, "speed", "s")
		    .executor(new CMDModule())
		    .build();
	
	private CommandSpec cmdCreate = CommandSpec.builder()
		    .permission("mobspawners.cmd.spawner.create")
		    .executor(new CMDCreate())
		    .build();
	
	private CommandSpec cmdList = CommandSpec.builder()
		    .permission("mobspawners.cmd.spawner.list")
		    .executor(new CMDList())
		    .build();

	public CommandSpec cmdSpawner = CommandSpec.builder()
		    .permission("mobspawners.cmd.spawner")
		    .child(cmdCreate, "create", "c")
		    .child(cmdList, "list", "l")
		    .child(cmdModule, "module", "m")
		    .executor(new CMDSpawner())
		    .build();
}
