package planner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides a method for finding a safe allocation of events to venues.
 */
public class Allocator {
	// Set of all possible event to venue allocations that are safe
	//private Set<Map<Event, Venue>> allocatedSet;
	/*
	 * Invariant: allocatedSet != null && Maps in allocatedSet != null
	 * && all allocations of events to venues in Set<Map<>> are safe.
	 * (traffic from event, on a corridor < corridorCapcity < VenueCapacity)
	 */

	/**
	 * <p>
	 * Returns a safe allocation of events to venues, if there is at least one
	 * possible safe allocation, or null otherwise.
	 * </p>
	 *
	 * <p>
	 * NOTE: What it means for an allocation of events to venues to be safe is
	 * defined in the assignment handout.
	 * </p>
	 *
	 * @require events != null && venues != null && !events.contains(null) &&
	 *          !venues.contains(null) && events does not contain duplicate
	 *          events && venues does not contain duplicate venues.
	 * @ensure Returns a safe allocation of events to venues, if there is at
	 *         least one possible safe allocation, or null otherwise.
	 */
	public static Map<Event, Venue> allocate(List<Event> events,
			List<Venue> venues) {
		// DO NOT MODIFY THE IMPLEMENTATION OF THIS METHOD
		Set<Map<Event, Venue>> allocations = allocations(events, venues);
		if (allocations.isEmpty()) {
			// returns null to signify that there is no possible safe allocation
			return null;
		} else {
			// returns one (any one) of the possible safe allocations
			return allocations.iterator().next();
		}
	}

	/**
	 * Returns the set of all possible safe allocations of events to venues.
	 *
	 * @require events != null && venues != null && !events.contains(null) &&
	 *          !venues.contains(null) && events does not contain duplicate
	 *          events && venues does not contain duplicate venues.
	 * @ensure Returns the set of all possible safe allocations of events to
	 *         venues. (Note: if there are no possible allocations, then this
	 *         method should return an empty set of allocations.)
	 */
	private static Set<Map<Event, Venue>> allocations(List<Event> events,
			List<Venue> venues) {
		Set<Map<Event, Venue>> safeOnes = new HashSet<Map<Event, Venue>>();
		Set<Map<Event, Venue>> unChecked = allAllocations(events, venues);
		// Loop checks traffic of each allocation is safe
		for (Map<Event, Venue> subAllocation : unChecked) {
			Traffic totalT = new Traffic(); // Traffic generated by allocations
			for (Event e : subAllocation.keySet()) {
				// Traffic generated by a venue
				Traffic venueT = subAllocation.get(e).getTraffic(e);
				totalT.addTraffic(venueT); // all venue's traffic
			}
			if (totalT.isSafe()) {
				// traffic generated by allocations is safe
				safeOnes.add(subAllocation);
			}
		}
		return safeOnes;
	}

	/**
	 * Returns the set of ALL allocations of events to venues.
	 *
	 * @require events != null && venues != null && !events.contains(null) &&
	 *          !venues.contains(null) && events does not contain duplicate
	 *          events && venues does not contain duplicate venues.
	 * @ensure Returns the set of all allocations of events to
	 *         venues where an event is allocated to a venue that "canHost" it.
	 *         (Note: it does not check whether allocations are safe or not.
	 *         		Method  returns an empty set if no events can be hosted.)
	 */
	private static Set<Map<Event, Venue>> allAllocations(List<Event> events,
			List<Venue> venues) {
		Set<Map<Event, Venue>> allocatedSet = new HashSet<>();
		// base case: events is an empty list
		if (events.isEmpty()) {
			allocatedSet.add(new HashMap<Event, Venue>());// Empty
			return allocatedSet;
		}

		// recursive case: events is non empty list
		// set allocatedSet = allocations(events - firstE, venues - safeVenue)
		for (int iE = 0; iE < events.size(); iE++) {
			List<Event> theRestE = new ArrayList<>(events);
			Event firstE = theRestE.remove(iE);
			// all potential venues
			for (int iV = 0; iV < venues.size(); iV++) {
				List<Venue> theRestV = new ArrayList<>(venues);
				Venue hostVenue = getHostingVenue(firstE, theRestV);
				if (hostVenue == null) {
					// No more venues left that can Host the event firstE
					break;
				}
				Set<Map<Event, Venue>> Left = allAllocations(theRestE, theRestV);
				for (Map<Event, Venue> subAllocation : Left) {
					Map<Event, Venue> copy  = new HashMap<>(subAllocation);
					// Add event that was first allocated a venue
					copy.put(firstE, hostVenue);
					// Add all other allocations as part of the subset
					allocatedSet.add(copy);
				}
			}
		}
		return allocatedSet;
	}

	/**
	 * Finds a venue among venues that can successfully host the event.
	 *
	 * @require event != null && venues != null  && !venues.contains(null)
	 * 			&& venues does not contain duplicate venues.
	 * @ensure \result == first venue available, capable of hosting event.
	 * 			(Note: if there are no venues that can host,
	 * 				   then \result == null.)
	 */
	private static Venue getHostingVenue(Event event, List<Venue> venues) {
		for (int v = 0; v < venues.size(); v++) {
			// event size <= venue capacity
			if (venues.get(v).canHost(event)) {
				return venues.remove(v); // allocate venue v to event
			}
		}
		return null;
	}
}
