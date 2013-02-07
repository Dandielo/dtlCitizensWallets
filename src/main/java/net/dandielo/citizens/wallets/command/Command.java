package net.dandielo.citizens.wallets.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) 
public @interface Command {
	String name();
	String syntax() default "";
	String desc() default "";
	String perm() default "";
	String usage() default "";
	String[] aliases() default { };
	boolean npc() default true;
	int priority() default 2;
}
