package com.pwncraftpvp.levels.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.milkbowl.vault.economy.Economy;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.pwncraftpvp.levels.core.LPlayer;
import com.pwncraftpvp.levels.core.Main;
import com.pwncraftpvp.levels.core.Skill;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Utils {
	
	static Main main = Main.getInstance();
	
	/**
	 * Get the WorldGuardPlugin
	 * @return - The WorldGuardPlugin
	 */
	public static WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = main.getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}
	
	/**
	 * Check if a player can build at the location	
	 * @param player - The player to perform checks on
	 * @return - True or false depending on if the player can build at the location or not
	 */
	public static boolean canBreakHere(Player player, Location loc){
		return getWorldGuard().canBuild(player, loc);
	}
	
	/**
	 * Set up the plugin's economy for use
	 */
    public static void setupEconomy(Main pl){
    	if(pl.getServer().getPluginManager().getPlugin("Vault") != null){
        	RegisteredServiceProvider<Economy> rsp = pl.getServer().getServicesManager().getRegistration(Economy.class);
        	if(rsp != null){
        		pl.econ = rsp.getProvider();
        	}
        }
    }
	
	/**
	 * Check if a string is also an integer
	 * @param isIt - The string to check
	 * @return True or false depending on if the string is an integer or not
	 */
	public static boolean isInteger(String isIt){
		try{
			Integer.parseInt(isIt);
			return true;
		}catch (Exception ex){
			return false;
		}
	}
	
	/**
	 * Get a skill from a string
	 * @param name - The string to convert to a skill
	 * @return The skill that has been converted from the string
	 */
	public static Skill getSkill(String name){
		Skill skill = null;
		for(Skill s : Skill.values()){
			if(s.toString().equalsIgnoreCase(name)){
				skill = s;
				break;
			}
		}
		return skill;
	}
	
	/**
	 * Get a skill's proper name
	 * @param skill - The skill to get the proper name of
	 * @return The proper name of the skill
	 */
	public static String getSkillName(Skill skill){
		return WordUtils.capitalizeFully(skill.toString());
	}
	
	/**
	 * Round a number to the decimal point
	 * @param d - The number to round
	 * @return The rounded number
	 */
	public static float roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        return Float.valueOf(twoDForm.format(d));
    }
	
	/**
	 * Get the percent of two integers
	 * @param n - The first integer
	 * @param v - The second integer
	 * @return The percent out of 100
	 */
	public static float getPercent(double n, int v){
		return roundTwoDecimals((float) ((n * 100) / v));
	}
	
	/**
	 * Get the payment for the overall leaderboard
	 * @return The overall leaderboard payment
	 */
	public static int getOverallPayment(){
		return main.getConfig().getInt("payments.overall");
	}
	
	/**
	 * Set the payment for the overall leaderboard
	 * @param amount - The overall leaderboard payment to set
	 */
	public static void setOverallPayment(int amount){
		main.getConfig().set("payments.overall", amount);
		main.saveConfig();
	}
	
	/**
	 * Get the payment for a skills leaderboard
	 * @return The overall skills payment
	 */
	public static int getSkillPayment(){
		return main.getConfig().getInt("payments.skills");
	}
	
	/**
	 * Set the payment for a skills leaderboard
	 * @param amount - The skills leaderboard payment to set
	 */
	public static void setSkillPayment(int amount){
		main.getConfig().set("payments.skills", amount);
		main.saveConfig();
	}
	
	/**
	 * Get a progress bar based on a percent
	 * @param percent - The percent to get a progress bar of
	 * @return A string with the progress bar
	 */
	public static String getProgressBar(float percent){
		int amount = (int) percent / 10;
		String bar = getBars(amount);
		return bar;
	}
	
	/**
	 * Get a bar with green and gray colors
	 * @param green - The amount of green colors
	 * @return A string with the bar
	 */
	public static String getBars(int green){
		green = (green * 2);
		String bars = "";
		int gray = 20 - green;
		int greenAmnt,grayAmnt;
		greenAmnt = 0;
		grayAmnt = 0;
		for(int x = 1; x <= 20; x++){
			if(greenAmnt != green){
				bars = bars + ChatColor.GREEN + UTFUtils.getBar();
				greenAmnt++;
			}else if(grayAmnt != gray){
				bars = bars + ChatColor.GRAY + UTFUtils.getBar();
				grayAmnt++;
			}
		}
		return bars;
	}
	
	/**
	 * Get the top three list of players
	 * @param skill - The skill to get the top three list of
	 * @return The top three list of players
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getTopThree(Skill skill){
		List<String> top = new ArrayList<String>();
		
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
	    	if(current <= 3){
		        Map.Entry pairs = (Map.Entry)it.next();
		        it.remove();
		        top.add((String) pairs.getKey());
		        current++;
	    	}else{
	    		break;
	    	}
	    }
	    
		return top;
	}
	
	/**
	 * Get the top three list of players
	 * @return The top three list of players
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getTopThree(){
		List<String> top = new ArrayList<String>();
		
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
			    top.add((String) pairs.getKey());
		        it.remove();
		        current++;
	    	}else{
	    		break;
	    	}
	    }
		
		return top;
	}
	
	/**
	 * Set the amount of money received upon leveling up
	 * @param money - The amount of money to be received upon leveling up
	 */
	public static void setLevelUpMoney(int money){
		main.getConfig().set("payments.levelup", money);
		main.saveConfig();
	}
	
	/**
	 * Get the amount of money received upon leveling up
	 * @return The amount of money received upon leveling up
	 */
	public static int getLevelUpMoney(){
		return main.getConfig().getInt("payments.levelup");
	}
}
