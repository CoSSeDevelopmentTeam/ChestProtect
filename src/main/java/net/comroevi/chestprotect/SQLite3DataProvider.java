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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite3DataProvider {

    private ChestProtect plugin;
    private Statement statement;

    public SQLite3DataProvider(ChestProtect plugin) {
        this.plugin = plugin;
        this.connect();
    }
    
    public boolean existsProtect(int x, int y, int z) {
    	try {
    		ResultSet rs = statement.executeQuery("select point from protect where xyz = '"+x+":"+y+":"+z+"'");
			if(rs.getString("xyz") != null) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
    }
    
    public boolean isOwner(String user, int x, int y, int z) {
    	if(existsProtect(x, y, z)) {
			try {
				ResultSet rs = statement.executeQuery("SELECT * from protect WHERE (xyz = '"+x+":"+y+":"+z+"')");
				if(user.equals(rs.getString("user"))) {
					return true;
				} else {
					return false;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return false;
    }
    
    public void addNormalProtect(String owner, int x, int y, int z) {
    	try {
			statement.executeUpdate("insert into protect(owner, xyz, type) values('"+owner+"', '"+x+":"+y+":"+z+"', normal)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void deleteProtect(String user, int x, int y, int z, String type) {
    	if(existsProtect(x, y, z)) {
    		if(isOwner(user, x, y, z)) {
				try {
					ResultSet rs = statement.executeQuery("SELECT * from protect WHERE (xyz = '"+x+":"+y+":"+z+"')");
					statement.executeUpdate("DELETE from protect WHERE id = "+ rs.getInt("id"));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		} else {
    			plugin.getServer().getPlayer(user).sendMessage(ChestProtect.ALERT + plugin.translateString("error-all"));
    		}
    	} else {
    		plugin.getServer().getPlayer(user).sendMessage(ChestProtect.ALERT + plugin.translateString("error-all"));;
    	}
    }

    public void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toString() + "/DataDB.db");
            statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("create table if not exists protect (id integer primary key autoincrement, owner text not null, xyz String not null, type text not null)");
            //statement.executeUpdate("CREATE table if not exists pass_protect (id integer not null, pass integer not null)");
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void printAllData() {
        try {
            ResultSet rs = statement.executeQuery("select * from protect");
            while(rs.next()) {
                System.out.println("id = " + rs.getInt("id"));
                System.out.println("owner = " + rs.getString("owner"));
                System.out.println("xyz = " + rs.getString("xyz"));
                System.out.println("type = " + rs.getInt("type"));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

}
