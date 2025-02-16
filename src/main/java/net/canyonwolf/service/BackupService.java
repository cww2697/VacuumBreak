package net.canyonwolf.service;

import net.canyonwolf.constants.Worlds;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupService {

    private static Logger logger;
    private static Boolean silent;
    private static int maxSnapshotCount;

    public static void createSnapshots(
            String sourceDir,
            String targetDir,
            boolean includeNether,
            boolean includeEnd,
            boolean includeOverworld
    ) {

        String snapshotTimestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        if (getCurrentSnapshotCount(targetDir) >= maxSnapshotCount) {
            if (!silent){
                logger.info("Maximum snapshot count exceeded. Removing oldest snapshot.");
                removeSnapshot(targetDir);
            }
        }
        if (includeOverworld) {
            backupWorld(sourceDir, targetDir, Worlds.overworldPath, snapshotTimestamp);
        }

        if (includeNether) {
            backupWorld(sourceDir, targetDir, Worlds.netherPath, snapshotTimestamp);
        }

        if (includeEnd) {
            backupWorld(sourceDir, targetDir, Worlds.endPath, snapshotTimestamp);
        }
    }

    private static void backupWorld(
            String sourceDir,
            String targetDir,
            @NotNull String worldPath,
            String snapshotTimestamp
    )
    {
        String worldName = switch (worldPath) {
            case Worlds.overworldPath -> Worlds.overworldName;
            case Worlds.netherPath -> Worlds.netherName;
            case Worlds.endPath -> Worlds.endName;
            default -> throw new IllegalStateException("Unexpected value: " + worldPath);
        };


        try {
            if (!silent) {
                logger.info("Backup started on world: " + worldName);
            }
            long start = System.nanoTime();
            pack(
                    worldName,
                    sourceDir + File.separator + worldPath,
                    targetDir + File.separator + snapshotTimestamp + "_"+worldPath+".zip"
            );
            long elapsed = System.nanoTime() - start;
            float seconds = ((float) elapsed / 1000000000);
            String secondsStr = String.format("%.3f", seconds);
            if(!silent) {
                logger.info("Backup completed on world: " + worldName + " (" + secondsStr + " seconds)");
            }
        } catch (RuntimeException e) {
            if (!silent) {
                logger.warning(e.getMessage());
            }
            logger.warning("Unable to backup world: " + worldName + ". Skipping...");
        }
    }

    private static void pack(String worldName, String sourceDir, String targetDir) throws RuntimeException{
        Path pp = Paths.get(sourceDir);
        boolean sourceExists = validateSourceDir(sourceDir, worldName);
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
                File source = new File(sourceDir);
                compressDirectory(source, source.getName(), zos);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create backup file for " + worldName, e);
        }
    }

    private static void compressDirectory(@NotNull File dir, String basePath, ZipOutputStream zos) throws IOException {

        File[] files = dir.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                compressDirectory(file, basePath + "/" + file.getName(), zos);
            } else {
                if (file.getName().endsWith(".lock")) {
                    // Exclude Spigot world lock files from archive
                    continue;
                }
                if (file.getName().toLowerCase().endsWith(".ds_store")) {
                    // Exclude macOS DS_Store file from archive
                    continue;
                }

                FileInputStream fis = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(basePath + "/" + file.getName());
                zos.putNextEntry(zipEntry);

                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                zos.closeEntry();
                fis.close();
            }
        }
    }

    public static void validateSnapshotDir(String backupDir) throws RuntimeException {
        Path pp = Paths.get(backupDir);
        if (!pp.toFile().exists()) {
            BackupService.logger.info("Backup directory (" + backupDir + ") does not exist. Creating directory...");
            boolean success = pp.toFile().mkdirs();
            if (!success) {
                throw new RuntimeException("Failed to create backup directory " + backupDir);
            }
        }
    }

    private static boolean validateSourceDir(String sourceDir, String worldName) {
        Path pp = Paths.get(sourceDir);
        if (!pp.toFile().exists()) {
            BackupService.logger.warning("Source directory for "+ worldName + " (" + sourceDir + ") does not exist. Skipping...");
            return false;
        }
        return true;
    }

    private static int getCurrentSnapshotCount(String snapshotDir) {
        int count = 0;
        File source = new File(snapshotDir);
        File[] files = source.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.getName().endsWith("world.zip")) {
                count++;
            }
        }
        return count;
    }

    private static void removeSnapshot(String snapshotDir) {
        File directory = new File(snapshotDir);
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            logger.warning("The directory is empty or cannot be accessed.");
            return;
        }

        File oldestSnapshot = Arrays.stream(files)
                .filter(File::isFile)
                .min(Comparator.comparingLong(File::lastModified))
                .orElse(null);

        assert oldestSnapshot != null;
        String oldestSnapshotTimestamp = oldestSnapshot.getName().substring(0, oldestSnapshot.getName().indexOf('_') + 1);

        File[] matchingFiles = directory.listFiles((dir, name) -> name.contains(oldestSnapshotTimestamp));
        if (matchingFiles == null || matchingFiles.length == 0) {
            throw new RuntimeException("The directory " + directory + " does not contain any snapshot files.");
        }

        Arrays.stream(matchingFiles).forEach(BackupService::deleteSnapshot);
    }

    private static void deleteSnapshot(@NotNull File snapshotFile) {
        boolean success = snapshotFile.delete();
        if (!success) {
            throw new RuntimeException("Failed to delete snapshot " + snapshotFile.getAbsolutePath());
        }
    }

    public void setLogger(Logger logger) {
        BackupService.logger = logger;
    }

    public void setSilent(boolean silent) {
        BackupService.silent = silent;
    }

    public void setMaxSnapshotCount(int maxSnapshotCount) {
        BackupService.maxSnapshotCount = maxSnapshotCount;
    }

}
