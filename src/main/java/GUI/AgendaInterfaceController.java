package GUI;

import DB.*;
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
import javafx.scene.shape.Circle;

public class AgendaInterfaceController {

    @FXML private SplitPane splitContainer;
    @FXML private SplitPane listsContainer;

    @FXML private TableView<Direccion> tablaDirecciones;
    @FXML private TableColumn<Direccion, Integer> colDirectionId;
    @FXML private TableColumn<Direccion, String> colStreet;
    @FXML private TableColumn<Direccion, Void> colDirectionModifications;

    @FXML private TableView<Persona_Direccion> tablaEnlaces;
    @FXML private TableColumn<Persona_Direccion, Integer> colAssociatedPersonId;
    @FXML private TableColumn<Persona_Direccion, Integer> colAssociatedDirectionId;
    @FXML private TableColumn<Persona_Direccion, Void> colAssociatedModifications;

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

    @FXML private TextField directionTextField;
    @FXML private Button addDirectionButton;

    @FXML private Button associationButton;
    @FXML private Circle indicator;

    private final PersonaDAO personaDAO = new PersonaDAO();
    private final TelefonoDAO telefonoDAO = new TelefonoDAO();
    private final DireccionDAO direccionDAO = new DireccionDAO();
    private final Persona_DireccionDAO persona_direccionDAO = new Persona_DireccionDAO();

    private Persona personaEnEdicion = null;
    private Telefono telefonoEnEdicion = null;
    private Direccion direccionEnEdicion = null;

    private boolean hayPersonaSeleccionada = false;
    private boolean hayDireccionSeleccionada = false;

    @FXML
    private void initialize() {

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        colTelID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPersonID.setCellValueFactory(new PropertyValueFactory<>("personaId"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        colDirectionId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStreet.setCellValueFactory(new PropertyValueFactory<>("calle"));

        colID.setStyle("-fx-alignment: CENTER; -fx-padding: 0 10 0 10;");
        colTelID.setStyle("-fx-alignment: CENTER; -fx-padding: 0 10 0 10;");
        colPersonID.setStyle("-fx-alignment: CENTER; -fx-padding: 0 10 0 10;");

        activateIndicator();

        ajustarAnchoColumnasPersonas();
        ajustarAnchoColumnasTelefonos();
        ajustarAnchoColumnasDirecciones();
        ajustarAnchoColumnasPersonas_Direcciones();

        configurarBotonesPersonas();
        configurarBotonesTelefonos();
        configurarBotonesDirecciones();
        configurarBotonesPersonas_Direcciones();

        cargarPersonas();
        cargarDirecciones();
        cargarPersonas_Direcciones();

        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldP, newP) -> {
            if (newP != null) {
                hayPersonaSeleccionada = true;
                cargarTelefonos(newP.getId());
            } else {
                hayPersonaSeleccionada = false;
                tablaTelefonos.getItems().clear();
            }
            activateIndicator();
        });

        tablaDirecciones.getSelectionModel().selectedItemProperty().addListener((obs, oldP, newP) -> {
            hayDireccionSeleccionada = newP != null;
            activateIndicator();
        });



        addPersonButton.setOnAction(e -> {
            if (personaEnEdicion == null) agregarPersona();
            else modificarPersona();
        });

        addPhoneButton.setOnAction(e -> {
            if (telefonoEnEdicion == null) agregarTelefono();
            else modificarTelefono();
        });

        addDirectionButton.setOnAction(e -> {
            if(direccionEnEdicion == null) agregarDireccion();
            else modificarDireccion();
        });

        associationButton.setOnAction(e -> {
            agregarAsociacion();
        });
    }

    private void activateIndicator(){
        if(hayPersonaSeleccionada && hayDireccionSeleccionada) indicator.setStyle("-fx-fill: #2ecc71;");
        else indicator.setStyle("-fx-fill: #e74c3c;");
    }

    private void ajustarAnchoColumnasPersonas() {
        colID.setPrefWidth(100);
        colName.setPrefWidth(250);
        colModifications.setPrefWidth(200);

        tablaPersonas.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double usableWidth = newWidth.doubleValue() - 15;

            colID.setPrefWidth(usableWidth * 0.15);
            colName.setPrefWidth(usableWidth * 0.45);
            colModifications.setPrefWidth(usableWidth * 0.40);
        });
    }

    private void ajustarAnchoColumnasDirecciones() {
        tablaDirecciones.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double usableWidth = newWidth.doubleValue() - 15;

            colDirectionId.setPrefWidth(usableWidth * 0.15);
            colStreet.setPrefWidth(usableWidth * 0.45);
            colDirectionModifications.setPrefWidth(usableWidth * 0.40);
        });
    }

    private void ajustarAnchoColumnasPersonas_Direcciones() {
        tablaEnlaces.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double usableWidth = newWidth.doubleValue() - 15;

            colAssociatedPersonId.setPrefWidth(usableWidth * 0.30);
            colAssociatedDirectionId.setPrefWidth(usableWidth * 0.30);
            colAssociatedModifications.setPrefWidth(usableWidth * 0.40);
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

    private void configurarBotonesDirecciones() {
        colDirectionModifications.setCellFactory(param -> new TableCell<>() {
            private final Button btnModificar = new Button("Modificar");
            private final Button btnEliminar = new Button("Eliminar");

            private final HBox pane = new HBox(10, btnModificar, btnEliminar);

            {
                btnModificar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;"); // verde
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // rojo

                pane.setAlignment(Pos.CENTER);

                btnModificar.setOnAction(event -> {
                    Direccion d = getTableView().getItems().get(getIndex());
                    activarModoEdicionDirecciones(d);
                });

                btnEliminar.setOnAction(event -> {
                    Direccion d = getTableView().getItems().get(getIndex());
                    eliminarDireccion(d);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void configurarBotonesPersonas_Direcciones() {
        // Configurar las cellValueFactory para que las columnas muestren los datos
        colAssociatedPersonId.setCellValueFactory(new PropertyValueFactory<>("id_persona"));
        colAssociatedDirectionId.setCellValueFactory(new PropertyValueFactory<>("id_direccion"));

        // Configurar el botón de eliminar
        colAssociatedModifications.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("Eliminar");

            private final HBox pane = new HBox(10, btnEliminar);

            {
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // rojo

                pane.setAlignment(Pos.CENTER);


                btnEliminar.setOnAction(event -> {
                    Persona_Direccion pd = getTableView().getItems().get(getIndex());
                    eliminarPersona_Direccion(pd);
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





    private void activarModoEdicionDirecciones(Direccion direccion) {
        direccionEnEdicion = direccion;
        directionTextField.setText(direccion.getCalle());
        addDirectionButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
        addDirectionButton.setText("Guardar Cambios");
    }

    private void cargarDirecciones() {
        try {
            List<Direccion> direcciones = direccionDAO.getAll();

            System.out.println("=== CARGANDO DIRECCIONES ===");
            System.out.println("Total Direcciones encontradas: " + direcciones.size());
            for (Direccion d : direcciones) {
                System.out.println("ID: " + d.getId() + " | Calle: " + d.getCalle());
            }
            System.out.println("========================");

            tablaDirecciones.getItems().setAll(direcciones);
            tablaDirecciones.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminarDireccion(Direccion d) {
        try {
            direccionDAO.delete(d.getId());
            cargarDirecciones();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void agregarDireccion() {
        String calle = directionTextField.getText().trim();

        if (calle.isEmpty()) return;

        try {
            Direccion nuevaDireccion = new Direccion(0, calle);
            direccionDAO.insert(nuevaDireccion);
            cargarDirecciones();
            limpiarCampoDireccion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void limpiarCampoDireccion() {
        direccionEnEdicion = null;
        directionTextField.clear();
        addDirectionButton.setText("Agregar");
        addDirectionButton.setStyle("-fx-background-color: #FFFFFF;");
    }

    private void modificarDireccion() {
        if (direccionEnEdicion == null) return;

        String calle = directionTextField.getText().trim();
        if (calle.isEmpty()) return;

        try {
            direccionEnEdicion.setCalle(calle);
            direccionDAO.update(direccionEnEdicion);
            cargarDirecciones();
            limpiarCampoDireccion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }







//PARTE FINAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAL



    private void eliminarPersona_Direccion(Persona_Direccion pd) {
        try {
            persona_direccionDAO.delete(pd.getId_persona(), pd.getId_direccion());
            cargarPersonas_Direcciones(); // ← LÍNEA CORREGIDA (antes era cargarDirecciones())
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarPersonas_Direcciones() {
        try {
            List<Persona_Direccion> personaDirecciones = persona_direccionDAO.getAll();

            System.out.println("=== CARGANDO ASOCIACIONES ===");
            System.out.println("Total asociaciones encontradas: " + personaDirecciones.size());
            for (Persona_Direccion pd : personaDirecciones) {
                System.out.println("ID Persona: " + pd.getId_persona() + " | ID Direccion: " + pd.getId_direccion());
            }
            System.out.println("========================");

            tablaEnlaces.getItems().setAll(personaDirecciones);
            tablaEnlaces.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void agregarAsociacion() {
        Persona personaSeleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        Direccion direccionSeleccionada = tablaDirecciones.getSelectionModel().getSelectedItem();

        // Verificar que ambos estén seleccionados
        if (personaSeleccionada == null || direccionSeleccionada == null) {
            System.out.println("ERROR: Debe seleccionar una persona y una dirección");
            return;
        }

        int personaId = personaSeleccionada.getId();
        int direccionId = direccionSeleccionada.getId();

        try {
            Persona_Direccion nuevaPD = new Persona_Direccion(personaId, direccionId);
            persona_direccionDAO.insert(nuevaPD);
            cargarPersonas_Direcciones();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}