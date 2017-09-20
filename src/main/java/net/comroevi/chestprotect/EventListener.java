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

package net.comroevi.chestprotect;

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
		    		if(player.isOp()) {
		    			player.sendMessage(ChestProtect.INFO + plugin.translateString("player-chest-interact-byOp"));
		    		} else {
		    			event.setCancelled();
			    		player.sendMessage(ChestProtect.INFO + plugin.translateString("error-chest-interact"));
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
		    		plugin.getSQL().deleteProtect(user, (int)block.getX(), (int)block.getY(), (int)block.getZ(), "normal");
		    		player.sendMessage(ChestProtect.INFO + plugin.translateString("player-chest-break"));
		    	} else {
		    		if(player.isOp()) {
		    			player.sendMessage(ChestProtect.INFO + plugin.translateString("player-chest-break-byOp"));
		    			plugin.getSQL().deleteProtect(user, (int)block.getX(), (int)block.getY(), (int)block.getZ(), "normal");
		    		} else {
		    			event.setCancelled();
			    		player.sendMessage(ChestProtect.ALERT + plugin.translateString("error-chest-break"));
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
		    	plugin.getSQL().addNormalProtect(owner, (int)block.getX(), (int)block.getY(), (int)block.getZ());
		    	player.sendMessage(ChestProtect.INFO + plugin.translateString("player-chest-place"));
		    	break;
	    }
	}
	
}
