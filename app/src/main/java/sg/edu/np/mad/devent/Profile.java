package sg.edu.np.mad.devent;

import java.util.Random;

public class Profile {
    String Username;
    String Title;
    int Id;
    int Contactnum;
    int Eventsattended;
    int Saltvalue;


    public Profile() {}

    public Profile(String username, String title, Integer contact, int id){
        Id = id;
        Username = username;
        Title = title;
        Contactnum = contact;
        Random rand = new Random();
        int lowerbound = 10000;
        int upperbound = 99999;
        int randomnum = rand.nextInt(upperbound);
        Saltvalue =
    }
}
