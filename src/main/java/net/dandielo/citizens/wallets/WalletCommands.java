package net.dandielo.citizens.wallets;

import java.util.Map;

import net.citizensnpcs.api.npc.NPC;
import net.dandielo.citizens.wallets.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class WalletCommands {
	private CommandManager cManager = Wallets.getInstance().getCommandManager();
	
	@Command(
	name = "wallet",
	syntax = "type (type)",
	perm = "dtl.wallets.commands")
	public boolean walletType(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		WalletTrait trait = npc.getTrait(WalletTrait.class);
		
		if ( args.containsKey("type") )
		{
			cManager.unregisterWalletObject(npc);
			
			AbstractWallet newWallet = Wallets.getInstance().getWalletObject(args.get("type"));
			if ( newWallet != null )
			{
				trait.setWallet(newWallet);	
				sender.sendMessage(ChatColor.GOLD + "Wallet changed");
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Wallet could not be changed");
			}
		
		}
		else
			sender.sendMessage(ChatColor.GOLD + "Wallet type: " + trait.getWallet().getType());
		return true;
	}

}
