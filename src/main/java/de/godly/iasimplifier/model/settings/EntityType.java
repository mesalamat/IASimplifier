package de.godly.iasimplifier.model.settings;

import lombok.Getter;

public enum EntityType {

    ARMOR_STAND("armor_stand"), ITEM_FRAME("item_display");


    @Getter
    private final String iaName;

    EntityType(String iaName){
        this.iaName = iaName;
    }

}
