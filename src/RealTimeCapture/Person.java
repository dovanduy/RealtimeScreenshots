/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RealTimeCapture;

import javafx.beans.property.*;

/**
 *
 * @author vicotr
 */
public class Person {
    private final IntegerProperty sn = new SimpleIntegerProperty();

    private final StringProperty timestart = new SimpleStringProperty();

    private final StringProperty screenname = new SimpleStringProperty();

    private final StringProperty event = new SimpleStringProperty();

    private final StringProperty timeend = new SimpleStringProperty();
    
    private final StringProperty xcoor = new SimpleStringProperty();
    
    private final StringProperty ycoor = new SimpleStringProperty();
    
    private final StringProperty mobilenumber = new SimpleStringProperty();

    public int getSn() {
        return sn.get();
    }

    public void setSn(int value) {
        sn.set(value);
    }

    public IntegerProperty snProperty() {
        return sn;
    }

    public String getTimeEnd() {
        return timeend.get();
    }

    public void setTimeEnd(String value) {
        timeend.set(value);
    }

    public StringProperty timeendProperty() {
        return timeend;
    }

    public String getEvent() {
        return event.get();
    }

    public void setEvent(String value) {
        event.set(value);
    }

    public StringProperty eventProperty() {
        return event;
    }

    public String getTimeStart() {
        return timestart.get();
    }

    public void setTimeStart(String value) {
        timestart.set(value);
    }

    public StringProperty timestartProperty() {
        return timestart;
    }

    public String getScreenName() {
        return screenname.get();
    }

    public void setScreenName(String value) {
        screenname.set(value);
    }

    public StringProperty screennameProperty() {
        return screenname;
    }
    
    public String getXCoor() {
        return xcoor.get();
    }

    public void setXCoor(String value) {
        xcoor.set(value);
    }

    public StringProperty xcoorProperty() {
        return xcoor;
    }
    
    public String getYCoor() {
        return ycoor.get();
    }

    public void setYCoor(String value) {
        ycoor.set(value);
    }

    public StringProperty ycoorProperty() {
        return ycoor;
    }
    
    public String getMobileNumber() {
        return mobilenumber.get();
    }

    public void setMobileNumber(String value) {
        mobilenumber.set(value);
    }

    public StringProperty mobilenumberProperty() {
        return mobilenumber;
    }
}
