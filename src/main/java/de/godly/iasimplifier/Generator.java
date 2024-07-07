package de.godly.iasimplifier;

import lombok.SneakyThrows;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;

public class Generator {


    final DumperOptions dumperOptions;
    final Yaml yaml;
    final File file;

    public Generator(File file) {
        this.file = file;
        dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setPrettyFlow(true);
        yaml = new Yaml(dumperOptions);
    }

    @SneakyThrows
    public boolean generateForModel(IAConfig model) {
        try {
            FileWriter writer = new FileWriter(file);
            yaml.dump(model.serialize(), writer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
