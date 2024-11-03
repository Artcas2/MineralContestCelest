package fr.synchroneyes.groups.Utils.FileManager;

import fr.synchroneyes.groups.Utils.FileManager.FileCopyVisitor;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.EnumSet;

public class FileCopy {
    public static void copyDirectoryContent(File sourceFolder, File destinationFolder) throws IOException {
        if (sourceFolder.isDirectory()) {
            if (destinationFolder.exists() && destinationFolder.isFile()) {
                throw new IllegalArgumentException("Destination exists but is not a folder: " + destinationFolder.getAbsolutePath());
            }
            if (!destinationFolder.exists()) {
                Files.createDirectory(destinationFolder.toPath(), new FileAttribute[0]);
            }
            for (File file : sourceFolder.listFiles()) {
                if (file.isDirectory()) {
                    FileCopy.copyDirectory(file, destinationFolder);
                    continue;
                }
                FileCopy.copyFile(file, destinationFolder);
            }
        }
    }

    public static void copyDirectory(File fromFile, File toParentFile) throws IOException {
        Path from = fromFile.toPath();
        Path to = Paths.get(toParentFile.getAbsolutePath() + File.separatorChar + fromFile.getName(), new String[0]);
        Files.walkFileTree(from, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new FileCopyVisitor(from, to));
    }

    public static void copyFile(File toCopy, File mainDestination) throws IOException {
        if (!mainDestination.exists()) {
            mainDestination.mkdirs();
        }
        Path to = Paths.get(mainDestination.getAbsolutePath() + File.separatorChar + toCopy.getName(), new String[0]);
        Files.copy(toCopy.toPath(), to, StandardCopyOption.REPLACE_EXISTING);
    }
}

