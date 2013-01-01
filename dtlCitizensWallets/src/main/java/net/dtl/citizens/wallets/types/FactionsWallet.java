package net.dtl.citizens.wallets.types;

import net.citizensnpcs.api.util.DataKey;
import net.dtl.citizens.wallets.AbstractWallet;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;


public class FactionsWallet extends AbstractWallet {

	public FactionsWallet(String typeName) {
		super(typeName);
		faction = null;
	}
	
	private Faction faction; 

	@Override
	public boolean deposit(double amount) {
		if ( faction == null )
			return false;
		
		return Econ.deposit(faction.getAccountId(), amount);
	}

	@Override
	public boolean withdraw(double amount) {
		if ( faction == null )
			return false;
		
		return Econ.withdraw(faction.getAccountId(), amount);
	}

	@Override
	public double balance() {
		if ( faction == null )
			return 0.0;
		
		return Econ.getBalance(faction.getAccountId());
	}

	@Override
	public void load(DataKey key) {
		faction = Factions.i.getByTag(key.getString("faction"));
	}

	@Override
	public void save(DataKey key) {
		key.setString("faction", faction.getTag());
	}
}
