package net.dandielo.citizens.wallets.types;

import java.util.HashMap;
import java.util.Map;

import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.AbstractWallet;

public class GroupWallet extends AbstractWallet {
	private static Map<String, Double> groups = new HashMap<String, Double>();
	
	private String group;
	
	public GroupWallet(String typeName) {
		super(typeName);
	}

	@Override
	public boolean deposit(double amount) {
		groups.put(group, groups.get(group) + amount);
		return true;
	}

	@Override
	public boolean withdraw(double amount) {
		if ( groups.get(group) < amount )
			return false;
		groups.put(group, groups.get(group) - amount);
		return true;
	}

	@Override
	public double balance() {
		return groups.get(group);
	}

	@Override
	public void load(DataKey key) {
		group = key.getString("group");
		groups.put(group, key.getDouble("balance", 0.0));
	}

	@Override
	public void save(DataKey key) {
		key.setString("group", group);
		key.setDouble("balance", groups.get(group));
	}

	
}
