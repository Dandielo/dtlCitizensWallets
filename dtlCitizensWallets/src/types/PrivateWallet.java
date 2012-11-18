package types;

import net.citizensnpcs.api.util.DataKey;
import dtl.citizens.wallets.AbstractWallet;

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
		key.getDouble("balance");
	}

	@Override
	public void save(DataKey key) {
		key.setDouble("balance", balance);
	}

}
