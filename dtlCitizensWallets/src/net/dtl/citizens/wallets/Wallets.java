package net.dtl.citizens.wallets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.api.util.DataKey;
import net.dtl.citizens.wallets.types.BankWallet;
import net.dtl.citizens.wallets.types.FactionsWallet;
import net.dtl.citizens.wallets.types.PlayerWallet;
import net.dtl.citizens.wallets.types.PrivateWallet;
import net.dtl.citizens.wallets.types.SimpleClansWallet;
import net.dtl.citizens.wallets.types.TownyWallet;
import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.P;
import com.palmergames.bukkit.towny.Towny;



public class Wallets extends JavaPlugin {
	protected final static Logger logger = Logger.getLogger("Minecraft");
	
	//wallet types
	private Map<String, Class<? extends AbstractWallet>> wallets = new HashMap<String, Class<? extends AbstractWallet>>();

	//Economy plugin
	private static Economy economy;
	private static SimpleClans clans;
	private static Towny towny;
	private static P factions;
	
	//plugin instance
	private static Wallets instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		registerWalletType("Player", PlayerWallet.class);
		registerWalletType("Bank", BankWallet.class);
		registerWalletType("Private", PrivateWallet.class);
		
		if ( towny != null )
			registerWalletType("Town", TownyWallet.class);
		if ( clans != null )
			registerWalletType("Clan", SimpleClansWallet.class);
		if ( factions != null )
			registerWalletType("Faction", FactionsWallet.class);
		
		initEcon();
		
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(WalletTrait.class).withName("wallet"));
	}
	
	public void initEcon()
	{
		//initializing vault plugin
		if ( getServer().getPluginManager().getPlugin("Vault") == null ) 
		{
			info("Vault plugin not found! Disabling plugin");
			this.setEnabled(false);
			this.getPluginLoader().disablePlugin(this);
			return;
		}
			
        RegisteredServiceProvider<Economy> rspEcon = getServer().getServicesManager().getRegistration(Economy.class);
       
        //check if there is an economy plugin
        if ( rspEcon != null ) 
        {
        	//economy exists, plugin enabled
        	economy = rspEcon.getProvider();
			info("Using " + economy.getName() + " plugin");
        } 
        else 
        {
        	//no economy plugin found disable the plugin
        	info("Economy plugin not found! Disabling plugin");
			this.getPluginLoader().disablePlugin(this);
			return;
		}
	}
	
	//Hooking into clans and towny bank account
	public void initializeSoftDependPlugins()
	{
		clans = (SimpleClans) Bukkit.getPluginManager().getPlugin("SimpleClans");
		if ( clans != null )
		{
			info("Hooked into " + clans.getDescription().getFullName());
		}
		towny = (Towny) Bukkit.getPluginManager().getPlugin("Towny");
		if ( towny != null )
		{
			info("Hooked into " + towny.getDescription().getFullName());
		}
		factions = (P) Bukkit.getPluginManager().getPlugin("Factions");
		if ( factions != null )
		{
			info("Hooked into " + factions.getDescription().getFullName());
		}
	}
	
	public boolean registerWalletType(String name, Class<? extends AbstractWallet> type)
	{
		if ( wallets.containsKey(name.toLowerCase()) )
			return false;
		
		try 
		{
			Constructor<? extends AbstractWallet> constructor = type.getConstructor(String.class);
			
			if ( constructor == null || name == null || name.isEmpty() )
				return false;
			
			wallets.put(name.toLowerCase(), type);
			
			info(name + " wallet registered sucessfully!");
		}
		catch (NoSuchMethodException e) 
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public static AbstractWallet getWallet(NPC npc)
	{
		WalletTrait trait = npc.getTrait(WalletTrait.class);
		if ( trait != null )
			return trait.getWallet();
		return null;
	}
	
	public static Wallets getInstance()
	{
		return instance;
	}
	
	AbstractWallet getWalletObject(DataKey dataKey)
	{
		final String type = dataKey.getString("type"); 
		if ( wallets.containsKey(type.toLowerCase()) )
		{
			try
			{
				return wallets.get(type.toLowerCase()).getConstructor(String.class).newInstance(type.toLowerCase());
			} 
			catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) 
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	AbstractWallet getWalletObject(String name)
	{
		if ( wallets.containsKey(name.toLowerCase()) )
		{
			try
			{
				return wallets.get(name.toLowerCase()).getConstructor(String.class).newInstance(name.toLowerCase());
			} 
			catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) 
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Economy getEconomy()
	{
		return economy;
	}
	
	public static P getFactions()
	{
		return factions;
	}
	
	public static SimpleClans getSimpleClans()
	{
		return clans;
	}
	
	public static Towny getTowny()
	{
		return towny;
	}
	
	//logger info
	public static void info(String message)
	{
		logger.info("["+getInstance().getDescription().getName()+"] " + message);
	}
	//logger warning
	public static void warning(String message)
	{
		logger.warning("["+getInstance().getDescription().getName()+"] " + message);
	}
	//logger severe
	public static void severe(String message)
	{
		logger.severe("["+getInstance().getDescription().getName()+"] " + message);
	}
}
