package de.godly.iasimplifier.model.behaviours;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.Map;

@Data
@AllArgsConstructor
public class Hat implements Behaviour{

    private boolean value;


    public Hat(){
        this(true);
    }

    @Override
    public String name() {
        return "hat";
    }

    @SneakyThrows
    @Override
    public Map<String, Object> serialize() {
        throw new NoSuchMethodException("Calling serialize() on Hat is not possible. Check Special Case Handling!");
    }
}
