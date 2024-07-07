package de.godly.iasimplifier.dialogs;

import de.godly.iasimplifier.model.settings.Hitbox;
import de.godly.iasimplifier.model.settings.PlaceableOn;
import de.godly.iasimplifier.util.ReflectionUtil;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.Data;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class PlaceableOnDialog extends Dialog<PlaceableOn> {

    @SneakyThrows
    public PlaceableOnDialog(String title, String headerText, String contentText, @Nullable Window owner) {
        super();
        this.setTitle(title);
        this.setHeaderText(headerText);
        this.setContentText(contentText);
        if (owner != null) initOwner(owner);
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        List<Node> toAdd = new ArrayList<>();
        //All Fields in PlaceableOn.class are booleans, we can add a CheckBox for each Field
        for(Field f : ReflectionUtil.getAllFields(PlaceableOn.class)){
            CheckBox radioButton = new CheckBox(f.getName());
            radioButton.setId(f.getName());
            radioButton.setSelected(false);
            toAdd.add(radioButton);
        }
        VBox vbox = new VBox(8, toAdd.toArray(new Node[0]));
        dialogPane.setContent(vbox);
        this.setResultConverter(constructPlaceableOn(vbox));

    }

    @NotNull
    private static Callback<ButtonType, PlaceableOn> constructPlaceableOn(VBox vbox) {
        return buttonType -> {
            //Construct PlaceableOn Instance when "Ok" button is pressed, else return null
            if (buttonType == ButtonType.OK) {
                PlaceableOn placeableOn = new PlaceableOn();
                for (Node child : vbox.getChildrenUnmodifiable()) {
                    Field f = ReflectionUtil.getFieldOnClass(placeableOn, child.getId());
                    f.setAccessible(true);
                    try {
                        f.set(placeableOn, ((CheckBox) child).isSelected());
                        f.setAccessible(false);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }

                }
                return placeableOn;
            }
            return null;
        };
    }
}
