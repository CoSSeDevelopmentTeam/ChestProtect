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

public class EventListener implements Listener {
	
	ChestProtect plugin;

	public EventListener(ChestProtect plugin) {
		this.plugin = plugin;
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
		    					Map<String, Object> map1 = plugin.getSQL().getOptionProtectData((int) plugin.getSQL().getProtectData((int)block.getX(), (int)block.getY(), (int)block.getZ()).get("id"), "share");
		    					plugin.getSQL().changeProtectType(user, (int)block.getX(), (int)block.getY(), (int)block.getZ(), str[0], (String) map1.get("data"));
		    					optionData.remove(user);
		    					player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-protect-share"));
		    					break;
		    				case "addshare":
		    					event.setCancelled();
		    					Map<String, Object> map2 = plugin.getSQL().getOptionProtectData((int) plugin.getSQL().getProtectData((int)block.getX(), (int)block.getY(), (int)block.getZ()).get("id"), "share");
		    					plugin.getSQL().changeProtectType(user, (int)block.getX(), (int)block.getY(), (int)block.getZ(), str[0], map2.get("data")+","+str[1]);
		    					optionData.remove(user);
		    					player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-protect-share"));
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
			    		switch((String) map.get("type")) {
			    			case "normal":
			    				event.setCancelled();
			    				player.sendMessage(TextValues.INFO + plugin.translateString("error-chest-interact"));
			    				break;
			    			case "pass":
			    				event.setCancelled();
			    				player.sendMessage(TextValues.INFO + plugin.translateString("error-all"));
			    				break;
			    			case "share":
			    				Map<String, Object> shareData = plugin.getSQL().getOptionProtectData((int) map.get("id"), (String) map.get("type"));
			    				if(shareData.get("data").toString().contains(",")) {
			    					String[] list = shareData.get("data").toString().split(",");
			    					if(!list.equals(user)) {
			    						event.setCancelled();
			    						player.sendMessage(TextValues.ALERT + plugin.translateString("error-chest-interact"));
			    					}
			    				} else {
			    					if(!shareData.get("data").equals(user)) {
			    						event.setCancelled();
			    						player.sendMessage(TextValues.ALERT + plugin.translateString("error-chest-interact"));
			    					}
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
