package de.godly.iasimplifier.model.settings;

import de.godly.iasimplifier.util.Serializable;
import de.godly.iasimplifier.util.YamlUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceableOn implements Serializable {

    private boolean placeAbleOnWall = true;
    private boolean placeAbleOnCeiling= true;
    private boolean placeAbleOnGround= true ;
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialised = new HashMap<>();
        serialised.put("walls", placeAbleOnWall);
        serialised.put("ceiling", placeAbleOnCeiling);
        serialised.put("floor", placeAbleOnGround);
        YamlUtil.prepareInput(serialised);
        return serialised;
    }
}
