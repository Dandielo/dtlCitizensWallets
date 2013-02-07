package net.dandielo.citizens.wallets.types;

import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.AbstractWallet;

public class OwnerWallet extends AbstractWallet {
	protected Owner owner;
	
	public OwnerWallet(String typeName) {
		super(typeName);
	}

	@Override
	public boolean deposit(double amount) {
		return econ.depositPlayer(owner.getOwner(), amount).transactionSuccess();
	}

	@Override
	public boolean withdraw(double amount) {
		return econ.withdrawPlayer(owner.getOwner(), amount).transactionSuccess();
	}

	@Override
	public double balance() {
		return econ.getBalance(owner.getOwner()); 
	}

	@Override
	public void save(DataKey key) {
		//Nothing to save here :P
	}

	@Override
	public void load(DataKey key) {
		owner = npc.getTrait(Owner.class);
	}
	
}
