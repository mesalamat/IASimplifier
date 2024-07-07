package de.godly.iasimplifier.model.behaviours;

import de.godly.iasimplifier.model.settings.Song;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
public class MusicDisc implements Behaviour{

    private Song song;

    public MusicDisc(){}

    @Override
    public String name() {
        return "music_disc";
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialised = new HashMap<>();
        serialised.put("song", song.serialize());
        return serialised;
    }
}
