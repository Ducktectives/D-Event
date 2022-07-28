package sg.edu.np.mad.devent;

import android.os.Parcelable;

import com.google.type.DateTime;

import java.io.Serializable;
import java.util.List;

public class Events implements Serializable {
    String Event_ID;
    String Event_Name;
    String Event_Location;
    String Event_Date;
    String Event_Description;
    String Event_Detail;
    String Event_StartTime;
    String Event_EndTime;
    String Event_UserID;
    String Event_StorageReferenceID;
    Double Event_TicketPrice;
    List<String> EventTypes;
    boolean Bookmarked;

    public Events() {}




    public Events(String event_ID, String event_Name, String event_Location, String event_Date,
                  String event_Description, String event_Detail, String event_StartTime,
                  String event_EndTime, String event_UserID, String event_StorageReferenceID,
                  boolean bookmarked, Double event_TicketPrice,List<String> eventTypes)  {

        Event_ID = event_ID;
        Event_Name = event_Name;
        Event_Location = event_Location;
        Event_Date = event_Date;
        Event_Description = event_Description;
        Event_Detail = event_Detail;
        Event_StartTime = event_StartTime;
        Event_EndTime = event_EndTime;
        Event_UserID = event_UserID;
        Event_StorageReferenceID = event_StorageReferenceID;
        Bookmarked = bookmarked;
        Event_TicketPrice = event_TicketPrice;
        EventTypes = eventTypes;
    }

    public String getEvent_EndTime() {
        return Event_EndTime;
    }

    public void setEvent_EndTime(String event_EndTime) {
        Event_EndTime = event_EndTime;
    }

    public String getEvent_ID() {
        return Event_ID;
    }

    public void setEvent_ID(String event_ID) {
        Event_ID = event_ID;
    }

    public String getEvent_Name() {
        return Event_Name;
    }

    public void setEvent_Name(String event_Name) {
        Event_Name = event_Name;
    }

    public String getEvent_Location() {
        return Event_Location;
    }

    public void setEvent_Location(String event_Location) {
        Event_Location = event_Location;
    }

    public String getEvent_Date() {
        return Event_Date;
    }

    public void setEvent_Date(String event_Date) {
        Event_Date = event_Date;
    }

    public String getEvent_Description() {
        return Event_Description;
    }

    public void setEvent_Description(String event_Description) {
        Event_Description = event_Description;
    }

    public String getEvent_Detail() {
        return Event_Detail;
    }

    public void setEvent_Detail(String event_Detail) {
        Event_Detail = event_Detail;
    }

    public String getEvent_UserID() {
        return Event_UserID;
    }

    public String getEvent_StartTime() {
        return Event_StartTime;
    }

    public void setEvent_StartTime(String event_StartTime) {
        Event_StartTime = event_StartTime;
    }

    public void setEvent_UserID(String event_UserID) {
        Event_UserID = event_UserID;
    }

    public String getEvent_StorageReferenceID() {
        return Event_StorageReferenceID;
    }

    public void setEvent_StorageReferenceID(String event_StorageReferenceID) {
        Event_StorageReferenceID = event_StorageReferenceID;
    }

    public boolean isBookmarked() {
        return Bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        Bookmarked = bookmarked;
    }

    public Double getEvent_TicketPrice() {
        return Event_TicketPrice;
    }

    public void setEvent_TicketPrice(Double event_TicketPrice) {
        Event_TicketPrice = event_TicketPrice;
    }
    public List<String> getEventTypes() { return EventTypes; }
}
