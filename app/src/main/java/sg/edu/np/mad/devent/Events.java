package sg.edu.np.mad.devent;

public class Events{
    String Event_Name;
    String Event_Location;
    String Event_Date;
    String Event_Description;
    String Event_UserID;
    boolean Bookmarked;


    public Events(String Name,String Description, boolean Bookmark){
        Event_Name = Name;
        Event_Description = Description;
        Bookmarked = Bookmark;
    }
}
