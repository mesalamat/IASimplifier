package de.godly.iasimplifier.model.behaviours;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class FurnitureSit implements Behaviour{


    private double sitHeight;
    private boolean sitOppositeDirection = false;
    private boolean sitAllSolids = true;


    public FurnitureSit(double sitHeight){
        this.sitHeight = sitHeight;
    }

    public FurnitureSit(){
        this(1.0);
    }

    @Override
    public String name() {
        return "furniture_sit";
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("sit_height", sitHeight);
        objectMap.put("sit_all_solid_blocks", sitAllSolids);
        objectMap.put("opposite_direction", sitOppositeDirection);
        return objectMap;
    }
}
