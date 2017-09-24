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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class SQLite3DataProvider {

    private ChestProtect plugin;
    private Statement statement;

    public SQLite3DataProvider(ChestProtect plugin) {
        this.plugin = plugin;
        this.connect();
    }
    
    public boolean existsProtect(int x, int y, int z) {
    	try {
    		ResultSet rs = statement.executeQuery("select * from protect where (xyz = '"+x+":"+y+":"+z+"')");
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
    
    public boolean existsOptionProtect(int x, int y, int z) {
    	if(existsProtect(x, y, z)) {
    		try {
    			ResultSet rs = statement.executeQuery("select * from protect where (xyz = '"+x+":"+y+":"+z+"')");
				if(statement.executeQuery("select * from option_protect where (id = "+rs.getInt("id")+")") != null) {
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
    
    public boolean isOwner(String user, int x, int y, int z) {
    	if(existsProtect(x, y, z)) {
			try {
				ResultSet rs = statement.executeQuery("select * from protect where (xyz = '"+x+":"+y+":"+z+"')");
				if(user.equals(rs.getString("owner"))) {
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
    
    public Map<String, Object> getProtectData(int x, int y, int z) {
    	Map<String, Object> list = new HashMap<String, Object>();
    	try {
			ResultSet rs = statement.executeQuery("select * from protect where (xyz = '"+x+":"+y+":"+z+"')");
			while(rs.next()) {
				list.put("id", rs.getInt("id"));
				list.put("owner", rs.getString("owner"));
				list.put("xyz", rs.getString("xyz"));
				list.put("type", rs.getString("type"));
			}
			rs.close();
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
    }
    
    public Map<String, Object> getOptionProtectData(int id) {
    	Map<String, Object> list = new HashMap<String, Object>();
    	try {
			ResultSet rs = statement.executeQuery("select * from option_protect where (id = "+id+")");
			while(rs.next()) {
				list.put("data", rs.getString("data"));
			}
			rs.close();
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
    }
    
    public void addProtect(String owner, int x, int y, int z, String type) {
    	try {
			statement.executeUpdate("insert into protect(owner, xyz, type) values('"+owner+"', '"+x+":"+y+":"+z+"', '"+type+"')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void addOptionProtect(String type, int x, int y, int z, String data) {
    	try {
    		Map<String, Object> map = getProtectData(x, y, z);
			statement.executeUpdate("insert into option_protect(id, data) values("+(int) map.get("id")+", '"+data+"')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void changeProtectType(String owner, int x, int y, int z, String type, String value) {
    	if(existsProtect(x, y, z)) {
			Map<String, Object> map = getProtectData(x, y, z);
			switch(type) {
				case "normal":
					try {
						statement.executeUpdate("update protect set type = 'normal' where (xyz = '"+x+":"+y+":"+z+"')");
						if(existsOptionProtect(x, y, z)) {
							statement.executeUpdate("delete from option_protect where (id = "+(int) map.get("id")+")");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case "pass":
					try {
						statement.executeUpdate("update protect set type = 'pass' where (xyz = '"+x+":"+y+":"+z+"')");
						if(existsOptionProtect(x, y, z)) {
							statement.executeUpdate("delete from option_protect where (id = "+(int) map.get("id")+")");
						}
						addOptionProtect("pass", x, y, z, value);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case "share":
					try {
						statement.executeUpdate("update protect set type = 'share' where (xyz = '"+x+":"+y+":"+z+"')");
						if(existsOptionProtect(x, y, z)) {
							statement.executeUpdate("delete from option_protect where (id = "+(int) map.get("id")+")");
						}
						addOptionProtect("share", x, y, z, value);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case "addshare":
					try {
						statement.executeUpdate("update option_protect set data = '"+value+"' where (id = "+(int) map.get("id")+")");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case "public":
					try {
						statement.executeUpdate("update protect set type = 'public' where (xyz = '"+x+":"+y+":"+z+"')");
						if(existsOptionProtect(x, y, z)) {
							statement.executeUpdate("delete from option_protect where (id = "+(int) map.get("id")+")");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
			}
    	}
    }

    public void deleteProtect(String user, int x, int y, int z) {
    	if(existsProtect(x, y, z)) {
			try {
				Map<String, Object> map = getProtectData(x, y, z);
				switch((String) map.get("type")) {
					case "normal":
					case "public":
						statement.executeUpdate("delete from protect where (id = "+(int) map.get("id")+")");
						break;
					case "pass":
					case "share":
						statement.executeUpdate("delete from option_protect where (id = "+(int) map.get("id")+")");
						statement.executeUpdate("delete from protect where (id = "+(int) map.get("id")+")");
						break;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			plugin.getServer().getPlayer(user).sendMessage(TextValues.ALERT + plugin.translateString("error-all"));
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
            statement.executeUpdate("create table if not exists protect (id integer primary key autoincrement, owner text not null, xyz text not null, type text not null)");
            statement.executeUpdate("create table if not exists option_protect (id integer not null, data text)");
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
                System.out.println("type = " + rs.getString("type"));
            }
            rs.close();
            ResultSet option_rs = statement.executeQuery("select * from option_protect");
            while(option_rs.next()) {
            	System.out.println("id = " + rs.getInt("id"));
                System.out.println("data = " + rs.getString("data"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

}
