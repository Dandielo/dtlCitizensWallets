package net.dandielo.citizens.wallets.command;

import net.dandielo.citizens.wallets.CommandManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WalletsExecutor implements CommandExecutor {
	public static CommandManager cManager;
	
	public WalletsExecutor(CommandManager cManager)
	{
		WalletsExecutor.cManager = cManager;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
		return cManager.execute(name, sender, args);
	}
   
}
