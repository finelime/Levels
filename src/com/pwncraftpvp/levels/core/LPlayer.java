package com.pwncraftpvp.levels.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.pwncraftpvp.levels.utils.ParticleEffects;
import com.pwncraftpvp.levels.utils.UTFUtils;
import com.pwncraftpvp.levels.utils.Utils;
import com.pwncraftpvp.levels.utils.ValueComparator;

public class LPlayer {
	
	Main main = Main.getInstance();
	private String gray = ChatColor.GRAY + "";
	private String yellow = ChatColor.YELLOW + "";
	
	Player player = null;
	public LPlayer(Player p){
		player = p;
	}
	
	File file = null;
	public LPlayer(File f){
		file = f;
	}
	
	String name = null;
	public LPlayer(String n){
		name = n;
	}
	
	/**
	 * Get the player's file
	 */
	public File getFile(){
		if(player != null){
			return new File(main.getDataFolder() + File.separator + "players", player.getUniqueId() + ".yml");
		}else if(file != null){
			return file;
		}else if(name != null){
			return new File(main.getDataFolder() + File.separator + "players", Bukkit.getPlayer(name).getUniqueId() + ".yml");
		}else{
			return null;
		}
	}
	
	/**
	 * Get the player's config
	 */
	public FileConfiguration getConfig(){
		return YamlConfiguration.loadConfiguration(getFile());
	}
	
	/**
	 * Set a value in the player's config
	 * 
	 * @param key - The location of the value to set
	 * @param entry - The value to set
	 */
	public void setConfigValue(String key, Object entry){
		FileConfiguration fc = getConfig();
	    fc.set(key, entry);
	    try{
	      fc.save(getFile());
	    }catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	
	/**
	 * Check if the player is new
	 * @return True if the player is new, false if the player is not new
	 */
	public boolean isNew(){
		if(this.getConfig().getBoolean("isNew") == false){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Set if the player is new
	 * @param set - True means they aren't new, false means they are new
	 */
	public void setNew(boolean set){
		if(set == true){
			this.setConfigValue("isNew", false);
		}else if(set == false){
			this.setConfigValue("isNew", true);
		}
	}
	
	/**
	 * Get the player's username
	 * @return The player's username
	 */
	public String getUsername(){
		return this.getConfig().getString("username");
	}
	
	/**
	 * Set the player's username
	 * @param name - The username to set
	 */
	public void setUsername(String name){
		this.setConfigValue("username", name);
	}
	
	/**
	 * Get the player's saved level
	 * @return The player's saved level
	 */
	public int getSavedLevel(){
		return this.getConfig().getInt("saved.level");
	}
	
	/**
	 * Set the player's saved level
	 * @return The player's saved level
	 */
	public void setSavedLevel(int level){
		this.setConfigValue("saved.level", level);
	}
	
	/**
	 * Get the player's saved xp
	 * @return The player's saved xp
	 */
	public float getSavedXP(){
		return this.getConfig().getInt("saved.xp");
	}
	
	/**
	 * Set the player's saved xp
	 * @return The player's saved xp
	 */
	public void setSavedXP(float xp){
		this.setConfigValue("saved.xp", xp);
	}
	
	/**
	 * Send a message header to the player
	 * @param header - The header to be sent
	 */
	public void sendMessageHeader(String header){
		player.sendMessage(gray + "-=(" + yellow + "*" + gray + ")=-" + "  " + yellow + header + "  " + gray + "-=(" + yellow + "*" + gray + ")=-");
	}
	
	/**
	 * Send a message to the player
	 * @param message - The message to be sent
	 */
	public void sendMessage(String message){
		player.sendMessage(ChatColor.GOLD + UTFUtils.getArrow() + gray + " " + message);
	}
	
	/**
	 * Send an error message to the player
	 * @param error - The error message to be sent
	 */
	public void sendError(String error){
		player.sendMessage(ChatColor.GOLD + UTFUtils.getArrow() + ChatColor.DARK_RED + " " + error);
	}
	
	/**
	 * Send the command help page to the player
	 */
	public void sendCommandHelp(){
		this.sendMessageHeader("Command Help");
		this.sendMessage(yellow + "/l stats " + gray + "- Get your level stats!");
		this.sendMessage(yellow + "/l top " + gray + "- Get the overall top list!");
		this.sendMessage(yellow + "/l top <skill> " + gray + "- Get a skill's top list!");
		for(Skill s : Skill.values()){
			player.sendMessage("  " + ChatColor.GOLD + UTFUtils.getArrow() + " " + gray + Utils.getSkillName(s));
		}
		if(player.isOp() == true){
			this.sendMessage(yellow + "/l setlevel <player> <skill> <level> " + gray + "- Set a player's skill level!");
			this.sendMessage(yellow + "/l setpayment overall <amount> " + gray + "- Set the top payment!");
			this.sendMessage(yellow + "/l setpayment skill <amount> " + gray + "- Set the top payment!");
		}
	}
	
	/**
	 * Get the player's skill level
	 * @param skill - The skill level to get
	 * @return The level of the specified skill
	 */
	public int getSkillLevel(Skill skill){
		return this.getConfig().getInt("skills." + skill.toString().toLowerCase() + ".level");
	}
	
	/**
	 * Set the player's skill level
	 * @param skill - The skill level to set
	 * @param level - The level of the skill to set
	 */
	public void setSkillLevel(Skill skill, int level){
		this.setConfigValue("skills." + skill.toString().toLowerCase() + ".level", level);
	}
	
	/**
	 * Get the player's overall skill level
	 * @return The player's overall skill level
	 */
	public int getOverallLevel(){
		int total = 0;
		for(Skill s : Skill.values()){
			total = total + this.getSkillLevel(s);
		}
		return total;
	}
	
	/**
	 * Get the xp required to level up a skill
	 * @param skill - The skill to check
	 * @return The xp required to level up the skill
	 */
	public int getXPToNextLevel(Skill skill){
		return ((this.getSkillLevel(skill) * 3) + 7);
	}
	
	/**
	 * Get the player's skill xp
	 * @param skill - The skill xp to get
	 * @return The xp of the specified skill
	 */
	public double getSkillXP(Skill skill){
		return this.getConfig().getDouble("skills." + skill.toString().toLowerCase() + ".xp");
	}
	
	/**
	 * Set the player's xp level
	 * @param skill - The xp level to set
	 * @param level - The xp of the skill to set
	 */
	public void setSkillXP(Skill skill, double xp){
		this.setConfigValue("skills." + skill.toString().toLowerCase() + ".xp", xp);
	}
	
	/**
	 * Add skill xp to the player
	 * @param skill - The skill to add xp to
	 * @param xp - The amount of xp to add
	 */
	public void addSkillXP(Skill skill, double xp){
		double newxp = this.getSkillXP(skill) + xp;
		if(newxp < this.getXPToNextLevel(skill)){
			this.setSkillXP(skill, newxp);
		}else{
			this.levelUp(skill);
		}
	}
	
	/**
	 * Level up a skill
	 * @param skill - The skill to level up
	 */
	public void levelUp(Skill skill){
		this.setSkillLevel(skill, this.getSkillLevel(skill) + 1);
		this.setSkillXP(skill, 0);
		this.sendMessage("You have leveled up your " + yellow + Utils.getSkillName(skill) + gray + " skill to level " + yellow + this.getSkillLevel(skill) + gray + "!");
		ParticleEffects.sendToLocation(ParticleEffects.GREEN_SPARKLE, player.getEyeLocation().subtract(0, .25, 0), .6F, .6F, .6F, 0F, 15);
	}
	
	/**
	 * Send the skill stats to the player
	 */
	public void sendSkillStatistics(){
		this.sendMessageHeader("Skill Statistics");
		for(Skill s : Skill.values()){
			int level = this.getSkillLevel(s);
			double xp = this.getSkillXP(s);
			int maxxp = this.getXPToNextLevel(s);
			this.sendMessage(yellow + Utils.getSkillName(s));
			player.sendMessage(gray + "    Level: " + yellow + level);
			player.sendMessage(gray + "    Progress: " + yellow + Utils.getProgressBar(Utils.getPercent(xp, maxxp)));
		}
		int totalLevels = this.getOverallLevel();
		this.sendMessage(gray + "Overall Level: " + yellow + totalLevels);
	}
	
	/**
	 * Send the overall skills leaderboard to the player
	 */
	@SuppressWarnings("rawtypes")
	public void sendOverallLeaderboard(){
		this.sendMessageHeader("Overall Skills Leaderboard");
		File folder = new File(main.getDataFolder() + File.separator + "players");
		HashMap<String,Integer> leaderboard = new HashMap<String,Integer>();
		ValueComparator bvc =  new ValueComparator(leaderboard);
        TreeMap<String,Integer> sorted = new TreeMap<String,Integer>(bvc);
		for(File f : folder.listFiles()){
	        LPlayer lp = new LPlayer(f);
	        leaderboard.put(lp.getUsername(), lp.getOverallLevel());
	    }
		sorted.putAll(leaderboard);
		
		Iterator it = sorted.entrySet().iterator();
		int current = 1;
	    while (it.hasNext()) {
	    	if(current <= 9){
		        Map.Entry pairs = (Map.Entry)it.next();
			    this.sendMessage(gray + current + ". " + yellow + pairs.getKey() + gray + " - Total Level: " + yellow + pairs.getValue());
		        it.remove();
		        current++;
	    	}else{
	    		break;
	    	}
	    }
	}
	
	/**
	 * Send an individual skills leaderboard to the player
	 * @param skill - The skill leaderboard to send to the player
	 */
	@SuppressWarnings("rawtypes")
	public void sendSkillLeaderboard(Skill skill){
		this.sendMessageHeader("Skills Leaderboard - " + Utils.getSkillName(skill));
		File folder = new File(main.getDataFolder() + File.separator + "players");
		HashMap<String,Integer> leaderboard = new HashMap<String,Integer>();
		ValueComparator bvc =  new ValueComparator(leaderboard);
        TreeMap<String,Integer> sorted = new TreeMap<String,Integer>(bvc);
		for(File f : folder.listFiles()){
	        LPlayer lp = new LPlayer(f);
	        leaderboard.put(lp.getUsername(), lp.getSkillLevel(skill));
	    }
		sorted.putAll(leaderboard);
		
		Iterator it = sorted.entrySet().iterator();
		int current = 1;
	    while (it.hasNext()) {
	    	if(current <= 9){
		        Map.Entry pairs = (Map.Entry)it.next();
			    this.sendMessage(gray + current + ". " + yellow + pairs.getKey() + gray + " - " + Utils.getSkillName(skill) + " Level: " + yellow + pairs.getValue());
		        it.remove();
		        current++;
	    	}else{
	    		break;
	    	}
	    }
	}
	
	/**
	 * Set the last payment time in milliseconds
	 */
	public void setLastTopPayment(){
		this.setConfigValue("lastTopPayment", System.currentTimeMillis() + 86400000);
	}
	
	/**
	 * Get the last payment time in milliseconds
	 * @return time of last redemption
	 */
	public long getLastTopPayment(){
		return this.getConfig().getLong("lastTopPayment");
	}
	
	/**
	 * Reset the player's cooldown for top payment
	 */
	public void resetLastTopPayment(){
		this.setConfigValue("lastTopPayment", null);
	}
	
	/**
	 * Get if the player can receive a top payment
	 * @return true or false depending on if the player can receive a top payment or not
	 */
	public boolean canGetTopPayment(){
		if((System.currentTimeMillis() - this.getLastTopPayment()) > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Give the player a top payment
	 * @param type - The type of payment to give
	 */
	@SuppressWarnings("deprecation")
	public void giveTopPayment(Leaderboard type){
		if(type == Leaderboard.OVERALL){
			main.econ.depositPlayer(player.getName(), Utils.getOverallPayment());
		}else if(type == Leaderboard.SKILL){
			main.econ.depositPlayer(player.getName(), Utils.getSkillPayment());
		}
	}
}
