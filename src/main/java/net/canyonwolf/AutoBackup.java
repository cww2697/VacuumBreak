package net.canyonwolf;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.TimerTask;
import java.util.logging.Logger;

public class AutoBackup extends TimerTask {

    String sourceDirectory;
    String targetDirectory;
    Boolean silent;
    Boolean includeNether;
    Boolean includeEnd;
    Logger logger;

    public AutoBackup(
            String sourceDir,
            String targetDir,
            Boolean silent,
            Boolean includeNether,
            Boolean includeEnd,
            Logger logger
    ) {
        this.sourceDirectory = sourceDir;
        this.targetDirectory = targetDir;
        this.silent = silent;
        this.includeNether = includeNether;
        this.includeEnd = includeEnd;
        this.logger = logger;
    }

    @Override
    public void run() {
        BackupService backupService = new BackupService();
        backupService.setLogger(logger);
        backupService.setSilent(silent);
        if (!silent){
            logger.info("Starting automated backup...");
        }
        BackupService.backup(sourceDirectory, targetDirectory, includeNether, includeEnd);
        if (!silent){
            logger.info("Finished automated backup...");
        }
    }
}
