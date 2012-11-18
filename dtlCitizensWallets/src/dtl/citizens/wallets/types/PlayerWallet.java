package dtl.citizens.wallets.types;

import net.citizensnpcs.api.util.DataKey;
import dtl.citizens.wallets.AbstractWallet;

public class PlayerWallet extends AbstractWallet {
	protected String playerName;
	
	public PlayerWallet(String typeName) {
		super(typeName);
		playerName = "";
	}

	@Override
	public boolean deposit(double amount) {
		return econ.depositPlayer(playerName, amount).transactionSuccess();
	}

	@Override
	public boolean withdraw(double amount) {
		return econ.withdrawPlayer(playerName, amount).transactionSuccess();
	}

	@Override
	public double balance() {
		return econ.getBalance(playerName); 
	}

	@Override
	public void save(DataKey key) {
		key.setString("player", playerName);
	}

	@Override
	public void load(DataKey key) {
		playerName = key.getString("player");
	}

}
