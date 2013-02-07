package net.dandielo.citizens.wallets;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.wallets.types.BankWallet;
import net.dandielo.citizens.wallets.types.FactionsWallet;
import net.dandielo.citizens.wallets.types.GroupWallet;
import net.dandielo.citizens.wallets.types.InfiniteWallet;
import net.dandielo.citizens.wallets.types.OwnerWallet;
import net.dandielo.citizens.wallets.types.PlayerWallet;
import net.dandielo.citizens.wallets.types.PrivateWallet;
import net.dandielo.citizens.wallets.types.SimpleClansWallet;
import net.dandielo.citizens.wallets.types.TownyWallet;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
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
	private static Permission permissions;
	private static SimpleClans clans;
	private static Towny towny;
	private static P factions;
	
	//plugin instance
	private static Wallets instance;
	
	//Command manager
	private CommandManager cManager;
	
	   
	@Override
	public void onEnable()
	{
		instance = this;
		
		cManager = new CommandManager();
		
		registerWalletType("Player", PlayerWallet.class);
		registerWalletType("Owner", OwnerWallet.class);
		registerWalletType("Bank", BankWallet.class);
		registerWalletType("Private", PrivateWallet.class);
		registerWalletType("Group", GroupWallet.class);
		
		registerWalletType("Infinite", InfiniteWallet.class);
		
		//And a second time as admin for CitiTraders compatibility
		registerWalletType("Admin", InfiniteWallet.class);

		initializeSoftDependPlugins();
		
		if ( towny != null )
			registerWalletType("Town", TownyWallet.class);
		if ( clans != null )
			registerWalletType("Clan", SimpleClansWallet.class);
		if ( factions != null )
			registerWalletType("Faction", FactionsWallet.class);
		
		initEcon();
		initPerms();
		
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(WalletTrait.class).withName("wallet"));
		cManager.registerCommands(WalletCommands.class);
		
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
	
	private void initPerms()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permissions = permissionProvider.getProvider();
			info("Using " + permissions.getName() + " plugin");
        }
        else 
        {
        	//no economy plugin found disable the plugin
        	info("Permissions plugin not found! Disabling plugin");
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
			cManager.registerCommands(name.toLowerCase(), type);
			
			for ( Class<?> face : type.getInterfaces() )
				if ( face.isInterface() && face.getSimpleName().equals("Listener") )
					getServer().getPluginManager().registerEvents(type.getConstructor(String.class).newInstance(""), this);
				
			
			info(name + " wallet registered sucessfully!");
		}
		catch (Exception e) 
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
		//Added CitiTraders compatibility
		final String type = dataKey.getString("type");
		if ( wallets.containsKey(type.toLowerCase()) )
		{
			try
			{
				return wallets.get(type.toLowerCase()).getConstructor(String.class).newInstance(type.toLowerCase());
			} 
			catch (Exception e) 
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
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public CommandManager getCommandManager() 
	{
		return cManager;
	}
	
	public static Economy getEconomy()
	{
		return economy;
	}
	
	public static Permission getPerms()
	{
		return permissions;
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
