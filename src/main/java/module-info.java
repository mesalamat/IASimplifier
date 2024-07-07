module de.godly.iasimplifier {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.yaml.snakeyaml;
    requires lombok;
    requires org.jetbrains.annotations;

    opens de.godly.iasimplifier to javafx.fxml;
    exports de.godly.iasimplifier;
}