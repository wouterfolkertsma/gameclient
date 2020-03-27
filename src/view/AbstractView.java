package view;

import controller.AbstractController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main view Class where all other views extend from. It builds a view from an FXML file and css.
 * The correct controller will be set in child Classes.
 *
 * @author Wouter
 */
public abstract class AbstractView {
    private Stage stage;
    private Scene scene;
    private AbstractController controller;

    AbstractView(Stage stage, AbstractController controller, String fxmlView) {
        this.stage = stage;
        this.controller = controller;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlView));
            loader.setController(controller);
            Parent root = loader.load();
            this.scene = new Scene(root, 800, 500);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void show() {
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public void hide() {
        this.stage.hide();
    }
}
