package de.godly.iasimplifier;

import de.godly.iasimplifier.dialogs.AttributeModifierDialog;
import de.godly.iasimplifier.dialogs.BehaviourDialog;
import de.godly.iasimplifier.model.Model;
import de.godly.iasimplifier.model.behaviours.*;
import de.godly.iasimplifier.model.settings.*;
import de.godly.iasimplifier.util.FileUtil;
import de.godly.iasimplifier.util.ReflectionUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.SneakyThrows;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.*;

@Getter
public class SimplifierController {

    @FXML
    private Button exitButton;


    @FXML
    private ListView listView;


    private HashMap<FlowPane, Model> flowPaneHashMap = new HashMap<>();

    @FXML
    private GridPane optionsPane;

    @FXML
    private Label currentlyEditing;

    @FXML
    private TextField namespace;

    public SimplifierController() throws FileNotFoundException {
    }


    @FXML
    public void onHandleQuit() {
        System.exit(0);
    }

    @SneakyThrows
    @FXML
    public void onHandleDirChooseButton() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose a Directory with your IA Models & Textures");
        File file = chooser.showDialog(SimplifierApplication.instance.getStage());
        if (file != null) {
            List<File> files = new ArrayList<>();
            if (FileUtil.subFilesFromDirectory(file, pathname -> pathname.getName().endsWith(".json"), files).isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                alert.setTitle("Error");
                alert.setHeaderText("Directory doesn't contain JSON Files!");
                alert.setContentText("Please select a different Directory!");
                alert.showAndWait();
            }


            SimplifierApplication.instance.getModelManager().loadFiles(files);
            FXMLLoader fxmlLoader = new FXMLLoader(SimplifierApplication.class.getResource("directory-view.fxml"));
            fxmlLoader.setController(this);
            Scene scene = new Scene(fxmlLoader.load());
            SimplifierApplication.instance.getStage().setScene(scene);
            for (File f : SimplifierApplication.instance.getModelManager().getModels().keySet()) {
                Model m = SimplifierApplication.instance.getModelManager().getModels().get(f);
                FlowPane flowPane = new FlowPane();
                flowPane.setPrefSize(180, 150);
                flowPane.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                flowPane.getChildren().add(new Label(f.getName() + " | " + m.getId()));
                listView.getItems().add(flowPane);
                flowPaneHashMap.put(flowPane, m);
            }
            listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FlowPane>() {
                @Override
                public void changed(ObservableValue<? extends FlowPane> observable, FlowPane oldValue, FlowPane newValue) {
                    optionsPane.setVisible(false);
                    optionsPane.getChildren().clear();
                    if (newValue != null) {
                        optionsPane.setVisible(true);
                        listView.getSelectionModel().select(newValue);
                        if (optionsPane.getChildren().isEmpty()) {
                            addToOptionsPane(newValue);
                        }
                        currentlyEditing.setText("Editing: " + (getModel(newValue).getDisplayName() != null ? getModel(newValue).getDisplayName() : getModel(newValue).getId()));
                    }
                }
            });

            //windowTitle.setText("IASimplifier - " + file.getAbsolutePath());

        }
    }


    @FXML
    public void onExport(){
        TextInputDialog td = new TextInputDialog();
        td.setTitle("Export Project");
        td.setContentText("Please follow the IA Doc Guidelines!");
        td.setHeaderText("Please provide your Namespace");
        Optional<String> namespace = td.showAndWait();
        if (namespace.isPresent()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Set Output File");
            fileChooser.setInitialFileName("iaconfig.yml");
            fileChooser.getExtensionFilters().add(FileUtil.YAML_FILTER);
            File file = fileChooser.showSaveDialog(SimplifierApplication.instance.getStage());
            if(file != null){
                Generator generator = new Generator(file);
                if(generator.generateForModel(new IAConfig((String) namespace.get(), new ArrayList<>(SimplifierApplication.instance.getModelManager().getModels().values())))){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Success", ButtonType.FINISH);
                    alert.setHeaderText("Successfully exported the Config!");
                    alert.showAndWait();
                }
            }
        }
    }

    private Model getModel(FlowPane newValue) {
        return flowPaneHashMap.get(newValue);
    }

    @SneakyThrows
    private void addToOptionsPane(FlowPane newValue) {
        final Model m = getModel(newValue);
        List<Button> buttons = new ArrayList<>();
        Button removeButton = new Button("Remove");
        removeButton.getStyleClass().add("coolbutton");
        removeButton.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("Remove " + m.getId());
            alert.setHeaderText("Removal");
            alert.setContentText("Do you want to remove " + m.getId() + "?");
            Optional<ButtonType> delete = alert.showAndWait();
            if (delete.isPresent()) {
                if (delete.get() == ButtonType.YES) {
                    SimplifierApplication.instance.getModelManager().getModels().remove(m.getFile());
                    listView.getItems().remove(newValue);
                    listView.getSelectionModel().selectFirst();
                    FlowPane newPane = (FlowPane) listView.getSelectionModel().getSelectedItem();
                    optionsPane.getChildren().clear();
                    if (optionsPane.getChildren().isEmpty()) {
                        addToOptionsPane(newPane);
                    }
                    currentlyEditing.setText("Editing: " + (getModel(newPane).getDisplayName() != null ? getModel(newPane).getDisplayName() : getModel(newPane).getId()));
                }
            }
        });
        /*
            Change Id Button
         */
        Button changeId = new Button("Change Id");
        changeId.getStyleClass().add("coolbutton");
        changeId.setOnAction(createTextInputFor(newValue, "Set ItemsAdder ID for: %file%", "Please set a new ItemsAdder ID(Not Displayname)", "", ReflectionUtil.getFieldOnClass(m, "id")));
        /*
            Change DisplayName Button
         */
        Button changeDisplayName = new Button("Change Display Name");
        changeDisplayName.getStyleClass().add("coolbutton");
        changeDisplayName.setOnAction(createTextInputFor(newValue, "Set ItemsAdder Displayname for: %file%", "If you don't know formatting, refer to the ItemsAdder Docs", "Please set a ItemsAdder Display Name", ReflectionUtil.getFieldOnClass(m, "displayName")));
        /*
            Behaviour Button
         */
        Button addOrEditBehaviour = new Button("Add/Edit Behaviour");
        addOrEditBehaviour.setOnAction(actionEvent -> {
            Dialog<BehaviourType> typeSelector = createBehaviourChooser();
            Optional<BehaviourType> optionalType = typeSelector.showAndWait();
            if(optionalType.isPresent()){
                BehaviourType type = optionalType.get();
                Behaviour b = m.getBehaviourByType(type);
                BehaviourDialog dialog = new BehaviourDialog("Edit Behaviour","" , "", type, b, newValue.getScene().getWindow());
                dialog.showAndWait().ifPresent(m::addOrReplaceBehaviour);
            }
        });
        addOrEditBehaviour.getStyleClass().add("coolbutton");
        /*
            Attribute Button
         */
        Button addOrEditAttribute = new Button("Add/Edit Attribute");
        addOrEditAttribute.setOnAction(actionEvent -> {
            AttributeModifierDialog attributeModifierDialog = new AttributeModifierDialog("Add/Edit Attribute", "Select an Attribute to add or modify!", "", newValue.getScene().getWindow());
            Optional<AttributeModifier> optionalType = attributeModifierDialog.showAndWait();
            if(optionalType.isPresent()){
                optionalType.ifPresent(m::addOrReplaceModifier);
            }
        });
        addOrEditAttribute.getStyleClass().add("coolbutton");
        /*
            Item Flags Button
         */
        Button editItemFlags = new Button("Edit ItemFlags");
        editItemFlags.setOnAction(actionEvent -> {
            Dialog<List<ItemFlag>> flagsEdit = createItemFlagChooser();
            flagsEdit.showAndWait().ifPresent(m::setItemFlags);
        });
        editItemFlags.getStyleClass().add("coolbutton");
        buttons.add(removeButton);
        buttons.add(changeId);
        buttons.add(changeDisplayName);
        buttons.add(addOrEditAttribute);
        buttons.add(addOrEditBehaviour);
        buttons.add(editItemFlags);
        int row = 0;
        int column = 0;
        for(Button button : buttons){
            optionsPane.add(button, column, row);
            column++;
            if(column > 5){
                column = 0;
                row++;
            }
        }
    }


    private Dialog<List<ItemFlag>> createItemFlagChooser() {
        Dialog<List<ItemFlag>> chooseType = new Dialog<>();
        chooseType.setTitle("Choose ItemFlag to add/or edit!");
        chooseType.setHeaderText("Click on the Dropdown");
        chooseType.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ObservableList<ItemFlag> types = FXCollections.observableArrayList(ItemFlag.values());
        CheckComboBox<ItemFlag> typeComboBox = new CheckComboBox<>(types);
        Label label = new Label("Item Flags", typeComboBox);
        VBox vBox = new VBox(8, typeComboBox, label);
        chooseType.getDialogPane().setContent(vBox);
        chooseType.setResultConverter(buttonType -> {
            if(buttonType == ButtonType.OK){
                List<ItemFlag> flags = new ArrayList<>();
                for(ItemFlag flag : typeComboBox.getItems()){
                    if(typeComboBox.getItemBooleanProperty(flag).get()){
                        flags.add(flag);
                    }
                }
                return flags;
            }
            return null;
        });
        return chooseType;
    }

    private Dialog<BehaviourType> createBehaviourChooser() {
        Dialog<BehaviourType> chooseType = new Dialog<>();
        chooseType.setTitle("Choose Behaviour Type to add or edit!");
        chooseType.setHeaderText("Click on the Dropdown");
        chooseType.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ObservableList<BehaviourType> types = FXCollections.observableArrayList(BehaviourType.values());
        ComboBox<BehaviourType> typeComboBox = new ComboBox<>(types);
        typeComboBox.getSelectionModel().selectFirst();
        Label label = new Label("Behaviour Type", typeComboBox);
        VBox vBox = new VBox(8, typeComboBox, label);
        chooseType.getDialogPane().setContent(vBox);
        chooseType.setResultConverter(buttonType -> {
            if(buttonType == ButtonType.OK){
                return typeComboBox.getSelectionModel().getSelectedItem();
            }
            return null;
        });
        return chooseType;
    }


    public EventHandler<ActionEvent> createTextInputFor(FlowPane flowPane, String title, String content, String header, Field variable) {
        return actionEvent -> {
            Model m = getModel(flowPane);
            TextInputDialog td = new TextInputDialog();
            if(m.getDisplayName() != null) td.getEditor().setText(m.getDisplayName());
            td.initOwner(flowPane.getScene().getWindow());
            td.setTitle(title.replaceAll("%file%", m.getFile().getName()));
            td.setContentText(content);
            td.setHeaderText(header);
            Optional<?> newDisplayName = td.showAndWait();
            if (newDisplayName.isPresent()) {
                variable.setAccessible(true);
                try {
                    variable.set(m, newDisplayName.get());
                    variable.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                flowPane.getChildren().stream().filter(node -> node instanceof Label).forEach(label -> ((Label) label).setText(m.getFile().getName() + " | " + m.getId()));
            }

        };
    }

}