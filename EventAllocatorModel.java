package planner.gui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import planner.Corridor;
import planner.Event;
import planner.FormatException;
import planner.InvalidTrafficException;
import planner.Traffic;
import planner.Venue;
import planner.VenueReader;

/**
 * The model for the event allocator program.
 */
public class EventAllocatorModel {

	// List of venues not  allocated
	private List<Venue> freeVenues;
	// List of Corridors belonging to venues with generated traffic
	private List<Corridor> trafficGen;
	// a Ordered Map of safe allocations of venues to specified events
	private Map<Event, Venue> currentAllocation;
	// Observable List of Venues that have not been allocated
	private ObservableList<Venue> venueList;
	// Observable List of current allocations of specified events to venues
	private ObservableList<Map.Entry<Event, Venue>> allocationsList;
	// Observable List of generated traffic
	// for Table view (identical to freeVenues)
	private ObservableList <Corridor> trafficList;
	// Indicates if loadFile was successful (true) or not (false)
	private boolean loadFile;
	// Error message resulting from user input
	private String error = null;

	/*
	 * invariant:
	 *
	 * freeVenues && trafficGen && currentAllocation && venueList
	 * && allocationsList && trafficList !=null
	 *
	 */

	/**
	 * Initialises the model for the event allocator program.
	 * @throws FormatException
	 * @throws IOException
	 */
	public EventAllocatorModel() throws IOException, FormatException {
		try {
			// list of venues read from venues.txt
			List<Venue> venues = VenueReader.read("venues.txt");
			loadFile = true; //successful reading of file
			//Initial list of free venues
			this.freeVenues = new ArrayList<Venue>(venues);
			//Observable list of free venues
			this.venueList =
					FXCollections.observableArrayList(freeVenues);
		} catch (IOException e) {
			this.loadFile = false;
			this.error = e.getMessage();
		} catch (FormatException e) {
			// allocations unsafe
			this.error = e.getMessage();
		} catch (Exception e) {
			// Catch other exception that may happen
			this.error = e.getMessage();
		}
		//Initial map of empty allocations
		this.currentAllocation =
				new TreeMap<Event, Venue>(getAllocationsCmp());
		// List of traffic generated
		this.trafficGen = new ArrayList<>();
		//Observable List of traffic generated to be used for Table View
		this.trafficList = FXCollections.
				observableArrayList(trafficGen);
		//Observable List of current allocations for use with Table View
		this.allocationsList = FXCollections.
				observableArrayList(currentAllocation.entrySet());
	}

	/**
	 * Indicates if reading the venue file worked
	 * @return true if venueReader was success && false if venueReader failed
	 */
	public boolean isLoadFileSuccess() {
		return loadFile;
	}

	/**
	 * Gets the error message from exception that occurs
	 * @return string error message and otherwise null
	 * 			if no errors present.
	 */
	public String getErrorMsg() {
		return error;
	}

	/**
	 * Returns List of the corridors with traffic caused by the current
	 * allocation of events to venues.
	 */
	public List<Corridor> getTrafficGen() {
		return trafficGen;
	}

	/**
	 * Returns List of the venues not
	 * allocated to events.
	 */
	public List<Venue> getFreeVenues() {
		return freeVenues;
	}

	/**
	 * Returns Map of the current allocations
	 * of events to venues.
	 */
	public Map<Event, Venue> getAllocations() {
		return currentAllocation;
	}

	/**
	 * Returns Observable List of the traffic caused by the current
	 * allocation of events to venues.
	 */
	public ObservableList<Corridor> getTrafficListing() {
		return trafficList;
	}

	public ObservableList<Venue> getListedVenues() {
		return venueList;
	}

	/**
	 * Returns list of unallocated venues read from file venue.txt
	 * @return ObservableList of free Venues
	 */
	public ObservableList<Map.Entry<Event, Venue>> getAllocationListing() {
		return allocationsList;
	}

	/**
	 *  Set up comparator for ordering the Events correctly
	 *  @return a comparator that will be used to sort allocations list
	 */
	private Comparator<Event> getAllocationsCmp() {
		Comparator<Event> compareMethod = new Comparator<Event>(){
			@Override
			public int compare(Event e1, Event e2){
				// First compare event names
				if(e1.getName().compareTo(e2.getName()) ==0) {
					// Compare event sizes if names are the same
					Integer eOne = new Integer(e1.getSize());
					Integer eTwo = new Integer(e2.getSize());
					return eOne.compareTo(eTwo);
				} else {
					return e1.getName().compareTo(e2.getName());
				}
			}
		};
		return compareMethod;
	}


	/**
	 * Returns true if the traffic caused by the given allocation is safe, and
	 * false otherwise.
	 */
	private boolean allocationSafe(Traffic trafficGen) {
		return trafficGen.isSafe();
	}

	/**
	 * Returns the traffic caused by the given allocation.
	 *
	 * @requires allocationsList!=null
	 * @ensures traffic is updated if allocation is safe and traffic
	 * 			doesn't change if allocation is unsafe.
	 */
	public boolean updateTraffic() throws InvalidTrafficException {
		Traffic result = new Traffic();
		// Add traffic from allocations
		for (Map.Entry<Event, Venue> e : allocationsList) {
			result.addTraffic(e.getValue().getTraffic(e.getKey()));
		}
		// Return traffic generated if allocation safe
		if (allocationSafe(result)) {
			trafficGen.clear();
			trafficGen.addAll(result.getCorridorsWithTraffic());
			// Set up natural ordering of traffic
			Comparator<Corridor> cmp = new Comparator<Corridor>() {
				@Override
				public int compare(Corridor c1, Corridor c2) {
					return c1.compareTo(c2);
				}
			};
			// naturally order traffic
			Collections.sort(trafficGen, cmp);
			trafficList.setAll(trafficGen);
			return true;
		} else {
			// allocation is unsafe
			this.error = "Warning: Allocation is unsafe";
			return false;
		}
	}
}
