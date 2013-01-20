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

import net.dandielo.citizens.wallets.command.BukkitCommand;
import net.dandielo.citizens.wallets.command.Command;
import net.dandielo.citizens.wallets.command.WalletsExecutor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_4_R1.CraftServer;

public class CommandManager {
	private static Wallets plugin = Wallets.getInstance();
	private static CommandMap commandMap;
	
	private WalletsExecutor executor;
	
	private Map<CommandSyntax, CommandBinding> commands;
	private Map<Class<?>, Object> objects = new HashMap<Class<?>, Object>();
	
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
			commandMap.register(plugin.getName(), cmd);
	        cmd.setExecutor(executor);
		}
	}
	
	private void initCommandsBase()
	{
		try{
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
	public void registerCommands(Class<?> clazz) throws Exception
	{
		if ( objects.containsKey(clazz) )
			return;
		objects.put(clazz, clazz.newInstance());
		
		for ( Method method : clazz.getMethods() )
		{
			Command annotation = method.getAnnotation(Command.class);
			CommandSyntax syntax = new CommandSyntax(annotation.name(), annotation.syntax());
			
			registerCommand(annotation.name(), annotation.desc(), annotation.usage(), annotation.aliases());
			
			if ( annotation != null )
			{
				commands.put(syntax, new CommandBinding(clazz, method, syntax));
			}
		}
	}
	
	public boolean execute(String name, CommandSender sender, String[] args)
	{
		return commands.get(new CommandSyntax(name, args)).execute(sender, args);
	}
	
/*	public static void main(String[] a)
	{
		Pattern p;
		String s = "user <name> and (another) (another2)";
		
		p = Pattern.compile("(<([^<>]*)>)|([ ]*\\(([^\\(\\)]*)\\))");
		
		Matcher m = p.matcher(s);
		
		//s = m.replaceAll("");
		while(m.find())
		{
			if ( m.group(1) != null )
			{
				System.out.println("a: " + m.group(2));
				s = s.replace(m.group(1), "(\\S+)");
			}
			if ( m.group(3) != null )
			{
				System.out.println("a: " + m.group(4));
				s = s.replace(m.group(3), "( [\\S]*){0,1}");
			}
		}
		
		System.out.println(s);
		
		Pattern pt = Pattern.compile(s);
		Matcher mm = pt.matcher("user Dandielo and 2 3");
		System.out.println(Pattern.matches(pt.pattern(), "user Dandielo and sth sth"));
		//mm.find();
		int i = 0;
		mm.find();
		//while()
		{
			System.out.println(i + " " + mm.group(1));
			System.out.println(i + " " + mm.group(2));
			System.out.println(i + " " + mm.group(3));
			++i;
		}
			
	}*/
	
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
		//	this.name = name;
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
					syntax = syntax.replace(matcher.group(3), "( [\\S]*)*");
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
				if ( matcher.group(i) != null )
					map.put(argumentNames.get(i), matcher.group(i));
				
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
			return Pattern.matches(syntax.pattern(), originalSyntax);
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
		private Class<?> clazz;
		private Method method; 
		private CommandSyntax syntax;
		
		public CommandBinding(Class<?> clazz, Method method, CommandSyntax syntax) 
		{  
			this.clazz = clazz;
			this.method = method;
			this.syntax = syntax;
		}
		
		public Boolean execute(CommandSender sender, String[] args)
		{
			try 
			{
				Object result = method.invoke(objects.get(clazz), plugin, sender, syntax.commandArgs(args));
				if ( result instanceof Boolean )
					return (Boolean) result;
				return false;
			}
			catch (Exception e)
			{
				System.out.print("Some error here lol");
			}
			return false;
		}
	}
}
