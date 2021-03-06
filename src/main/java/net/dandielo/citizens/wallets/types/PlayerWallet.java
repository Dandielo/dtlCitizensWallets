package net.dandielo.citizens.wallets.types;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.AbstractWallet;
import net.dandielo.citizens.wallets.Wallets;
import net.dandielo.citizens.wallets.command.Command;

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

	public void setPlayer(String player)
	{
		playerName = player;
	}
	
	public String getPlayer()
	{
		return playerName;
	}
	
	@Command(
	name = "wallet",
	syntax = "player",
	perm = "dtl.wallets.commands.player",
	desc = "shows the player assigned to the wallet")
	public void groupWallet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		sender.sendMessage(ChatColor.GOLD + "Wallet owner: " + ChatColor.GREEN + playerName);
	}
	
	
	@Command(
	name = "wallet",
	syntax = "player set <player>",
	perm = "dtl.wallets.commands.player.set",
	usage = "- /wallet player set dandielo",
	desc = "assigns a new player to the wallet")
	public void groupWalletSet(Wallets plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		playerName = args.get("player");
		sender.sendMessage(ChatColor.GOLD + "New wallet owner: " + ChatColor.GREEN + playerName);
	}
	
}
