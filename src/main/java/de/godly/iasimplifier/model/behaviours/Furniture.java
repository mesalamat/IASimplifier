package de.godly.iasimplifier.model.behaviours;

import de.godly.iasimplifier.model.settings.EntityType;
import de.godly.iasimplifier.model.settings.Hitbox;
import de.godly.iasimplifier.model.settings.PlaceableOn;
import de.godly.iasimplifier.util.MapUtil;
import de.godly.iasimplifier.util.YamlUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Furniture implements Behaviour{


    private EntityType entityType = EntityType.ARMOR_STAND;
    private boolean small = false;
    private boolean gravity = false;
    private boolean fixedRotation = true;
    private int lightLevel = 0;
    private boolean solid = false;
    private boolean oppositeDirection = false;
    private Hitbox hitbox;
    private PlaceableOn placeableOn;

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialised = new HashMap<>();
        MapUtil.putIfNotNull(serialised, "entity", entityType.getIaName());
        MapUtil.putIfNotNull(serialised, "light_level", lightLevel);
        MapUtil.putIfNotNull(serialised, "solid", solid);

        if(hitbox != null)MapUtil.putIfNotNull(serialised, "hitbox", hitbox.serialize());
        if(placeableOn != null)MapUtil.putIfNotNull(serialised, "placeable_on", placeableOn.serialize());
        MapUtil.putIfNotNull(serialised,"small", small);
        MapUtil.putIfNotNull(serialised,"gravity", gravity);
        MapUtil.putIfNotNull(serialised, "opposite_direction", oppositeDirection);
        YamlUtil.prepareInput(serialised);
        return serialised;
    }

    @Override
    public String name() {
        return "furniture";
    }
}
