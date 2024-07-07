package de.godly.iasimplifier.model;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ModelManager {

    private final HashMap<File, Model> models = new HashMap<File, Model>();


    @SneakyThrows
    public void loadFiles(List<File> files) {
        for(File f : files){
            System.out.println();
            models.put(f, new Model(f, f.getName().split("\\.")[0]));
        }
    }
}
