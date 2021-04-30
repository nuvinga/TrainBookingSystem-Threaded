package sample;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.bson.Document;

import javax.security.auth.login.AccountLockedException;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Station extends Application {

    private static ArrayList<String> seatList = new ArrayList<>();
    private static ArrayList<String> newList = new ArrayList<>();
    private static Passenger[] waitingRoom = new Passenger[42];
    private static PassengerQueue trainQueueOne = new PassengerQueue();
    private static PassengerQueue trainQueueTwo = new PassengerQueue();
    private static ArrayList<Passenger> boardedToTrain = new ArrayList<>();
    private static final String[] stops={"Colombo-Fort","Polgahawela","Peradeniya","Gampola","Nawalapitiya","Hatton",
            "Thalawakele","Nanuoya","Haputale","Diyatalawa","Bandarawela","Ella","Badulla"};
    private static int station= 0;
    private static int maxLengthQueueOne= 0;
    private static int maxLengthQueueTwo= 0;
    private static int minLengthQueueOne= 10000;
    private static int minLengthQueueTwo= 10000;
    private static String direction= null;

    public static void main(String[] args) { launch(args); }

    public void start(Stage primaryStage) { checkIn(); }

    public static void checkIn() {

        //============================================================================================================//
        //                                         INITIALIZING ELEMENTS

        //------------------------------------------------------------------------------------------------------- Stages
        Stage stageOne = new Stage();
        BorderPane rootOne = new BorderPane();
        Scene sceneOne = new Scene(rootOne,700,250);
        stageOne.setScene(sceneOne);
        rootOne.getStylesheets().add("/style.css");
        stageOne.setResizable(false);
        stageOne.setTitle("Station Selection");
        stageOne.show();

        Stage stageTwo = new Stage();
        BorderPane rootTwo = new BorderPane();
        Scene sceneTwo = new Scene(rootTwo,1000,700);
        stageTwo.setScene(sceneTwo);
        rootTwo.getStylesheets().add("/style.css");
        stageTwo.setResizable(false);
        stageTwo.setTitle("Denuwara Menike Terminal- Self Check In");

        LocalDate date = LocalDate.now();

        //------------------------------------------------------------------------------------------------------- Labels
        Label mainLabel = new Label("Denuwara Menike Terminal Check-In");
        mainLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR, 30));
        mainLabel.setPadding(new Insets(40,20,20,20));

        Label stationMaster = new Label("Station Master, select the direction of the train and the Station Name");
        stationMaster.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR, 18));
        stationMaster.setPadding(new Insets(40,20,5,20));


        Label stationMasterFooter = new Label("Today's date: "+date);


        //------------------------------------------------------------------------------------------------------ Buttons

        Button stationSubmit = new Button("OK");
        stationSubmit.setId("stationButtons");

        Button stationClear = new Button("Clear Selections");
        stationClear.setId("stationButtons");

        Button closeCheckIn = new Button("Close Check In");
        closeCheckIn.setId("closeCheckIn");

        //-------------------------------------------------------------------------------------------------- Combo Boxes
        ComboBox selectDirection = new ComboBox();
        selectDirection.setId("combo");
        selectDirection.getItems().addAll("Colombo To Badulla", "Badulla to Colombo");
        selectDirection.setPromptText("Select Direction");

        ComboBox selectStation = new ComboBox();
        selectStation.setId("combo");
        selectStation.setPromptText("Select Station");



        //-------------------------------------------------------------------------------------------------- Alert Boxes

        //============================================================================================================//
        //                                      GUI INITIALIZATION

        //-----------Stage One
        VBox headerOne = new VBox(20);
        HBox headerLineOne = new HBox(20);
        HBox footerLineOne = new HBox(20);

        headerOne.setAlignment(Pos.CENTER);
        headerLineOne.setAlignment(Pos.CENTER);
        footerLineOne.setAlignment(Pos.CENTER);

        rootOne.setTop(headerOne);

        headerOne.getChildren().add(stationMaster);
        headerOne.getChildren().add(headerLineOne);
        headerOne.getChildren().add(footerLineOne);
        headerOne.getChildren().add(stationMasterFooter);

        headerLineOne.getChildren().add(selectDirection);
        headerLineOne.getChildren().add(selectStation);
        footerLineOne.getChildren().add(stationSubmit);
        footerLineOne.getChildren().add(stationClear);

        selectStation.setDisable(true);
        stationSubmit.setDisable(true);
        stationClear.setDisable(true);


        //-----------Stage Two
        VBox headerTwo = new VBox(20);
        HBox centerTwo = new HBox(75);
        VBox footerTwo = new VBox(20);

        HBox headerLine = new HBox(20);
        ScrollPane centerScroll = new ScrollPane(centerTwo);

        VBox names = new VBox(30);
        VBox ids = new VBox(30);
        VBox buttons = new VBox(20);

        names.setPadding(new Insets(14,0,0,0));
        ids.setPadding(new Insets(18,0,0,0));
        buttons.setPadding(new Insets(14,0,0,0));

        rootTwo.setTop(headerTwo);
        rootTwo.setCenter(centerScroll);
        rootTwo.setBottom(footerTwo);

        headerTwo.setAlignment(Pos.CENTER);
        centerTwo.setAlignment(Pos.CENTER);
        footerTwo.setAlignment(Pos.CENTER);
        headerLine.setAlignment(Pos.CENTER);

        names.setAlignment(Pos.BASELINE_RIGHT);
        ids.setAlignment(Pos.CENTER);

        centerScroll.setContent(centerTwo);
        centerScroll.setFitToWidth(true);

        headerTwo.getChildren().add(mainLabel);

        centerTwo.getChildren().add(names);
        centerTwo.getChildren().add(ids);
        centerTwo.getChildren().add(buttons);

        for (int i=0;i<=12;i++){
            selectStation.getItems().add(stops[i]);  // populating comboBox using array- stops
        }

        selectDirection.valueProperty().addListener((observable, oldValue, newValue) -> {

            selectStation.setDisable(false);
            selectDirection.setDisable(true);
            stationClear.setDisable(false);

            selectStation.valueProperty().addListener(((observable1, oldValue1, newValue1) -> {
                for (int i=0;i<=12;i++){
                    if (stops[i].equals(newValue1.toString())){  //traversing array to get selected value index
                        station=i;  //storing index of value selected
                    }

                    if (newValue.toString().contains("Colombo To Badulla")){
                        direction="ctb";
                    }else{
                        direction="btc";
                    }
                }

                stationSubmit.setDisable(false);
                System.out.println(station);
                System.out.println(direction);

            }));
        });

        stationSubmit.setOnAction(event -> {
            stageOne.close();
            stageTwo.show();

            MongoClient client = MongoClients.create();
            MongoDatabase dataBase = client.getDatabase("BookingDB");
            MongoCollection<Document> baseCollection = dataBase.getCollection("bookCollection");
            Document tempHold = baseCollection.find().first();
            Object seats = tempHold.get("name");
            seatList = (ArrayList<String>) seats;

            for (int i = seatList.size() - 1; i >= 0; i--) {  //checking for old data and deleting them
                String current = seatList.get(i).substring(13, 23);
                String curDirection = seatList.get(i).substring(3, 6);
                String strDate = date.toString();
                if (current.compareTo(strDate) != 0 || (!(curDirection.equals(direction)))) {
                    seatList.remove(i);
                }
            }

            Button[] checkIn = new Button[seatList.size()];

            for (int i=0;i<seatList.size();i++){
                String fullName= seatList.get(i).substring(24,seatList.get(i).length()-5);
                String id= seatList.get(i).substring(seatList.get(i).length()-4);
                String[] name=fullName.split(" ",   2);
                String firstName= name[0].substring(0,1).toUpperCase()+name[0].substring(1);
                String secondName= name[1].substring(0,1).toUpperCase()+name[1].substring(1);

                names.getChildren().add(new Label(firstName+" "+secondName));
                ids.getChildren().add(new Label(id));
                checkIn[i]=new Button(" Click to Check In");
                checkIn[i].setId("checkInButtons");
                buttons.getChildren().add(checkIn[i]);
            }

            for (int i=0;i<seatList.size();i++){
                int finalI = i;

                checkIn[i].setOnAction(event1 -> {
                    checkIn[finalI].setDisable(true);
                    checkIn[finalI].setText("Checked In- Successful");

                    String wholeName = seatList.get(finalI).substring(24, seatList.get(finalI).length() - 5);
                    String uniqueID = seatList.get(finalI).substring(seatList.get(finalI).length() - 4);
                    String seatNumber = seatList.get(finalI).substring(0,2);
                    String[] splitNames = wholeName.split(" ", 2);

                    newList.add(seatList.get(finalI));
                    seatList.set(finalI,"null");
                    System.out.println(seatNumber);
                    waitingRoom[Integer.parseInt(seatNumber)]=new Passenger(splitNames[0],splitNames[1],uniqueID,seatNumber);

                    System.out.println(waitingRoom[Integer.parseInt(seatNumber)].getName()+
                            waitingRoom[Integer.parseInt(seatNumber)].getId()+
                            waitingRoom[Integer.parseInt(seatNumber)].getSeat());
                    System.out.println(newList);
                    System.out.println(Arrays.toString(waitingRoom));
                });
            }
        });

        footerTwo.getChildren().add(closeCheckIn);

        closeCheckIn.setOnAction(event -> {
            stageTwo.close();
            int count = 0;
            for (int i=seatList.size()-1;i>=0;i--){
                if (seatList.get(i).equals("null")){
                    seatList.remove(i);
                }else{
                    count++;
                }
            }

            if (count>0) {
                System.out.println(count+" passenger(s) have not checked in.");
            }else{
                System.out.println("All passengers checked in!");
            }

            menu();
        });

        stationClear.setOnAction(event -> {
            stageOne.close();
            try {
                checkIn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public static void menu() {
        Scanner input = new Scanner(System.in);
        String option;
        System.out.println("Welcome to the Denuwara Menike Terminal!");
        System.out.println("‘A’ to add a passenger to the trainQueue");
        System.out.println("‘V’ to view the trainQueue");
        System.out.println("‘D’: Delete passenger from the trainQueue");
        System.out.println("‘S’: Store trainQueue data");
        System.out.println("‘L’: Load data back");
        System.out.println("‘R’ : Run the simulation and produce report");
        System.out.print("Enter your option here: ");
        option = input.next();  // receives input from user
        option = option.toUpperCase();
        switch (option) {
            case "A":
                addPassenger(); //call add customers and evokes GUI
                break;
            case "V":
                view(); //call view all seats and evokes GUI
                break;
            case "D":
                delete(); //call delete customer data
                break;
            case "S":
                store(); //call store data
                break;
            case "L":
                load(); //call load data
                break;
            case "R":
                run(); //call Simulaton
                break;
            case "Q":
                System.exit(1);
            default:
                System.out.println("Oops! We couldn't read that. Please check the options again and re-enter.");
                menu();
        }
    }

    public static void addPassenger(){

        Stage stage = new Stage();
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root,1000,800);
        stage.setScene(scene);
        root.getStylesheets().add("/style.css");
        stage.setResizable(false);
        stage.setTitle("Denuwara Menike Terminal- ");

        Label mainLabel = new Label("Denuwara Menike Terminal - Train Queue waiting to Board");
        mainLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR, 30));
        mainLabel.setPadding(new Insets(60,20,5,20));

        Label subLabel = new Label("Select a seat to view more Information");
        subLabel.setFont(Font.font("sans-serif", FontPosture.REGULAR, 18));
        subLabel.setPadding(new Insets(5,20,5,20));

        Label queueOneLabel = new Label("Train Queue One");
        queueOneLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR,18));
        queueOneLabel.setPadding(new Insets(0,0,5,0));

        Label queueTwoLabel = new Label("Train Queue Two");
        queueTwoLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR,18));
        queueTwoLabel.setPadding(new Insets(0,0,5,0));

        LocalDate date = LocalDate.now();

        Label deets = new Label("Denuwara Menike Train Terminal;    Station Name: "+stops[station]+
                ";    Date: "+date);

        Label emptyQueue = new Label("Empty Queue");

        Button addToQueue = new Button("Add Passengers to Queue");
        addToQueue.setId("addToQueue");

        Button exit = new Button(" Exit ");
        exit.setId("closeExit");

        VBox header = new VBox();
        VBox left = new VBox();
        VBox center = new VBox();
        VBox right = new VBox();
        VBox footer = new VBox();

        root.setTop(header);
        root.setLeft(left);
        root.setCenter(center);
        root.setRight(right);
        root.setBottom(footer);

        left.setPadding(new Insets(70, 70, 10, 0));
        center.setPadding(new Insets(70, 0, 70, 70));
        right.setPadding(new Insets(70, 0, 70, 70));

        header.getChildren().add(mainLabel);
        header.getChildren().add(subLabel);

        left.getChildren().add(queueOneLabel);
        center.getChildren().add(queueTwoLabel);
        right.getChildren().add(addToQueue);
        right.getChildren().add(exit);

        Button[] queueButtons = new Button[20];

        left.getChildren().clear();
        center.getChildren().clear();

        left.getChildren().add(queueOneLabel);
        center.getChildren().add(queueTwoLabel);
        left.getChildren().add(emptyQueue);
        center.getChildren().add(emptyQueue);



        addToQueue.setOnAction(event -> {
            left.getChildren().clear();
            center.getChildren().clear();
            left.getChildren().add(queueOneLabel);
            center.getChildren().add(queueTwoLabel);
            Random rand = new Random();
            int num = rand.nextInt(6) + 1;

            for (int i=1;i<=num;i++){

                for (int count=0;count<42;count++){
                    int queueOne= trainQueueOne.getLength();
                    int queueTwo= trainQueueTwo.getLength();

                    if (waitingRoom[count]!=null && !waitingRoom[count].getAdded() && !trainQueueOne.isFull() &&
                            (queueTwo>queueOne || queueOne==queueTwo)){
                        System.out.println("Boarded to queue ONE");
                        trainQueueOne.add(waitingRoom[count]);
                        waitingRoom[count].setAdded(true);
                        int diceOne= rand.nextInt(6)+1;
                        int diceTwo= rand.nextInt(6)+1;
                        int diceThree= rand.nextInt(6)+1;
                        int time= diceOne+diceTwo+diceThree;
                        int thisTime= trainQueueOne.setTime(time);
                        Thread queueOneThread = new Thread(() -> {
                            try {
                                System.out.println("Entering Sleep..");
                                Thread.sleep(thisTime * 1000);
                                trainQueueOne.board();
                                System.out.println("Added to Train.");
                                trainQueueOne.display();
                            } catch (InterruptedException e) {
                                System.out.println("Unable to process to Train");
                            }
                        });
                        queueOneThread.start();
                        break;
                    }else if (waitingRoom[count]!=null && !waitingRoom[count].getAdded() && !trainQueueTwo.isFull() &&
                            queueOne>queueTwo){
                        System.out.println("Boarded to Queue TWO");
                        trainQueueTwo.add(waitingRoom[count]);
                        waitingRoom[count].setAdded(true);
                        int diceOne= rand.nextInt(6)+1;
                        int diceTwo= rand.nextInt(6)+1;
                        int diceThree= rand.nextInt(6)+1;
                        int time= diceOne+diceTwo+diceThree;
                        trainQueueTwo.setTime(time);
                        Thread queueTwoThread = new Thread(() -> {
                            try {
                                System.out.println("Entering Sleep..");
                                Thread.sleep(trainQueueTwo.getSeconds() * 1000);
                                trainQueueTwo.board();
                                System.out.println("Added to Train.");
                                trainQueueTwo.display();
                            } catch (InterruptedException e) {
                                System.out.println("Unable to process to Train");
                            }
                        });
                        queueTwoThread.start();
                        break;
                    }else if (trainQueueTwo.isFull() && trainQueueOne.isFull()){
                        break;
                    }
                }
                if (trainQueueOne.isFull()){
                    break;
                }
            }

            for (int i=0;i<trainQueueOne.getLength();i++){
                if (trainQueueOne.accessName(i)!=null){
                    queueButtons[i]=new Button(trainQueueOne.accessName(i));
                    left.getChildren().add(queueButtons[i]);
                }
            }

            for (int i=0;i<trainQueueTwo.getLength();i++){
                if (trainQueueTwo.accessName(i)!=null){
                    queueButtons[i]=new Button(trainQueueTwo.accessName(i));
                    center.getChildren().add(queueButtons[i]);
                }
            }

        });

        stage.show();
        footer.getChildren().add(deets);

        exit.setOnAction(event -> {
            stage.close();
            menu();
        });
    }

    public static void view(){

        Stage stageOne = new Stage();
        BorderPane rootOne = new BorderPane();
        Scene sceneOne = new Scene(rootOne,800,800);
        stageOne.setScene(sceneOne);
        rootOne.getStylesheets().add("/style.css");
        stageOne.setResizable(false);
        stageOne.setTitle("Station Selection");
        stageOne.show();

        LocalDate date = LocalDate.now();

        Label mainLabel = new Label("Denuwara Menike Terminal - Waiting Room");
        mainLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR, 30));
        mainLabel.setPadding(new Insets(60,20,5,20));

        Label subLabel = new Label("Select a seat to view more Information");
        subLabel.setFont(Font.font("sans-serif", FontPosture.REGULAR, 18));
        subLabel.setPadding(new Insets(5,20,5,20));

        Label deets = new Label("Denuwara Menike Train Terminal;    Station Name: "+stops[station]+
                ";    Date: "+date);


        Button exit = new Button("Exit");
        exit.setId("closeViewButton");

        VBox headerOne = new VBox();
        HBox centerOne = new HBox(50);
        VBox footerOne = new VBox(20);

        VBox centerLeft = new VBox(50);
        VBox centerMid = new VBox(50);
        VBox centerRight = new VBox();

        FlowPane left = new FlowPane(15,15);
        FlowPane mid = new FlowPane(15,15);

        rootOne.setTop(headerOne);
        rootOne.setLeft(centerOne);
        rootOne.setBottom(footerOne);

        headerOne.setAlignment(Pos.CENTER);
        centerOne.setAlignment(Pos.CENTER);
        footerOne.setAlignment(Pos.CENTER);

        headerOne.getChildren().add(mainLabel);
        headerOne.getChildren().add(subLabel);

        centerOne.getChildren().add(centerLeft);
        centerOne.getChildren().add(centerMid);
        centerOne.getChildren().add(centerRight);

        centerLeft.getChildren().add(left);
        centerMid.getChildren().add(mid);

        Button[] waitingButtons = new Button[42];

        left.setPrefWrapLength(270);
        left.setPadding(new Insets(70, 10, 10, 100));

        for (int i=0;i<21;i++){
            waitingButtons[i]=new Button("Empty");
            waitingButtons[i].setId("waitingButtons");
            waitingButtons[i].setStyle("-fx-background-color: #4f65a8");
            left.getChildren().add(waitingButtons[i]);
        }

        mid.setPrefWrapLength(270);
        mid.setPadding(new Insets(70, 10, 10, 20));
        for (int i=21;i<42;i++){
            waitingButtons[i]=new Button("Empty");
            waitingButtons[i].setId("waitingButtons");
            waitingButtons[i].setStyle("-fx-background-color: #4f65a8");
            mid.getChildren().add(waitingButtons[i]);
        }

        for (int i=0;i<42;i++){
            if (waitingRoom[i]!=null){ //i+"\n Empty"
                waitingButtons[Integer.parseInt(waitingRoom[i].getSeat())].setText(waitingRoom[i].getSeat()+"\n"+
                        waitingRoom[i].getName());
                waitingButtons[Integer.parseInt(waitingRoom[i].getSeat())].setStyle("-fx-background-color: #c29ecd");
            }
        }

        footerOne.getChildren().add(exit);
        footerOne.getChildren().add(deets);

        exit.setOnAction(event -> {
            stageOne.close();
            menu();
        });

    }

    public static void delete(){
        Scanner input = new Scanner(System.in);
        System.out.println("Enter your name or ID to delete: ");
        String name= input.next().toLowerCase();
        boolean found= trainQueueOne.delete(name);
        boolean nextFound;
        if (!found){
            nextFound= trainQueueTwo.delete(name);
            if (!nextFound){
                System.out.println("Oops! Name not found!");
            }
        }
        trainQueueOne.display();   //TESTING
        trainQueueTwo.display();   //TESTING
        menu();
    }

    public static void store(){
        try{
            File storeQueue = new File("queueDetails.txt");
            FileOutputStream fileOutputStream = new FileOutputStream(storeQueue);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(waitingRoom);
            objectOutputStream.writeObject(trainQueueOne.getQueue());
            objectOutputStream.writeObject(trainQueueTwo.getQueue());
            objectOutputStream.writeObject(boardedToTrain);
            objectOutputStream.flush();
            objectOutputStream.close();
            fileOutputStream.close();
            System.out.println("Store Successful");
        } catch (IOException e) {
            System.out.println("Error loading data to file!");
        }
        menu();
    }

    public static void load(){
        try{
            File storeQueue = new File("queueDetails.txt");
            FileInputStream fileInputStream = new FileInputStream(storeQueue);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            waitingRoom=((Passenger[]) objectInputStream.readObject());
            trainQueueOne.setQueue((Passenger[]) objectInputStream.readObject());
            trainQueueTwo.setQueue((Passenger[]) objectInputStream.readObject());
            boardedToTrain= (ArrayList<Passenger>) objectInputStream.readObject();

        }catch (IOException | ClassNotFoundException e) {
            System.out.println("Error retrieving data from file");
        }
        System.out.println(Arrays.toString(waitingRoom));
        trainQueueOne.display();
        trainQueueTwo.display();
        System.out.println(boardedToTrain);
        System.out.println(trainQueueOne.getLength());
        System.out.println(trainQueueTwo.getLength());
        menu();
    }

    public static void run(){

        Stage stage = new Stage();
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root,1000,900);
        stage.setScene(scene);
        root.getStylesheets().add("/style.css");
        stage.setResizable(false);
        stage.setTitle("Denuwara Menike Terminal- Terminal Operations Summary Report");
        stage.show();

        LocalDate date = LocalDate.now();

        Label mainLabel = new Label("Denuwara Menike Terminal Operations Summary Report");
        mainLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR, 30));
        mainLabel.setPadding(new Insets(40,20,20,20));

        Label deets = new Label("Denuwara Menike Train Terminal;    Station Name: "+stops[station]+
                ";    Date: "+date);

        Label dateInfo = new Label("Train boarding summary as at Date: ");
        dateInfo.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR,17));
        dateInfo.setPadding(new Insets(0,20,0,20));

        Label nowDate = new Label(date.toString());

        Label directionLabel = new Label("Train Direction: ");
        directionLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR,17));
        directionLabel.setPadding(new Insets(0,20,0,20));

        Label side = new Label();
        if (direction.equals("ctb")) {
            side.setText("Colombo-Fort to Badulla ");
        }else{
            side.setText("Badulla to Colombo-Fort ");
        }

        Label stationLabel = new Label("Station Name: ");
        stationLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR,17));
        stationLabel.setPadding(new Insets(0,20,0,20));

        Label stationInfo = new Label(stops[station]);

        Label queueOneLabel = new Label("Train Queue One");
        queueOneLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR,18));
        queueOneLabel.setPadding(new Insets(0,0,5,0));

        Label queueTwoLabel = new Label("Train Queue Two");
        queueTwoLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR,18));
        queueTwoLabel.setPadding(new Insets(0,0,5,0));

        Label queueOneShortest = new Label("Shortest Stay in Queue: "+trainQueueOne.getShortestStay());

        Label queueOneLongest = new Label("Longest Stay in Queue: "+trainQueueOne.getLongestStay());

        Label queueOneLength = new Label("Maximum length Attained: "+trainQueueOne.getLength());

        Label queueOneMaxStay = new Label("Total Time in Queue: "+trainQueueOne.getMaxStay());

        Label queueOneAverage = new Label("Average Waiting Time in Queue: "+trainQueueOne.getAverage());


        Label queueTwoShortest = new Label("Shortest Stay in Queue: "+trainQueueTwo.getShortestStay());

        Label queueTwoLongest = new Label("Longest Stay in Queue: "+trainQueueTwo.getLongestStay());

        Label queueTwoLength = new Label("Maximum length Attained: "+trainQueueTwo.getLength());

        Label queueTwoMaxStay = new Label("Total Time in Queue: "+trainQueueTwo.getMaxStay());

        Label queueTwoAverage = new Label("Average Waiting Time in Queue: "+trainQueueTwo.getAverage());

        Label fullName = new Label ("Full Name");
        fullName.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR,17));

        Label id = new Label("Ticket ID");
        id.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR,17));

        Label seatNumber = new Label("Seat Number");
        seatNumber.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR,17));

        Label secondsLabel = new Label("Seconds In Queue");
        secondsLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, FontPosture.REGULAR,17));

        Button exit = new Button("Exit");
        exit.setId("closeRun");

        VBox header = new VBox(10);
        HBox center = new HBox(50);
        ScrollPane centerScroll = new ScrollPane(center);

        HBox headerSummary = new HBox(50);

        HBox dateBox = new HBox();
        HBox directionBox = new HBox();
        HBox stationBox = new HBox();

        VBox queueOne = new VBox(2);
        queueOne.setPadding(new Insets(5,0,20,0));
        VBox queueTwo = new VBox(2);
        queueTwo.setPadding(new Insets(5,0,20,0));

        VBox names = new VBox(30);
        VBox ids = new VBox(30);
        VBox seat = new VBox(30);
        VBox seconds = new VBox(30);

        VBox footer = new VBox(20);
        footer.setPadding(new Insets(20,0,0,0));

        names.setPadding(new Insets(9,0,0,0));
        ids.setPadding(new Insets(14,0,0,0));
        seat.setPadding(new Insets(14,0,0,0));
        seconds.setPadding(new Insets(11,0,0,0));

        root.setTop(header);
        root.setCenter(centerScroll);
        root.setBottom(footer);

        header.setAlignment(Pos.CENTER);
        header.getChildren().add(mainLabel);
        header.getChildren().add(dateBox);
        header.getChildren().add(directionBox);
        header.getChildren().add(stationBox);
        header.getChildren().add(headerSummary);

        dateBox.setAlignment(Pos.CENTER);
        dateBox.getChildren().add(dateInfo);
        dateBox.getChildren().add(nowDate);

        directionBox.setAlignment(Pos.CENTER);
        directionBox.getChildren().add(directionLabel);
        directionBox.getChildren().add(side);

        stationBox.setAlignment(Pos.CENTER);
        stationBox.getChildren().add(stationLabel);
        stationBox.getChildren().add(stationInfo);

        headerSummary.setAlignment(Pos.CENTER);
        headerSummary.getChildren().add(queueOne);
        headerSummary.getChildren().add(queueTwo);

        queueOne.setAlignment(Pos.CENTER);
        queueOne.getChildren().add(queueOneLabel);
        queueOne.getChildren().add(queueOneShortest);
        queueOne.getChildren().add(queueOneLongest);
        queueOne.getChildren().add(queueOneLength);
        queueOne.getChildren().add(queueOneMaxStay);
        queueOne.getChildren().add(queueOneAverage);

        queueTwo.setAlignment(Pos.CENTER);
        queueTwo.getChildren().add(queueTwoLabel);
        queueTwo.getChildren().add(queueTwoShortest);
        queueTwo.getChildren().add(queueTwoLongest);
        queueTwo.getChildren().add(queueTwoLength);
        queueTwo.getChildren().add(queueTwoMaxStay);
        queueTwo.getChildren().add(queueTwoAverage);

        center.setAlignment(Pos.CENTER);
        center.getChildren().add(names);
        center.getChildren().add(ids);
        center.getChildren().add(seat);
        center.getChildren().add(seconds);
        centerScroll.setContent(center);

        names.setAlignment(Pos.CENTER);
        ids.setAlignment(Pos.CENTER);
        seat.setAlignment(Pos.CENTER);
        seconds.setAlignment(Pos.CENTER);
        centerScroll.setFitToWidth(true);

        names.getChildren().add(fullName);
        ids.getChildren().add(id);
        seat.getChildren().add(seatNumber);
        seconds.getChildren().add(secondsLabel);

        for (Passenger passenger : waitingRoom) {
            if (passenger != null && passenger.getAdded()) {
                names.getChildren().add(new Label (passenger.getFullName()));
                ids.getChildren().add(new Label (passenger.getId()));
                seat.getChildren().add(new Label(passenger.getSeat()));
                seconds.getChildren().add(new Label(String.valueOf(passenger.getSeconds())));
            }
        }

        footer.setAlignment(Pos.CENTER);
        footer.getChildren().add(exit);
        exit.setOnAction(event -> {
            stage.close();
            menu();
        });
        footer.getChildren().add(deets);


        System.out.println(seatList.size());

        try {
            int longest=0;
            for (int i=0;i<42;i++){
                //Comparing sizes of the names to get longest
                if (waitingRoom[i]!=null && waitingRoom[i].getFullName().length()>longest) {
                    longest=waitingRoom[i].getFullName().length();
                }
            }
            File storeQueue = new File("runDetails.txt");
            FileWriter writer = new FileWriter(storeQueue);
            writer.write("Denuwara Menike Train Terminal");
            writer.write("\r\n");
            writer.write("Train boarding summary as at " + date);
            writer.write("\r\n");
            if (direction.equals("ctb")) {
                writer.write("Train Direction: Colombo-Fort to Badulla ");
            }else{
                writer.write("Train Direction: Badulla to Colombo-Fort ");
            }
            writer.write("\r\n");
            writer.write("Station Name: "+stops[station]);
            writer.write("\r\n");writer.write("\r\n");
            writer.write("Passenger Details: ");
            writer.write("\r\n");
            writer.write("   | Name");
            for (int i=0;i<=longest-4;i++){ writer.write(" "); }
            writer.write(" | Ticket ID  | Seat Number  | Time In Queue |");
            writer.write("\r\n"); //  Move to next line
            writer.write("   |-----");
            for (int i=0;i<=longest-4;i++){ writer.write("-"); }
            writer.write("-|------------|--------------|---------------| ");
            writer.write("\r\n");
            for (Passenger passenger : waitingRoom) {
                if (passenger != null && passenger.getAdded()) {
                    writer.write("   | ");
                    writer.write(passenger.getFullName());
                    for (int nextI = 0; nextI <= longest - passenger.getFullName().length(); nextI++) {
                        writer.write(" ");
                    }
                    writer.write(" |   " + passenger.getId() + "    ");
                    writer.write(" |     " + passenger.getSeat() + "      ");
                    if (passenger.getSeconds()<10){
                        writer.write(" |      0" + passenger.getSeconds() + "       |");
                    }else{
                        writer.write(" |      " + passenger.getSeconds() + "       |");
                    }
                    writer.write("\r\n");
                }
            }
            writer.write("\r\n");writer.write("\r\n");
            writer.write("Processing Summary: ");
            writer.write("\r\n");
            writer.write("               | Shortest Stay in queue | Longest Stay in queue |" +
                    " Maximum length Attained | Total Time in Queue | Average Waiting Time in Queue |");
            writer.write("\r\n");
            writer.write("Train Queue 1  |           "+trainQueueOne.getShortestStay()+ "            |           "+
                    trainQueueOne.getLongestStay()+ "           |            "+trainQueueOne.getLength()+
                    "            |          "+trainQueueOne.getMaxStay()+ "          |             "+
                    trainQueueOne.getAverage()+"                 |");
            writer.write("\r\n");
            writer.write("Train Queue 2  |           "+trainQueueTwo.getShortestStay()+ "            |           "+
                    trainQueueTwo.getLongestStay()+ "           |            "+trainQueueTwo.getLength()+
                    "            |          "+trainQueueTwo.getMaxStay()+ "          |             "+
                    trainQueueTwo.getAverage()+"                 |");
            writer.close();
        }catch (IOException e) {
            System.out.println("Error loading data to file.");
        }
    }

}
