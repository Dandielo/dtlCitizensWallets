package net.dtl.citizens.wallets;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

public class WalletTrait extends Trait {
	public WalletTrait() {
		super("wallet");
	}

	private AbstractWallet wallet;
	
	public AbstractWallet getWallet()
	{
		return wallet;
	}
	
	@Override
	public void onAttach()
	{
		wallet = Wallets.getInstance().getWalletObject("private");
	}
	
	@Override
	public void load(DataKey data)
	{
		wallet = Wallets.getInstance().getWalletObject(data);
		if ( wallet != null )
			wallet.load(data);
	}
	
	@Override
	public void save(DataKey data)
	{
		if ( wallet != null )
		{
			wallet.save(data);
			data.setString("type", wallet.getType());
		}
	}
	
	
}
