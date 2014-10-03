package com.pwncraftpvp.levels.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.pwncraftpvp.levels.utils.Utils;

public class Events implements Listener{
	
	static Main main = Main.getInstance();
	private String gray = ChatColor.GRAY + "";
	private String yellow = ChatColor.YELLOW + "";
	
	public static List<MinedBlock> minedBlocks = new ArrayList<MinedBlock>();
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		final LPlayer lplayer = new LPlayer(player);
		if(lplayer.getUsername() == null || !lplayer.getUsername().equalsIgnoreCase(player.getName())){
			lplayer.setUsername(player.getName());
		}
		
		/*
		 * Fixes for things on Prison that are broken (for some unknown reasons)
		 */
		main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
			public void run(){
				if(lplayer.isNew() == true){
					player.getInventory().setHelmet(null);
					player.getInventory().setChestplate(null);
					player.getInventory().setLeggings(null);
					player.getInventory().setBoots(null);
					player.getInventory().clear();
					
					boolean wasOp = player.isOp();
					player.setOp(true);
					try{
						player.performCommand("kit beginner");
					}catch (Exception ex){
						player.setOp(wasOp);
					}
					player.setOp(wasOp);
					lplayer.setNew(false);
				}
				
				boolean gotPaid = false;
				if(Utils.getTopThree().contains(player.getName())){
					if(lplayer.canGetTopPayment() == true){
						gotPaid = true;
						lplayer.giveTopPayment(Leaderboard.OVERALL);
						lplayer.sendMessage("You have received $" + yellow + Utils.getOverallPayment() + gray + " for being on the leaderboard!");
					}
				}
				for(Skill s : Skill.values()){
					if(Utils.getTopThree(s).contains(player.getName())){
						if(lplayer.canGetTopPayment() == true){
							gotPaid = true;
							lplayer.giveTopPayment(Leaderboard.SKILL);
							lplayer.sendMessage("You have received $" + yellow + Utils.getSkillPayment() + gray + " for being on the leaderboard!");
						}
					}
				}
				if(gotPaid == true){
					lplayer.setLastTopPayment();
				}
				
				player.setLevel(lplayer.getSavedLevel());
				player.setExp(lplayer.getSavedXP());
			}
		}, 30);
	}
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		LPlayer lplayer = new LPlayer(player);
		lplayer.setSavedLevel(player.getLevel());
		lplayer.setSavedXP(player.getExp());
	}
	
	@EventHandler
	public void playerKick(PlayerKickEvent event){
		Player player = event.getPlayer();
		LPlayer lplayer = new LPlayer(player);
		lplayer.setSavedLevel(player.getLevel());
		lplayer.setSavedXP(player.getExp());
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent event){
		if(event.getEntity().getKiller() != null){
			Player killer = (Player) event.getEntity().getKiller();
			LPlayer lkiller = new LPlayer(killer);
			lkiller.addSkillXP(Skill.COMBAT, 1.5);
		}
	}
	
	/*
	 * BlockMine plugin now handles Mining level
	 * 
	@EventHandler
	public void blockBreak(BlockBreakEvent event){
		if(event.getPlayer() != null){
			Player player = event.getPlayer();
			LPlayer lplayer = new LPlayer(player);
			
			if(Utils.canBreakHere(player, event.getBlock().getLocation()) == true){
				if(event.getBlock().getType() == Material.STONE || event.getBlock().getType() == Material.COAL_ORE || event.getBlock().getType() == Material.LAPIS_ORE ||
						event.getBlock().getType() == Material.REDSTONE_ORE || event.getBlock().getType() == Material.IRON_ORE || event.getBlock().getType() == Material.GOLD_ORE ||
						event.getBlock().getType() == Material.DIAMOND_ORE || event.getBlock().getType() == Material.CROPS){
					
					double x,y,z;
					x = event.getBlock().getLocation().getX();
					y = event.getBlock().getLocation().getY();
					z = event.getBlock().getLocation().getZ();
					
					boolean canAddXP = true;
					for(MinedBlock b : minedBlocks){
						if(x == b.getX() && y == b.getY() && z == b.getZ()){
							canAddXP = false;
							break;
						}
					}
					
					if(canAddXP == true){
						MinedBlock block = new MinedBlock(x, y, z);
						if(event.getBlock().getType() == Material.STONE){
							lplayer.addSkillXP(Skill.MINING, 0.2);
							minedBlocks.add(block);
						}else if(event.getBlock().getType() == Material.COAL_ORE || event.getBlock().getType() == Material.LAPIS_ORE || event.getBlock().getType() == Material.REDSTONE_ORE){
							lplayer.addSkillXP(Skill.MINING, 0.75);
							minedBlocks.add(block);
						}else if(event.getBlock().getType() == Material.IRON_ORE){
							lplayer.addSkillXP(Skill.MINING, 2);
							minedBlocks.add(block);
						}else if(event.getBlock().getType() == Material.GOLD_ORE){
							lplayer.addSkillXP(Skill.MINING, 1);
							minedBlocks.add(block);
						}else if(event.getBlock().getType() == Material.DIAMOND_ORE){
							lplayer.addSkillXP(Skill.MINING, 10);
							minedBlocks.add(block);
						}else if(event.getBlock().getType() == Material.CROPS){
							Crops crop = (Crops) event.getBlock().getState().getData();
							if(crop.getState() == CropState.RIPE){
								lplayer.addSkillXP(Skill.FARMING, 1);
							}else if(crop.getState() == CropState.VERY_TALL){
								lplayer.addSkillXP(Skill.FARMING, 0.5);
							}else if(crop.getState() == CropState.TALL){
								lplayer.addSkillXP(Skill.FARMING, 0.35);
							}else if(crop.getState() == CropState.MEDIUM){
								lplayer.addSkillXP(Skill.FARMING, 0.2);
							}else if(crop.getState() == CropState.SMALL){
								lplayer.addSkillXP(Skill.FARMING, 0.05);
							}
							minedBlocks.add(block);
						}
					}
				}
			}
		}
	}
	*/
	
	@EventHandler
	public void playerFish(PlayerFishEvent event){
		Player player = event.getPlayer();
		LPlayer lplayer = new LPlayer(player);
		if(event.getHook().getLocation().getBlock().getType() == Material.WATER || event.getHook().getLocation().getBlock().getType() == Material.STATIONARY_WATER){
			if(event.getCaught() == null){
				lplayer.addSkillXP(Skill.FISHING, 0.15);
			}else if(event.getCaught() != null){
				if(event.getCaught().getType() == EntityType.DROPPED_ITEM){
					lplayer.addSkillXP(Skill.FISHING, 7);
				}
			}
		}
	}
}
