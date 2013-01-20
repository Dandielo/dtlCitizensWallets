package net.dandielo.citizens.wallets.command;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.dandielo.citizens.wallets.CommandManager;
import net.dandielo.citizens.wallets.WalletTrait;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WalletsExecutor implements CommandExecutor {
	public static CommandManager cManager;
	public static Citizens citizens;
	
	public WalletsExecutor(CommandManager manager)
	{
		cManager = manager;
		citizens = (Citizens) CitizensAPI.getPlugin();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
		
		if ( sender instanceof Player )
		{
			NPC npc = citizens.getNPCSelector().getSelected(sender);
			if ( npc != null )
			{
				if ( npc.hasTrait(WalletTrait.class) )
					return cManager.execute(name, sender, npc, args);
				else
					sender.sendMessage("This npc does not have the Wallet Trait");
			}
			else
				sender.sendMessage("No npc was selected");
			
		}
		return true;
	}
   
}
