package net.dandielo.citizens.wallets.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BukkitCommand extends Command {
	private CommandExecutor exe = null;
	
	public BukkitCommand(String name, String description, String usageMessage, List<String> aliases) {
		super(name, description, usageMessage, aliases);
	}

	@Override
	public boolean execute(CommandSender sender, String name, String[] args) 
	{
		if( exe != null )
		{
            exe.onCommand(sender, this, name, args);
        }
        return false;
	}
	
	public void setExecutor(CommandExecutor exe)
	{
        this.exe = exe;
    }

}
