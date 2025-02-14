package net.canyonwolf;

import net.canyonwolf.automation.AutoBackup;
import net.canyonwolf.commands.Snapshot;
import net.canyonwolf.service.BackupService;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
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

        Objects.requireNonNull(this.getCommand("vb-snapshot")).setExecutor(new Snapshot(getLogger(), getConfig(), sourceDirectory, backupDirectory));
    }

    private void buildFilePaths() {
        this.sourceDirectory = this.getDataFolder().getPath() +
                File.separator + ".." + File.separator + ".." + File.separator;

        this.backupDirectory = this.getDataFolder().getPath() +
                File.separator + getConfig().getString("snapshot-dir");

        try {
            BackupService.validateSnapshotDir(this.backupDirectory);
        } catch (Exception e) {
            this.getLogger().severe(e.getMessage());
            this.getLogger().warning("Disabling VacuumBreak...");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void scheduleTask() throws RuntimeException {
        long milliseconds;

        if (!this.getConfig().getBoolean("prefer-hours")) {
            int minutes = this.getConfig().getInt("backup-every-minutes");
            if (!(minutes > 0)) {
                throw new IllegalArgumentException("Backup minutes must be a positive integer");
            }

            milliseconds = minutes * 60000L;
        } else {
            int hours = this.getConfig().getInt("backup-every-hours");
            if (!(hours > 0)) {
                throw new IllegalArgumentException("Backup hours must be a positive integer");
            }
            milliseconds = hours * 3600000L;
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
                milliseconds
        );
    }

    @Override
    public void onDisable(){
        getLogger().info("Vacuum Break Disabled");
    }

}
