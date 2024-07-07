package de.godly.iasimplifier.model;

import de.godly.iasimplifier.model.behaviours.*;
import de.godly.iasimplifier.model.settings.*;
import de.godly.iasimplifier.util.MapUtil;
import de.godly.iasimplifier.util.ReflectionUtil;
import de.godly.iasimplifier.util.Serializable;
import de.godly.iasimplifier.util.YamlUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Data
public class Model implements Serializable {

    private final File file;
    private String id;
    private String displayName;
    private String permission;
    private List<String> lore;
    private List<String> enchantments;
    private String material = "PAPER";
    private boolean generate = false;
    private int modelId;
    private List<String> textures;
    //Attributes
    private List<AttributeModifier> modifiers;
    //Item Flags
    private List<ItemFlag> itemFlags;
    //Furniture
    private List<Behaviour> behaviours;



    public Model(File file, String id){
        this.file = file;
        this.id = id;
    }




    @SneakyThrows
    public Map<String, Object> serialize(String namespace) {
        Map<String, Object> serialised = new HashMap<>();
        serialised.put("display_name", displayName != null ? displayName : file.getName().split("\\.")[0]);
        MapUtil.putIfNotNull(serialised, "permission", permission);
        MapUtil.putIfNotNull(serialised, "resource.material", material);
        MapUtil.putIfNotNull(serialised, "resource.generate", generate);
        MapUtil.putIfNotNull(serialised, "resource.textures", textures);
        MapUtil.putIfNotNull(serialised, "resource.model_path", modelPath());
        if(modifiers != null) {
            for (AttributeModifier modifier : modifiers) {
                MapUtil.putIfNotNull(serialised, "attribute_modifiers." + modifier.getEquipmentslot().name().toLowerCase() + "." + modifier.getModifier().getKey(), modifier.getModifierValue());
            }
        }
        MapUtil.putIfNotNull(serialised, "enchants", enchantments);
        MapUtil.putIfNotNull(serialised, "lore", lore);
        if(itemFlags != null)MapUtil.putIfNotNull(serialised, "item_flags", itemFlags.stream().map(ItemFlag::name).collect(Collectors.toList()));
        if(modelId != 0){
            serialised.put("resource.model_id", modelId);
        }

        if(behaviours != null) {
            for (Behaviour b : behaviours) {
                //Custom Serialisation for Boolean Behaviours cause IA handles them strangely?
                if (b instanceof Hat || b instanceof KeepOnDeath) {
                    Field f = ReflectionUtil.getFieldOnClass(b, "value");
                    f.setAccessible(true);
                    serialised.put("behaviours." + b.name(), f.get(b));
                    f.setAccessible(false);
                } else serialised.put("behaviours." + b.name(), b.serialize());
            }
        }
        YamlUtil.prepareInput(serialised);
        return serialised;
    }

    @SneakyThrows
    private String modelPath() {
       String path = file.getCanonicalPath().split("models")[1].split("\\.")[0];
       if(path.startsWith("/") || path.startsWith("\\")){
           path = path.substring(1);
       }
       //Replace \ with / to support all File System
       path = path.replaceAll("\\\\", "/");
       return path;
    }

    public Behaviour getBehaviourByType(BehaviourType type){
        initialiseBehaviourList();
        //We could also use Streams here but that's less performant
        for(Behaviour b : behaviours){
            if(b.getClass().equals(type.getBehaviourClass())){
                return b;
            }
        }
        return null;
    }


    public void addOrReplaceModifier(AttributeModifier attributeModifier){
        if(modifiers == null) modifiers = new ArrayList<>();
        AttributeModifier toReplace = null;
        for(AttributeModifier mod : modifiers){
            if(mod.getEquipmentslot() == attributeModifier.getEquipmentslot() && mod.getModifier() == attributeModifier.getModifier())toReplace = mod;
        }
        if(toReplace != null)modifiers.remove(toReplace);
        modifiers.add(attributeModifier);
    }



    public AttributeModifier getBySlotAndModifier(EquipmentSlot slot, Attributes modifier){
        if(modifiers == null) return null;
        for(AttributeModifier mod : modifiers){
            if(mod.getEquipmentslot() == slot && mod.getModifier() == modifier)return mod;
        }
        return null;
    }

    public Behaviour getBehaviourByName(String name){
        //We could also use Streams here but that's less performant
        initialiseBehaviourList();
        for(Behaviour b : behaviours){
            if(b.name().equalsIgnoreCase(name)){
                return b;
            }
        }
        return null;
    }

    private void initialiseBehaviourList() {
        if(behaviours == null){
            behaviours = new ArrayList<>();
        }
    }

    public void addOrReplaceBehaviour(Behaviour b) {
        Behaviour toRemove;
        if((toRemove = getBehaviourByName(b.name())) != null){
            behaviours.remove(toRemove);
        }
        addBehaviour(b);
    }
    public void addBehaviour(Behaviour b) {
        initialiseBehaviourList();
        behaviours.add(b);
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

}
