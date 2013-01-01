package net.dtl.citizens.wallets.types;

import net.citizensnpcs.api.util.DataKey;
import net.dtl.citizens.wallets.AbstractWallet;

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
		bankAccount = key.getString("bank-account");
	}
	
	@Override
	public void save(DataKey key) {
		key.setString("bank-account", bankAccount);
	}


}
