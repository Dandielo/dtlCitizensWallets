package net.dandielo.citizens.wallets.types;

import java.text.DecimalFormat;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.AbstractWallet;
import net.dandielo.citizens.wallets.Wallets;
import net.dandielo.citizens.wallets.command.Command;

public class PrivateWallet extends AbstractWallet {
	private double balance;
	
	public PrivateWallet(String typeName) {
		super(typeName);
		balance = 0.0;
	}

	@Override
	public boolean deposit(double amount) {
		balance += amount;
		return true;
	}

	@Override
	public boolean withdraw(double amount) {
		if ( balance < amount )
			return false;
		
		balance -= amount;
		return true;
	}

	@Override
	public double balance() {
		return balance;
	}

	@Override
	public void load(DataKey key) {
		balance = key.getDouble("balance", /*CitiTrader compatibility*/ key.getDouble("amount"));
	}

	@Override
	public void save(DataKey key) {
		key.setDouble("balance", balance);
	}

	@Command(
	name = "wallet",
	syntax = "private <action> (value)",
	desc = "Allows to manage wallets",
	usage = "The <action> argument may be 'balance/withddraw/deposit', where (value) sets the value do deposit or withdraw",
	perm = "dtl.wallets.commands")
	public boolean privateWallet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		DecimalFormat f = new DecimalFormat("#.##");
		String action = args.get("action");
		
		if ( action.equals("balance") )
		{
			sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.GREEN + f.format(balance()));
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
