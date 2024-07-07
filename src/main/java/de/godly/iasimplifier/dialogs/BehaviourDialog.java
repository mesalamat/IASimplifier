package de.godly.iasimplifier.dialogs;

import de.godly.iasimplifier.model.behaviours.*;
import de.godly.iasimplifier.model.settings.EntityType;
import de.godly.iasimplifier.model.settings.Hitbox;
import de.godly.iasimplifier.model.settings.PlaceableOn;
import de.godly.iasimplifier.model.settings.Song;
import de.godly.iasimplifier.util.FXUtil;
import de.godly.iasimplifier.util.ReflectionUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Data
public class BehaviourDialog extends Dialog<Behaviour> {


    private BehaviourType behaviourType;
    @Nullable
    private Hitbox hitbox;
    @Nullable
    private PlaceableOn placeableOn;

    @SneakyThrows
    public BehaviourDialog(String title, String headerText, String contentText, BehaviourType behaviourType, @Nullable Behaviour behaviour, @Nullable Window owner) {
        super();
        this.behaviourType = behaviourType;
        this.setTitle(title);
        this.setHeaderText(headerText);
        this.setContentText(contentText);
        if (owner != null) initOwner(owner);
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        List<Node> toAdd = new ArrayList<>();
        addNodes(behaviourType, behaviour, toAdd);
        VBox vbox = new VBox(8, toAdd.toArray(new Node[0]));
        dialogPane.setContent(vbox);

        this.setResultConverter(constructBehaviour(behaviourType, behaviour, vbox));

    }

    @NotNull
    private Callback<ButtonType, Behaviour> constructBehaviour(BehaviourType behaviourType, @Nullable Behaviour behaviour, VBox vbox) {
        return buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    Constructor declared = behaviourType.getBehaviourClass().getDeclaredConstructor();
                    Behaviour b = (Behaviour) declared.newInstance();
                    //Check for Special Cases in Behaviour
                    if (behaviourType == BehaviourType.MUSIC_DISC) {
                        //This is hella ugly but works
                        String songName = ((TextField) vbox.getChildren().stream().filter(textField -> textField.getId().equals("songName") && textField instanceof TextField).
                                findFirst().get()).getText();
                        String songDescription = ((TextField) vbox.getChildren().stream().filter(textField -> textField.getId().equals("songDescription") && textField instanceof TextField).
                                findFirst().get()).getText();
                        ((MusicDisc) b).setSong(new Song(songName, songDescription));
                        return b;
                    } else if (behaviourType == BehaviourType.FURNITURE) {
                        //Add Optionals to Furniture
                        if (hitbox != null) ((Furniture) b).setHitbox(hitbox);
                        if(placeableOn != null) ((Furniture)b).setPlaceableOn(placeableOn);
                    }
                    for (Node children : vbox.getChildrenUnmodifiable()) {
                        //Exempt special cases
                        if ((children.getId() != null && children.getId().contains("song")) || children instanceof Button || children instanceof Label) continue;
                        Field f = ReflectionUtil.getFieldOnClass(b, children.getId());
                        f.setAccessible(true);
                        if (children instanceof CheckBox button) {
                            f.set(b, button.isSelected());
                        }else if (children instanceof TextField field) {
                            if (f.getGenericType() == String.class) {
                                f.set(b, field.getText());
                            } else if (f.getGenericType() == int.class) {
                                f.set(b, field.getText().isEmpty() ? 0 : Integer.parseInt(field.getText()));
                            } else if (f.getGenericType() == double.class) {
                                f.set(b, field.getText().isEmpty() ? 0 : Double.parseDouble(field.getText()));
                            }
                        }else if(children instanceof ComboBox<?> c){
                            f.set(b, c.getSelectionModel().getSelectedItem());
                        }
                        f.setAccessible(false);
                    }
                    return b;
                } catch (NoSuchMethodException | InstantiationException | InvocationTargetException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
            return behaviour;
        };
    }

    private void addNodes(BehaviourType behaviourType, @Nullable Behaviour behaviour, List<Node> toAdd) throws IllegalAccessException {
        for (Field field : ReflectionUtil.getAllFields(behaviourType.getBehaviourClass())) {
            field.setAccessible(true);
            //Lots of If-Statements, could probably do this better but this does the trick for now and overengineering this is not really necessary
            if (field.getGenericType() == String.class || field.getGenericType() == int.class || field.getGenericType() == double.class) {
                TextField textField = new TextField();
                textField.setPromptText("Enter " + field.getName());
                FXUtil.makeNumeric(textField, field);
                if (behaviour != null && field.get(behaviour) != null) {
                    textField.setText(field.get(behaviour).toString());
                }
                textField.setId(field.getName());
                toAdd.add(textField);
            }
            if(field.getGenericType() == EntityType.class){
                ObservableList<EntityType> types = FXCollections.observableArrayList(EntityType.values());
                ComboBox<EntityType> typeComboBox = new ComboBox<>(types);
                Label label = new Label("Entity Type", typeComboBox);
                typeComboBox.setId(field.getName());
                typeComboBox.getSelectionModel().selectFirst();
                toAdd.add(typeComboBox);
                toAdd.add(label);
            }
            if (field.getGenericType() == boolean.class) {
                CheckBox radioButton = new CheckBox(field.getName());
                Label label = new Label(field.getName(), radioButton);
                radioButton.setId(field.getName());
                radioButton.setSelected((Boolean) (behaviour != null ? field.get(behaviour) : false));
                toAdd.add(radioButton);
                toAdd.add(label);
            }
            if(field.getGenericType() == Hitbox.class){
                Button button = new Button("Change Hitbox");
                button.setOnAction(actionEvent ->  {
                    HitboxDialog dialog = new HitboxDialog("Configure Hitbox", "Change your Hitbox", "You can leave out the Offset Values", null);
                    //Return type of the Dialog is a Hitbox so we can just set the HitBox if Present
                    dialog.showAndWait().ifPresent(this::setHitbox);
                });
                button.setId("hitbox");
                toAdd.add(button);
            }
            if(field.getGenericType() == PlaceableOn.class){
                Button button = new Button("Change Placeable");
                button.setOnAction(actionEvent ->  {
                    PlaceableOnDialog dialog = new PlaceableOnDialog("Configure Placeable", "Change where this Furniture can be placed on", "At least one Checkbox must be active!", null);
                    //Same case as HitboxDialog, check comment above
                    dialog.showAndWait().ifPresent(this::setPlaceableOn);
                });
                button.setId("placeable");
                toAdd.add(button);
            }
            if (field.getGenericType() == Song.class) {
                TextField nameTextField = new TextField("Enter Song Name");
                nameTextField.setId("songName");
                TextField descriptionTextField = new TextField("Enter Song Description");
                descriptionTextField.setId("songDescription");
                if (behaviour != null && field.get(behaviour) != null) {
                    nameTextField.setText(((Song)field.get(behaviour)).getName());
                    descriptionTextField.setText(((Song)field.get(behaviour)).getDescription());
                }
                toAdd.add(nameTextField);
                toAdd.add(descriptionTextField);
            }
            field.setAccessible(false);
        }
    }

}
