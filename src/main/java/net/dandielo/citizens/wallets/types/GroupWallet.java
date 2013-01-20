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
	syntax = "group <action> (value)",
	perm = "dtl.wallets.commands")
	public boolean groupWallet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		String action = args.get("action");
		
		if ( action.equals("balance") )
		{
			sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.GREEN + f.format(balance()));
			return true;
		}
		else if ( action.equals("group") )
		{
			if ( args.containsKey("value") )
			{
				group = args.get("value");
				if ( !groups.containsKey(group) )
					groups.put(group, 0.0);
				sender.sendMessage(ChatColor.GOLD + "Changed group to: " + ChatColor.GREEN + group);
			}
			else
				sender.sendMessage(ChatColor.GOLD + "Group: " + ChatColor.GREEN + group);
			return true;
		}
		else
		{
			if ( !args.containsKey("value") )
			{
				sender.sendMessage(ChatColor.RED + "Value wasn't set");
				return true;
			}
			
			double value;
			try
			{
				value = Double.parseDouble(args.get("value"));
			}
			catch(Exception e)
			{
				sender.sendMessage(ChatColor.RED + "Value must be a number");
				return true;
			}
			
			if ( value <= 0 )
			{
				sender.sendMessage(ChatColor.RED + "Cannot accept this value");
				return true;
			}
			
			if ( action.equals("withdraw") )
			{
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
			if ( action.equals("deposit") )
			{
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
			
			
			return true;
		}
	}

}
