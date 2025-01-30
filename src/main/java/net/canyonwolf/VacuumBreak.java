package net.canyonwolf;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class VacuumBreak extends JavaPlugin {

    String sourceDirectory;
    String backupDirectory;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        getLogger().info("Vacuum Break Enabled");
        BackupService backup = new BackupService();
        backup.setLogger(getLogger());
        backup.setSilent(getConfig().getBoolean("silent"));
        this.buildFilePaths();
        BackupService.pack("world", this.sourceDirectory, this.backupDirectory);
    }

    private void buildFilePaths() {
        this.sourceDirectory = this.getDataFolder().getPath() +
                File.separator + ".." + File.separator + ".." + File.separator;


        this.backupDirectory = this.getDataFolder().getPath() +
                File.separator + getConfig().getString("backup-dir");

        try {
            BackupService.validateBackupDir(this.backupDirectory);
        } catch (Exception e) {
            this.getLogger().severe(e.getMessage());
            this.getLogger().warning("Disabling VacuumBreak...");
            this.getServer().getPluginManager().disablePlugin(this);
        }

    }

    @Override
    public void onDisable(){
        getLogger().info("Vacuum Break Disabled");
    }

}
