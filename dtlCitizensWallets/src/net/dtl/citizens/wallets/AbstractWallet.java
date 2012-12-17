package net.dtl.citizens.wallets;

import net.citizensnpcs.api.util.DataKey;
import net.milkbowl.vault.economy.Economy;

public abstract class AbstractWallet {
	protected Economy econ = Wallets.getEconomy();
	protected final String typeName;
	
	public AbstractWallet(String typeName)
	{
		this.typeName = typeName;
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

