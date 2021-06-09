module com.github {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.java;
    requires mongo.java.driver;
    requires log4j;

    opens com.github to javafx.fxml;
    exports com.github;
    exports com.github.db;
    exports com.github.exceptions;
}