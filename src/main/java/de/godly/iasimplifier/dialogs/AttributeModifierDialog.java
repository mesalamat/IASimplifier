package de.godly.iasimplifier.dialogs;

import de.godly.iasimplifier.model.behaviours.BehaviourType;
import de.godly.iasimplifier.model.settings.*;
import de.godly.iasimplifier.util.FXUtil;
import de.godly.iasimplifier.util.ReflectionUtil;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class AttributeModifierDialog extends Dialog<AttributeModifier> {

    @SneakyThrows
    public AttributeModifierDialog(String title, String headerText, String contentText, @Nullable Window owner) {
        super();
        this.setTitle(title);
        this.setHeaderText(headerText);
        this.setContentText(contentText);
        if (owner != null) initOwner(owner);
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        List<Node> toAdd = new ArrayList<>();
        for (Field f : ReflectionUtil.getAllFields(AttributeModifier.class)) {
            if (f.getGenericType() == EquipmentSlot.class) {
                ObservableList<EquipmentSlot> types = FXCollections.observableArrayList(EquipmentSlot.values());
                ComboBox<EquipmentSlot> typeComboBox = new ComboBox<>(types);
                Label label = new Label("Equipment Slot", typeComboBox);

                typeComboBox.getSelectionModel().selectFirst();
                typeComboBox.setId(f.getName());
                toAdd.add(typeComboBox);
                toAdd.add(label);
            } else if (f.getGenericType() == Attributes.class) {
                ObservableList<Attributes> types = FXCollections.observableArrayList(Attributes.values());
                ComboBox<Attributes> typeComboBox = new ComboBox<>(types);
                typeComboBox.getSelectionModel().selectFirst();
                typeComboBox.setId(f.getName());
                Label label = new Label("Attribute Type", typeComboBox);
                toAdd.add(typeComboBox);
                toAdd.add(label);
            } else if (f.getGenericType() == double.class) {
                TextField field = new TextField();
                field.setId(f.getName());
                field.setPromptText("Enter " + f.getName());
                FXUtil.makeNumeric(field, f);
                toAdd.add(field);
            }
        }
        VBox vbox = new VBox(8, toAdd.toArray(new Node[0]));
        dialogPane.setContent(vbox);
        this.setResultConverter(constructPlaceableOn(vbox));
    }

    @NotNull
    private static Callback<ButtonType, AttributeModifier> constructPlaceableOn(VBox vbox) {
        return buttonType -> {
            if (buttonType == ButtonType.OK) {
                AttributeModifier attributeModifier = new AttributeModifier();
                for (Node child : vbox.getChildrenUnmodifiable()) {
                    if(child instanceof Label)continue;
                    Field f = ReflectionUtil.getFieldOnClass(attributeModifier, child.getId());
                    f.setAccessible(true);
                    try {
                        if (f.getGenericType() == Attributes.class) {
                            f.set(attributeModifier, ((ComboBox<Attributes>) child).getSelectionModel().getSelectedItem());
                        }else if(f.getGenericType() == EquipmentSlot.class){
                            f.set(attributeModifier, ((ComboBox<EquipmentSlot>) child).getSelectionModel().getSelectedItem());
                        }else if(f.getGenericType() == double.class){
                            f.set(attributeModifier, Double.parseDouble(((TextField)child).getText()));
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    f.setAccessible(false);
                }
                return attributeModifier;
            }
            return null;
        };
    }

}
