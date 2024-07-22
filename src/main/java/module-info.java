module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires org.jetbrains.annotations;

    opens org.example to javafx.fxml;
    exports org.example;
}
