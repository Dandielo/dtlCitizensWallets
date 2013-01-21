package net.dandielo.citizens.wallets;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

public class WalletTrait extends Trait {
	private CommandManager cManager = Wallets.getInstance().getCommandManager();
	
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
		wallet.setNPC(npc);
		cManager.registerWalletObject(npc, wallet);
	}
	
	@Override
	public void load(DataKey data)
	{
		wallet = Wallets.getInstance().getWalletObject(data);
		if ( wallet != null )
		{
			wallet.setNPC(npc);
			wallet.load(data);
			cManager.registerWalletObject(npc, wallet);
		}
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
	
	void setWallet(AbstractWallet wallet)
	{
		this.wallet = wallet;
		cManager.registerWalletObject(npc, wallet);
	}
}
