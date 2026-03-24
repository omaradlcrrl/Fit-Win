module org.example.fitwin {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires org.json;
    requires java.desktop;
    requires java.prefs;
    requires javafx.graphics;
    requires javafx.base;


    exports org.example.fitwin;

    opens org.example.fitwin.model to com.google.gson;

    exports org.example.fitwin.model;
    opens org.example.fitwin to com.google.gson, javafx.fxml;
}
