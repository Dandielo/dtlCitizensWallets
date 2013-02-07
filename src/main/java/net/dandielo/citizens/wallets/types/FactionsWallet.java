package net.dandielo.citizens.wallets.types;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.AbstractWallet;
import net.dandielo.citizens.wallets.Wallets;
import net.dandielo.citizens.wallets.command.Command;

public class FactionsWallet extends AbstractWallet {

	private Faction faction;
	
	public FactionsWallet(String name)
	{
		super(name);
		faction = null;
	}

	@Override
	public boolean deposit(double amount)
	{
		if ( faction == null ) return false;

		double balance = Econ.getBalance(faction.getTag());
		return Econ.setBalance(faction.getTag(), balance + amount);
	}

	@Override
	public boolean withdraw(double amount)
	{
		if ( faction == null ) return false;

		double balance = Econ.getBalance(faction.getTag());
		if ( amount > balance ) return false;
		
		return Econ.setBalance(faction.getTag(), balance - amount);
	}

	@Override
	public double balance() 
	{
		if ( faction == null ) return 0.0;
		
		return Econ.getBalance(faction.getTag());
	}

	@Override
	public void load(DataKey key) {
		faction = Factions.i.getByTag(key.getString("faction"));
	}

	@Override
	public void save(DataKey key) {
		key.setString("faction", faction == null ? "" : faction.getTag());
	}
	
	@Command(
	name = "wallet",
	syntax = "faction",
	desc = "Shows the faction assigned to the wallet",
	perm = "dtl.wallets.commands.factions")
	public void faction(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		sender.sendMessage(ChatColor.GOLD + "Faction: " + ChatColor.WHITE + ( faction == null ? "" : faction.getTag()));
	}
	
	@Command(
	name = "wallet",
	syntax = "faction set <tag>",
	desc = "assigns a new faction to this wallet",
	usage = "- /wallet faction set mytag",
	perm = "dtl.wallets.commands.factions.set")
	public void setFaction(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		Faction faction = Factions.i.getByTag(args.get("tag"));
		if ( faction != null )
		{
			if ( faction.getFPlayerLeader().getPlayer().getName().equals(sender.getName()) )
			{
				this.faction = faction;
				sender.sendMessage(ChatColor.GOLD + "New faction: " + ChatColor.WHITE + faction.getTag());
			}
			else
				sender.sendMessage(ChatColor.RED + "You can't change this");
		}
		else
			sender.sendMessage(ChatColor.RED + "This town does not exists");
	}
	
}
