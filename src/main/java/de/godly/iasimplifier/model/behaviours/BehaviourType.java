package de.godly.iasimplifier.model.behaviours;

import lombok.Getter;
import lombok.Setter;

public enum BehaviourType {

    HAT(Hat.class), FURNITURE(Furniture.class), FURNITURE_SIT(FurnitureSit.class), KEEP_ON_DEATH(KeepOnDeath.class), GUN(Gun.class), MUSIC_DISC(MusicDisc.class);


    @Getter
    @Setter
    private Class<? extends Behaviour> behaviourClass;

    BehaviourType(Class<? extends Behaviour> behaviourClass){
        this.behaviourClass = behaviourClass;
    }

}
