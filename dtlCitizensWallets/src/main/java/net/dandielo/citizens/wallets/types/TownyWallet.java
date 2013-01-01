package net.dandielo.citizens.wallets.types;

import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.object.Town;

import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.AbstractWallet;
import net.dandielo.citizens.wallets.Wallets;

public class TownyWallet extends AbstractWallet {

	public TownyWallet(String typeName) {
		super(typeName);
		town = null;
	}
	
	private Town town;

	@Override
	public boolean deposit(double amount) {
		if ( town == null )
			return false;
		
		try
		{
			town.setBalance(town.getHoldingBalance() + amount);
		} 
		catch (EconomyException e) 
		{
			Wallets.warning("Something went wrong when tried to deposit money to town bank!");
		}
		return true;
	}

	@Override
	public boolean withdraw(double amount) {
		if ( town != null )
		{
		
			try 
			{
				if ( town.getHoldingBalance() >= amount )
				{
					town.setBalance(town.getHoldingBalance() - amount);
					return true;
				}
			} 
			catch (Exception e) 
			{
				Wallets.warning("Something went wrong when tried to withdraw money from town bank!");
			}
		}
		return false;
	}

	@Override
	public double balance() {
		if ( town == null )
			return 0.0;
		try 
		{
			return town.getHoldingBalance();
		} 
		catch (EconomyException e)
		{
			Wallets.warning("Something went wrong when tried to get balance from town bank!");
		}
		return 0.0;
	}

	@Override
	public void load(DataKey key) {
		town = Wallets.getTowny().getTownyUniverse().getTownsMap().get(key.getString("town"));
	}

	@Override
	public void save(DataKey key) {
		key.setString("town", town.getName());
	}

}
