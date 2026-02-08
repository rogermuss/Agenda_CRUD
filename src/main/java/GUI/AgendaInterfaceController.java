package GUI;

import DB.Persona;
import DB.PersonaDAO;
import DB.Telefono;
import DB.TelefonoDAO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.List;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AgendaInterfaceController {

    @FXML private SplitPane splitContainer;
    @FXML private SplitPane listsContainer;

    @FXML private TableView<Persona> tablaPersonas;
    @FXML private TableColumn<Persona, Integer> colID;
    @FXML private TableColumn<Persona, String> colName;
    @FXML private TableColumn<Persona, Void> colModifications;

    @FXML private TableView<Telefono> tablaTelefonos;
    @FXML private TableColumn<Telefono, Integer> colPersonID;
    @FXML private TableColumn<Telefono, Integer> colTelID;
    @FXML private TableColumn<Telefono, String> colNumber;
    @FXML private TableColumn<Telefono, Void> colPhoneModifications;

    // Campos personas
    @FXML private TextField nameTextField;
    @FXML private Button addPersonButton;

    // Campos telefonos
    @FXML private TextField phoneTextField;
    @FXML private Button addPhoneButton;

    private final PersonaDAO personaDAO = new PersonaDAO();
    private final TelefonoDAO telefonoDAO = new TelefonoDAO();

    private Persona personaEnEdicion = null;
    private Telefono telefonoEnEdicion = null;

    @FXML
    private void initialize() {

        // Configuración de columnas
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        colTelID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPersonID.setCellValueFactory(new PropertyValueFactory<>("personaId"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        // Centrar y agregar padding a las columnas ID
        colID.setStyle("-fx-alignment: CENTER; -fx-padding: 0 10 0 10;");
        colTelID.setStyle("-fx-alignment: CENTER; -fx-padding: 0 10 0 10;");
        colPersonID.setStyle("-fx-alignment: CENTER; -fx-padding: 0 10 0 10;");

        ajustarAnchoColumnasPersonas();
        ajustarAnchoColumnasTelefonos();

        configurarBotonesPersonas();
        configurarBotonesTelefonos();

        cargarPersonas();

        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldP, newP) -> {
            if (newP != null) {
                cargarTelefonos(newP.getId());
            } else {
                tablaTelefonos.getItems().clear();
            }
        });

        addPersonButton.setOnAction(e -> {
            if (personaEnEdicion == null) agregarPersona();
            else modificarPersona();
        });

        addPhoneButton.setOnAction(e -> {
            if (telefonoEnEdicion == null) agregarTelefono();
            else modificarTelefono();
        });
    }

    private void ajustarAnchoColumnasPersonas() {
        // Establecer ancho inicial inmediatamente
        colID.setPrefWidth(100);
        colName.setPrefWidth(250);
        colModifications.setPrefWidth(200);

        // Listener para ajuste dinámico
        tablaPersonas.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double usableWidth = newWidth.doubleValue() - 15;

            colID.setPrefWidth(usableWidth * 0.15);
            colName.setPrefWidth(usableWidth * 0.45);
            colModifications.setPrefWidth(usableWidth * 0.40);
        });
    }

    private void ajustarAnchoColumnasTelefonos() {
        tablaTelefonos.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double usableWidth = newWidth.doubleValue() - 15;

            colTelID.setPrefWidth(usableWidth * 0.15);
            colPersonID.setPrefWidth(usableWidth * 0.15);
            colNumber.setPrefWidth(usableWidth * 0.40);
            colPhoneModifications.setPrefWidth(usableWidth * 0.30);
        });
    }

    private void configurarBotonesPersonas() {
        colModifications.setCellFactory(param -> new TableCell<>() {
            private final Button btnModificar = new Button("Modificar");
            private final Button btnEliminar = new Button("Eliminar");

            private final HBox pane = new HBox(10, btnModificar, btnEliminar);

            {
                btnModificar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;"); // verde
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // rojo

                pane.setAlignment(Pos.CENTER);

                btnModificar.setOnAction(event -> {
                    Persona persona = getTableView().getItems().get(getIndex());
                    activarModoEdicionPersona(persona);
                });

                btnEliminar.setOnAction(event -> {
                    Persona persona = getTableView().getItems().get(getIndex());
                    eliminarPersona(persona);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void configurarBotonesTelefonos() {
        colPhoneModifications.setCellFactory(param -> new TableCell<>() {
            private final Button btnModificar = new Button("Modificar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox pane = new HBox(10, btnModificar, btnEliminar);

            {
                btnModificar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;"); // verde
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // rojo

                pane.setAlignment(Pos.CENTER);

                btnModificar.setOnAction(event -> {
                    Telefono telefono = getTableView().getItems().get(getIndex());
                    activarModoEdicionTelefono(telefono);
                });

                btnEliminar.setOnAction(event -> {
                    Telefono telefono = getTableView().getItems().get(getIndex());
                    eliminarTelefono(telefono);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void cargarPersonas() {
        try {
            List<Persona> personas = personaDAO.getAll();

            // DEBUG: Imprimir en consola lo que se carga
            System.out.println("=== CARGANDO PERSONAS ===");
            System.out.println("Total personas encontradas: " + personas.size());
            for (Persona p : personas) {
                System.out.println("ID: " + p.getId() + " | Nombre: " + p.getNombre());
            }
            System.out.println("========================");

            tablaPersonas.getItems().setAll(personas);
            tablaPersonas.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarTelefonos(int personaId) {
        try {
            tablaTelefonos.getItems().setAll(telefonoDAO.getAllByPersonaId(personaId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void agregarPersona() {
        String nombre = nameTextField.getText().trim();

        if (nombre.isEmpty()) return;

        try {
            Persona nuevaPersona = new Persona(0, nombre);
            personaDAO.insert(nuevaPersona);
            cargarPersonas();
            limpiarCamposPersona();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void activarModoEdicionPersona(Persona persona) {
        personaEnEdicion = persona;
        nameTextField.setText(persona.getNombre());
        addPersonButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");

        addPersonButton.setText("Guardar Cambios");
    }

    private void modificarPersona() {
        if (personaEnEdicion == null) return;

        String nombre = nameTextField.getText().trim();
        if (nombre.isEmpty()) return;

        try {
            personaEnEdicion.setNombre(nombre);
            personaDAO.update(personaEnEdicion);
            cargarPersonas();
            limpiarCamposPersona();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminarPersona(Persona persona) {
        try {
            personaDAO.delete(persona.getId());
            cargarPersonas();
            tablaTelefonos.getItems().clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void limpiarCamposPersona() {
        personaEnEdicion = null;
        nameTextField.clear();
        addPersonButton.setText("Agregar");
        addPersonButton.setStyle("-fx-background-color: #FFFFFF;");
    }

    private void agregarTelefono() {
        Persona personaSeleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) return;

        String numeroTelefono = phoneTextField.getText().trim();
        if (numeroTelefono.isEmpty()) return;

        try {
            Telefono nuevoTelefono = new Telefono(personaSeleccionada.getId(), numeroTelefono);
            telefonoDAO.insert(nuevoTelefono);
            cargarTelefonos(personaSeleccionada.getId());
            limpiarCamposTelefono();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void activarModoEdicionTelefono(Telefono telefono) {
        telefonoEnEdicion = telefono;
        phoneTextField.setText(telefono.getTelefono());
        addPhoneButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
        addPhoneButton.setText("Guardar Cambios");
    }

    private void modificarTelefono() {
        if (telefonoEnEdicion == null) return;

        String numeroTelefono = phoneTextField.getText().trim();
        if (numeroTelefono.isEmpty()) return;

        try {
            telefonoEnEdicion.setTelefono(numeroTelefono);
            telefonoDAO.update(telefonoEnEdicion);
            cargarTelefonos(telefonoEnEdicion.getPersonaId());
            limpiarCamposTelefono();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminarTelefono(Telefono telefono) {
        try {
            telefonoDAO.delete(telefono.getId());
            cargarTelefonos(telefono.getPersonaId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void limpiarCamposTelefono() {
        telefonoEnEdicion = null;
        phoneTextField.clear();
        addPhoneButton.setText("Agregar");
        addPhoneButton.setStyle("-fx-background-color: #FFFFFF;");
    }
}