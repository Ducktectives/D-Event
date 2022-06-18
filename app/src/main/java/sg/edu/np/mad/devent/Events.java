package sg.edu.np.mad.devent;

public class Events{
    String Event_ID;
    String Event_Name;
    String Event_Location;
    String Event_Date;
    String Event_Description;
    String Event_UserID;
    String Event_Picture;
    boolean Bookmarked;

    public Events() {}


    public Events(String event_ID, String event_Name, String event_Location, String event_Date, String event_Description, String event_UserID, String event_Picture, boolean bookmarked) {
        Event_ID = event_ID;
        Event_Name = event_Name;
        Event_Location = event_Location;
        Event_Date = event_Date;
        Event_Description = event_Description;
        Event_UserID = event_UserID;
        Event_Picture = event_Picture;
        Bookmarked = bookmarked;
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

    public String getEvent_UserID() {
        return Event_UserID;
    }

    public void setEvent_UserID(String event_UserID) {
        Event_UserID = event_UserID;
    }

    public String getEvent_Picture() {
        return Event_Picture;
    }

    public void setEvent_Picture(String event_Picture) {
        Event_Picture = event_Picture;
    }

    public boolean isBookmarked() {
        return Bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        Bookmarked = bookmarked;
    }
}
