import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        Scene scene = new Scene(root, 1100, 750);
        try { scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); }
        catch (Exception e) { System.out.println("style.css не знайдено"); }

        stage.setTitle("Страхова компанія");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}