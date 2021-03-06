package net.dandielo.citizens.wallets.types;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.object.Town;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.AbstractWallet;
import net.dandielo.citizens.wallets.Wallets;
import net.dandielo.citizens.wallets.command.Command;

public class TownyWallet extends AbstractWallet {

	private static Towny towny = Wallets.getTowny();
	
	public TownyWallet(String typeName) {
		super(typeName);
		town = null;
	}
	
	private Town town;

	@Override
	public boolean deposit(double amount) {
		if ( town == null )
			return false;
		
		try
		{
			town.setBalance(town.getHoldingBalance() + amount);
		} 
		catch (EconomyException e) 
		{
			Wallets.warning("Something went wrong when tried to deposit money to town bank!");
		}
		return true;
	}

	@Override
	public boolean withdraw(double amount) {
		if ( town != null )
		{
		
			try 
			{
				if ( town.getHoldingBalance() >= amount )
				{
					town.setBalance(town.getHoldingBalance() - amount);
					return true;
				}
			} 
			catch (Exception e) 
			{
				Wallets.warning("Something went wrong when tried to withdraw money from town bank!");
			}
		}
		return false;
	}

	@Override
	public double balance() {
		if ( town == null )
			return 0.0;
		try 
		{
			return town.getHoldingBalance();
		} 
		catch (EconomyException e)
		{
			Wallets.warning("Something went wrong when tried to get balance from town bank!");
		}
		return 0.0;
	}

	@Override
	public void load(DataKey key) {
		town = towny.getTownyUniverse().getTownsMap().get(key.getString("town"));
	}

	@Override
	public void save(DataKey key) {
		key.setString("town", town == null ? "" : town.getName());
	}
	
	@Command(
	name = "wallet",
	syntax = "town",
	desc = "Shows the town assigned to the wallet",
	perm = "dtl.wallets.commands")
	public void groupWallet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		sender.sendMessage(ChatColor.GOLD + "Town: " + ChatColor.WHITE + ( town == null ? "" : town.getTag() ));
	}
	
	@Command(
	name = "wallet",
	syntax = "town set <tag>",
	desc = "assigns a new town to this wallet",
	usage = "- /wallet town set clantag",
	perm = "dtl.wallets.commands")
	public void groupWalletSet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		Town town = towny.getTownyUniverse().getTownsMap().get(args.get("tag"));
		if ( town != null )
		{
			if ( town.isMayor(towny.getTownyUniverse().getResidentMap().get(sender.getName())) )
			{
				this.town = town;
				sender.sendMessage(ChatColor.GOLD + "New town: " + ChatColor.WHITE + town.getTag());
			}
			else
				sender.sendMessage(ChatColor.RED + "You can't change this");
		}
		else
			sender.sendMessage(ChatColor.RED + "This town does not exists");
	}
}