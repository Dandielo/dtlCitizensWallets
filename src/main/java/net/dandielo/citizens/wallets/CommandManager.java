package net.dandielo.citizens.wallets;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.citizensnpcs.api.npc.NPC;
import net.dandielo.citizens.wallets.command.BukkitCommand;
import net.dandielo.citizens.wallets.command.Command;
import net.dandielo.citizens.wallets.command.WalletsExecutor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_4_R1.CraftServer;

public class CommandManager {
	private static Wallets plugin = Wallets.getInstance();
	private static CommandMap commandMap;
	
	private WalletsExecutor executor;
	
	private Map<CommandSyntax, CommandBinding> commands;
	private Map<Class<?>, Object> objects = new HashMap<Class<?>, Object>();
	
	private Map<NPC, ObjectEntry<Class<? extends AbstractWallet>,Object>> npcObjects = new HashMap<NPC, ObjectEntry<Class<? extends AbstractWallet>,Object>>();
	
	public CommandManager()
	{
		commands = new HashMap<CommandSyntax, CommandBinding>();
		executor = new WalletsExecutor(this);
		initCommandsBase();
	}
	
	void registerCommand(String name, String desc, String usage, String[] aliases)
	{
		if ( commandMap.getCommand(name) == null )
		{
			BukkitCommand cmd = new BukkitCommand(name, desc, usage, Arrays.asList(aliases));
			Wallets.info("registered " + name + " command");
			commandMap.register(plugin.getName(), cmd);
	        cmd.setExecutor(executor);
		}
	}
	
	void registerWalletObject(NPC npc, AbstractWallet wallet)
	{
		npcObjects.put(npc, new ObjectEntry<Class<? extends AbstractWallet>, Object>(wallet.getClass(), wallet));
	}
	void unregisterWalletObject(NPC npc)
	{
		npcObjects.remove(npc);
	}
	
	private void initCommandsBase()
	{
		try
		{
			if(Bukkit.getServer() instanceof CraftServer)
			{
				final Field f = CraftServer.class.getDeclaredField("commandMap");
	            f.setAccessible(true);
	            commandMap = (CommandMap) f.get(Bukkit.getServer());
			} 
        } 
		catch (Exception e)
        {
            e.printStackTrace();
        }
	}
	
	protected void newInstance(Class<?> clazz)
	{
		try
		{
			objects.put(clazz, clazz.newInstance());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	void registerCommands(Class<?> clazz)
	{
		if ( objects.containsKey(clazz) ) 
			return;
		
		if ( !clazz.getSuperclass().equals(AbstractWallet.class) )
			newInstance(clazz);
		
		for ( Method method : clazz.getMethods() )
		{
			Command annotation = method.getAnnotation(Command.class);
			
			if ( annotation != null )
			{
				Wallets.info("Added new command method");
				CommandSyntax syntax = new CommandSyntax(annotation.name(), annotation.syntax());
				
				registerCommand(annotation.name(), annotation.desc(), annotation.usage(), annotation.aliases());
				
				commands.put(syntax, new CommandBinding(clazz, method, syntax, annotation.perm()));
			}
		}
	}
	
	public boolean execute(String name, CommandSender sender, NPC npc, String[] args)
	{
		for ( Map.Entry<CommandSyntax, CommandBinding> command : commands.entrySet() )
			if ( new CommandSyntax(name, args).equals(command.getKey()) )
				return command.getValue().execute(sender, npc, args);
		return false;
	}
	
	
	private static class CommandSyntax
	{
		private static final Pattern commandPattern = Pattern.compile("(<([^<>]*)>)|([ ]*\\(([^\\(\\)]*)\\))");
		
		private List<String> argumentNames = new ArrayList<String>();
		private String name;
		private String originalSyntax;
		private Pattern syntax;
		
		public CommandSyntax(String name, String[] args) 
		{
			this.name = name;
			originalSyntax = name + " " + toString(args);
		}
		
		public CommandSyntax(String name, String args) 
		{
			this.name = name;
			originalSyntax = args;
			String syntax = name + " " + originalSyntax;
			
			Matcher matcher = commandPattern.matcher(originalSyntax);
			while(matcher.find())
			{
				if ( matcher.group(1) != null )
				{
					argumentNames.add(matcher.group(2));
					syntax = syntax.replace(matcher.group(1), "(\\S+)");
				}
				if ( matcher.group(3) != null )
				{
					argumentNames.add(matcher.group(4));
					syntax = syntax.replace(matcher.group(3), "( [\\S]*){0,1}");
				}
			}
			this.syntax = Pattern.compile(syntax);
		}
		
		public Map<String, String> commandArgs(String[] args)
		{
			Map<String, String> map = new HashMap<String, String>();
			Matcher matcher = syntax.matcher(name + " " + toString(args));
			int max = matcher.groupCount();
			
			matcher.find();
			for ( int i = 0 ; i < max ; ++i )
				if ( matcher.group(i+1) != null && !matcher.group(i+1).trim().isEmpty() )
					map.put(argumentNames.get(i), matcher.group(i+1).trim());
				
			return map;
		}
		
		@Override
		public int hashCode()
		{
			return originalSyntax.hashCode();
		}
		
		@Override 
		public boolean equals(Object o)
		{
			if ( !(o instanceof CommandSyntax) )
				return false;
			return Pattern.matches(((CommandSyntax)o).syntax.pattern(), originalSyntax);
		}
		
		//Utils
		public static String toString(String[] args)
		{
			if ( args.length < 1 )
				return "";
			
			String res = args[0];
			for ( int i = 1 ; i < args.length ; ++i )
				res += " " + args[i];
			return res;
		}
	}
	
	private class CommandBinding
	{
		private Method method; 
		private CommandSyntax syntax;
		private Class<?> clazz;
		private String perm;
		
		public CommandBinding(Class<?> clazz, Method method, CommandSyntax syntax, String perm) 
		{
			this.clazz = clazz;
			this.method = method;
			this.syntax = syntax;
			this.perm = perm;
		}
		
		public Boolean execute(CommandSender sender, NPC npc, String[] args)
		{
			if ( !Wallets.getPerms().has(sender, perm) )
			{
				sender.sendMessage(ChatColor.RED + "You don't have permissions to use this command");
				return true;
			}
			try 
			{
				Object executer = null;
				if ( clazz.getSuperclass().equals(AbstractWallet.class) )
				{
					if ( npcObjects.get(npc).getKey().equals(clazz) )
						executer = npcObjects.get(npc).getValue();
					else
					{
						sender.sendMessage(ChatColor.RED + "You cannot use this command for this wallet type");
						return true;
					}
				}
				else executer = objects.get(clazz);
				
				Object result = method.invoke(executer, plugin, sender, npc, syntax.commandArgs(args));
				if ( result instanceof Boolean )
					return (Boolean) result;
				return false;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return false;
		}
	}
	
	final class ObjectEntry<K, V> implements Map.Entry<K, V> {
	    private final K key;
	    private V value;

	    public ObjectEntry(K key, V value) {
	        this.key = key;
	        this.value = value;
	    }

	    @Override
	    public K getKey() {
	        return key;
	    }

	    @Override
	    public V getValue() {
	        return value;
	    }

	    @Override
	    public V setValue(V value) {
	        V old = this.value;
	        this.value = value;
	        return old;
	    }
	}
}
