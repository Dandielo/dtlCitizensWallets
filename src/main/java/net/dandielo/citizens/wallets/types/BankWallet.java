package net.dandielo.citizens.wallets.types;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.AbstractWallet;
import net.dandielo.citizens.wallets.Wallets;
import net.dandielo.citizens.wallets.command.Command;

public class BankWallet extends AbstractWallet {
	protected String bankAccount;
	
	public BankWallet(String typeName) {
		super(typeName);
		bankAccount = "";
	}

	@Override
	public boolean deposit(double amount) {
		return econ.bankDeposit(bankAccount, amount).transactionSuccess();
	}

	@Override
	public boolean withdraw(double amount) {
		return econ.bankWithdraw(bankAccount, amount).transactionSuccess();
	}

	@Override
	public double balance() {
		return econ.bankBalance(bankAccount).balance;
	}

	@Override
	public void load(DataKey key) {
		bankAccount = key.getString("bank-account", /* CitiTraders compatibility */ key.getString("account"));
	}
	
	@Override
	public void save(DataKey key) {
		key.setString("bank-account", bankAccount);
	}
	
	public void setBank(String bank)
	{
		bankAccount = bank;
	}
	
	public String getBank()
	{
		return bankAccount;
	}
	
	@Command(
	name = "wallet",
	syntax = "bank",
	perm = "dtl.wallets.commands",
	desc = "Shows the current bank account")
	public void bankWallet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		sender.sendMessage(ChatColor.GOLD + "Bank account: " + ChatColor.GREEN + bankAccount);
	}
	
	@Command(
	name = "wallet",
	syntax = "bank set <account>",
	perm = "dtl.wallets.commands",
	desc = "Sets a new bank account for a bank wallet",
	usage = "- /wallet bank set BankAccount")
	public void groupWallet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		bankAccount = args.get("account");
		sender.sendMessage(ChatColor.GOLD + "New bank account: " + ChatColor.GREEN + bankAccount);
	}

}
