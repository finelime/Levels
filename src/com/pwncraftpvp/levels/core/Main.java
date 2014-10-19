package com.pwncraftpvp.levels.core;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.pwncraftpvp.levels.utils.Utils;

public class Main extends JavaPlugin{
	
	private static Main instance;
	
	public Economy econ;
	
	private String gray = ChatColor.GRAY + "";
	private String yellow = ChatColor.YELLOW + "";
	
	/**
	 * Get the instance of this class
	 * @return - The instance of this class
	 */
	public static Main getInstance(){
		return instance;
	}
	
	public void onEnable(){
		instance = this;
		this.getServer().getPluginManager().registerEvents(new Events(), this);
		Utils.setupEconomy(this);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			public void run(){
				Events.minedBlocks.clear();
			}
		}, 0, 3600);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			LPlayer lplayer = new LPlayer(player);
			if(cmd.getName().equalsIgnoreCase("levels") || cmd.getName().equalsIgnoreCase("l")){
				if(args.length > 0){
					if(args[0].equalsIgnoreCase("top")){
						if(args.length == 1){
							lplayer.sendOverallLeaderboard();
						}else if(args.length == 2){
							boolean isValid = false;
							Skill skill = null;
							for(Skill s : Skill.values()){
								if(s.toString().equalsIgnoreCase(args[1])){
									isValid = true;
									skill = s;
									break;
								}
							}
							if(isValid == true){
								lplayer.sendSkillLeaderboard(skill);
							}else{
								lplayer.sendError("The skill you specified is invalid!");
							}
						}else{
							lplayer.sendError("Usage: /l top [skill]");
						}
					}else if(args[0].equalsIgnoreCase("stats")){
						lplayer.sendSkillStatistics();
					}else if(args[0].equalsIgnoreCase("setlevel")){
						if(args.length == 4){
							LPlayer lp = new LPlayer(args[1]);
							if(Utils.getSkill(args[2]) != null){
								Skill skill = Utils.getSkill(args[2]);
								if(Utils.isInteger(args[3]) == true){
									int level = Integer.parseInt(args[3]);
									lp.setSkillLevel(skill, level);
									lplayer.sendMessage("You have set " + yellow + args[1] + gray + "'s " + yellow + Utils.getSkillName(skill) + gray + " level to " + yellow + level + gray + "!");
								}else{
									lplayer.sendError("You must enter a valid level!");
								}
							}else{
								lplayer.sendError("You must enter a valid skill!");
							}
						}
					}else if(args[0].equalsIgnoreCase("setpayment")){
						if(args.length == 3){
							if(args[1].equalsIgnoreCase("overall")){
								if(Utils.isInteger(args[2]) == true){
									int amount = Integer.parseInt(args[2]);
									Utils.setOverallPayment(amount);
									lplayer.sendMessage("You have set the overall payment amount to $" + yellow + amount + gray + "!");
								}else{
									lplayer.sendError("You must enter a valid amount!");
								}
							}else if(args[1].equalsIgnoreCase("skill")){
								if(Utils.isInteger(args[2]) == true){
									int amount = Integer.parseInt(args[2]);
									Utils.setSkillPayment(amount);
									lplayer.sendMessage("You have set the skill payment amount to $" + yellow + amount + gray + "!");
								}else{
									lplayer.sendError("You must enter a valid amount!");
								}
							}else if(args[1].equalsIgnoreCase("levelup")){
								if(Utils.isInteger(args[2]) == true){
									int amount = Integer.parseInt(args[2]);
									Utils.setLevelUpMoney(amount);
									lplayer.sendMessage("You have set the level up payment amount to $" + yellow + amount + gray + "!");
								}else{
									lplayer.sendError("You must enter a valid amount!");
								}
							}else{
								lplayer.sendError("Usage: /l setpayment <overall/skill> <amount>");
							}
						}
					}else if(args[0].equalsIgnoreCase("help")){
						lplayer.sendCommandHelp();
					}else{
						lplayer.sendCommandHelp();
					}
				}else{
					lplayer.sendCommandHelp();
				}
			}
		}
		return false;
	}
}
