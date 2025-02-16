package net.canyonwolf.service;

import net.canyonwolf.constants.Worlds;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Logger;

public class SnapshotService {

    private static Logger logger;
    private static Boolean silent;
    private static int maxSnapshotCount;

    /**
     * @param sourceDir Directory to snapshot
     * @param targetDir Snapshot directory
     * @param includeNether Snapshot Nether
     * @param includeEnd Snapshot The End
     * @param includeOverworld Snapshot Overworld
     */
    public static void createSnapshots(
            String sourceDir,
            String targetDir,
            boolean includeNether,
            boolean includeEnd,
            boolean includeOverworld
    ) {

        String snapshotTimestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        if (FileService.getCurrentSnapshotCount(targetDir) >= maxSnapshotCount) {
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
                logger.info("Snapshot process started on world: " + worldName);
            }
            long start = System.nanoTime();
            FileService.pack(
                    worldName,
                    sourceDir + File.separator + worldPath,
                    targetDir + File.separator + snapshotTimestamp + "_"+worldPath+".zip",
                    logger,
                    silent
            );
            long elapsed = System.nanoTime() - start;
            float seconds = ((float) elapsed / 1000000000);
            String secondsStr = String.format("%.3f", seconds);
            if(!silent) {
                logger.info("Snapshot process completed on world: " + worldName + " (" + secondsStr + " seconds)");
            }
        } catch (RuntimeException e) {
            if (!silent) {
                logger.warning(e.getMessage());
            }
            logger.warning("Unable to snapshot world: " + worldName + ". Skipping...");
        }
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

        File[] matchingFiles = directory.listFiles((_, name) -> name.contains(oldestSnapshotTimestamp));
        if (matchingFiles == null || matchingFiles.length == 0) {
            throw new RuntimeException("The directory " + directory + " does not contain any snapshot files.");
        }

        Arrays.stream(matchingFiles).forEach(FileService::deleteSnapshot);
    }


    public void setLogger(Logger logger) {
        SnapshotService.logger = logger;
    }

    public void setSilent(boolean silent) {
        SnapshotService.silent = silent;
    }

    public void setMaxSnapshotCount(int maxSnapshotCount) {
        SnapshotService.maxSnapshotCount = maxSnapshotCount;
    }

}
