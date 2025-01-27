package net.canyonwolf;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class VacuumBreak extends JavaPlugin {

    @Override
    public void onEnable(){
        saveDefaultConfig();
        getLogger().info("Vacuum Break Enabled");
    }

    @Override
    public void onDisable(){
        getLogger().info("Vacuum Break Disabled");
    }

}
