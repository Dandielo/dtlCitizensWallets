package net.dandielo.citizens.wallets;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public abstract class AbstractWallet implements Listener {
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
}

