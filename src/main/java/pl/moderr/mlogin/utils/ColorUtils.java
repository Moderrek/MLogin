package pl.moderr.mlogin.utils;

import org.bukkit.ChatColor;

public class ColorUtils {

    public static String color(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
