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
import org.bukkit.plugin.RegisteredServiceProvider;

import com.pwncraftpvp.levels.core.LPlayer;
import com.pwncraftpvp.levels.core.Main;
import com.pwncraftpvp.levels.core.Skill;

public class Utils {
	
	static Main main = Main.getInstance();
	
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

}
