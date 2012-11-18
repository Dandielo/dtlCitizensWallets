package dtl.citizens.wallets.types;

import net.citizensnpcs.api.util.DataKey;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import dtl.citizens.wallets.AbstractWallet;
import dtl.citizens.wallets.Wallets;

public class SimpleClansWallet extends AbstractWallet {

	public SimpleClansWallet(String typeName) {
		super(typeName);
		clan = null;
	}

	private Clan clan;
	
	@Override
	public boolean deposit(double amount) {
		if ( clan == null )
			return false;
		
		clan.setBalance(clan.getBalance() + amount);
		return true;
	}

	@Override
	public boolean withdraw(double amount) {
		if ( clan == null )
			return false;
		
		if ( clan.getBalance() >= amount )
		{
			clan.setBalance(clan.getBalance() - amount);
			return true;
		}
		return false;
	}

	@Override
	public double balance() {
		if ( clan == null )
			return 0.0;
		
		return clan.getBalance();
	}

	@Override
	public void load(DataKey key) {
		clan = Wallets.getSimpleClans().getClanManager().getClan(key.getString("clan"));
	}

	@Override
	public void save(DataKey key) {
		key.setString("clan", clan.getTag());
	}

}
