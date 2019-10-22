package planner.gui;

import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import planner.Event;
import planner.InvalidTrafficException;
import planner.Venue;

/**
 * The controller for the event allocator program.
 */
public class EventAllocatorController {

	// the model of the event allocator
	private EventAllocatorModel model;
	// the view of the event allocator
	private EventAllocatorView view;

	/**
	 * Initialises the controller for the event allocator program.
	 *
	 * @param model
	 *            the model of the event allocator
	 * @param view
	 *            the view of the event allocator
	 */
	public EventAllocatorController(EventAllocatorModel model,
			EventAllocatorView view) {
		this.model = model;
		this.view = view;
		// Was loading the file successful ?
		if (model.isLoadFileSuccess() == false) {
			view.errorDialog("load file", model.getErrorMsg());
		}
		view.addAddHandler(new addActionHandler());
		view.addRemoveHandler(new removeActionHandler());
	}

	/**
	 * EventHandler class for ADD button
	 */
	private class addActionHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			try {
				String eventName = view.getEventName();
				if (eventName.equals("")) {
					view.errorDialog("normal", "event name Invalid");
					throw new Exception();
				}
				int eventSize = Integer.parseInt(view.getEventSize());
				Event newEvent = new Event(eventName, eventSize);
				// retrieve venue selected by user to be allocated
				Venue selectedV =
						view.getVenueTable().
						getSelectionModel().getSelectedItem();

				// Add new allocation to allocations table
				if (selectedV == null) {
					view.errorDialog("normal", "Venue not selected");
					throw new Exception();
				} else {
					//update traffic according to current allocations
					if (model.updateTraffic() == false) {
						// traffic allocation was unsafe
						view.errorDialog("normal", model.getErrorMsg());
						throw new Exception();
					}
					model.getAllocations().put(newEvent, selectedV);
					// update current allocation's Observable list
					model.getAllocationListing().setAll(
							model.getAllocations().entrySet());
					// remove allocated venue from freeVenues table
					model.getListedVenues().remove(selectedV);
				}
			} catch (NumberFormatException e) {
				// User event size input invalid
				view.errorDialog("normal", "Invalid event size");
			} catch (Exception e) {
				// exception
			}
			view.reset(); //Erase previous user input
		}
	}

	/**
	 * EventHandler class for REMOVE button
	 */
	private class removeActionHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			try {
				// retrieve allocation selected by user to be deleted
				Map.Entry<Event, Venue> allocation =
						view.getAllocTable().getSelectionModel().getSelectedItem();
				// return the venue of former allocation back to free venueListing
				model.getListedVenues().add(allocation.getValue());
				// remove allocation from the AllocListing
				model.getAllocationListing().remove(allocation);
				//update traffic according to current allocations
				model.updateTraffic();

			} catch (InvalidTrafficException e) {
				view.errorDialog("normal", e.getMessage());
			}
			view.reset(); //Erase previous user input
		}
	}
}
