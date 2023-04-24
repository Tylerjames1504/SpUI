module com.tcj.spuiTests {
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
  requires se.michaelthelin.spotify;
  requires org.fusesource.jansi;
  requires nv.i18n;
  requires org.apache.httpcomponents.core5.httpcore5;
  requires com.google.gson;
  requires java.net.http;
  requires org.json;
  requires org.junit.jupiter.api;
  requires com.tcj.spui;
  requires junit;
  requires org.junit.jupiter;

  opens com.tcj.spuiTests to javafx.fxml;
  exports com.tcj.spuiTests;
}