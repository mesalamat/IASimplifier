package de.godly.iasimplifier.util;

import javafx.stage.FileChooser;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {

    public static final FileChooser.ExtensionFilter YAML_FILTER = new FileChooser.ExtensionFilter("YAML Files (*.yml)", "*.yml");


    @SneakyThrows
    public static List<File> subFilesFromDirectory(File file) {
        List<File> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(file.toPath())) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    files.addAll(subFilesFromDirectory(entry.toFile()));
                } else {
                    files.add(entry.toFile());
                }
            }
        }
        return files;
    }
    @SneakyThrows
    public static List<File> subFilesFromDirectory(File file, FileFilter fileFilter, List<File> files) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(file.toPath()))
        {
            for (Path entry : stream) {
                if (fileFilter.accept(entry.toFile()) ||Files.isDirectory(entry)) {
                    if (Files.isDirectory(entry)) {
                        subFilesFromDirectory(entry.toFile(), fileFilter, files);
                    } else {
                        files.add(entry.toFile());
                    }
                }
            }
        }
        return files;
    }

    public static List<File> findJsonFiles(List<File> files){
        return files.stream().filter(file -> file.getName().endsWith(".json")).collect(Collectors.toList());
    }

}
