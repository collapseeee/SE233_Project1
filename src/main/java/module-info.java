module se233.se233_project1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.github.kokorin.jaffree;
    requires java.desktop;
    requires org.apache.logging.log4j;


    opens se233.se233_project1 to javafx.fxml;
    opens se233.se233_project1.controller to javafx.fxml;
    exports se233.se233_project1;
    exports se233.se233_project1.controller;
}