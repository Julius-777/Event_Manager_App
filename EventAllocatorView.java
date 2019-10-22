package planner.gui;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import planner.Corridor;
import planner.Event;
import planner.Venue;

/**
 * The view for the event allocator program.
 */
public class EventAllocatorView {

	// the model of the event allocator
	private EventAllocatorModel model;
	// root node for the view
	private BorderPane root;
	// event name user input text field
	private TextField eventName;
	// event size user input text field
	private TextField eventSize;
	// VBox containing add and remove buttons
	private VBox buttons;
	// User GUI buttons
	private Button add, remove;
	// table view of current allocation
	private TableView<Map.Entry<Event, Venue>> allocTable;
	// table view of free venues
	private TableView<Venue> freeVenuesTable;
	// table view of list of corridors with their traffic
	private TableView<Corridor> genTraffic;
	// current allocations table colum
	private TableColumn<Map.Entry<Event, Venue>, String> allocations;
	// events allocated column
	private TableColumn<Map.Entry<Event, Venue>, String> events;
	// venues allocated column
	private TableColumn<Map.Entry<Event, Venue>, String> venues;
	// dialog window for displaying errors
	private Stage dialog;
	// the font type for all the text on the buttons and textField
	private Font font1 = Font.font("Calibri", FontWeight.LIGHT, 12);

	/**
	 * Initializes the view for the event allocator program.
	 *
	 * @param model
	 *            the model of the event allocator
	 */
	public EventAllocatorView(EventAllocatorModel model) {
		this.model = model;
		root = new BorderPane();
		// Add user interaction interfaces to the Top Region of BorderPane
		root.setTop(addUserInterface());
		// Add list of free venues to left side of border pane
		setUpFreeVenuesTable();
		// Add table of allocations to the display, at center of BorderPane
		setUpAllocTable();
		// Add a table displaying generated traffic to the right side of BorderPane
		setUpTrafficTable();

		root.setRight(genTraffic);
		root.setCenter(allocTable);
		root.setLeft(freeVenuesTable);
		setBorderConstraints();
		root.setStyle("-fx-background-color: #778899");
	}

	/**
	 * Returns the scene for the event allocator application.
	 *
	 * @return returns the scene for the application
	 */
	public Scene getScene() {
		Scene scene = new Scene(root, 800, 500);
		return scene;
	}

	/**
	 * Set the constraints of the Border Pane.
	 */
	private void setBorderConstraints() {
		// columns to always extend when window is resized
		ColumnConstraints columnConstraint = new ColumnConstraints();
		columnConstraint.setHgrow(Priority.ALWAYS);
		root.isResizable();
		root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		root.autosize();
	}

	/**
	 *  SetUp the Table view for the current allocations of events to
	 *  venues.
	 */
	private void setUpAllocTable() {
		// Convert Map<Event,Venue> = allocations, to an observable list
		allocTable = new TableView<>(model.getAllocationListing());
		allocTable.setEditable(false);
		// Main column: allocations of events to venues
		allocations = new TableColumn<>("Current Allocations");
		// allocations sub column 1: Allocated events column
		events = new TableColumn<>("Event(size)");
		// allocations sub column 2: column of corresponding allocated venues
		venues = new TableColumn<>("Venue");
		//Setup data display settings of the table
		allocTableSettings();
		// Put sub columns events & venues into main column allocations
		allocations.getColumns().add(events);
		allocations.getColumns().add(venues);
		allocTable.getColumns().add(allocations);
		allocTable.setColumnResizePolicy(
				TableView.CONSTRAINED_RESIZE_POLICY);
	}

	/**
	 *  Linking an observable list of the allocations to a TableView
	 *  and setting up how data will be displayed in the sub columns
	 *  event and venue e.g columns, title, cells.
	 */
	private void allocTableSettings() {
		events.setCellValueFactory
		(new Callback<CellDataFeatures<Map.Entry<Event, Venue>, String>,
				ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(TableColumn.
					CellDataFeatures<Map.Entry<Event, Venue>, String> p) {
				// callback returns property for a column's cell
				// toString representation of Event is displayed in cell
				return new SimpleStringProperty(p.getValue().
						getKey().toString());
			}
		});

		venues.setCellValueFactory
		(new Callback<CellDataFeatures<Map.Entry<Event, Venue>, String>,
				ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(TableColumn.
					CellDataFeatures<Map.Entry<Event, Venue>, String> p) {
				// callback returns property for a column's cell
				// toString representation of venue is displayed in cell
				return new SimpleStringProperty(p.getValue().
						getValue().toString());
			}
		});
	}

	/**
	 *  Linking an observable list of the venues to a TableView and setting
	 *  up how data will be displayed in the sub columns event and venue
	 *  e.g columns, title, cells.
	 */
	private void setUpFreeVenuesTable() {
		// Convert Map<Event,Venue> = allocations, to an observable list
		freeVenuesTable = new TableView<>(model.getListedVenues());
		TableColumn<Venue, String> venueTable =
				new TableColumn<Venue,String>("Free Venues [NAME (CAPACITY)]");
		// Table data display settings
		venueTable.setCellValueFactory
		(new Callback<CellDataFeatures<Venue, String>,
				ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(TableColumn.
					CellDataFeatures<Venue, String> p) {
				// callback returns property for a column's cell
				// toString representation of venue is displayed in cell
				return new SimpleStringProperty(p.getValue().toString());
			}
		});

		// Add table of free venues to display
		freeVenuesTable.getColumns().add(venueTable);
		freeVenuesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	/**
	 * SetUp visuals && GUI interaction for the generated traffic table.
	 * e.g columns, title, cells
	 */
	private void setUpTrafficTable() {
		genTraffic = new TableView<Corridor>(model.getTrafficListing());
		TableColumn<Corridor, String> generatedT =
				new TableColumn<>("Generated traffic [CORRIDOR: TRAFFIC]");
		// Table data display settings
		generatedT.setCellValueFactory
		(new Callback<CellDataFeatures<Corridor, String>,
				ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(TableColumn.
					CellDataFeatures<Corridor, String> p) {
				// callback returns property for a column's cell
				// toString representation of venue is displayed in cell
				return new SimpleStringProperty(p.getValue().toString());
			}
		});
		genTraffic.getColumns().add(generatedT);
		genTraffic.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	/**
	 * Sets up a HBox Pane containing the buttons
	 * and TextField for user interaction.
	 * @return returns the HBox Pane contain button and TextField features
	 */
	private HBox addUserInterface() {
		HBox userInteract; // user GUI input
		userInteract = new HBox();
		userInteract.autosize();
		userInteract.setSpacing(10);
		userInteract.setStyle("-fx-background-color: #d3d3d3");
		// Set up user TextFields
		eventName = settingTextField("Enter event name");
		eventSize = settingTextField("Enter event size");
		// Set up Add and Remove buttons
		addButtons();
		// Add all user interaction features to Top HBox
		userInteract.getChildren().addAll(
				eventName, eventSize, buttons);
		userInteract.setPadding(new Insets(5,50,5,50));
		userInteract.setMinSize(userInteract.getPrefWidth(),
				userInteract.getPrefHeight());
		return userInteract;

	}

	/**
	 * set up either the remove or add button.
	 * @return button
	 */
	private Button setButton(String name) {
		Button button = new Button(name);
		if (name.equals("ADD")) {
			button.setTextFill(Color.CHARTREUSE);
		} else {
			button.setTextFill(Color.RED);
		}
		// setup button style visuals
		button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		button.setTextAlignment(TextAlignment.CENTER);
		button.setStyle("-fx-background-color: #000000");
		button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		button.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		return button;
	}

	/**
	 * adds buttons for user interaction to a vertical pane
	 */
	private void addButtons() {
		// create add and remove buttons
		add = setButton("ADD");
		remove = setButton("REMOVE");
		buttons = new VBox();
		buttons.autosize();
		buttons.setSpacing(5);
		buttons.setPadding(new Insets(5,5,5,5));
		buttons.setAlignment(Pos.CENTER_RIGHT);
		// Minimum size is set to size of buttons
		buttons.getChildren().addAll(add, remove);
	}

	/**
	 * Settings for User input text field in the display.
	 * @return returns the text field for the display
	 */
	private TextField settingTextField(String text) {
		TextField input = new TextField();
		input.setEditable(true);
		input.setAlignment(Pos.CENTER_LEFT);
		input.setFocusTraversable(false);
		input.setFont(font1);
		input.setStyle("-fx-text-fill: black;");
		input.setPromptText(text);
		return input;
	}

	/**
	 * Reset TextFields back to default, user prompt message.
	 *
	 */
	public void reset() {
		eventName.clear();
		eventSize.clear();
	}

	/**
	 * Get the name of event in TextField.
	 *
	 * @return return the text inside the event name text field
	 */
	public String getEventName() {
		return eventName.getText();
	}

	/**
	 * Get the size of event in TextField.
	 *
	 * @return return the text inside the event size text field
	 */
	public String getEventSize() {
		return eventSize.getText();
	}

	/**
	 * Add handler to the add operation.
	 * @param handler
	 *            the handler to be added
	 */
	public void addAddHandler(EventHandler<ActionEvent> handler) {
		add.setOnAction(handler);
	}

	/**
	 * Add handler to the remove operation.
	 * @param handler
	 *            the handler to be added
	 */
	public void addRemoveHandler(EventHandler<ActionEvent> handler) {
		remove.setOnAction(handler);
	}

	/**
	 * Return table of venues not allocated
	 */
	public TableView<Venue> getVenueTable() {
		return this.freeVenuesTable;
	}

	/**
	 * Return table of allocated events to venues
	 */
	public TableView<Map.Entry<Event, Venue>> getAllocTable() {
		return this.allocTable;
	}

	/** Displays a new window that gives useful information to user on
	 * how to deal with their GUI input error
	 * @require errorType!=null && message!=null
	 * @ensure PopUp window is displayed with an error message when
	 * 		   user provides invalid data
	 */
	public void errorDialog(String errorType, String message){
		dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		VBox Vbox = new VBox(10);
		Label label1 = new Label(message);
		Scene dialogScene = new Scene(Vbox, 500, 100);
		label1.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 14));
		label1.setStyle("-fx-text-fill: blue;");
		label1.autosize();
		if (errorType.equals("load file")) {
			// Load File Error = exit application
			Vbox.getChildren().addAll(label1, new Label("CLICK "
					+ "On WINDOW TO EXIT"));
		} else {
			// Other errors = fix input and continue using application
			Vbox.getChildren().addAll(label1, new Label("EXIT WINDOW "
					+ "TO CONTINUE"));
		}

		// exit GUI only if file is incorrect
		dialogScene.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (errorType.equals("load file")) {
					Platform.exit();
				}
			}
		});
		// set up error window's display visuals
		Vbox.autosize();
		Vbox.setAlignment(Pos.CENTER);
		Vbox.setStyle("-fx-background-color: #b0e0e6");
		dialog.setScene(dialogScene);
		dialog.setAlwaysOnTop(true);
		dialog.show();
	}



}
