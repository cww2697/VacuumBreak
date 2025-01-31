package net.canyonwolf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupService {

    private static Logger logger;
    private static Boolean silent;


    public static void backup(String sourceDir, String targetDir, boolean includeNether, boolean includeEnd) {

        if (!silent) {
            logger.info("Backup started on world: world");
        }
        try {
            pack("world", sourceDir + File.separator + "world", targetDir + File.separator + "world.zip");
        } catch (RuntimeException e) {
            if (!silent) {
                logger.warning(e.getMessage());
            }
            logger.warning("Unable to backup world: world. Skipping...");
        }
        if(!silent) {
            logger.info("Backup completed on world: world");
        }

        if (includeNether) {
            if (!silent) {
                logger.info("Backup started on world: world_nether");
            }
            try {
                pack("world_nether", sourceDir + File.separator + "world_the_end", targetDir + File.separator + "world_nether.zip");
            } catch (RuntimeException e) {
                if (!silent) {
                    logger.warning(e.getMessage());
                }
                logger.warning("Unable to backup world: world_nether. Skipping...");
            }
            if(!silent) {
                logger.info("Backup completed on world: world_nether");
            }
        }

        if (includeEnd) {
            if (!silent) {
                logger.info("Backup started on world: world_the_end");
            }
            try{
                pack("world_the_end", sourceDir + File.separator + "world_the_end", targetDir + File.separator + "world_the_end.zip");
            } catch (RuntimeException e) {
                if (!silent) {
                    logger.warning(e.getMessage());
                }
                logger.warning("Unable to backup world: world_the_end. Skipping...");
            }
            if(!silent) {
                logger.info("Backup completed on world: world_the_end");
            }
        }

    }

    public static void pack(String world, String sourceDir, String targetDir) throws RuntimeException{
        Path pp = Paths.get(sourceDir);
        boolean sourceExists = verifySourceDir(sourceDir, world);
        if (!sourceExists) {
            return;
        }

        try {
            Path target = Files.createFile(Paths.get(targetDir));
            if (!silent){
                logger.info(pp.toString());
                logger.info(target.toString());
            }
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(target.toFile()))) {
                Files.walk(pp).forEach(p -> {
                    ZipEntry ze = new ZipEntry(p.toString());
                    try {
                        zos.putNextEntry(ze);
                        Files.copy(p, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to add entry to zip file.", e);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Filed to create backup file for " + world, e);
        }
    }

    public static void validateBackupDir(String backupDir) throws RuntimeException {
        Path pp = Paths.get(backupDir);
        if (!pp.toFile().exists()) {
            BackupService.logger.info("Backup directory (" + backupDir + ") does not exist. Creating directory...");
            boolean success = pp.toFile().mkdirs();
            if (!success) {
                throw new RuntimeException("Failed to create backup directory " + backupDir);
            }
        }
    }

    private static boolean verifySourceDir(String sourceDir, String world) {
        Path pp = Paths.get(sourceDir);
        if (!pp.toFile().exists()) {
            BackupService.logger.warning("Source directory for "+ world + " (" + sourceDir + ") does not exist. Skipping...");
            return false;
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
