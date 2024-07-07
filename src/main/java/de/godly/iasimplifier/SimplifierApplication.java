package de.godly.iasimplifier;

import de.godly.iasimplifier.model.ModelManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;

import java.io.IOException;

@Getter
public class SimplifierApplication extends Application {


    public static SimplifierApplication instance;
    private ModelManager modelManager;

    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
        modelManager = new ModelManager();
        FXMLLoader fxmlLoader = new FXMLLoader(SimplifierApplication.class.getResource("choose-directory.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        this.stage = stage;
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("IASimplifier");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }


}