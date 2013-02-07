package net.dandielo.citizens.wallets.types;

import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.AbstractWallet;

public class InfiniteWallet extends AbstractWallet {

	public InfiniteWallet(String typeName) {
		super(typeName);
	}

	@Override
	public boolean deposit(double amount) {
		return true;
	}

	@Override
	public boolean withdraw(double amount) {
		return true;
	}

	@Override
	public double balance() {
		return 0;
	}

	@Override
	public void save(DataKey key) {
	}

	@Override
	public void load(DataKey key) {
	}

}
