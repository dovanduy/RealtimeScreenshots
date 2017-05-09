/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RealTimeCapture;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author victor
 */
public class AdbInterfaceController {

    /**
     * For the data transformation
     */
    public static DataFormat dataFormat = new DataFormat("mydata");
    public static int MAXNUMBER = 100;

    @FXML
    private TableColumn<Person, String> time_start;

    @FXML
    private TableColumn<Person, String> time_end;

    @FXML
    private TableColumn<Person, String> screenshot_name;

    @FXML
    private TableColumn<Person, String> dialog_event;

    @FXML
    private TableColumn<Person, String> x_coor;

    @FXML
    private TableColumn<Person, String> y_coor;

    @FXML
    private TableColumn<Person, String> mobile_number;

    @FXML
    private TableColumn<Person, String> snCol;

    @FXML
    private TableView<Person> tableView;

    @FXML
    private TextField mobileText;
    
    @FXML
    private AnchorPane ap;

    public int filter = 0;
    ObservableList<Integer> selectedIndexes = FXCollections.observableArrayList();
    public Process process;
    public String[] DeviceList = new String[MAXNUMBER];
    public int recordNumber = 0;
    public StringBuffer returnValue;
    public int count = 0;
    public DateFormat dateformat = new SimpleDateFormat("YYYYMMDDHHMMSS");
    public String screenName = dateformat.format(new Date());
    public ADBThread adbThread;
    public FileWriter fw;
    public BufferedWriter bw;
    public String DirectoryPath = "";

    @FXML
    void initialize() {
        assert time_start != null : "fx:id=\"time_start\" was not injected: check your FXML file 'TableViewDataFXML.fxml'.";
        assert time_end != null : "fx:id=\"time_end\" was not injected: check your FXML file 'TableViewDataFXML.fxml'.";
        assert screenshot_name != null : "fx:id=\"screenshot_name\" was not injected: check your FXML file 'TableViewDataFXML.fxml'.";
        assert dialog_event != null : "fx:id=\"dialog_event\" was not injected: check your FXML file 'TableViewDataFXML.fxml'.";
        assert snCol != null : "fx:id=\"snCol\" was not injected: check your FXML file 'TableViewDataFXML.fxml'.";
        assert tableView != null : "fx:id=\"tableView\" was not injected: check your FXML file 'TableViewDataFXML.fxml'.";

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // set cell value factories
        setCellValueFactories();
        try {

            String deviceName = runADBCommand("adb devices");
            BufferedReader bufReader = new BufferedReader(new StringReader(deviceName));
            String line = null;

            while ((line = bufReader.readLine()) != null) {
                System.out.println(line);
                if (!line.contains("emulator") && !line.contains("List") && !line.isEmpty() && line.contains("device")) {
                    DeviceList[count] = line.replace("device", " ").trim();
                    runADBCommand("adb -s " + DeviceList[count] + " shell mkdir /sdcard/ADB/");
                    //runADBCommand("adb -s " + DeviceList[count] + " shell mkdir /storage/emulated/0/PPTEST");
                    System.out.println(count + DeviceList[count]);
                    count++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AdbInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableView.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
            @Override
            public void onChanged(Change<? extends Integer> change) {
                selectedIndexes.setAll(change.getList());
            }
        });

        try {
            String filename = dateformat.format(new Date());
            System.out.println("Karvy");
            fw = new FileWriter("./Data/" + filename + ".txt");
            bw = new BufferedWriter(fw);
            //bw.write("|| --Sn-- || -Screenshot Name- || ---TimeStamp--- || -Dialog Event- || --X-- || --Y-- || -Mobile Number- ||");
            bw.write("Karvy");
            bw.newLine();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AdbInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AdbInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setCellValueFactories() {
        snCol.setCellValueFactory(new PropertyValueFactory("sn"));
        screenshot_name.setCellValueFactory(new PropertyValueFactory("screenname"));
        dialog_event.setCellValueFactory(new PropertyValueFactory("event"));
        time_start.setCellValueFactory(new PropertyValueFactory("timestart"));
        x_coor.setCellValueFactory(new PropertyValueFactory("xcoor"));
        y_coor.setCellValueFactory(new PropertyValueFactory("ycoor"));
        mobile_number.setCellValueFactory(new PropertyValueFactory("mobilenumber"));
    }

    public String runADBCommand(String adbCommand) throws IOException {
        System.out.println("Running given command= " + adbCommand + "$$$");
        returnValue = new StringBuffer();
        String line;

        InputStream inStream = null;
        try {
            System.out.println("adbCommand = " + adbCommand);
            process = Runtime.getRuntime().exec("./DLL/" + adbCommand);

            inStream = process.getInputStream();
            BufferedReader brCleanUp = new BufferedReader(
                    new InputStreamReader(inStream));
            while ((line = brCleanUp.readLine()) != null) {
                if (!line.equals("")) {
                    System.out.println("After exec");
                    System.out.println("Line=" + line);
                }

                returnValue.append(line).append("\n");
            }

            brCleanUp.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
        return returnValue.toString();
    }
    
    public String copyToDataCommand(String adbCommand) throws IOException {
        System.out.println("copyToDataCommand Running given command= " + adbCommand + "$$$");
        returnValue = new StringBuffer();
        String line;

        InputStream inStream = null;
        try {
            System.out.println("adbCommand = " + adbCommand);
            process = Runtime.getRuntime().exec(adbCommand);

            inStream = process.getInputStream();
            BufferedReader brCleanUp = new BufferedReader(
                    new InputStreamReader(inStream));
            
           
            while ((line = brCleanUp.readLine()) != null) {
                if (!line.equals("")) {
                    System.out.println("After exec");
                   
                    System.out.println("copyToDataCommandLine=" + line);
                }

                returnValue.append(line).append("\n");
            }

            brCleanUp.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
        return returnValue.toString();
    }

    public void RunButton() {
        for (int deviceNumber = 0; deviceNumber < count; deviceNumber++) {
            System.out.println("");
            adbThread = new ADBThread(deviceNumber);
            adbThread.start();
        }
    }

    public int HextoInt(String str) {
        int i = Integer.parseInt(str.trim(), 16);
        return Integer.parseInt(str.trim(), 16);
    }

    public void onStop() throws Throwable {
        for (int i = 0; i < count; i++) {
            runADBCommand("adb -s " + DeviceList[count] + " shell ps");
        }
        
        //copyToDataCommand("adb pull /sdcard/ADB \"./images/");
        
        System.out.println("onStop");
        adbThread.stop();
        process.destroy();
        finalize();
        bw.close();
    }

    public class ADBThread extends Thread {

        int devNum = 0;

        public ADBThread(int number) {
            devNum = number;
        }

        @Override
        public void run() {
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$" + DeviceList[devNum]);
                //String lineCommand = "adb -s " + DeviceList[devNum] + " shell getevent  -lt /dev/input/event3";
                
                String lineCommand = "adb -s " + DeviceList[devNum] + " shell getevent";
                System.out.println("lineCommand:-"+lineCommand);
                process = Runtime.getRuntime().exec(lineCommand);

                //process.waitFor();
                InputStream inStream = process.getInputStream();
                BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(inStream));
                //System.out.println(brCleanUp.readLine());
                String line = null;
                String prev="";
                String curr="";
                String event ="";
                while ((line = brCleanUp.readLine()) != null) {
                    
                    if(!line.equals(""))
                    {
                        curr=line;
                        if((line.contains("ts") || line.contains("mtk-tpd") || line.contains("touchscreen")) && ! line.contains("mtk-tpd-kpd"))
                        {
                            System.out.println("Event1:-"+prev);
                            String spltstr[]=prev.split(":");
                            event=spltstr[1];
                            break;
                        }
                         
                        prev=curr;
                    }
                    
                    
                }
                
                System.out.println("Event2:-"+event);
                
                lineCommand = "adb -s " + DeviceList[devNum] + " shell getevent -lt "+event;
                System.out.println("lineCommand2:-"+lineCommand);
                process = Runtime.getRuntime().exec(lineCommand);

                //process.waitFor();
                inStream = process.getInputStream();
                brCleanUp = new BufferedReader(new InputStreamReader(inStream));
                
                line = null;
                ObservableList<Person> persons = FXCollections.observableArrayList();
                int SNumber = 0;

                while ((line = brCleanUp.readLine()) != null) {
                    if (!line.equals("")) {
                        Person p = new Person();
                        String upXCoor = "", upYCoor = "";
                        //if (line.contains("BTN_TOOL_FINGER") && line.split("BTN_TOOL_FINGER")[1].trim().equals("DOWN")) {
                        if (line.contains("BTN_TOUCH") && line.split("BTN_TOUCH")[1].trim().equals("DOWN")) {
                            line = brCleanUp.readLine();
                            line = brCleanUp.readLine();
                            System.out.println(line);

                            while (!(line = brCleanUp.readLine()).contains("BTN_TOUCH")) {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        filter++;
                                        screenName = dateformat.format(new Date());
                                        try {
                                            if (filter == 20) {
                                                runADBCommand("adb -s " + DeviceList[devNum] + " shell screencap /sdcard/ADB/" + screenName + ".png");
                                                runADBCommand("adb pull /sdcard/ADB/"+screenName + ".png \"./images/");
                                                runADBCommand("adb shell rm /sdcard/" + screenName + ".png");
                                                filter = 0;
                                            }
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException ex) {
                                                Logger.getLogger(AdbInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        } catch (IOException ex) {
                                            Logger.getLogger(AdbInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }).start();
                                p.setScreenName(screenName);
                                if (line.contains("ABS_MT_POSITION_X")) {

                                    p.setXCoor(HextoInt(line.split("ABS_MT_POSITION_X")[1].trim()) + "");
                                }
                                if (line.contains("ABS_MT_POSITION_Y")) {

                                    p.setYCoor(HextoInt(line.split("ABS_MT_POSITION_Y")[1].trim()) + "");
                                    p.setSn(SNumber++);
                                    p.setTimeStart(line.split("]")[0].replace("[", " ").trim());
                                    p.setEvent("SCREEN_DOWN");
                                    p.setMobileNumber(mobileText.getText().toString());
                                    persons.add(p);
                                    tableView.setItems(persons);
                                    upXCoor = p.getXCoor();
                                    upYCoor = p.getYCoor();
                                    bw.write("|| -- " + p.getSn() + "-- || -" + p.getScreenName() + "- || -" + p.getTimeStart()
                                            + "- || -" + p.getEvent() + "- || -" + p.getXCoor() + "- || -" + p.getYCoor() + "- || -" + p.getMobileNumber() + "- ||");
                                    bw.newLine();
                                    p = new Person();
                                }

                            }
                            if ((line.split("BTN_TOUCH")[1].trim().equals("UP"))) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        screenName = dateformat.format(new Date());
                                        try {
                                            runADBCommand("adb -s " + DeviceList[devNum] + " shell screencap /sdcard/ADB/" + screenName + ".png");
                                            runADBCommand("adb pull /sdcard/ADB/"+screenName + ".png \"./images/");
                                            runADBCommand("adb shell rm /sdcard/" + screenName + ".png");
                                        } catch (IOException ex) {
                                            Logger.getLogger(AdbInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                }).start();

                                p.setScreenName(screenName);
                                p.setSn(SNumber++);

                                p.setTimeStart(line.split("]")[0].replace("[", " ").trim());
                                p.setEvent("SCREEN_UP");
                                p.setXCoor(upXCoor);
                                p.setYCoor(upYCoor);
                                persons.add(p);
                                tableView.setItems(persons);
                                bw.write("|| -- " + p.getSn() + "-- || -" + p.getScreenName() + "- || -" + p.getEvent()
                                        + "- || -" + p.getEvent() + "- || -" + p.getXCoor() + "- || -" + p.getYCoor() + "- || -" + p.getMobileNumber() + "- ||");
                                bw.newLine();
                            }

                        }
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(AdbInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(AdbInterfaceController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
