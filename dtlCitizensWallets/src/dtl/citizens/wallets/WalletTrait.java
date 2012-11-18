package dtl.citizens.wallets;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

public class WalletTrait extends Trait {
	protected WalletTrait(String name) {
		super(name);
	}

	private AbstractWallet wallet;
	
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
