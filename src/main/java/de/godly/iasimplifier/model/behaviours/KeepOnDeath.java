package de.godly.iasimplifier.model.behaviours;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.Map;

@Data
@AllArgsConstructor
public class KeepOnDeath implements Behaviour{


    private boolean value;

    public KeepOnDeath() {
        this(true);
    }
    @Override
    public String name() {
        return "keep_on_death";
    }


    @SneakyThrows
    @Override
    public Map<String, Object> serialize() {
        throw new NoSuchMethodException("Calling serialize() on KeepOnDeath is not possible. Check Special Case Handling!");
    }
}
