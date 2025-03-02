package david.foto;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class PhotoOrganizer {
    private static final String TARGET_FOLDER = "G:\\Bilder";
    private static final String[] IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp", "tiff", "mp4", "mov"};

    public static void main(String[] args) {
        File[] dirsToSearch = {new File("C:/"), new File("\\\\SERVERMORIN\\David\\Photos")};

        System.out.println("Suche nach Fotos...");

        for (File dir : dirsToSearch) {
            if (dir.exists()) {
                searchAndMovePhotos(dir);
            } else {
                System.out.println("Ordner nicht gefunden: " + dir.getAbsolutePath());
            }
        }

        System.out.println("Fertig");
    }

    private static void searchAndMovePhotos(File dir) {
        if (dir.getAbsolutePath().contains("Windows")
                || dir.getAbsolutePath().contains("Program Files")
                || dir.getAbsolutePath().contains("Programm")
                || dir.getAbsolutePath().contains("Program Files (x86)")
                || dir.getAbsolutePath().contains("AppData")
                || dir.getAbsolutePath().contains("Downloads")
                || dir.getAbsolutePath().contains("OneDrive")
                || dir.getAbsolutePath().contains("Users")
                || dir.getAbsolutePath().contains("$Recycle.Bin")
                || dir.getAbsolutePath().contains("ProgramData")
                || dir.getAbsolutePath().contains(".vscode")) {
            return;
        }

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        searchAndMovePhotos(file);
                    } else if (isImageFile(file)) {
                        moveFileToDateFolder(file);
                    }
                }
            }
        }
    }

    private static boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return Arrays.stream(IMAGE_EXTENSIONS).anyMatch(name::endsWith);
    }

    private static void moveFileToDateFolder(File source) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(source.toPath(), BasicFileAttributes.class);
            FileTime fileTime = attrs.creationTime();
            String year = new SimpleDateFormat("yyyy").format(new Date(fileTime.toMillis()));

            File yearFolder = new File(TARGET_FOLDER, year);
            if (!yearFolder.exists()) yearFolder.mkdirs();

            File target = new File(yearFolder, source.getName());

            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Kopiert: " + source.getAbsolutePath() + " -> " + target.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Fehler beim Kopieren: " + source.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
