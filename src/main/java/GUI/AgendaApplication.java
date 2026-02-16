package GUI;
//Al programa realizado en la Meta 1.1 agregarle la funcionalidad de gestionar varias direcciones
// asociadas a una misma persona y además que varias personas puedan compartir la misma dirección.
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AgendaApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AgendaApplication.class.getResource("/agenda_crud/InterfaceAgenda.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("AgendaSystem");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.fullScreenProperty().addListener((obs, wasFull, isFull) -> {
            if (!isFull) {
                stage.setIconified(true); // minimiza
            }
        });
        stage.iconifiedProperty().addListener((obs, wasMin, isMin) -> {
            if (!isMin) {
                stage.setFullScreen(true);
            }
        });
        stage.show();
    }
}
