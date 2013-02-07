package net.dandielo.citizens.wallets;

import java.util.Map;

import net.citizensnpcs.api.npc.NPC;
import net.dandielo.citizens.wallets.command.Command;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class WalletCommands {
//	private CommandManager cManager = Wallets.getInstance().getCommandManager();
	Permission perms = Wallets.getPerms();
	
	@Command(
	name = "wallet",
	syntax = "type",
	perm = "dtl.wallets.commands.type")
	public boolean walletType(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		WalletTrait trait = npc.getTrait(WalletTrait.class);
	
		sender.sendMessage(ChatColor.GOLD + "Wallet type: " + trait.getWallet().getType());
		return true;
	}
	
	@Command(
	name = "wallet",
	syntax = "set <wallet>",
	perm = "dtl.wallets.commands.set")
	public void walletSet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		WalletTrait trait = npc.getTrait(WalletTrait.class);
		
		AbstractWallet newWallet = Wallets.getInstance().getWalletObject(args.get("type"));
		
		if ( newWallet != null )
		{
			if ( !perms.has(sender, newWallet.typePerm()) )
				sender.sendMessage(ChatColor.RED + "You dont have permissions to use this wallet");
			
			trait.setWallet(newWallet);	
			sender.sendMessage(ChatColor.GOLD + "Wallet changed");
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Wallet could not be changed");
		}
	}

	@Command(
	name = "wallet",
	syntax = "",
	perm = "dtl.wallets.commands.info")
	public void wallet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		WalletTrait trait = npc.getTrait(WalletTrait.class);
		trait.getWallet().sendDescription(sender);
	}
	
}
