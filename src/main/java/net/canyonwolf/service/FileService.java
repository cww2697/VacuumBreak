package net.canyonwolf.service;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileService {

    @NotNull
    @Contract(pure = true)
    public static String buildWorldDirPath (String currentDir) {
        return currentDir + File.separator + ".." + File.separator + ".." + File.separator;
    }

    /**
     * @param currentDir Current working directory.
     * @param snapshotDirConfig Configuration value for snapshot storage.
     * @param logger System logger
     * @return Path to directory for snapshot storage.
     * @throws FileNotFoundException Error occurring if snapshot directory cannot be accessed or created.
     */
    @NotNull
    @Contract(pure = true)
    public static String buildSnapshotDirPath(String currentDir, String snapshotDirConfig, Logger logger) throws FileNotFoundException {

        String targetDir = currentDir +
                File.separator + snapshotDirConfig;

        try {
            FileService.validateSnapshotDir(targetDir, logger);
        } catch (Exception e) {
            throw new FileNotFoundException("Unable to access Snapshot Directory.");
        }

        return targetDir;
    }

    /**
     * @param worldName World name of current snapshot process
     * @param sourceDir Source directory to be snapshot
     * @param targetDir Target directory of processed zip file
     * @param logger System logger
     * @param silent Toggles information loggers of non-critical events
     * @throws RuntimeException Exception for failure creating zip file
     */
    public static void pack(String worldName, String sourceDir, String targetDir, Logger logger, Boolean silent) throws RuntimeException{
        Path pp = Paths.get(sourceDir);
        boolean sourceExists = validateSourceDir(sourceDir, worldName, logger);
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
            throw new RuntimeException("Failed to create snapshot for " + worldName, e);
        }
    }

    /**
     * @param dir Directory to be compressed
     * @param basePath Path to be compressed
     * @param zos Zip output stream
     * @throws IOException Exception to be thrown if error occurs while compressing directory
     */
    public static void compressDirectory(@NotNull File dir, String basePath, ZipOutputStream zos) throws IOException {

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

    /**
     * @param snapshotDir Path to configured snapshot directory
     * @param logger System logger
     * @throws RuntimeException Error if failure occurs creating snapshot directory
     */
    public static void validateSnapshotDir(String snapshotDir, Logger logger) throws RuntimeException {
        Path pp = Paths.get(snapshotDir);
        if (!pp.toFile().exists()) {
            logger.info("Snapshot directory (" + snapshotDir + ") does not exist. Creating directory...");
            boolean success = pp.toFile().mkdirs();
            if (!success) {
                throw new RuntimeException("Failed to create snapshot directory " + snapshotDir);
            }
        }
    }


    /**
     * @param snapshotDir Configured snapshot directory
     * @return Total number of stored world snapshots
     */
    public static int getCurrentSnapshotCount(String snapshotDir) {
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

    /**
     * @param sourceDir Path to world directory
     * @param worldName World name of current snapshot process
     * @param logger System logger
     * @return Boolean representing if the source directory exists
     */
    public static boolean validateSourceDir(String sourceDir, String worldName, Logger logger) {
        Path pp = Paths.get(sourceDir);
        if (!pp.toFile().exists()) {
            logger.warning("Source directory for "+ worldName + " (" + sourceDir + ") does not exist. Skipping...");
            return false;
        }
        return true;
    }

    /**
     * @param snapshotFile Snapshot file to be deleted
     * @throws RuntimeException Exception occurring if plugin is unable to remove the requested snapshot
     */
    public static void deleteSnapshot(@NotNull File snapshotFile) throws RuntimeException {
        boolean success = snapshotFile.delete();
        if (!success) {
            throw new RuntimeException("Failed to delete snapshot " + snapshotFile.getAbsolutePath());
        }
    }
}
