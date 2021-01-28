package pl.moderr.mlogin.commands;

import com.destroystokyo.paper.Title;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.moderr.mlogin.utils.ColorUtils;
import pl.moderr.mlogin.utils.HexResolver;
import pl.moderr.mlogin.MLogin;
import pl.moderr.mlogin.mysql.MySQL;

import java.util.HashMap;

public class ZalogujCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(!(args.length > 0)){
                p.sendMessage(ColorUtils.color("&cUżyj: /zaloguj <hasło>"));
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                return false;
            }
            if(!MLogin.onlineUsers.get(p.getUniqueId())){
                MLogin.database.doAsyncPassword(p, args[0], new MySQL.PasswordCallback<HashMap<String, String>>() {

                    @Override
                    public void onRegistered() {
                    }
                    @Override
                    public void notRegistered() {
                        p.sendMessage(ColorUtils.color("&cMusisz się najpierw zarejestrować! /zarejestruj <hasło> <powtórz hasło>"));
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                        p.sendTitle(new Title("", ColorUtils.color("&cZarejestruj się!")));
                    }
                    @Override
                    public void onPasswordSuccess(HashMap<String, String> success) {
                        MLogin.onlineUsers.remove(p.getUniqueId());
                        MLogin.onlineUsers.put(p.getUniqueId(), true);
                        p.sendMessage(ColorUtils.color("&aPomyślnie zalogowano!\n&eWitaj ponownie &6" + p.getName()));
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1,1);
                        p.sendTitle(new Title("", ColorUtils.color("&aZalogowano!")));
                    }
                    @Override
                    public void onPasswordFailure(HashMap<String, String> fail) {
                        p.sendMessage(ColorUtils.color("&cPodane hasło jest niepoprawne! Spróbuj ponownie"));
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                    }
                    @Override
                    public void onSuccess(HashMap<String, String> done) {

                    }
                    @Override
                    public void onFailure(Throwable cause) {
                        p.sendMessage(ColorUtils.color("&cPrzepraszamy. Wystąpił błąd z systemem logowania"));
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                    }
                });
                return true;
            }else{
                p.sendMessage(ColorUtils.color(HexResolver.parseHexString("<gradient:#FD4F1D:#FCE045>Moderrkowo") + " &cJuż jesteś zalogowany!"));
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                return false;
            }
        }else{
            sender.sendMessage("Nie jestes graczem nie mozesz sie logowac!");
            return false;
        }
    }
}
