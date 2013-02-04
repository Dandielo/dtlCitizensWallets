package net.dandielo.citizens.wallets.types;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.AbstractWallet;
import net.dandielo.citizens.wallets.Wallets;
import net.dandielo.citizens.wallets.command.Command;
import net.sacredlabyrinth.phaed.simpleclans.Clan;

public class SimpleClansWallet extends AbstractWallet {

	public SimpleClansWallet(String typeName) {
		super(typeName);
		clan = null;
	}

	private Clan clan;
	
	@Override
	public boolean deposit(double amount) {
		if ( clan == null ) return false;
		
		clan.setBalance(clan.getBalance() + amount);
		return true;
	}

	@Override
	public boolean withdraw(double amount) {
		if ( clan == null )	return false;
		
		if ( clan.getBalance() >= amount )
		{
			clan.setBalance(clan.getBalance() - amount);
			return true;
		}
		return false;
	}

	@Override
	public double balance() {
		if ( clan == null )	return 0.0;
		
		return clan.getBalance();
	}

	@Override
	public void load(DataKey key) {
		clan = Wallets.getSimpleClans().getClanManager().getClan(key.getString("clan"));
	}

	@Override
	public void save(DataKey key) {
		key.setString("clan", clan == null ? "" : clan.getTag());
	}
	
	@Command(
	name = "wallet",
	syntax = "clan (tag)",
	perm = "dtl.wallets.commands")
	public boolean groupWallet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		if ( args.containsKey("tag") )
		{
			Clan clan = Wallets.getSimpleClans().getClanManager().getClan(args.get("tag"));
			if ( clan != null )
			{
				if ( clan.isLeader((Player) sender) )
				{
					this.clan = clan;
					sender.sendMessage(ChatColor.GOLD + "New clan: " + ChatColor.WHITE + clan.getTag());
				}
				else
					sender.sendMessage(ChatColor.RED + "You can't change this");
			}
			else
				sender.sendMessage(ChatColor.RED + "This clan does not exists");
		}
		else
			sender.sendMessage(ChatColor.GOLD + "Clan: " + ChatColor.WHITE + ( clan == null ? "" : clan.getTag()));
		return true;
	}

}
