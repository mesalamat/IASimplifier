package de.godly.iasimplifier.util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.lang.reflect.Field;

public class FXUtil {

    public static void makeNumeric(TextField textField, Field field) {
        if (field.getGenericType() == int.class) {
            textField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        } else if (field.getGenericType() == double.class) {
            textField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
        }
    }
}
