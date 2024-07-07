package de.godly.iasimplifier.model.settings;

import de.godly.iasimplifier.util.Serializable;
import de.godly.iasimplifier.util.YamlUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Hitbox implements Serializable {

    private final int length, width, height;

    private double lengthOffset, widthOffset, heightOffset;



    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialised = new HashMap<>();
        //Check values to not spam unnecessary Config Values
        if(length != 0) serialised.put("length", length);
        if(width != 0) serialised.put("width", width);
        if(height != 0) serialised.put("height", height);
        if(lengthOffset != 0.0) serialised.put("length_offset", lengthOffset);
        if(widthOffset != 0.0) serialised.put("width_offset", widthOffset);
        if(heightOffset != 0.0) serialised.put("height_offset", heightOffset);
        YamlUtil.prepareInput(serialised);
        return serialised;
    }
}
