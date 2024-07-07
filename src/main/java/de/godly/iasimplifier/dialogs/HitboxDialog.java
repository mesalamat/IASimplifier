package de.godly.iasimplifier.dialogs;

import de.godly.iasimplifier.model.behaviours.Behaviour;
import de.godly.iasimplifier.model.behaviours.BehaviourType;
import de.godly.iasimplifier.model.behaviours.MusicDisc;
import de.godly.iasimplifier.model.settings.Hitbox;
import de.godly.iasimplifier.model.settings.Song;
import de.godly.iasimplifier.util.FXUtil;
import de.godly.iasimplifier.util.ReflectionUtil;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.Data;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class HitboxDialog extends Dialog<Hitbox> {

    @SneakyThrows
    public HitboxDialog(String title, String headerText, String contentText, @Nullable Window owner) {
        super();
        this.setTitle(title);
        this.setHeaderText(headerText);
        this.setContentText(contentText);
        if (owner != null) initOwner(owner);
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        List<Node> toAdd = new ArrayList<>();
        //Because Hitbox.class only contains int or double fields we can create a TextField for each one of them
        for(Field f : ReflectionUtil.getAllFields(Hitbox.class)){
            TextField field = new TextField();
            field.setId(f.getName());
            field.setPromptText("Enter " + f.getName());
            FXUtil.makeNumeric(field, f);
            toAdd.add(field);
        }
        VBox vbox = new VBox(8, toAdd.toArray(new Node[0]));
        dialogPane.setContent(vbox);
        this.setResultConverter(buttonType -> {
            //If "Ok" Button was pressed convert the Result, else return null
            if (buttonType == ButtonType.OK) {
                int width = 0, length = 0, height= 0;
                //First get the required Arguments or set them to 0 if no Input was given
                for(Node n : vbox.getChildren()){
                    if(n instanceof TextField field) {
                        if (field.getId().equals("width")) {
                            width = field.getText().isEmpty() ? 0 : Integer.parseInt(field.getText());
                        }else if (field.getId().equals("length")) {
                            length = field.getText().isEmpty() ? 0 : Integer.parseInt(field.getText());
                        }else if (field.getId().equals("height")) {
                            height = field.getText().isEmpty() ? 0 : Integer.parseInt(field.getText());
                        }
                    }
                }
                Hitbox hitbox = new Hitbox(length,width, height);
                //Add optionals if existent
                for (Node children : vbox.getChildrenUnmodifiable()) {
                    Field f = ReflectionUtil.getFieldOnClass(hitbox, children.getId());
                    if (children instanceof TextField field) {
                        f.setAccessible(true);
                        if(f.getGenericType() == double.class) {
                            try {
                                if(field.getText().isEmpty())continue;
                                f.set(hitbox,Double.parseDouble(field.getText()));
                                f.setAccessible(false);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                return hitbox;
            }
            return null;
        });

    }

}
