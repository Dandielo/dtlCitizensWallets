package net.dandielo.citizens.wallets.types;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.AbstractWallet;
import net.dandielo.citizens.wallets.Wallets;
import net.dandielo.citizens.wallets.command.Command;

public class GroupWallet extends AbstractWallet {
	private static Map<String, Double> groups = new HashMap<String, Double>();
	
	private String group;
	
	public GroupWallet(String typeName) {
		super(typeName);
		group = "default";
		if ( !groups.containsKey(group) )
			groups.put(group, 0.0);
	}

	@Override
	public boolean deposit(double amount) {
		groups.put(group, groups.get(group) + amount);
		return true;
	}

	@Override
	public boolean withdraw(double amount) {
		if ( groups.get(group) < amount )
			return false;
		groups.put(group, groups.get(group) - amount);
		return true;
	}

	@Override
	public double balance() {
		return groups.get(group);
	}

	@Override
	public void load(DataKey key) {
		group = key.getString("group", "default");
		groups.put(group, key.getDouble("balance", 0.0));
	}

	@Override
	public void save(DataKey key) {
		key.setString("group", group);
		key.setDouble("balance", groups.get(group));
	}

	@Command(
	name = "wallet",
	syntax = "group",
	perm = "dtl.wallets.commands.group",
	desc = "shows the group the wallet is assigned to")
	public void groupWallet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		sender.sendMessage(ChatColor.GOLD + "Group: " + ChatColor.GREEN + group);
	}

	@Command(
	name = "wallet",
	syntax = "group set <group>",
	perm = "dtl.wallets.commands.group.set",
	usage = "- /wallet group set newgroup",
	desc = "assigns the wallet to a new gorup")
	public void groupWalletSet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		group = args.get("group");
		if ( !groups.containsKey(group) )
			groups.put(group, 0.0);
		sender.sendMessage(ChatColor.GOLD + "Changed group to: " + ChatColor.GREEN + group);
	}
	
	@Command(
	name = "wallet",
	syntax = "group balance",
	desc = "shows the balance of the wallets group",
	perm = "dtl.wallets.commands.group.balance")
	public void groupWalletBalance(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.GREEN + f.format(balance()));
	}
	
	@Command(
	name = "wallet",
	syntax = "group deposit <amount>",
	desc = "deposits <amount> to the wallets group",
	usage = "- /wallet group deposit 10.00",
	perm = "dtl.wallets.commands.group.deposit")
	public void groupWalletDeposit(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{		
		double value;
		try
		{
			value = Double.parseDouble(args.get("amount"));
		}
		catch(Exception e)
		{
			sender.sendMessage(ChatColor.RED + "Amount must be a number");
			return;
		}
		
		if ( value <= 0 )
		{
			sender.sendMessage(ChatColor.RED + "Cannot accept this amount");
			return;
		}

		if ( econ.withdrawPlayer(sender.getName(), value).transactionSuccess() )
		{
			deposit(value);
			sender.sendMessage(ChatColor.GOLD + "Deposited: " + ChatColor.GREEN + f.format(value));
			sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.GREEN + f.format(balance()));
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "You dont have enough money");
		}
	}
	
	@Command(
	name = "wallet",
	syntax = "group withdraw <amount>",
	desc = "withdraws <amount> from the wallets group",
	usage = "- /wallet group withdraw 10.10",
	perm = "dtl.wallets.commands.group.withdraw")
	public void groupWalletWithdraw(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		
		double value;
		try
		{
			value = Double.parseDouble(args.get("amount"));
		}
		catch(Exception e)
		{
			sender.sendMessage(ChatColor.RED + "Amount must be a number");
			return;
		}
		
		if ( value <= 0 )
		{
			sender.sendMessage(ChatColor.RED + "Cannot accept this amount");
			return;
		}

		if ( withdraw(value) )
		{
			sender.sendMessage(ChatColor.GOLD + "Withdrawed: " + ChatColor.GREEN + f.format(value));
			sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.GREEN + f.format(balance()));
			econ.depositPlayer(sender.getName(), value);
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Not enough money within this wallet");
		}
	}

}
