///////////////////////////////////////////////////////////////////////////////
// Title: Opti-List
// File: Main.java
// Last Changed: 9/1/2020
//
// Author: Luis Finol
// Email: lfinol@wisc.edu
//////////////////////////////////////////////////////////////////////////////

package application;

import java.awt.Color;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * The following class establishes a main stage for activities to be displayed and inserted with a
 * GUI the user can interact with. It creates a global data structure that holds two lists, one
 * holds instances of the activity class ordered by which has a sooner due date, and the other holds
 * an array list of 10 elements each representing a priority number from 1 - 10 and each holding a
 * linked list with the activities corresponding to the respective priority.
 * 
 * @author Luis J Finol
 *
 */
public class Main extends Application {


  private int pageRank = 1; // integer indicating which page of activities is displayed
  private Boolean mainSceneByPriority = true; // indicates if the main scene is currently in prior

  // the main data structure that contains the array list ordering activities by priority and the
  // linked list ordering activities by due date
  private ArrayList<List> activities = new ArrayList<List>();

  // each element corresponds to the number of days in a certain month, and their position
  // corresponds to the specific month
  private int[] validDate = new int[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

  // each element is the total number of days in the year so far at the start of the corresponding
  // month
  private int[] daysSoFar = new int[] {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};

  // indicates if no due date checkbox is selected when adding
  // multiple activities at once
  private boolean selection = true;

  private int activityNum = 0; // total number of activities

  /**
   * Creates the global activities list and establishes the main stage of the program
   */
  @Override
  public void start(Stage primaryStage) {

    activities.add(new ArrayList<LinkedList<Activity>>());

    // adds lists for every priority level, index 0 will correspond to priority 10, 1 to priority 9,
    // and so on
    for (int i = 0; i < 10; ++i) {
      activities.get(0).add(new LinkedList<Activity>());
    }

    activities.add(new LinkedList<Activity>()); // list for activities ordered by due date

    primaryStage.setScene(createPriorityMainScene(primaryStage));
    primaryStage.show();
  }

  /**
   * Creates the scene that displays the activities ordered with respect to their priority number
   * 
   * @param primaryStage, The main stage of the program
   * @return Scene with activities displayed when ordered by their priority
   */
  private Scene createPriorityMainScene(Stage primaryStage) {

    Scene scene = null; // scene to be returned by method

    try {

      BorderPane root = new BorderPane();
      BorderPane innerPane = new BorderPane();

      Label title = new Label("OPTI-LIST");
      title.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");

      Button dueToday = new Button("Due Today");
      Button add = new Button("Add Activity");
      Button delete = new Button("Delete All");
      Button order = new Button("Order By Due Date");
      Button nextPage = new Button("Next Page");
      Button prevPage = new Button("Previous Page");

      // indicates activities are ordered by priority
      Label orderIndication = new Label("Ordered By Priority");
      orderIndication.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");

      VBox vbox = new VBox();

      dueToday.setOnAction(e -> primaryStage.setScene(dueToday(primaryStage)));
      order.setOnAction(e -> {
        mainSceneByPriority = false; // now the main scene will be ordered by due date
        pageRank = 1;
        primaryStage.setScene(createDateMainScene(primaryStage));
      });
      add.setOnAction(e -> primaryStage.setScene(createAddActivityScene(primaryStage)));
      delete.setOnAction(e -> deleteAll(primaryStage));

      // the buttons are inserted into HBoxes so that they can spread out evenly when the stage size
      // is readjusted
      HBox todayBox = new HBox(dueToday);
      HBox.setHgrow(todayBox, Priority.ALWAYS);
      HBox orderBox = new HBox(order);
      HBox.setHgrow(orderBox, Priority.ALWAYS);
      HBox indicationBox = new HBox(orderIndication);
      HBox.setHgrow(indicationBox, Priority.ALWAYS);
      HBox deleteBox = new HBox(delete);
      HBox.setHgrow(deleteBox, Priority.ALWAYS);
      HBox addBox = new HBox(add);

      HBox hbox = new HBox();
      hbox.getChildren().addAll(todayBox, orderBox, indicationBox, deleteBox, addBox);

      HBox nextBox = new HBox(nextPage);
      HBox prevBox = new HBox(prevPage);
      HBox.setHgrow(prevBox, Priority.ALWAYS);
      HBox innerBox = new HBox();

      // array list containing 10 elements each of which contains a list of activities corresponding
      // to a respective priority number
      ArrayList<LinkedList> priorityList = (ArrayList) activities.get(0);
      // number of activities of all the priorities that have been traversed so far
      int total = 0;

      vbox.getChildren().removeAll(); // ensures list items are not repeated

      // lower end of activities displayed depending on which page of activities the user is on
      int lowerEnd = (pageRank * 10) - 10;
      // higher end of activities
      int higherEnd = (pageRank * 10) - 1;
      int elementNum = 0; // number of activities displayed

      // loop iterates over the priority list, displaying 10 activities per page at most, and
      // beginning with activities of greater priority
      for (int i = 0; i < priorityList.size(); ++i) {
        total = total + priorityList.get(i).size();
        if (total < lowerEnd) { // skips over unnecessary iterations of code
          continue;
        }

        int diff = total - priorityList.get(i).size();
        LinkedList<Activity> temp = priorityList.get(i);
        int j = lowerEnd - diff;

        // prevents index out of bounds exception when one of the displayed activities begins on a
        // different priority level list
        if (elementNum > 0 || j < 0) {
          j = 0;
        }

        for (int m = j; m < priorityList.get(i).size(); ++m) {
          Button button;
          Activity act = temp.get(m);

          if (act.hasDeadline()) {
            button = new Button(act.getDescription() + " [" + temp.get(m).getDate() + "]");
          } else {
            button = new Button(act.getDescription() + " [no due date]");
          }

          if (act.getPriority() > 7) {
            button.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
          } else if (act.getPriority() < 5) {
            button.setStyle("-fx-text-fill: green; -fx-font-size: 18px;");
          } else {
            button.setStyle("-fx-text-fill: orange; -fx-font-size: 18px;");
          }

          button.setOnAction(e -> {
            Stage newStage = editActivity(primaryStage, act);
            newStage.show();
          });

          vbox.getChildren().add(button);
          ++elementNum;

          if (elementNum == 10) {
            break;
          }
        }

        if (elementNum == 10) {
          break;
        }
      }

      root.setTop(title);
      root.setAlignment(title, Pos.CENTER);
      innerPane.setTop(hbox);
      innerPane.setCenter(vbox);
      innerPane.setBottom(innerBox);
      vbox.setAlignment(Pos.CENTER);
      root.setCenter(innerPane);
      root.setAlignment(innerPane, Pos.CENTER);

      if (pageRank > 1) {
        innerBox.getChildren().add(prevBox);
        prevPage.setOnAction(e -> {
          --pageRank;
          primaryStage.setScene(createPriorityMainScene(primaryStage));
        });
      }

      if (activityNum > higherEnd + 1) {
        innerBox.getChildren().add(nextPage);
        innerBox.setAlignment(Pos.BOTTOM_RIGHT);
        nextPage.setOnAction(e -> {
          ++pageRank;
          primaryStage.setScene(createPriorityMainScene(primaryStage));
        });
      }

      scene = new Scene(root, 900, 500);
      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

    } catch (Exception e) {
      e.printStackTrace();
    }

    return scene;
  }

  /**
   * Creates the scene that displays the activities ordered with respect to their due date
   * 
   * @param primaryStage, The main stage of the program
   * @return Scene with activities displayed when ordered by their due date
   */
  private Scene createDateMainScene(Stage primaryStage) {

    Scene scene = null; // Scene to be returned

    try {
      BorderPane root = new BorderPane();
      BorderPane innerPane = new BorderPane();
      Label title = new Label("OPTI-LIST");
      title.setStyle("-fx-text-fill: green; -fx-font-size: 16px;"); // fix thiss

      Button dueToday = new Button("Due Today");
      Button order = new Button("Order By Priority");
      Button add = new Button("Add Activity");
      Button delete = new Button("Delete All");
      Button nextPage = new Button("Next Page");
      Button prevPage = new Button("Previous Page");

      // indicates activities are ordered by due date
      Label orderIndication = new Label("Ordered By Due Date");
      orderIndication.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");

      VBox vbox = new VBox();

      dueToday.setOnAction(e -> primaryStage.setScene(dueToday(primaryStage)));
      order.setOnAction(e -> {
        mainSceneByPriority = true;
        pageRank = 1;
        primaryStage.setScene(createPriorityMainScene(primaryStage));
      });
      add.setOnAction(e -> primaryStage.setScene(createAddActivityScene(primaryStage)));
      delete.setOnAction(e -> deleteAll(primaryStage));

      // buttons situated in HBoxes so they can spread out evenly on the scene
      HBox todayBox = new HBox(dueToday);
      HBox.setHgrow(todayBox, Priority.ALWAYS);
      HBox orderBox = new HBox(order);
      HBox.setHgrow(orderBox, Priority.ALWAYS);
      HBox indicationBox = new HBox(orderIndication);
      HBox.setHgrow(indicationBox, Priority.ALWAYS);
      HBox deleteBox = new HBox(delete);
      HBox.setHgrow(deleteBox, Priority.ALWAYS);
      HBox addBox = new HBox(add);

      HBox hbox = new HBox();
      hbox.getChildren().addAll(todayBox, orderBox, indicationBox, deleteBox, addBox);

      HBox nextBox = new HBox(nextPage);
      HBox prevBox = new HBox(prevPage);
      HBox.setHgrow(prevBox, Priority.ALWAYS);
      HBox innerBox = new HBox();

      // the linked list containing all the activities ordered by due date
      LinkedList<Activity> dateList = (LinkedList) activities.get(1);

      vbox.getChildren().removeAll(); // ensures list items are not repeated

      int lowerEnd = (pageRank * 10) - 10;
      int higherEnd = (pageRank * 10) - 1;
      int elementNum = 0;

      // ensures that the high end is limited by the list size
      if (higherEnd >= dateList.size()) {
        higherEnd = dateList.size() - 1;
      }

      for (int i = lowerEnd; i <= higherEnd; ++i) {
        Button button;
        Activity act = dateList.get(i);
        if (act.hasDeadline()) {
          button =
              new Button(dateList.get(i).getDescription() + " [" + dateList.get(i).getDate() + "]");
        } else {
          button = new Button(dateList.get(i).getDescription() + " [no due date]");
        }

        if (act.getPriority() > 7) {
          button.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
        } else if (act.getPriority() < 5) {
          button.setStyle("-fx-text-fill: green; -fx-font-size: 18px;");
        } else {
          button.setStyle("-fx-text-fill: orange; -fx-font-size: 18px;");
        }

        button.setOnAction(e -> {
          Stage newStage = editActivity(primaryStage, act);
          newStage.show();
        });

        vbox.getChildren().add(button);
      }

      root.setTop(title);
      root.setAlignment(title, Pos.CENTER);
      innerPane.setTop(hbox);
      innerPane.setCenter(vbox);
      innerPane.setBottom(innerBox);
      vbox.setAlignment(Pos.CENTER);
      root.setCenter(innerPane);
      root.setAlignment(innerPane, Pos.CENTER);

      if (pageRank > 1) {
        innerBox.getChildren().add(prevBox);
        prevPage.setOnAction(e -> {
          --pageRank;
          primaryStage.setScene(createDateMainScene(primaryStage));
        });
      }

      if (activityNum > higherEnd + 1) {
        innerBox.getChildren().add(nextPage);
        innerBox.setAlignment(Pos.BOTTOM_RIGHT);
        nextPage.setOnAction(e -> {
          ++pageRank;
          primaryStage.setScene(createDateMainScene(primaryStage));
        });
      }

      scene = new Scene(root, 900, 500);
      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

    } catch (Exception e) {
      e.printStackTrace();
    }

    return scene;
  }

  /**
   * Creates the scene that provides the user with GUI features to input an activity into the
   * program
   * 
   * @param primaryStage, The main stage of the program
   * @return Scene that allows user to input activities
   */
  private Scene createAddActivityScene(Stage primaryStage) {

    BorderPane pane = new BorderPane();
    Label title = new Label("Add Activity");
    Button create = new Button("Create");
    Button cancel = new Button("Cancel");

    Label instructions =
        new Label("Enter activity description, importance level, and due date if any");

    TextField description = new TextField();
    description.setMaxWidth(500);
    description.setPromptText("Activity Description");

    ComboBox priorityLevel = new ComboBox();
    priorityLevel.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    priorityLevel.setValue(1);

    CheckBox noDate = new CheckBox("No Due Date");
    noDate.setSelected(selection);

    final TextField date = new TextField();
    date.setMaxWidth(200);
    date.setPromptText("Format: mm/dd/yyyy");

    HBox hbox = new HBox();
    hbox.setSpacing(10);
    hbox.getChildren().addAll(instructions, description, priorityLevel, noDate);

    VBox vbox = new VBox();
    vbox.setSpacing(40);
    vbox.getChildren().addAll(create, cancel);

    pane.setTop(title);
    pane.setAlignment(title, Pos.CENTER);
    pane.setCenter(hbox);
    pane.setBottom(vbox);
    hbox.setAlignment(Pos.CENTER);
    vbox.setAlignment(Pos.CENTER);
    pane.setAlignment(create, Pos.CENTER);
    pane.setAlignment(cancel, Pos.CENTER);

    // this allows for the date checkbox to remain selected or unselected when the user leaves the
    // add activity scene
    if (noDate.isSelected()) {
      hbox.getChildren().remove(date);
    } else {
      hbox.getChildren().add(3, date);
    }

    // checkbox removes and introduces date text field if the user chooses to assign a due date
    noDate.setOnAction(e -> {
      if (noDate.isSelected()) {
        hbox.getChildren().remove(date);
        selection = true;
      } else {
        hbox.getChildren().add(3, date);
        selection = false;
      }
    });

    create.setOnAction(e -> {

      Activity newActivity;
      if (!noDate.isSelected()) {
        newActivity =
            new Activity(description.getText(), (int) priorityLevel.getValue(), date.getText());
      } else {
        newActivity = new Activity(description.getText(), (int) priorityLevel.getValue());
      }

      if (correctInputEntered(newActivity).equals("Success")) {
        // the new activity is added to both the date list and the priority list
        addByPriority(newActivity);
        addByDate(newActivity);
        ++activityNum;
        primaryStage.setScene(createAddActivityScene(primaryStage));

      } else {
        Stage secondaryStage = new Stage();
        Label errorMessage = new Label(correctInputEntered(newActivity));
        BorderPane secondPane = new BorderPane();
        secondPane.setCenter(errorMessage);
        secondPane.setAlignment(errorMessage, Pos.CENTER);
        Scene secondScene = new Scene(secondPane, 400, 200);
        secondaryStage.setScene(secondScene);
        secondaryStage.show();
      }
    });

    cancel.setOnAction(e -> {
      if (mainSceneByPriority) {
        primaryStage.setScene(createPriorityMainScene(primaryStage));
      } else {
        primaryStage.setScene(createDateMainScene(primaryStage));
      }
    });

    Scene scene = new Scene(pane, 900, 500);
    return scene;
  }

  /**
   * Creates a secondary stage where a selected activity can be edited or deleted
   * 
   * @param primaryStage, The main stage of the program
   * @param activity, The activity to be edited
   * @return The stage in which the activity will be edited
   */
  private Stage editActivity(Stage primaryStage, Activity activity) {

    Stage secondaryStage = new Stage(); // Stage to be returned

    BorderPane pane = new BorderPane();
    Label title = new Label("Edit Activity");
    Button apply = new Button("Apply and Close");
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete Activity");

    VBox vbox = new VBox();
    vbox.setSpacing(20);
    vbox.getChildren().addAll(apply, delete, cancel);

    TextField description = new TextField();
    description.setMaxWidth(500);
    description.setPromptText("Activity Description");
    description.setText(activity.getDescription());

    ComboBox priorityLevel = new ComboBox();
    priorityLevel.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    priorityLevel.setValue(activity.getPriority());

    TextField date = new TextField();
    date.setMaxWidth(200);
    date.setPromptText("Format: mm/dd/yyyy");
    date.setText(activity.getDate());

    CheckBox noDate = new CheckBox("No Due Date");

    HBox hbox = new HBox();
    hbox.setSpacing(10);
    hbox.getChildren().addAll(description, priorityLevel, noDate);

    if (activity.hasDeadline()) {
      noDate.setSelected(false);
    } else {
      noDate.setSelected(true);
    }

    if (noDate.isSelected()) {
      hbox.getChildren().remove(date);
    } else {
      hbox.getChildren().add(2, date);
    }
    // checkbox removes and introduces date text field if the user chooses to assign a due date
    noDate.setOnAction(e -> {
      if (noDate.isSelected()) {
        hbox.getChildren().remove(date);
      } else {
        hbox.getChildren().add(2, date);
      }
    });

    pane.setTop(title);
    pane.setAlignment(title, Pos.CENTER);
    pane.setBottom(vbox);
    vbox.setAlignment(Pos.CENTER);
    pane.setCenter(hbox);
    hbox.setAlignment(Pos.CENTER);

    // when changes are applied, the activity is deleted from the date and priority lists, and it is
    // reintroduced with its new corresponding values
    apply.setOnAction(e -> {

      deleteActivity(activity);
      Activity newActivity;

      if (!noDate.isSelected()) {
        newActivity =
            new Activity(description.getText(), (int) priorityLevel.getValue(), date.getText());
      } else {
        newActivity = new Activity(description.getText(), (int) priorityLevel.getValue());
      }

      if (correctInputEntered(newActivity).equals("Success")) {
        addByPriority(newActivity);
        addByDate(newActivity);
        ++activityNum;
        secondaryStage.close();

        if (mainSceneByPriority) {
          primaryStage.setScene(createPriorityMainScene(primaryStage));
        } else {
          primaryStage.setScene(createDateMainScene(primaryStage));
        }

      } else {
        Stage newStage = new Stage();
        Label errorMessage = new Label(correctInputEntered(newActivity));
        BorderPane newPane = new BorderPane();
        newPane.setCenter(errorMessage);
        newPane.setAlignment(errorMessage, Pos.CENTER);
        Scene newScene = new Scene(newPane, 400, 200);
        newStage.setScene(newScene);
        newStage.show();
      }
    });

    delete.setOnAction(e -> {
      deleteActivity(activity);
      if (mainSceneByPriority) {
        primaryStage.setScene(createPriorityMainScene(primaryStage));
      } else {
        primaryStage.setScene(createDateMainScene(primaryStage));
      }
      secondaryStage.close();
    });

    cancel.setOnAction(e -> secondaryStage.close());

    Scene scene = new Scene(pane, 500, 300);
    secondaryStage.setScene(scene);

    return secondaryStage;
  }

  /**
   * Creates scene that displays activities due today and allows the option to edit them
   * 
   * @param primaryStage, The main stage of the program
   * @return Scene displaying activities due today
   */
  private Scene dueToday(Stage primaryStage) {

    // values representing current date
    LocalDate currentDate = LocalDate.now();
    int day = currentDate.getDayOfMonth();
    int month = currentDate.getMonthValue();
    int year = currentDate.getYear();

    ArrayList<ArrayList> pairing = new ArrayList<ArrayList>(); // paring of labels and buttons

    long today = 0; // long value representing the current date

    ComboBox<String> choice = new ComboBox<String>();
    BorderPane pane = new BorderPane();
    HBox hbox = new HBox();
    Label title = new Label("Activities Due Today");
    Button close = new Button("Close");
    Button edit = new Button("Edit");

    hbox.getChildren().addAll(choice, edit);
    hbox.setSpacing(10);

    // the current date is calculated in terms of days that have passed
    if (year % 4 == 0) {
      today = (long) (day + daysSoFar[month - 1] + (year * 365.25));
      if (month > 2) { // accounts for the extra day in the leap year
        ++today;
      }
    } else {
      int i = year % 4;
      int j = year - i;
      today = (long) (day + daysSoFar[month - 1] + (j * 365.25 + i * 365));
    }

    List<Activity> dateList = activities.get(1); // the date ordered activity list

    for (int i = 0; i < dateList.size(); ++i) {
      Activity act = dateList.get(i);
      // list is already ordered in ascending order, if next activity in list has a posterior date,
      // there are no more activities due today
      if (today < calculateDate(act)) {
        break;
      }

      if (today == calculateDate(act)) {
        String description = act.getDescription();
        Button button = new Button();
        choice.getItems().add(description);

        button.setOnAction(e -> {
          Stage newStage = editActivity(primaryStage, act);
          newStage.show();
        });

        // this list will encapsulate the specific label and button for each activity
        ArrayList list = new ArrayList();
        list.add(description);
        list.add(button);
        pairing.add(list);
      }
    }

    edit.setOnAction(e -> {
      String selectedActivity = choice.getValue();
      if (selectedActivity != null) {
        for (int i = 0; i < pairing.size(); ++i) {
          // by using == the specific label instance of an activity is checked, allowing for the
          // existence of activities with identical descriptions
          if (selectedActivity == pairing.get(i).get(0)) {
            Button button = (Button) pairing.get(i).get(1);
            button.fire();
            choice.setValue(null);
          }
        }
      }
    });

    close.setOnAction(e -> {
      if (mainSceneByPriority) {
        primaryStage.setScene(createPriorityMainScene(primaryStage));
      } else {
        primaryStage.setScene(createDateMainScene(primaryStage));
      }
    });

    pane.setTop(title);
    pane.setAlignment(title, Pos.CENTER);
    pane.setCenter(hbox);
    hbox.setAlignment(Pos.CENTER);
    pane.setBottom(close);
    pane.setAlignment(close, Pos.CENTER);

    Scene scene = new Scene(pane, 900, 500);

    return scene;
  }

  /**
   * Deletes activity from both the date and priority lists
   * 
   * @param activity, The activity being deleted
   */
  public void deleteActivity(Activity activity) {

    int index = 10 - activity.getPriority(); // makes search through priority list faster
    List<List> priorityList = activities.get(0);
    List<List> dateList = activities.get(1);

    priorityList.get(index).remove(activity);
    dateList.remove(activity);
    --activityNum;
  }

  /**
   * Deletes all the activities in the program
   * 
   * @param primaryStage, The main stage of the program
   */
  public void deleteAll(Stage primaryStage) {

    BorderPane pane = new BorderPane();
    Label prompt = new Label("Are you sure you want to delete all activities?");
    Button yes = new Button("Yes");
    Button no = new Button("No");
    VBox vbox = new VBox();
    vbox.setSpacing(30);
    vbox.getChildren().addAll(prompt, yes, no);

    pane.setCenter(vbox);
    vbox.setAlignment(Pos.CENTER);

    Scene scene = new Scene(pane, 400, 200);
    Stage secondaryStage = new Stage();
    secondaryStage.setScene(scene);

    yes.setOnAction(e -> {
      activityNum = 0;
      pageRank = 1;
      List<List> priorityList = activities.get(0);

      for (int i = 0; i < priorityList.size(); ++i) {
        priorityList.get(i).clear();
      }

      activities.get(1).clear();
      secondaryStage.close();

      if (mainSceneByPriority) {
        primaryStage.setScene(createPriorityMainScene(primaryStage));
      } else {
        primaryStage.setScene(createDateMainScene(primaryStage));
      }
    });

    no.setOnAction(e -> secondaryStage.close());

    secondaryStage.show();
  }

  /**
   * Adds a newly created activity to the priority list, inserting it in the sub list whose index
   * corresponds to the activity's priority. Sublist at index 0 will hold activities of priority 10,
   * sublist at index 1 will hold those of priority 9, and so on.
   * 
   * @param addedActivity, The activity to be added
   */
  public void addByPriority(Activity addedActivity) {

    int priority = addedActivity.getPriority();
    int index = (10 - priority); // the corresponding index for an activity with a specific priority

    LinkedList<Activity> priorityList = (LinkedList<Activity>) activities.get(0).get(index); // this

    if (priorityList.size() == 0) {
      priorityList.add(addedActivity);
    } else {
      // activities of the same priority will be sorted by their due date
      if (addedActivity.hasDeadline()) {
        for (int i = 0; i < priorityList.size(); ++i) {
          // sort by which one has closest due date
          if (calculateDate(addedActivity) < calculateDate(priorityList.get(i))) {
            priorityList.add(i, addedActivity);
            break;
          } else if (i == priorityList.indexOf(priorityList.getLast())) {
            priorityList.add(addedActivity);
            break;
          }
        }
      } else { // if activity has no deadline, it is not more important than others
        priorityList.add(addedActivity);
      }
    }
  }

  /**
   * Adds the newly created activity to the date list, inserted after activities with previous due
   * dates
   * 
   * @param addedActivity, The activity to be added
   */
  public void addByDate(Activity addedActivity) {

    int priority = addedActivity.getPriority();
    LinkedList<Activity> dateList = (LinkedList<Activity>) activities.get(1);

    if (dateList.size() == 0) {
      dateList.add(addedActivity);

    } else {
      if (addedActivity.hasDeadline() == true) {
        for (int i = 0; i < dateList.size(); ++i) {
          // sort by which one has closest due date
          if (calculateDate(addedActivity) < calculateDate(dateList.get(i))) {
            dateList.add(i, addedActivity);
            break;

          } else if (calculateDate(addedActivity) == calculateDate(dateList.get(i))) {

            // activities with the same due date are sorted by their priority
            if (addedActivity.getPriority() > dateList.get(i).getPriority()) {
              dateList.add(i, addedActivity);

            } else {
              // 'if' statement used to prevent an out of bounds exception
              if (i + 1 == dateList.size()) {
                dateList.add(addedActivity);

              } else {
                if (calculateDate(addedActivity) == calculateDate(dateList.get(i + 1))) {
                  continue;
                }
                // if next activity in the list doesn't have the same date, it means it has a
                // posterior date, so new activity is inserted before it
                dateList.add(i + 1, addedActivity);
              }
            }
            break;

          } else if (i == (dateList.size() - 1)) {
            // if entire list is traversed the new activity is the one with latest due date
            dateList.add(addedActivity);
            break;
          }
        }

      } else {
        if (dateList.getLast().hasDeadline()) {
          dateList.add(addedActivity);

        } else {
          if (addedActivity.getPriority() > dateList.getLast().getPriority()) {
            dateList.add(dateList.indexOf(dateList.getLast()), addedActivity);

          } else {
            dateList.add(addedActivity);
          }
        }
      }
    }

  }

  /**
   * Calculates the date of an activity as a long value representing the number of days that have
   * passed
   * 
   * @param addedActivity, The activity to be added
   * @return The activity's date represented as the number of days so far accumulated
   */
  public long calculateDate(Activity addedActivity) {

    long dateVal = 0;
    String dateInput = addedActivity.getDate();

    // activities without due date have their date calculated as the most posterior date possible
    if (!addedActivity.hasDeadline()) {
      return Long.MAX_VALUE;
    }

    dateInput = dateInput.trim();

    int month = 0;
    int day = 0;
    int year = 0;
    String[] numbers = dateInput.split("/");

    // correct date format input has been checked by correctInputEntered() method
    try {
      month = Integer.parseInt(numbers[0]);
      day = Integer.parseInt(numbers[1]);
      year = Integer.parseInt(numbers[2]);

      if (year % 4 == 0) {
        dateVal = (long) (day + daysSoFar[month - 1] + (year * 365.25));
        // accounts for the extra day in the leap year
        if (month > 2) {
          ++dateVal;
        }

      } else {
        int i = year % 4;
        int j = year - i;
        dateVal = (long) (day + daysSoFar[month - 1] + (j * 365.25 + i * 365));
      }

    } catch (NumberFormatException e) {
      e.printStackTrace();
    }

    return dateVal;
  }

  /**
   * Checks if user entered activity fields in a correct format
   * 
   * @param enteredActivity, The activity that is entered by the user
   * @return String indicating if user used correct format for inputed fields
   */
  public String correctInputEntered(Activity enteredActivity) {

    String description = enteredActivity.getDescription();
    description = description.trim();

    if (description.isEmpty()) {
      return "Error: No activity description was entered.";
    }

    if (enteredActivity.hasDeadline() == true) {

      String date = enteredActivity.getDate();
      date = date.trim();
      String[] numbers = date.split("/");

      // multiple scenarios of bad input checked for, such as
      if (numbers.length != 3) {
        return "Error: Improper date format.";
      }

      int month;
      int day;
      int year;

      try {
        month = Integer.parseInt(numbers[0]);
        day = Integer.parseInt(numbers[1]);
        year = Integer.parseInt(numbers[2]);

      } catch (NumberFormatException e) {
        return "Error: Improper date format, numbers must be entered.";
      }

      if (!isValidDate(month, day, year)) {
        return "Error: Improper values for date numbers.";
      }
    }

    return "Success";
  }

  /**
   * Checks if date entered by the user is valid or not
   * 
   * @param month, The month the user specified
   * @param day, The day the user specified
   * @param year, The year the user specified
   * @return true if the date is valid, false otherwise
   */
  public Boolean isValidDate(int month, int day, int year) {

    if (year > 0) {
      
      if (month > 12 || month < 1) {
        return false;
      }
      // takes leap year into consideration
      if (year % 4 == 0 && day == 29 && month == 2) {
        return true;

      } else {
        if (day <= validDate[month - 1] && day >= 1) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Starts the program
   * 
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }
}
