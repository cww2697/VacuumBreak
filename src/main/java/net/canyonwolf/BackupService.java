package net.canyonwolf;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class BackupService {

    private static Logger logger;
    private static Boolean silent;

    public static void pack(String world, String sourceDir, String targetDir) {
        Path pp = Paths.get(sourceDir);
        Path target = Paths.get(targetDir);
        if (!silent){
            logger.info(pp.toString());
            logger.info(target.toString());
        }
    }

    public static boolean validateBackupDir(String backupDir) {
        Path pp = Paths.get(backupDir);
        if (!pp.toFile().exists()) {
            BackupService.logger.info("BackupService directory " + backupDir + " does not exist. Creating directory...");
            boolean success = pp.toFile().mkdirs();
            if (!success) {
                throw new RuntimeException("Failed to create backup directory " + backupDir);
            }
        }
        return true;
    }

    public void setLogger(Logger logger) {
        BackupService.logger = logger;
    }

    public void setSilent(boolean silent) {
        BackupService.silent = silent;
    }

}
