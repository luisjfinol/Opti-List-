///////////////////////////////////////////////////////////////////////////////
// Title: Opti-List
// File: Activity.java
// Last Changed: 9/1/2020
//
// Author: Luis Finol
// Email: lfinol@wisc.edu
//////////////////////////////////////////////////////////////////////////////
package application;

/**
 * The following class serves to encapsulate all the relevant fields an activity possesses into an
 * object. 
 * 
 * @author Luis J Finol
 *
 */
public class Activity {

  private boolean deadline = false; // indicates if activity has due date or not
  private String description; // the description of the activity
  private int priority; // the priority number of the activity
  private String date; // the due date the activity possesses, null if it doesn't have one

  /**
   * Constructor of activity without due date
   * 
   * @param description, The activity description
   * @param priority, The activity priority number
   */
  public Activity(String description, int priority) {

    this.description = description;
    this.priority = priority;
  }

  /**
   * Constructor of activity with due date
   * 
   * @param description, The activity description
   * @param priority, The activity priority number
   * @param date, The activity due date
   */
  public Activity(String description, int priority, String date) {

    this.description = description;
    this.priority = priority;
    this.date = date;
    deadline = true;
  }

  /**
   * Getter method for activity's priority
   * 
   * @return priority number 
   */
  public int getPriority() {

    return priority;
  }

  /**
   * Getter method for activity's due date
   * 
   * @return due date 
   */
  public String getDate() {

    return date;
  }

  /**
   * Getter method for activity's description
   * 
   * @return activity description
   */
  public String getDescription() {

    return description;
  }

  /**
   * Indicates if activity has due date
   * 
   * @return true if activity has due date, false otherwise 
   */
  public Boolean hasDeadline() {

    return deadline;
  }

  /**
   * Setter method for activity's description
   * 
   * @return activity description
   */
  public void setDescription(String description) {

    this.description = description;
  }

  /**
   * Setter method for activity's due date
   * 
   * @return due date 
   */
  public void setDate(String date) {

    this.date = date;
  }

  /**
   * Setter method for activity's priority
   * 
   */
  public void setPriority(int priority) {

    this.priority = priority;
  }
}
