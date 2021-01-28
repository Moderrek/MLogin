package pl.moderr.mlogin.mysql;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.moderr.mlogin.MLogin;
import pl.moderr.mlogin.ModerrEncrypt;

import java.sql.*;
import java.util.HashMap;

public class MySQL {


    private Connection connection;
    private final String passwordTable = "MLogin_password";
    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;
    public MySQL(){
        host = MLogin.instance.getConfig().getString("database-host");
        port = MLogin.instance.getConfig().getString("database-port");
        database = MLogin.instance.getConfig().getString("database-database");
        username = MLogin.instance.getConfig().getString("database-username");
        password = MLogin.instance.getConfig().getString("database-password");
        try {
            if(ConnectionClosed()) {
                OpenConnection();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void InitializePasswordTable() throws SQLException {
        repairConnection();
        String sqlCreate = "CREATE TABLE IF NOT EXISTS `" + database + "`.`" + passwordTable + "` ( `UUID` TEXT NOT NULL, `PASSWORD` TEXT NOT NULL)";
        Statement stmt = connection.createStatement();
        stmt.execute(sqlCreate);
    }

    public void doAsyncRegister(Player p, String registerPassword, final Callback<HashMap<String, String>> callback){
        repairConnection();
        Bukkit.getScheduler().runTaskAsynchronously(MLogin.instance, () -> {
            try{
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO `" + passwordTable +"` (`UUID`,`PASSWORD`) VALUES (?,?)");
                stmt.setString(1, p.getUniqueId().toString());
                stmt.setString(2, ModerrEncrypt.encrypt(registerPassword, "moderrhaslo123"));
                stmt.execute();
                stmt.close();
            } catch (SQLException ex) { callback.onFailure(ex); System.out.println("-> doAsyncRegister"); ex.printStackTrace(); }
            Bukkit.getScheduler().runTask(MLogin.instance, () -> callback.onDone(null));
        });
    }
    public void doAsyncPassword(Player p, String password, final PasswordCallback<HashMap<String, String>> callback){
        repairConnection();
        Bukkit.getScheduler().runTaskAsynchronously(MLogin.instance, () -> {
            final HashMap<String, String> result = new HashMap<>();
            try{
                ResultSet rs = QueryPasswordTable(p.getUniqueId().toString());
                if(rs.next()){
                    result.put("PASSWORD", rs.getString("PASSWORD"));
                    callback.onRegistered();
                    if(result.containsKey("PASSWORD")){
                        if(ModerrEncrypt.decrypt(result.get("PASSWORD"), "moderrhaslo123").equals(password)){
                            callback.onPasswordSuccess(result);
                        }else{
                            callback.onPasswordFailure(result);
                        }
                    } else{
                        callback.onPasswordFailure(result);
                    }
                }else{
                    callback.notRegistered();
                    callback.onSuccess(result);
                    return;
                }
                rs.close();
            } catch (SQLException ex) { callback.onFailure(ex); System.out.println("-> doAsyncPassword"); ex.printStackTrace(); }
            Bukkit.getScheduler().runTask(MLogin.instance, () -> {
                callback.onSuccess(result);
            });
        });
    }

    public void repairConnection(){
        try{
            connection.createStatement().close();
        }catch(Exception e){
            try {
                OpenConnection();
                connection.createStatement().close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    public boolean ConnectionClosed() throws SQLException {
        return connection == null || connection.isClosed();
    }
    public void OpenConnection(){
        Bukkit.getScheduler().runTaskAsynchronously(MLogin.instance, () -> {
            try {
                if (connection != null && !connection.isClosed()) {
                    try{
                        System.out.println("Połączenie jest już otwarte");
                    }catch(Exception ignored){

                    }
                }else{
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useJDBCCompliantTimezoneShift=true&&serverTimezone=UTC&&useUnicode=true&autoReconnect=true&useSSL=false&tcpKeepAlive=true", username, password);
                    InitializePasswordTable();
                    try{
                        System.out.println("Connected");
                        System.out.println("Połączenie jest zerwane trwa łaczenie...");
                        System.out.println("Połączono!");
                    }catch(Exception ignored){
                    }
                }
            } catch (SQLException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        });
    }
    public ResultSet QueryPasswordTable(String UUID) throws SQLException {
        repairConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT *  FROM `" + passwordTable + "` WHERE `UUID`=?");
        stmt.setString(1, UUID);
        return stmt.executeQuery();
    }

    public interface Callback<T> {
        void onSuccess(T success);
        void onFail(T fail);
        void onDone(T done);
        void onFailure(Throwable cause);
    }
    public interface PasswordCallback<T>{
        void onRegistered();
        void notRegistered();
        void onSuccess(T done);
        void onPasswordSuccess(T success);
        void onPasswordFailure(T fail);
        void onFailure(Throwable cause);
    }

}
