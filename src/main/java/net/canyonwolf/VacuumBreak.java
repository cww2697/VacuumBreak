package net.canyonwolf;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class VacuumBreak extends JavaPlugin {

    private boolean silent;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        getLogger().info("Vacuum Break Enabled");
    }

    @Override
    public void onLoad() {
        this.loadConfig();
        if(!getConfig().getBoolean("silent")){
            getLogger().info("Vacuum Break Loaded");
        }
    }

    @Override
    public void onDisable(){
        getLogger().info("Vacuum Break Disabled");
    }

    private void loadConfig(){
        FileConfiguration config = this.getConfig();
        this.silent = config.getBoolean("silent");

    }

}
