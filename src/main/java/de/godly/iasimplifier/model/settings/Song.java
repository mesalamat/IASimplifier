package de.godly.iasimplifier.model.settings;

import de.godly.iasimplifier.util.Serializable;
import de.godly.iasimplifier.util.YamlUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Song implements Serializable {

    private final String name;
    private final String description;

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialised = new HashMap<>();
        serialised.put("name", name);
        serialised.put("description", description);
        YamlUtil.prepareInput(serialised);
        return serialised;
    }
}
