package dtl.citizens.wallets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.citizensnpcs.api.util.DataKey;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import dtl.citizens.wallets.types.PlayerWallet;


public class Wallets extends JavaPlugin {
	protected final static Logger logger = Logger.getLogger("Minecraft");
	
	//wallet types
	private Map<String, Class<? extends AbstractWallet>> wallets = new HashMap<String, Class<? extends AbstractWallet>>();

	//Economy plugin
	private static Economy economy;
	
	//plugin instance
	private static Wallets instance;
	
	@Override
	public void onEnable()
	{
		registerWalletType("player", PlayerWallet.class);
		initEcon();
		
		instance = this;
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
	
	public void registerWalletType(String name, Class<? extends AbstractWallet> type)
	{
		try 
		{
			Constructor<? extends AbstractWallet> constructor = type.getConstructor(String.class);
			
			if ( constructor == null || name == null || name.isEmpty() )
				return;
			
			wallets.put(name, type);
			
			System.out.print(name + " wallet registered");
		}
		catch (NoSuchMethodException e) 
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
	}
	
	public static Wallets getInstance()
	{
		return instance;
	}
	
	public AbstractWallet getWalletObject(DataKey dataKey)
	{
		final String type = dataKey.getString("type"); 
		if ( wallets.containsKey(type) )
		{
			try
			{
				return wallets.get(type).getConstructor(String.class, DataKey.class).newInstance(type, dataKey);
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
