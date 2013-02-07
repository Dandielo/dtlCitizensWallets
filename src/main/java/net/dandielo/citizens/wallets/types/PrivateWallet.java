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
	syntax = "private balance",
	desc = "Shows the current balance",
	perm = "dtl.wallets.commands.private.balance")
	public void privateWalletBalance(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		DecimalFormat f = new DecimalFormat("#.##");
		sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.GREEN + f.format(balance()));
	}

	@Command(
	name = "wallet",
	syntax = "private deposit <amount>",
	desc = "deposits given <amount> to the wallet",
	usage = "- /wallet private deposit 10.33",
	perm = "dtl.wallets.commands.private.deposit")
	public void privateWalletDeposit(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		DecimalFormat f = new DecimalFormat("#.##");
		
		double value;
		try
		{
			value = Double.parseDouble(args.get("amount"));
		}
		catch(Exception e)
		{
			sender.sendMessage(ChatColor.RED + "Value must be a number");
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
	syntax = "private withdraw <amount>",
	desc = "Withdraws the given <amount> from the wallet",
	usage = "- /wallet private withdraw 30",
	perm = "dtl.wallets.commands.private.withdraw")
	public void privateWallet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		DecimalFormat f = new DecimalFormat("#.##");
			
		double value;
		try
		{
			value = Double.parseDouble(args.get("amount"));
		}
		catch(Exception e)
		{
			sender.sendMessage(ChatColor.RED + "Value must be a number");
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
