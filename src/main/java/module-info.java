module com.tcj.spui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
//    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires se.michaelthelin.spotify;
    requires org.fusesource.jansi;
    requires nv.i18n;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires com.google.gson;
  requires java.net.http;

  opens com.tcj.spui to javafx.fxml;
    exports com.tcj.spui;
}