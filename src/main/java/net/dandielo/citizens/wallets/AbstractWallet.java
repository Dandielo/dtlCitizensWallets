package net.dandielo.citizens.wallets;

import java.text.DecimalFormat;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public abstract class AbstractWallet {
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

