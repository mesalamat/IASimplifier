package de.godly.iasimplifier;

import de.godly.iasimplifier.model.Model;
import de.godly.iasimplifier.util.Serializable;
import de.godly.iasimplifier.util.YamlUtil;
import lombok.Data;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class IAConfig implements Serializable {

    final String namespace;
    final List<Model> models;


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialised = new HashMap<>();

        for(Model m : models){
            Map<String,Object> obj = m.serialize(namespace);
            serialised.put("items." + m.getId(), obj);
        }
        serialised.put("info.namespace", namespace);
        YamlUtil.prepareInput(serialised);
        return serialised;
    }
}
