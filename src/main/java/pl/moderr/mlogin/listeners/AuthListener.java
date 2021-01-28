package pl.moderr.mlogin.listeners;


import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import pl.moderr.mlogin.utils.ColorUtils;
import pl.moderr.mlogin.MLogin;
import pl.moderr.mlogin.mysql.MySQL;

import java.util.ArrayList;
import java.util.HashMap;

public class AuthListener implements Listener {

    ArrayList<String> allowedCommandWithoutLogin = new ArrayList<>();

    public AuthListener(){
        this.allowedCommandWithoutLogin.add("zaloguj");
        this.allowedCommandWithoutLogin.add("login");
        this.allowedCommandWithoutLogin.add("zarejestruj");
        this.allowedCommandWithoutLogin.add("register");
    }


    @EventHandler
    public void move(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(!MLogin.onlineUsers.get(p.getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(!MLogin.onlineUsers.get(p.getUniqueId())){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(!MLogin.onlineUsers.get(p.getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void openInv(InventoryOpenEvent e){
        Player p = (Player) e.getPlayer();
        if(!MLogin.onlineUsers.get(p.getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void invokeCommand(PlayerCommandPreprocessEvent event){
        Player p = event.getPlayer();
        if(!MLogin.onlineUsers.get(p.getUniqueId())){
            String command = event.getMessage().substring(1);
            if(command.contains(" ")){
                command = command.split(" ")[0];
            }
            boolean b = true;
            if(allowedCommandWithoutLogin.contains(command)){
                b = false;
            }
            event.setCancelled(b);
            if(b){
                MLogin.database.doAsyncPassword(p, null, new MySQL.PasswordCallback<HashMap<String, String>>() {
                    @Override
                    public void onRegistered() {
                        p.sendMessage(ColorUtils.color("&cNajpierw zaloguj się!\n/zaloguj <hasło>"));
                    }

                    @Override
                    public void notRegistered() {
                        p.sendMessage(ColorUtils.color("&cNajpierw zarejestruj się!\n/zarejestruj <hasło> <powtórz hasło>"));
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
            }
        }
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent e){
        Player p = e.getPlayer();
        if(!MLogin.onlineUsers.get(p.getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void pickupItem(PlayerAttemptPickupItemEvent e){
        Player p = e.getPlayer();
        if(!MLogin.onlineUsers.get(p.getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void chat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        if(!MLogin.onlineUsers.get(p.getUniqueId())){
            MLogin.database.doAsyncPassword(p, null, new MySQL.PasswordCallback<HashMap<String, String>>() {
                @Override
                public void onRegistered() {
                    p.sendMessage(ColorUtils.color("&cNajpierw zaloguj się!\n/zaloguj <hasło>"));
                }

                @Override
                public void notRegistered() {
                    p.sendMessage(ColorUtils.color("&cNajpierw zarejestruj się!\n/zarejestruj <hasło> <powtórz hasło>"));
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
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1,1);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e){
        if(MLogin.onlineUsers.containsKey(e.getPlayer().getUniqueId())){
            MLogin.onlineUsers.remove(e.getPlayer().getUniqueId());
        }
    }
    @EventHandler
    public void join(PlayerJoinEvent e){
        MLogin.database.doAsyncPassword(e.getPlayer(), "", new MySQL.PasswordCallback<HashMap<String, String>>() {
            @Override
            public void onRegistered() {
                e.getPlayer().sendMessage(ColorUtils.color("&c/zaloguj <hasło>"));
            }

            @Override
            public void notRegistered() {
                e.getPlayer().sendMessage(ColorUtils.color("&c/zarejestruj <hasło> <powtórz hasło>"));
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
        if(MLogin.onlineUsers.containsKey(e.getPlayer().getUniqueId())){
            MLogin.onlineUsers.remove(e.getPlayer().getUniqueId());
        }
        MLogin.onlineUsers.put(e.getPlayer().getUniqueId(), false);
    }

}
