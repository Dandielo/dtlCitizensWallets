package net.dandielo.citizens.wallets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		AbstractWallet wallet = trait.getWallet();
		
		if ( wallet == null )
		{
			sender.sendMessage(ChatColor.RED + "This npc has no wallet set");
			return;
		}
		
		trait.getWallet().sendDescription(sender);
	}

	//Commands description holder
	private static Map<String, List<Command>> commands = new HashMap<String, List<Command>>(); 
	
	static void registerCommandInfo(String type, Command command)
	{
		List<Command> list = commands.get(type);
		if ( list == null )
			list = new ArrayList<Command>();
		list.add(command);
		commands.put(type, list);
	}
	
	//Type help command
	@Command(
	name = "wallet",
	syntax = "<type> help",
	npc = false,
	priority = 0)
	public void walletHelp(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		List<Command> cmds = commands.get(args.get("type"));
		
		if ( cmds == null )
			sender.sendMessage(ChatColor.RED + "No commands are registered for this type");
		
		sender.sendMessage(ChatColor.RED + "== "  + ChatColor.GREEN + args.get("type") + ChatColor.RED + " wallet commands ==");
		
		for ( Command cmd : cmds )
		{
			sender.sendMessage(nameAndSyntax(cmd));
			sender.sendMessage(perm(cmd));
			sender.sendMessage(description(cmd));
			sender.sendMessage(usage(cmd));
			sender.sendMessage(ChatColor.GREEN + "---");
		}
	}
	
	private static String perm(Command cmd)
	{
		return ChatColor.GOLD + "Permission: " + ChatColor.DARK_AQUA + cmd.perm();
	}

	private static String usage(Command cmd) {
		return cmd.usage().isEmpty() ? "" : ChatColor.GOLD + "Usage: " + ChatColor.YELLOW + cmd.usage();
	}
	
	private static String description(Command cmd) {
		return ChatColor.GOLD + "Description: " + ChatColor.WHITE + ( cmd.desc().isEmpty() ? ChatColor.RED + "none" : cmd.desc() );
	}

	private static String nameAndSyntax(Command cmd)
	{
		return ChatColor.GOLD + "Command: " + ChatColor.DARK_PURPLE + cmd.name() + " " + cmd.syntax();
	}
	
}
