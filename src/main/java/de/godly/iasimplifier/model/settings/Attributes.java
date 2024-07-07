package de.godly.iasimplifier.model.settings;

import lombok.Getter;

public enum Attributes {

    MAX_HEALTH("maxHealth"), MOVEMENT_SPEED("movementSpeed"),
    ARMOR("armor"), LUCK("luck"), ATTACK_SPEED("attackSpeed"),
    ATTACK_DAMAGE("attackDamage"), ATTACK_KNOCKBACK("attackKnockback"),
    ARMOR_TOUGHNESS("armorToughness");


    @Getter
    private final String key;

    Attributes(String key){
        this.key = key;
    }

}
