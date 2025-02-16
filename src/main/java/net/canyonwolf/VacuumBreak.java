package net.canyonwolf;

import net.canyonwolf.automation.AutoBackup;
import net.canyonwolf.commands.Snapshot;
import net.canyonwolf.service.SnapshotService;
import net.canyonwolf.service.FileService;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Timer;

public class VacuumBreak extends JavaPlugin {

    String sourceDirectory;
    String snapshotDirectory;

    Timer timer;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        getLogger().info("Vacuum Break Enabled");
        SnapshotService backup = new SnapshotService();
        backup.setLogger(getLogger());
        backup.setSilent(getConfig().getBoolean("silent"));
        this.buildFilePaths();

        if (this.getConfig().getBoolean("automated-backups")) {
            scheduleTask();
        }

        Objects.requireNonNull(this.getCommand("vb-snapshot")).setExecutor(new Snapshot(getLogger(), getConfig(), sourceDirectory, snapshotDirectory));
    }

    private void buildFilePaths() {
        String currentDir = this.getDataFolder().getPath();
        this.sourceDirectory = FileService.buildWorldDirPath(currentDir);
        try {
            this.snapshotDirectory = FileService.buildSnapshotDirPath(currentDir, getConfig().getString("snapshot-dir"), this.getLogger());
        } catch (Exception e) {
            this.getLogger().severe(e.getMessage());
            this.getLogger().warning("Disabling VacuumBreak...");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void scheduleTask() throws RuntimeException {
        long milliseconds;

        if (!this.getConfig().getBoolean("prefer-hours")) {
            int minutes = this.getConfig().getInt("snapshot-every-minutes");
            if (!(minutes > 0)) {
                throw new IllegalArgumentException("Snapshot minutes must be a positive integer");
            }

            milliseconds = minutes * 60000L;
        } else {
            int hours = this.getConfig().getInt("snapshot-every-hours");
            if (!(hours > 0)) {
                throw new IllegalArgumentException("Snapshot hours must be a positive integer");
            }
            milliseconds = hours * 3600000L;
        }

        Timer timer = new Timer();
        timer.schedule(
                new AutoBackup(
                        sourceDirectory,
                        snapshotDirectory,
                        getConfig().getInt("snapshot-count"),
                        getConfig().getBoolean("silent"),
                        getConfig().getBoolean("include-nether"),
                        getConfig().getBoolean("include-end"),
                        this.getLogger()
                ),
                0 ,
                milliseconds
        );

        this.timer = timer;
    }

    @Override
    public void onDisable(){
        getLogger().info("Vacuum Break Disabled");
    }

}
