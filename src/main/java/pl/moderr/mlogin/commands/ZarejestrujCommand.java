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

public class ZarejestrujCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length >= 2) {
                String password = args[0];
                if (password.equals(args[1])) {
                    if(MLogin.onlineUsers.get(p.getUniqueId())){
                        p.sendMessage(ColorUtils.color(HexResolver.parseHexString("<gradient:#FD4F1D:#FCE045>Moderrkowo") + " &cJuż jesteś zalogowany!"));
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                       return false;
                    }
                    MLogin.database.doAsyncPassword(p, null, new MySQL.PasswordCallback<HashMap<String, String>>() {

                        @Override
                        public void onRegistered() {
                            p.sendMessage(ColorUtils.color(HexResolver.parseHexString("<gradient:#FD4F1D:#FCE045>Moderrkowo") + " &cJuż jesteś zarejestrowany!"));
                            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        }

                        @Override
                        public void notRegistered() {
                            MLogin.database.doAsyncRegister(p, password, new MySQL.Callback<HashMap<String, String>>() {

                                @Override
                                public void onSuccess(HashMap<String, String> success) {

                                }
                                @Override
                                public void onFail(HashMap<String, String> fail) {

                                }

                                @Override
                                public void onDone(HashMap<String, String> done) {
                                    MLogin.onlineUsers.remove(p.getUniqueId());
                                    MLogin.onlineUsers.put(p.getUniqueId(), true);
                                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                                    p.sendTitle(new Title(ColorUtils.color(HexResolver.parseHexString("<gradient:#FD4F1D:#FCE045>Moderrkowo")), ColorUtils.color("&bWitaj, " + p.getName())));
                                    p.sendMessage(ColorUtils.color(HexResolver.parseHexString("<gradient:#FD4F1D:#FCE045>Moderrkowo") + " &aPomyślnie zarejestrowano!"));
                                }

                                @Override
                                public void onFailure(Throwable cause) {
                                    p.sendMessage(ColorUtils.color("&cNie udało się zarejestrować"));
                                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Throwable cause) {
                            p.sendMessage(ColorUtils.color("&cPrzepraszamy. Wystąpił błąd z systemem logowania"));
                            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
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

                    });
                } else {
                    p.sendMessage(ColorUtils.color("&cHasła nie są takie same!"));
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    return false;
                }
            } else {
                p.sendMessage(ColorUtils.color("&cUżyj: /zarejestruj <hasło> <powtórz hasło>"));
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return false;
            }
        } else {
            sender.sendMessage("Nie jestes graczem!");
            return false;
        }
        return false;
    }
}
