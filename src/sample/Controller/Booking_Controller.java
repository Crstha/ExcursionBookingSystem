package sample.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Booking_Controller implements Initializable {
    private static String Excursion_ID, Port_ID, Excursion_Name, Booked_Seat, Booked_Date;
    private static ObservableList<booking_info> booking;

    @FXML
    private Label Cancel_ID;

    @FXML
    private StackPane userBooking_StackPane;

    @FXML
    private JFXTextField Update_textfield_Seat;

    @FXML
    private TableView<booking_info> tableView;

    @FXML
    private TableColumn<booking_info, String> ExcursionID_Column;

    @FXML
    private TableColumn<booking_info, String> PortID_Column;

    @FXML
    private TableColumn<booking_info, String> ExcursionName_Column;

    @FXML
    private TableColumn<booking_info, String> SeatNo_Column;

    @FXML
    private TableColumn<booking_info, String> BookedDate_Column;

    //For refreshing the table data when some action is performed
    public void refresh(){
        sample.Controller.Login_Controller login_controller;
        login_controller = new sample.Controller.Login_Controller();
        booking.clear();
        try {
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/group-8", "root", "");
            Statement statement = myConn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM `booking` WHERE `Booked By` = '" + login_controller.loggedInID + "' AND `Status` = 'Booked'");
            while (rs.next()) {
                Excursion_ID = rs.getString("Excursion ID");
                Port_ID = rs.getString("Port ID");
                Excursion_Name = rs.getString("Excursion Name");
                Booked_Seat = rs.getString("Booked Seat");
                Booked_Date = rs.getString("Booked Date");
                booking.add(new booking_info(Excursion_ID, Port_ID, Excursion_Name, Booked_Seat, Booked_Date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sample.Controller.Login_Controller login_controller;
        login_controller = new sample.Controller.Login_Controller();

        ExcursionID_Column.setCellValueFactory(new PropertyValueFactory<booking_info, String>("Excursion_ID"));
        PortID_Column.setCellValueFactory(new PropertyValueFactory<booking_info, String>("Port_ID"));
        ExcursionName_Column.setCellValueFactory(new PropertyValueFactory<booking_info, String>("Excursion_Name"));
        SeatNo_Column.setCellValueFactory(new PropertyValueFactory<booking_info, String>("Booked_Seat"));
        BookedDate_Column.setCellValueFactory(new PropertyValueFactory<booking_info, String>("Booked_Date"));

        tableView.setItems(getBooking());

        try {
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/group-8", "root", "");
            Statement statement = myConn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM `booking` WHERE `Booked By` = '" + login_controller.loggedInID + "' AND `Status` = 'Booked'");
            while (rs.next()) {
                Excursion_ID = rs.getString("Excursion ID");
                Port_ID = rs.getString("Port ID");
                Excursion_Name = rs.getString("Excursion Name");
                Booked_Seat = rs.getString("Booked Seat");
                Booked_Date = rs.getString("Booked Date");
                booking.add(new booking_info(Excursion_ID, Port_ID, Excursion_Name, Booked_Seat, Booked_Date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Cancel_ID.setText(tableView.getSelectionModel().getSelectedItem().getExcursion_ID());
            }
        });
    }

    public void cancelBooking_Button() throws SQLException {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            userBooking_StackPane.setVisible(true);
            JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Text("Error"));
            content.setBody(new Text("Select a table"));
            JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

            JFXButton closeButton = new JFXButton("Close");
            closeButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.close();
                    userBooking_StackPane.setVisible(false);
                }
            });
            content.setActions(closeButton);
            dialog.show();
        } else {
            userBooking_StackPane.setVisible(true);

            JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Text("Cancel"));
            content.setBody(new Text("Booking that are cancelled are deleted forever. Are you sure you want to Cancel?"));
            JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);
            JFXButton closeButton = new JFXButton("Close");
            closeButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.close();
                    userBooking_StackPane.setVisible(false);
                }
            });

            JFXButton okayButton = new JFXButton("Okay");
            okayButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String bookedSeatByUser = null;
                    sample.Controller.Login_Controller login_controller;
                    login_controller = new sample.Controller.Login_Controller();
                    try {
                        Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/group-8", "root", "");
                        Statement statement = myConn.createStatement();
                        statement.execute("UPDATE `booking` SET `Status`='Cancelled' WHERE `Booked By`='" + login_controller.loggedInID + "' AND `Excursion ID`='" + tableView.getSelectionModel().getSelectedItem().getExcursion_ID() + "'AND `Status` = 'Booked'");
                        ResultSet bookedSeatByUser_RS = statement.executeQuery("SELECT `Booked Seat` FROM `booking` WHERE `Booked By`='" + login_controller.loggedInID + "' AND `Excursion ID`='" + tableView.getSelectionModel().getSelectedItem().getExcursion_ID() + "'");
                        while (bookedSeatByUser_RS.next()) {
                            bookedSeatByUser = bookedSeatByUser_RS.getString("Booked Seat");
                        }
                        statement.execute("UPDATE `excursions` SET `Seat`=`Seat`+'" + Integer.parseInt(bookedSeatByUser) + "' WHERE `ID` = '" + tableView.getSelectionModel().getSelectedItem().getExcursion_ID() + "'");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    dialog.close();
                    userBooking_StackPane.setVisible(false);
                    refresh();
                }
            });
            content.setActions(closeButton, okayButton);

            dialog.show();

        }

    }

    public void updateBooking_Add() throws SQLException {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            userBooking_StackPane.setVisible(true);
            JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Text("Error"));
            content.setBody(new Text("Select a table"));
            JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

            JFXButton closeButton = new JFXButton("Close");
            closeButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.close();
                    userBooking_StackPane.setVisible(false);
                }
            });
            content.setActions(closeButton);
            dialog.show();
        } else {
            if (Update_textfield_Seat.getText().isEmpty()) {
                userBooking_StackPane.setVisible(true);
                JFXDialogLayout content = new JFXDialogLayout();
                content.setHeading(new Text("Error"));
                content.setBody(new Text("TextField is Null"));
                JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

                JFXButton closeButton = new JFXButton("Close");
                closeButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        dialog.close();
                        userBooking_StackPane.setVisible(false);
                    }
                });
                content.setActions(closeButton);
                dialog.show();
            } else {
                if (Integer.parseInt(Update_textfield_Seat.getText()) > 32) {
                    userBooking_StackPane.setVisible(true);
                    JFXDialogLayout content = new JFXDialogLayout();
                    content.setHeading(new Text("Error"));
                    content.setBody(new Text("The Seat limit is 32!"));
                    JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

                    JFXButton closeButton = new JFXButton("Close");
                    closeButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            dialog.close();
                            userBooking_StackPane.setVisible(false);
                        }
                    });
                    content.setActions(closeButton);
                    dialog.show();
                }
                else if (Integer.parseInt(Update_textfield_Seat.getText())<=0){
                    userBooking_StackPane.setVisible(true);
                    JFXDialogLayout content = new JFXDialogLayout();
                    content.setHeading(new Text("Error"));
                    content.setBody(new Text("Seat cannot be 0"));
                    JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

                    JFXButton closeButton = new JFXButton("Close");
                    closeButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            dialog.close();
                            userBooking_StackPane.setVisible(false);
                        }
                    });
                    content.setActions(closeButton);
                    dialog.show();
                }
                else {
                    String exSeat = null;
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/group-8", "root", "");
                    Statement statement = con.createStatement();
                    ResultSet exSeatRS = statement.executeQuery("SELECT `Seat` FROM `excursions` WHERE `ID`='" + tableView.getSelectionModel().getSelectedItem().getExcursion_ID() + "'");
                    while (exSeatRS.next()) {
                        exSeat = exSeatRS.getString("Seat");
                    }
                    if (Integer.parseInt(exSeat) <= 0) {
                        userBooking_StackPane.setVisible(true);
                        JFXDialogLayout content = new JFXDialogLayout();
                        content.setHeading(new Text("Error"));
                        content.setBody(new Text("Sorry no Seat Available"));
                        JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

                        JFXButton closeButton = new JFXButton("Close");
                        closeButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                dialog.close();
                                userBooking_StackPane.setVisible(false);
                            }
                        });
                        content.setActions(closeButton);
                        dialog.show();
                    } else if (Integer.parseInt(Update_textfield_Seat.getText()) > Integer.parseInt(exSeat)) {
                        userBooking_StackPane.setVisible(true);
                        JFXDialogLayout content = new JFXDialogLayout();
                        content.setHeading(new Text("Error"));
                        content.setBody(new Text(exSeat + " seat available"));
                        JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

                        JFXButton closeButton = new JFXButton("Close");
                        closeButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                dialog.close();
                                userBooking_StackPane.setVisible(false);
                            }
                        });
                        content.setActions(closeButton);
                        dialog.show();
                    } else {
                        userBooking_StackPane.setVisible(true);

                        JFXDialogLayout content = new JFXDialogLayout();
                        content.setHeading(new Text("Update Seat"));
                        content.setBody(new Text("Confirm Adding " + Update_textfield_Seat.getText() + " Seats?"));
                        JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

                        JFXButton closeButton = new JFXButton("Close");
                        closeButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                dialog.close();
                                userBooking_StackPane.setVisible(false);
                            }
                        });

                        JFXButton okayButton = new JFXButton("Okay");
                        okayButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                String bookedSeatByUser = null;
                                sample.Controller.Login_Controller login_controller;
                                login_controller = new sample.Controller.Login_Controller();
                                try {
                                    Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/group-8", "root", "");
                                    Statement statement = myConn.createStatement();
                                    statement.execute("UPDATE `booking` SET `Booked Seat`= `Booked Seat` +'" + Integer.parseInt(Update_textfield_Seat.getText()) + "'WHERE `Excursion ID`='" + tableView.getSelectionModel().getSelectedItem().getExcursion_ID() + "'AND `Booked By`='" + login_controller.loggedInID + "'AND `Status` = 'Booked'");
                                    statement.execute("UPDATE `excursions` SET `Seat`=`Seat`-'" + Integer.parseInt(Update_textfield_Seat.getText()) + "' WHERE `ID` = '" + tableView.getSelectionModel().getSelectedItem().getExcursion_ID() + "'");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                dialog.close();
                                userBooking_StackPane.setVisible(false);
                                refresh();
                            }
                        });
                        content.setActions(closeButton, okayButton);

                        dialog.show();
                    }

                }
            }
        }

    }
    public void updateBooking_Sub()throws SQLException{
        if (tableView.getSelectionModel().getSelectedItem() == null){
            userBooking_StackPane.setVisible(true);
            JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Text("Error"));
            content.setBody(new Text("Please select a table row"));
            JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

            JFXButton closeButton = new JFXButton("Close");
            closeButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.close();
                    userBooking_StackPane.setVisible(false);
                }
            });
            content.setActions(closeButton);
            dialog.show();
        }
        else{
            if (Update_textfield_Seat.getText().isEmpty()){
                userBooking_StackPane.setVisible(true);
                JFXDialogLayout content = new JFXDialogLayout();
                content.setHeading(new Text("Error"));
                content.setBody(new Text("TextField is Null"));
                JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

                JFXButton closeButton = new JFXButton("Close");
                closeButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        dialog.close();
                        userBooking_StackPane.setVisible(false);
                    }
                });
                content.setActions(closeButton);
                dialog.show();
            }
            else {
                if (Integer.parseInt(Update_textfield_Seat.getText()) > 32){
                    userBooking_StackPane.setVisible(true);
                    JFXDialogLayout content = new JFXDialogLayout();
                    content.setHeading(new Text("Error"));
                    content.setBody(new Text("The Seat limit is 32!"));
                    JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

                    JFXButton closeButton = new JFXButton("Close");
                    closeButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            dialog.close();
                            userBooking_StackPane.setVisible(false);
                        }
                    });
                    content.setActions(closeButton);
                    dialog.show();
                }
                else if (Integer.parseInt(Update_textfield_Seat.getText())<=0){
                    userBooking_StackPane.setVisible(true);
                    JFXDialogLayout content = new JFXDialogLayout();
                    content.setHeading(new Text("Error"));
                    content.setBody(new Text("Seat cannot be 0"));
                    JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

                    JFXButton closeButton = new JFXButton("Close");
                    closeButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            dialog.close();
                            userBooking_StackPane.setVisible(false);
                        }
                    });
                    content.setActions(closeButton);
                    dialog.show();
                }
                else {
                    String boSeat = null;
                    sample.Controller.Login_Controller login_controller;
                    login_controller = new sample.Controller.Login_Controller();

                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/group-8","root", "");
                    Statement statement = con.createStatement();
                    ResultSet boSeatRS = statement.executeQuery("SELECT `Booked Seat` FROM `booking` WHERE `Excursion ID`='"+tableView.getSelectionModel().getSelectedItem().getExcursion_ID()+"'AND `Booked By`='"+login_controller.loggedInID+"'AND `Status` = 'Booked'");
                    while (boSeatRS.next()){
                        boSeat = boSeatRS.getString("Booked Seat");
                    }
                    if (Integer.parseInt(boSeat) <= 1){
                        userBooking_StackPane.setVisible(true);
                        JFXDialogLayout content = new JFXDialogLayout();
                        content.setHeading(new Text("Error"));
                        content.setBody(new Text("Sorry Seat must be atleast 1"));
                        JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

                        JFXButton closeButton = new JFXButton("Close");
                        closeButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                dialog.close();
                                userBooking_StackPane.setVisible(false);
                            }
                        });
                        content.setActions(closeButton);
                        dialog.show();
                    }
                    else if (Integer.parseInt(Update_textfield_Seat.getText())>=Integer.parseInt(boSeat)){
                        userBooking_StackPane.setVisible(true);
                        JFXDialogLayout content = new JFXDialogLayout();
                        content.setHeading(new Text("Error"));
                        content.setBody(new Text(boSeat + " seat in your bookings"));
                        JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

                        JFXButton closeButton = new JFXButton("Close");
                        closeButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                dialog.close();
                                userBooking_StackPane.setVisible(false);
                            }
                        });
                        content.setActions(closeButton);
                        dialog.show();
                    }
                    else {
                        userBooking_StackPane.setVisible(true);

                        JFXDialogLayout content = new JFXDialogLayout();
                        content.setHeading(new Text("Update Seat"));
                        content.setBody(new Text("Confirm Substract "+Update_textfield_Seat.getText()+" Seats?"));
                        JFXDialog dialog = new JFXDialog(userBooking_StackPane, content, JFXDialog.DialogTransition.CENTER);

                        JFXButton closeButton = new JFXButton("Close");
                        closeButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                dialog.close();
                                userBooking_StackPane.setVisible(false);
                            }
                        });

                        JFXButton okayButton = new JFXButton("Okay");
                        okayButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event){
                                sample.Controller.Login_Controller login_controller;
                                login_controller = new sample.Controller.Login_Controller();
                                try {
                                    Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/group-8","root", "");
                                    Statement statement = myConn.createStatement();
                                    statement.execute("UPDATE `booking` SET `Booked Seat`= `Booked Seat` -'"+Integer.parseInt(Update_textfield_Seat.getText())+"'WHERE `Excursion ID`='"+tableView.getSelectionModel().getSelectedItem().getExcursion_ID()+"'AND `Booked By`='"+login_controller.loggedInID+"' AND `Status` = 'Booked'");
                                    statement.execute("UPDATE `excursions` SET `Seat`=`Seat`+'"+Integer.parseInt(Update_textfield_Seat.getText())+"' WHERE `ID` = '"+tableView.getSelectionModel().getSelectedItem().getExcursion_ID()+"'");
                                }
                                catch (SQLException e){
                                    e.printStackTrace();
                                }
                                dialog.close();
                                userBooking_StackPane.setVisible(false);
                                refresh();
                            }
                        });
                        content.setActions(closeButton, okayButton);

                        dialog.show();
                    }

                }
            }
        }


    }

    public ObservableList<booking_info> getBooking() {
        booking = FXCollections.observableArrayList();
        return booking;
    }

}
