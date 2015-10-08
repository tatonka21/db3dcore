package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uos.igf.db3d.dbms.geom.Point3D;

/**
 * This class represents a 4D TriangleNet. A TriangleNet objects consists of one
 * to many TriangleComponent objects.
 * 
 * Jedes TriangleComponent Objekt hat ein Zeitinterval, was dem des TriangleNet
 * Objektes entspricht. Allerdings kann jedes TriangleComponent objekt eine
 * andere zeitliche Diskretisierung besitzen (s. Dissertation).
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class TriangleNet4D implements Net4D {

	// The TriangleComponents of this TriangleNet
	// Due to the TriangleComponents we can handle different parts of the net
	// with a different temporal discretisation.
	Map<Integer, TriangleComponent4D> components;

	// Connects TimeIntervals to TriangleComponent4D objects
	Map<TimeInterval, List<Integer>> timeIntervals;

	// Dates with a change of net topology
	LinkedList<Date> changeDates;

	// the start date of the existence interval for this TriangleNet4D object
	Date start;

	// the end date of the existence interval for this TriangleNet4D object
	Date end;

	// TimeInterval to Component Mapper
	TimeInterval currentInterval;

	// Die Elemente dieses Netzes mit ID. Fuer jeden Topologiewechsel eine Map:
	List<Map<Integer, Element4D>> elements;

	/**
	 * Constructor for a TriangleNet4D. The initial start date is set. Call
	 * createEndOfExistenceInterval() function to set an end date.
	 * 
	 * @param start
	 *            - Start Date
	 */
	public TriangleNet4D(Date start) {
		super();
		components = new HashMap<Integer, TriangleComponent4D>();
		timeIntervals = new HashMap<TimeInterval, List<Integer>>();
		changeDates = new LinkedList<Date>();
		elements = new LinkedList<Map<Integer, Element4D>>();

		// Add first Post object:
		elements.add(new HashMap<Integer, Element4D>());

		this.start = start;
		this.end = null;

		currentInterval = new TimeInterval(start, null);

		timeIntervals.put(currentInterval, new LinkedList<Integer>());
	}

	/**
	 * Add a single triangle to the net.
	 * 
	 * @param triangle
	 */
	public void addTriangleComponent(TriangleComponent4D component) {

		if (components.containsKey(component.getID())) {
			throw new IllegalArgumentException(
					"You tried to add a triangleComponent that already exists to the TriangleNet.");
		}
		components.put(component.getID(), component);

		// update TimeInterval to Component Mapper
		timeIntervals.get(currentInterval).add(component.getID());
	}

	/**
	 * Creates the end of the time interval.
	 * 
	 * @param end
	 */
	public void createEndOfExistenceInterval(Date end) {
		this.end = end;
	}

	/**
	 * Returns TriangleComponents of this TriangleNet.
	 * 
	 * @return Map<Integer, TriangleComponent4D> - All TriangleComponents of
	 *         this net.
	 */
	public Map<Integer, TriangleComponent4D> getComponents() {
		return components;
	}

	/**
	 * Returns a specific TriangleComponents of this TriangleNet.
	 * 
	 * @return TriangleComponent4D
	 */
	public TriangleComponent4D getComponent(int ID) {
		return components.get(ID);
	}

	/**
	 * Add a single triangle to the net.
	 * 
	 * @param triangle
	 */
	public void addTriangle(Triangle4D triangle) {

		// Immer an der aktuellen Stelle einfuegen:
		if (elements.get(elements.size()).containsKey(triangle.getID())) {
			throw new IllegalArgumentException(
					"You tried to add a triangle that already exists to the TriangleNet.");
		}
		elements.get(elements.size()).put(triangle.getID(), triangle);
	}

	/**
	 * Returns elements of this TriangleNet.
	 * 
	 * @return Map<Integer, Element4D> - All elements of this net.
	 */
	public List<Map<Integer, Element4D>> getElements() {
		return elements;
	}

	public void addTimestep(Component4D component,
			HashMap<Integer, Point3D> newPoints, Date date) {

	}

	@Override
	public void TopologyChange(Date date) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addChangeTimestep(Date date) {
		changeDates.add(date);
	}

	public LinkedList<Date> getChangeDates() {
		return changeDates;
	}

	public Date getLastChangeDate() {
		return changeDates.getLast();
	}

	public void preparePostObject(Date date) {
		closeAllComponents(date);
		closeTimeInterval(date);
	}

	/**
	 * We need to close the TimeIntervals of this net
	 * 
	 * @param date
	 */
	private void closeTimeInterval(Date date) {

		// close old TimeInterval
		currentInterval.setEnd(date);

		// start new TimeInterval
		currentInterval = new TimeInterval(date, null);

		// generate new List of Components for the new TimeInterval
		timeIntervals.put(currentInterval, new LinkedList<Integer>());
	}

	/**
	 * We need to close all TimeIntervals of all current components of this net
	 * 
	 * @param date
	 */
	private void closeAllComponents(Date date) {

		for (Integer ID : timeIntervals.get(currentInterval)) {
			Component4D comp = components.get(ID);
			comp.getTimeInterval().setEnd(date);
		}
	}
}
