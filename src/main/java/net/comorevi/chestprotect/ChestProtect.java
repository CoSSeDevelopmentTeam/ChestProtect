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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;

public class ChestProtect extends PluginBase {
	
	private Config translateFile;
    private Map<String, Object> configData = new HashMap<String, Object>();
    private Map<String, Object> pluginData = new HashMap<String, Object>();
    private Config conf;
    
    public static Map<String, String[]> optionData = new HashMap<String, String[]>();
    
    private SQLite3DataProvider sql;
	private String debug;
	
	@Override
	public void onEnable() {
		
		this.getDataFolder().mkdir();
		this.initMessageConfig();
        this.initChestProtectConfig();
        this.initHelpFile();
		
		this.sql = new SQLite3DataProvider(this);
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        
        if(debug.equals("true")) {
        	sql.printAllData();
        }
		
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args){

        if(command.getName().equals("chest")){

            if(sender instanceof ConsoleCommandSender){
                sender.sendMessage(TextValues.WARNING + this.translateString("error-command-console"));
                return true;
            }

            try {
            	if(args[0] != null);
            } catch(ArrayIndexOutOfBoundsException e) {
                this.helpMessage(sender);
                return true;
            }

            switch(args[0]) {
                case "info":
                	sender.sendMessage("このコマンドは未実装です。");
                	break;
                case "mode":
                	if(args.length <= 1) {
                		sender.sendMessage(TextValues.HELP + "/chest mode [type]");
                	} else {
                		switch(args[1]) {
                			case "normal":
	            				String[] str_normal = {"normal", null};
	            				optionData.put(sender.getName(), str_normal);
	            				sender.sendMessage(TextValues.INFO + this.translateString("player-command-message1"));
	            				break;
	            			case "public":
	            				String[] str_public = {"public", null};
	            				optionData.put(sender.getName(), str_public);
	            				sender.sendMessage(TextValues.INFO + this.translateString("player-command-message1"));
	            				break;
                		}
                	}
                	if(args.length <= 2) {
                		sender.sendMessage(TextValues.HELP + "/chest [ pass | share | addshare ] [ value ]");
                	} else {
                		switch(args[1]) {
                			case "pass":
                				String[] str_pass = {"pass", args[2]};
                				optionData.put(sender.getName(), str_pass);
                				sender.sendMessage(TextValues.INFO + this.translateString("player-command-message1"));
                				break;
                			case "share":
                				String[] str_share = {"share", args[2]};
                				optionData.put(sender.getName(), str_share);
                				sender.sendMessage(TextValues.INFO + this.translateString("player-command-message1"));
                				break;
                			case "addshare":
                				if(new File(getServer().getFilePath().toString() + "/players/"+args[2].toLowerCase()+".dat").exists()) {
                        			String[] str_addshare = {"addshare", args[2]};
                    				optionData.put(sender.getName(), str_addshare);
                    				sender.sendMessage(TextValues.INFO + this.translateString("player-command-message1"));
                        		} else {
                        			sender.sendMessage(TextValues.ALERT + this.translateString("error-command-message4"));
                        		}
                				break;
                		}
                	}
                	break;
                case "share":
                	if(args.length <= 1) {
                		sender.sendMessage(TextValues.HELP + "/chest share [ target ]");
                	} else {
                		if(new File(getServer().getFilePath().toString() + "/players/"+sender.getName().toLowerCase()+".dat").exists()) {
                			String[] str_share = {"addshare", args[1]};
            				optionData.put(sender.getName(), str_share);
            				sender.sendMessage(TextValues.INFO + this.translateString("player-command-message1"));
                		} else {
                			sender.sendMessage(TextValues.ALERT + this.translateString("error-command-message4"));
                		}
                	}
                	break;
            }
        }
        return false;
    }
	
	public SQLite3DataProvider getSQL() {
		return this.sql;
	}
	
	public void helpMessage(CommandSender sender){
        Thread th = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(getDataFolder().toString() + "/Help.txt")), "UTF-8"));
                    String txt;
                    boolean op = (boolean) sender.isOp();
                    boolean send = true;
                    while(true){
                        txt = br.readLine();
                        if(txt == null)break;
                        if(txt.startsWith("##"))continue;
                        if(txt.equals("::op")){
                            send = false;
                            continue;
                        }
                        if(op)send = true;
                        if(txt.equals("::all")){
                            send = true;
                            continue;
                        }
                        if(send) sender.sendMessage(txt);
                    }
                    br.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
                return;
            }
        });
        th.start();
    }

    public String translateString(String key, String... args){
        if(configData != null || !configData.isEmpty()){
            String src = (String) configData.get(key);
            if(src == null || src.equals("")) return TextValues.ALERT + (String) configData.get("error-notFoundKey");
            for(int i=0;i < args.length;i++){
                src = src.replace("{%" + i + "}", args[i]);
            }
            return src;
        }
        return null;
    }

    public String parseMessage(String message) {
        return "";
    }

    private void initMessageConfig(){
        if(!new File(getDataFolder().toString() + "/Message.yml").exists()){
            try {
                FileWriter fw = new FileWriter(new File(getDataFolder().toString() + "/Message.yml"), true);//trueで追加書き込み,falseで上書き
                PrintWriter pw = new PrintWriter(fw);
                pw.println("");
                pw.close();
                Utils.writeFile(new File(getDataFolder().toString() + "/Message.yml"), this.getClass().getClassLoader().getResourceAsStream("Message.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.translateFile = new Config(new File(getDataFolder().toString() + "/Message.yml"), Config.YAML);
        this.translateFile.load(getDataFolder().toString() + "/Message.yml");
        this.configData = this.translateFile.getAll();
        return;
    }

    private void initChestProtectConfig(){
        if(!new File(getDataFolder().toString() + "/Config.yml").exists()){
            try {
                FileWriter fw = new FileWriter(new File(getDataFolder().toString() + "/Config.yml"), true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println("");
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.conf = new Config(new File(getDataFolder().toString() + "/Config.yml"), Config.YAML);
            this.conf.load(getDataFolder().toString() + "/Config.yml");
            this.conf.set("PrintData", "false");
            this.conf.save();
        }

        this.conf = new Config(new File(getDataFolder().toString() + "/Config.yml"), Config.YAML);
        this.conf.load(getDataFolder().toString() + "/Config.yml");
        this.pluginData = this.conf.getAll();

        /*コンフィグからデータを取得*/        
        debug = (String) pluginData.get("PrintData");

        return;
    }
    
    public void initHelpFile(){
    	if(!new File(getDataFolder().toString() + "/Help.txt").exists()){
            try {
                FileWriter fw = new FileWriter(new File(getDataFolder().toString() + "/Help.txt"), true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println("");
                pw.close();
                
                Utils.writeFile(new File(getDataFolder().toString() + "/Help.txt"), this.getClass().getClassLoader().getResourceAsStream("Help.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
    	}
    	return;
    }

}
