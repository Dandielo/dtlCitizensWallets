package net.dandielo.citizens.wallets;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.command.Command;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public abstract class AbstractWallet implements Listener {
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
	
	//Decimal format helper ;P
	protected static DecimalFormat f = new DecimalFormat("#.##");
	
	//Abstract Wallet
	protected static Permission perm = Wallets.getPerms();
	protected static Economy econ = Wallets.getEconomy();
	protected final String typeName;
	protected NPC npc;
	
	public AbstractWallet(String typeName)
	{
		this.typeName = typeName;
	}
	
	void setNPC(NPC npc)
	{
		this.npc = npc;
	}
	
	//description method (used for /wallet command)
	public void sendDescription(CommandSender sender)
	{
		sender.sendMessage(ChatColor.GOLD + "Wallet type: " + typeName);
	}
	public String typePerm()
	{
		return "dtl.wallets.types." + typeName.toLowerCase();
	}
	
	//"virtual money" methods
	public abstract boolean deposit(double amount);
	public abstract boolean withdraw(double amount);
	public abstract double balance();
	
	public abstract void load(DataKey key);
	public abstract void save(DataKey key);
	
	public final String getType() {
		return typeName;
	}

	//Type help command
	@Command(
	name = "wallet",
	syntax = "<type> help")
	public void walletHelp(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		List<Command> cmds = commands.get(args.get("type"));
		
		if ( cmds == null )
			sender.sendMessage(ChatColor.RED + "No commands are registered for this type");
		
		for ( Command cmd : cmds )
		{
			sender.sendMessage(nameAndSyntax(cmd));
			sender.sendMessage(description(cmd));
			sender.sendMessage(usage(cmd));
		}
	}

	private static String usage(Command cmd) {
		return ChatColor.GOLD + "Usage: " + ChatColor.WHITE + ( cmd.usage().isEmpty() ? ChatColor.RED + "none" : cmd.usage() );
	}
	
	private static String description(Command cmd) {
		return ChatColor.GOLD + "Description: " + ChatColor.WHITE + ( cmd.desc().isEmpty() ? ChatColor.RED + "none" : cmd.desc() );
	}

	private static String nameAndSyntax(Command cmd)
	{
		return ChatColor.GOLD + "Command: " + ChatColor.BLUE + cmd.name() + " " + cmd.syntax();
	}
}

