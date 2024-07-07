package de.godly.iasimplifier.model.behaviours;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Gun implements Behaviour{

    private String projectile;



    public Gun(String projectile){
        this.projectile = projectile;
    }

    @Override
    public String name() {
        return "gun";
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialised = new HashMap<>();
        serialised.put("projectile", projectile);
        return serialised;
    }
}
