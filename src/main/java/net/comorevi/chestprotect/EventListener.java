/**
 * ChestProtect
 *
 *
 * CosmoSunriseServerPluginEditorsTeam
 *
 * HP: http://info.comorevi.net
 * GitHub: https://github.com/CosmoSunriseServerPluginEditorsTeam
 *
 *
 *
 *
 * [Java版]
 * @author popkechupki
 *
 *
 */

package net.comorevi.chestprotect;

import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
	
	ChestProtect plugin;

	public EventListener(ChestProtect plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		String user = event.getPlayer().getName();
		Map<String, String[]> optionData = ChestProtect.optionData;
		if(optionData.containsKey(user)) {
			optionData.remove(user);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
	    Player player = event.getPlayer();
	    String user = player.getName();
	    Block block = event.getBlock();
	    switch (block.getId()) {
		    case Block.CHEST:
		    	if(plugin.getSQL().isOwner(user, (int)block.getX(), (int)block.getY(), (int)block.getZ())) {
		    		Map<String, String[]> optionData = ChestProtect.optionData;
		    		if(optionData.containsKey(user)) {
		    			String[] str = optionData.get(user);
		    			switch(str[0]) {
		    				case "info":
		    					event.setCancelled();
		    					Map<String, Object> protectData = plugin.getSQL().getProtectData((int)block.getX(), (int)block.getY(), (int)block.getZ());
		    					if(protectData.get("type").toString().equals("share") || protectData.toString().equals("pass")) {
		    						Map<String, Object> optionProtectData = plugin.getSQL().getOptionProtectData((int) protectData.get("id"));
		    						player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-optionprotect-info", protectData.get("id").toString(), protectData.get("owner").toString(), protectData.get("xyz").toString(), protectData.get("type").toString(), optionProtectData.get("data").toString()));
		    					} else {
		    						player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-protect-info", protectData.get("id").toString(), protectData.get("owner").toString(), protectData.get("xyz").toString(), protectData.get("type").toString()));
		    					}
		    					optionData.remove(user);
		    					break;
		    				case "normal":
		    					event.setCancelled();
		    					plugin.getSQL().changeProtectType(user, (int)block.getX(), (int)block.getY(), (int)block.getZ(), str[0], null);
		    					player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-protect-normal"));
		    					optionData.remove(user);
		    					break;
		    				case "pass":
		    					event.setCancelled();
		    					plugin.getSQL().changeProtectType(user, (int)block.getX(), (int)block.getY(), (int)block.getZ(), str[0], str[1]);
		    					player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-protect-pass"));
		    					optionData.remove(user);
		    					break;
		    				case "share":
		    					event.setCancelled();
	    						plugin.getSQL().changeProtectType(user, (int)block.getX(), (int)block.getY(), (int)block.getZ(), str[0], str[1]);
		    					player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-protect-share"));
		    					optionData.remove(user);
		    					break;
		    				case "addshare":
		    					event.setCancelled();
		    					if(plugin.getSQL().existsOptionProtect((int)block.getX(), (int)block.getY(), (int)block.getZ())) {
		    						Map<String, Object> map2 = plugin.getSQL().getOptionProtectData((int) plugin.getSQL().getProtectData((int)block.getX(), (int)block.getY(), (int)block.getZ()).get("id"));
			    					if(map2.get("data").toString().contains(str[1])) {
			    						player.sendMessage(TextValues.INFO + plugin.translateString("error-chest-addshare"));
			    					} else {
			    						plugin.getSQL().changeProtectType(user, (int)block.getX(), (int)block.getY(), (int)block.getZ(), str[0], map2.get("data")+","+str[1]);
				    					player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-protect-addshare", str[1]));
			    					}
		    					}
		    					optionData.remove(user);
		    					break;
		    				case "public":
		    					event.setCancelled();
		    					plugin.getSQL().changeProtectType(user, (int)block.getX(), (int)block.getY(), (int)block.getZ(), str[0], null);
		    					player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-protect-public"));
		    					optionData.remove(user);
		    					break;
		    			}
		    		}
		    	} else {
		    		if(player.isOp()) {
		    			player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-interact-byOp"));
		    		} else {
		    			Map<String, Object> map = plugin.getSQL().getProtectData((int)block.getX(), (int)block.getY(), (int)block.getZ());
		    			Map<String, Object> shareData = plugin.getSQL().getOptionProtectData((int) map.get("id"));
			    		switch((String) map.get("type")) {
			    			case "normal":
			    				event.setCancelled();
			    				player.sendMessage(TextValues.INFO + plugin.translateString("error-chest-interact"));
			    				break;
			    			case "pass":
			    				if(ChestProtect.optionData.containsKey(user)) {
			    					if(!shareData.get("data").toString().equals(ChestProtect.optionData.get(user)[1])) {
				    					event.setCancelled();
				    					player.sendMessage(TextValues.INFO + plugin.translateString("error-chest-interact-pass"));
				    				}
			    					ChestProtect.optionData.remove(user);
			    				} else {
			    					event.setCancelled();
			    					player.sendMessage(TextValues.INFO + plugin.translateString("error-chest-interact"));
			    				}
			    				break;
			    			case "share":
			    				if(!shareData.get("data").toString().contains(user)) {
			    					event.setCancelled();
			    					player.sendMessage(TextValues.INFO + plugin.translateString("error-chest-interact"));
			    				}
			    				break;
			    			case "public":
			    				player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-interact-type-public"));
			    				break;
			    		}
		    		}	
		    	}
		    	break;
	    }
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		String user = player.getName();
	    Block block = event.getBlock();
	    switch (block.getId()) {
		    case Block.CHEST:
	    		if(plugin.getSQL().isOwner(user, (int)block.getX(), (int)block.getY(), (int)block.getZ())) {
		    		plugin.getSQL().deleteProtect(user, (int)block.getX(), (int)block.getY(), (int)block.getZ());
		    		player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-break"));
		    	} else {
		    		if(player.isOp()) {
		    			player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-break-byOp"));
		    			plugin.getSQL().deleteProtect(user, (int)block.getX(), (int)block.getY(), (int)block.getZ());
		    		} else {
		    			event.setCancelled();
			    		player.sendMessage(TextValues.ALERT + plugin.translateString("error-chest-break"));
		    		}	
		    	}
		    	break;
	    }
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		String owner = player.getName();
	    Block block = event.getBlock();
	    switch (block.getId()) {
		    case Block.CHEST:
		    	plugin.getSQL().addProtect(owner, (int)block.getX(), (int)block.getY(), (int)block.getZ(), "normal");
		    	player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-place"));
		    	break;
	    }
	}
	
}
