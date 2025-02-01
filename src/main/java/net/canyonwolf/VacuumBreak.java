package net.canyonwolf;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Calendar;
import java.util.Timer;

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

        if (this.getConfig().getBoolean("automated-backups")) {
            scheduleTask();
        }
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

    private void scheduleTask() throws RuntimeException {
        long miliseconds = 0;

        if (!this.getConfig().getBoolean("prefer-hours")) {
            int minutes = this.getConfig().getInt("backup-every-minutes");
            if (!(minutes > 0)) {
                throw new IllegalArgumentException("Backup minutes must be a positive integer");
            }

            miliseconds = minutes * 60000L;
        } else {
            int hours = this.getConfig().getInt("backup-every-hours");
            if (!(hours > 0)) {
                throw new IllegalArgumentException("Backup hours must be a positive integer");
            }
            miliseconds = hours * 3600000L;
        }

        Timer timer = new Timer();
        timer.schedule(
                new AutoBackup(
                        sourceDirectory,
                        backupDirectory,
                        getConfig().getInt("snapshot-count"),
                        getConfig().getBoolean("silent"),
                        getConfig().getBoolean("include-nether"),
                        getConfig().getBoolean("include-end"),
                        this.getLogger()
                ),
                0 ,
                miliseconds
        );
    }

    @Override
    public void onDisable(){
        getLogger().info("Vacuum Break Disabled");
    }

}
