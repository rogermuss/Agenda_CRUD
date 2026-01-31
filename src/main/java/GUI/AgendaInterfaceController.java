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
    @FXML private TableColumn<Persona, String> colAdress;
    @FXML private TableColumn<Persona, Void> colModifications;

    @FXML private TableView<Telefono> tablaTelefonos;
    @FXML private TableColumn<Telefono, Integer> colPersonID;
    @FXML private TableColumn<Telefono, Integer> colTelID;
    @FXML private TableColumn<Telefono, String> colNumber;
    @FXML private TableColumn<Telefono, Void> colPhoneModifications;

    // Campos para agregar/modificar personas
    @FXML private TextField nameTextField;
    @FXML private TextField directionTextField;
    @FXML private Button addPersonButton;

    // Campos para agregar/modificar teléfonos
    @FXML private TextField phoneTextField;
    @FXML private Button addPhoneButton;

    private final PersonaDAO personaDAO = new PersonaDAO();
    private final TelefonoDAO telefonoDAO = new TelefonoDAO();

    // Variables para modo edición
    private Persona personaEnEdicion = null;
    private Telefono telefonoEnEdicion = null;

    @FXML
    private void initialize() {

        //splitContainer.setMouseTransparent(true);
        //listsContainer.setMouseTransparent(true);

        // Configurar columnas de Personas
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colAdress.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        // Configurar columnas de Teléfonos
        colTelID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPersonID.setCellValueFactory(new PropertyValueFactory<>("personaId"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        ajustarAnchoColumnasPersonas();

        ajustarAnchoColumnasTelefonos();


        // Configurar botones de modificación y eliminacion para Personas
        configurarBotonesPersonas();

        // Configurar botones de modificación y eliminación para Telefonos
        configurarBotonesTelefonos();

        cargarPersonas();

        // Al seleccionar persona - cargar telefonos
        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldP, newP) -> {
            if (newP != null) {
                cargarTelefonos(newP.getId());
            } else {
                tablaTelefonos.getItems().clear();
            }
        });

        // Boton para agregar/modificar persona
        addPersonButton.setOnAction(e -> {
            if (personaEnEdicion == null) {
                agregarPersona();
            } else {
                modificarPersona();
            }
        });

        // Boton para agregar/modificar telefono
        addPhoneButton.setOnAction(e -> {
            if (telefonoEnEdicion == null) {
                agregarTelefono();
            } else {
                modificarTelefono();
            }
        });
    }

    private void ajustarAnchoColumnasPersonas() {
        tablaPersonas.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();

            // Resta el ancho de la barra de scroll (si existe)
            double scrollBarWidth = 15;
            double usableWidth = tableWidth - scrollBarWidth;

            // Distribucion del ancho entre las columnas
            colID.setPrefWidth(usableWidth * 0.10);
            colName.setPrefWidth(usableWidth * 0.30);
            colAdress.setPrefWidth(usableWidth * 0.30);
            colModifications.setPrefWidth(usableWidth * 0.30);
        });
    }

    private void ajustarAnchoColumnasTelefonos() {
        tablaTelefonos.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();

            // Resta el ancho de la barra de scroll (si existe)
            double scrollBarWidth = 15;
            double usableWidth = tableWidth - scrollBarWidth;

            // Distribuye el ancho entre las columnas
            colTelID.setPrefWidth(usableWidth * 0.15);
            colPersonID.setPrefWidth(usableWidth * 0.15);
            colNumber.setPrefWidth(usableWidth * 0.40);
            colPhoneModifications.setPrefWidth(usableWidth * 0.30);
        });
    }

    //Metodo para configutar los botones y sus acciones (styles & actionListeners)
    private void configurarBotonesPersonas() {
        colModifications.setCellFactory(param -> new TableCell<>() {
            private final Button btnModificar = new Button("Modificar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox pane = new HBox(10, btnModificar, btnEliminar);

            {
                pane.setAlignment(Pos.CENTER);
                btnModificar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

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

    //Metodo para configutar los botones y sus acciones (styles & actionListeners) pero para telefonos
    private void configurarBotonesTelefonos() {
        colPhoneModifications.setCellFactory(param -> new TableCell<>() {
            private final Button btnModificar = new Button("Modificar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox pane = new HBox(10, btnModificar, btnEliminar);

            {
                pane.setAlignment(Pos.CENTER);
                btnModificar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

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

    //Mando a llamar el metodo de PersonaDAO para cargar las personas de la BD
    //Luego las agrego a la tabla de Personas
    private void cargarPersonas() {
        try {
            tablaPersonas.getItems().setAll(personaDAO.getAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Mando a llamar el metodo de personaDAO para cargar los telefonos de una persona seleccionada
    private void cargarTelefonos(int personaId) {
        try {
            tablaTelefonos.getItems().setAll(telefonoDAO.getAllByPersonaId(personaId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // OPERACIONES CRUD PARA PERSONAS

    //Obtengo los datos del formulario y los inserto en la BD
    private void agregarPersona() {
        String nombre = nameTextField.getText().trim();
        String direccion = directionTextField.getText().trim();

        if (nombre.isEmpty()) {
            System.out.println("El nombre no puede estar vacío");
            return;
        }

        try {
            Persona nuevaPersona = new Persona(0, nombre, direccion);
            personaDAO.insert(nuevaPersona);
            cargarPersonas();
            limpiarCamposPersona();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //La persona seleccionada se pasa como parametro para poder modificarla
    private void activarModoEdicionPersona(Persona persona) {
        personaEnEdicion = persona;
        nameTextField.setText(persona.getNombre());
        directionTextField.setText(persona.getDireccion());
        addPersonButton.setText("Guardar Cambios");
        addPersonButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
    }

    //Dependiendo de si hay un persona seleccionada o no, se llama al metodo de
    // personaDAO para modificarla o eliminarla
    private void modificarPersona() {
        if (personaEnEdicion == null) return;

        String nombre = nameTextField.getText().trim();
        String direccion = directionTextField.getText().trim();

        if (nombre.isEmpty()) {
            System.out.println("El nombre no puede estar vacío");
            return;
        }

        try {
            personaEnEdicion.setNombre(nombre);
            personaEnEdicion.setDireccion(direccion);
            personaDAO.update(personaEnEdicion);
            cargarPersonas();
            limpiarCamposPersona();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Elimino la persona seleccionada de la BD y la actualizo en la tabla
    private void eliminarPersona(Persona persona) {
        try {
            personaDAO.delete(persona.getId());
            cargarPersonas();
            tablaTelefonos.getItems().clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //METODO para limpiar los campos del formulario
    private void limpiarCamposPersona() {
        personaEnEdicion = null;
        nameTextField.clear();
        directionTextField.clear();
        addPersonButton.setText("Agregar");
        addPersonButton.setStyle("-fx-background-color: #FFFFFF;");
    }

    // OPERACIONES CRUD PARA TELEFONOS, lo mismo que arriba, pero para los telefonos

    private void agregarTelefono() {
        Persona personaSeleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            System.out.println("Debe seleccionar una persona primero");
            return;
        }

        String numeroTelefono = phoneTextField.getText().trim();
        if (numeroTelefono.isEmpty()) {
            System.out.println("El numero de telefono no puede estar vacío");
            return;
        }

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
        addPhoneButton.setText("Guardar Cambios");
        addPhoneButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
    }

    private void modificarTelefono() {
        if (telefonoEnEdicion == null) return;

        String numeroTelefono = phoneTextField.getText().trim();
        if (numeroTelefono.isEmpty()) {
            System.out.println("El numero de telefono no puede estar vacío");
            return;
        }

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