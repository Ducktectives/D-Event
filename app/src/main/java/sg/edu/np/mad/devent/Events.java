package sg.edu.np.mad.devent;

public class Events{
    String Event_Name;
    String Event_Location;
    String Event_Date;
    String Event_Description;
    String Event_UserID;
    int image;
    boolean Bookmarked;


    public Events(String Name,String Description, boolean Bookmark, int image){
        Event_Name = Name;
        Event_Description = Description;
        Bookmarked = Bookmark;
        this.image = image;
    }

    // both of these are used for display in homepage gridview
    public int getImage(){
        return image;
    }
    public String getName(){
        return this.Event_Name;
    }
}
