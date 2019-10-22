package planner;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * <p>
 * A mutable class for recording the amount of traffic on traffic corridors.
 * </p>
 *
 * <p>
 * The traffic on a corridor is measured in non-negative integer units,
 * representing people.
 * </p>
 */
public class Traffic {
	// Naturally Ordered list of Corridors with their corresponding traffic
	private TreeMap<Corridor, Integer> traffic;
	/*
	 * Invariant: traffic never equal to null
	 * no corridors are equal to null
	 * traffic >= zero
	 *
	 */

	/**
	 * <p>
	 * Creates a new instance of the class in which every traffic corridor
	 * initially has no (i.e. zero) traffic.
	 * </p>
	 *
	 * <p>
	 * That is, for any non-null traffic corridor c, this.getTraffic(c) == 0.
	 * </p>
	 */
	public Traffic() {
		// No known corridors thus zero traffic
		this.traffic = new TreeMap<Corridor, Integer>();
	}

	/**
	 * <p>
	 * Creates a new instance of this class that initially has the same traffic
	 * as parameter initialTraffic.
	 * </p>
	 *
	 * <p>
	 * The parameter initialTraffic should not be modified by this method.
	 * Furthermore, future changes to the parameter initialTraffic should not
	 * affect this instance of the class, and vice versa. That is, the new
	 * instance of the class should be a deep copy of initialTraffic.
	 * </p>
	 *
	 * @param initialTraffic
	 *            the initial traffic for this instance of the class
	 * @throws NullPointerException
	 *             if initialTraffic is null
	 */
	public Traffic(Traffic initialTraffic) {
		// iterate over initalTraffic (corridors) and copy it to
		// a new instance of Traffic
		this.traffic = new TreeMap<Corridor, Integer>();
		for (Map.Entry<Corridor, Integer> entry :
			initialTraffic.traffic.entrySet()) {
			this.traffic.put(entry.getKey(), entry.getValue());
		}
	}


	/**
	 * <p>
	 * Returns the amount of traffic on the given corridor.
	 * </p>
	 *
	 * <p>
	 * The amount of traffic on a corridor is always non-negative, meaning that
	 * this method always a returns an integer that is greater than or equal to
	 * zero.
	 * </p>
	 *
	 * @param corridor
	 *            the corridor whose associated amount of traffic will be
	 *            returned
	 * @return the amount of traffic on the given corridor
	 * @throws NullPointerException
	 *             if the parameter corridor is null
	 */
	public int getTraffic(Corridor corridor) {
		if (corridor == null) {
			throw new NullPointerException("Parameter corridor is null");
		} else if (this.traffic.containsKey(corridor) == false) {
			// traffic does not exist thus it's zero
			return 0;
		} else {
			return this.traffic.get(corridor);
		}
	}

	/**
	 * Returns the set of all traffic corridors c for which this.getTraffic(c)
	 * is greater than zero.
	 *
	 * @return the set of traffic corridors with an amount of traffic that is
	 *         greater than zero
	 */
	public Set<Corridor> getCorridorsWithTraffic() {
		Set<Corridor> trafficPresent = new TreeSet<Corridor>();
		// for each corridor with traffic > 0, add to new set then return set
		for (Corridor entry : this.traffic.keySet()) {
			if (this.getTraffic(entry) > 0) {
				trafficPresent.add(entry);
			}
		}
		return trafficPresent;
	}

	/**
	 * <p>
	 * Returns true if parameter other currently records the same traffic as
	 * this traffic record, and false otherwise.
	 * </p>
	 *
	 * <p>
	 * That is, it returns true if and only if for every traffic corridor, the
	 * traffic currently on that corridor in this object equals the traffic
	 * currently on that corridor in the other object.
	 * </p>
	 *
	 * @param other
	 *            the Traffic object to compare
	 * @return true if this object and other currently record the same traffic,
	 *         and false otherwise
	 * @throws NullPointerException
	 *             if other is null
	 */
	public boolean sameTraffic(Traffic other) {
		if (other == null) {
			throw new NullPointerException("other traffic object is null");
		}
		for (Corridor entry : this.traffic.keySet()) {
			if (this.traffic.get(entry) != other.traffic.get(entry)) {
				// Either corridor does not exist or corridor does not
				// have the same amount of traffic
				return false;
			}
		}
		// corresponding corridors all match up
		return true;
	}

	/**
	 * <p>
	 * Updates the traffic on the given corridor by adding parameter amount to
	 * the existing traffic on the corridor.
	 * </p>
	 *
	 * <p>
	 * Parameter amount may be either a negative or positive integer (or zero),
	 * but an InvalidTrafficException will be thrown if the result of adding
	 * amount to the current traffic on the corridor will result in a negative
	 * quantity of traffic on that corridor.
	 * </p>
	 *
	 * @param corridor
	 *            the corridor whose amount of traffic will be updated
	 * @param amount
	 *            the amount that will be added to the traffic on the given
	 *            corridor
	 * @throws NullPointerException
	 *             if corridor is null
	 * @throws InvalidTrafficException
	 *             if the addition of amount and the current amount of traffic
	 *             on the given corridor is negative (i.e. less than zero).
	 */
	public void updateTraffic(Corridor corridor, int amount) {
		int totalTraffic;
		if (corridor.equals(null)) {
			throw new NullPointerException("corridor equals null");
		}
		// if corridor does not exist in this traffic set, it's traffic is zero
		totalTraffic = this.traffic.get(corridor) == null ? 0 :
			this.traffic.get(corridor);
		if ((totalTraffic += amount) < 0 ) {
			throw new InvalidTrafficException("current traffic cannot be < 0");
		} else {
			this.traffic.put(corridor, totalTraffic);
		}
	}

	/**
	 * <p>
	 * This method adds all of the traffic defined by parameter extraTraffic to
	 * this object.
	 * </p>
	 *
	 * <p>
	 * That is, for each traffic corridor c, this method updates the traffic on
	 * that corridor in this object by adding to it the traffic that parameter
	 * extraTraffic associates with c.
	 * </p>
	 *
	 * <p>
	 * (Unless this == extraTraffic) this method must not modify the given
	 * parameter, and future modifications to this object should not affect the
	 * parameter extraTraffic and vice versa.
	 * </p>
	 *
	 * @param extraTraffic
	 *            the traffic to be added to this object
	 * @throws NullPointerException
	 *             if extraTraffic is null
	 */
	public void addTraffic(Traffic extraTraffic) {
		if (extraTraffic == null) {
			throw new NullPointerException("Extra traffic is null");
		}
		for (Map.Entry<Corridor, Integer> entry :
			extraTraffic.traffic.entrySet()) {
			// if corridor did not exist, it is added to set by update traffic
			updateTraffic(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * <p>
	 * The string representation is the concatenation of strings of the form
	 * <br>
	 * <br>
	 *
	 * "CORRIDOR: TRAFFIC" + LINE_SEPARATOR <br>
	 * <br>
	 *
	 * where CORRIDOR is the toString() representation of a traffic corridor c
	 * for which this.getTraffic(c) is greater than zero, and TRAFFIC is its
	 * corresponding amount of traffic, and LINE_SEPARATOR is the line separator
	 * retrieved in a machine-independent way by calling
	 * System.getProperty("line.separator").
	 * </p>
	 *
	 * <p>
	 * In the string representation, the corridors should appear in the order of
	 * their natural ordering (i.e. using the order defined by the compareTo
	 * method in the Corridor class).
	 * </p>
	 *
	 * <p>
	 * If there are no traffic corridors c for which this.getTraffic(c) is
	 * greater than zero, then the string representation is the empty string "".
	 * </p>
	 *
	 * <p>
	 * (Note that we have one line for each corridor with a non-zero amount of
	 * traffic in this string representation, and no lines for corridors with a
	 * zero amount of traffic.)
	 * </p>
	 */
	@Override
	public String toString() {
		StringBuilder trafficString = new StringBuilder("");
		Set<Corridor> corridors = this.getCorridorsWithTraffic();
		for (Corridor entry : corridors) {
			trafficString.append(entry.toString() + ": " // Corridor
					+ this.getTraffic(entry) 			// Traffic
					+ System.getProperty("line.separator")); // L-separator
		}
		return trafficString.toString();
	}

	/**
	 * <p>
	 * Determines whether this class is internally consistent (i.e. it satisfies
	 * its class invariant).
	 * </p>
	 *
	 * <p>
	 * NOTE: This method is only intended for testing purposes.
	 * </p>
	 *
	 * @return true if this class is internally consistent, and false otherwise.
	 */
	public boolean checkInvariant() {
		if (traffic == null) {
			return false;
		}
		for (Map.Entry<Corridor, Integer> entry: this.traffic.entrySet()) {
			if (entry.getKey().equals(null) || entry.getValue() < 0) {
				return false; // if a corridor is null or traffic < 0
			}
		}
		return true;
	}

}
