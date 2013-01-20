package net.dandielo.citizens.wallets.command.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;

import net.dandielo.citizens.wallets.Wallets;
import net.dandielo.citizens.wallets.command.Command;

public class WalletCommands {
	
	@Command(
	name = "wallet",
	syntax = "private <action> (value)",
	desc = "Allows to manage private wallets",
	usage = "The <action> argument may be 'balance/withddraw/deposit', where (value) sets the value do deposit or withdraw",
	perm = "dtl.wallets.commands")
	public boolean privateWallet(Wallets plugin, CommandSender sender, Map<String, String> args)
	{
		for ( Map.Entry<String, String> arg : args.entrySet())
		{
			sender.sendMessage(arg.getKey() + " " + arg.getValue());
		}
		return false;
	}

}
