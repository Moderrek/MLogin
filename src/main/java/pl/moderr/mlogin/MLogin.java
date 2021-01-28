package pl.moderr.mlogin;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.moderr.mlogin.commands.ZalogujCommand;
import pl.moderr.mlogin.commands.ZarejestrujCommand;
import pl.moderr.mlogin.listeners.AuthListener;
import pl.moderr.mlogin.mysql.MySQL;
import pl.moderr.mlogin.utils.ColorUtils;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class MLogin extends JavaPlugin {

    public static MySQL database;
    public static MLogin instance;
    public static HashMap<UUID, Boolean> onlineUsers = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveConfig();
        if(getConfig().getBoolean("enable")){
            onlineUsers.clear();
            for(Player p : Bukkit.getOnlinePlayers()){
                onlineUsers.put(p.getUniqueId(), false);
            }
            database = new MySQL();
            getServer().getPluginManager().registerEvents(new AuthListener(), this);
            Objects.requireNonNull(getCommand("zaloguj"), "Komenda nie moze byc pusta!").setExecutor(new ZalogujCommand());
            Objects.requireNonNull(getCommand("zarejestruj"),"Komenda nie moze byc pusta!").setExecutor(new ZarejestrujCommand());
        }
    }

    @Override
    public void onDisable() {
        onlineUsers.clear();
        for(Player p : Bukkit.getOnlinePlayers()){
            MLogin.database.doAsyncPassword(p, "", new MySQL.PasswordCallback<HashMap<String, String>>() {
                @Override
                public void onRegistered() {
                    p.sendMessage(ColorUtils.color("&c/zaloguj <hasło>"));
                }

                @Override
                public void notRegistered() {
                    p.sendMessage(ColorUtils.color("&c/zarejestruj <hasło>"));
                }

                @Override
                public void onSuccess(HashMap<String, String> done) {

                }
                @Override
                public void onPasswordSuccess(HashMap<String, String> success) {

                }
                @Override
                public void onPasswordFailure(HashMap<String, String> fail) {

                }
                @Override
                public void onFailure(Throwable cause) {

                }
            });
            onlineUsers.put(p.getUniqueId(), false);
            p.sendMessage(ColorUtils.color("&cWylogowano z powodu restartu"));
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1,1);
            if(p.isOp()){
                p.sendMessage(ColorUtils.color("&aWylogowano wszystkich &8(&7" + Bukkit.getOnlinePlayers().size() + "&8)"));
            }
        }
    }
}
