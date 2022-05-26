package sg.edu.np.mad.devent;
public class Profile {
    String Username;
    String Description;
    Boolean Followed;

    public Profile(String username, String description, boolean followed){
        Username = username;
        Description = description;
        Followed = followed;
    }
}
