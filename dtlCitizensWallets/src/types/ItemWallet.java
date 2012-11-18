package types;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.util.DataKey;
import dtl.citizens.wallets.AbstractWallet;

public class ItemWallet extends AbstractWallet {

	public ItemWallet(String typeName) {
		super(typeName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean deposit(double amount) {
		return false;
	}

	@Override
	public boolean withdraw(double amount) {
		return false;
	}

	@Override
	public double balance() {
		return 0;
	}
	
	@Override
	public boolean depositItems(List<ItemStack> items)
	{
		return false;
	}
	
	@Override
	public boolean withdrawItems(List<ItemStack> items)
	{
		return false;
	}

	@Override
	public void load(DataKey key) {
		
	}

	@Override
	public void save(DataKey key) {
		
	}

}
