package net.canyonwolf;

import java.util.TimerTask;
import java.util.logging.Logger;

public class AutoBackup extends TimerTask {

    String sourceDirectory;
    String targetDirectory;
    int snapshotCount;
    Boolean silent;
    Boolean includeNether;
    Boolean includeEnd;
    Logger logger;

    public AutoBackup(
            String sourceDir,
            String targetDir,
            int snapshotCount,
            Boolean silent,
            Boolean includeNether,
            Boolean includeEnd,
            Logger logger
    ) {
        this.sourceDirectory = sourceDir;
        this.targetDirectory = targetDir;
        this.snapshotCount = snapshotCount;
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
        backupService.setMaxSnapshotCount(snapshotCount);

        logger.info("Starting automated backup...");
        long start = System.nanoTime();
        BackupService.backup(sourceDirectory, targetDirectory, includeNether, includeEnd);
        long elapsed = System.nanoTime() - start;
        float seconds = ((float) elapsed / 1000000000);
        String secondsStr = String.format("%.3f", seconds);
        logger.info("Finished automated backup... (" + secondsStr + " seconds)");

    }
}
