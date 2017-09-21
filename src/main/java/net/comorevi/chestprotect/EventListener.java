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
		    	if(!plugin.getSQL().isOwner(user, (int)block.getX(), (int)block.getY(), (int)block.getZ())) {
		    		Map<String, Object> map = plugin.getSQL().getProtectData((int)block.getX(), (int)block.getY(), (int)block.getZ());
		    		switch((String) map.get("type")) {
		    			case "pass":
		    				event.setCancelled();
		    				player.sendMessage(TextValues.INFO + plugin.translateString("error-all"));
		    				break;
		    			case "share":
		    				//Map<String, Object> sharedata = plugin.getSQL().getOptionProtectData((int) map.get("id"), (String) map.get("type"));
		    				//,が含まれてるか。含まれてなければそのまま比較、含まれてたら分割して配列内にあるか確認
		    				break;
		    			case "public":
		    				player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-interact-type-public"));
		    				break;
		    		}
		    		if(player.isOp()) {
		    			player.sendMessage(TextValues.INFO + plugin.translateString("player-chest-interact-byOp"));
		    		} else {
		    			event.setCancelled();
			    		player.sendMessage(TextValues.INFO + plugin.translateString("error-chest-interact"));
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
