module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires org.fusesource.jansi;
    requires se.michaelthelin.spotify;
    requires com.google.gson;
    requires nv.i18n;
    requires org.apache.httpcomponents.core5.httpcore5;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}