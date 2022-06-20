package sg.edu.np.mad.devent;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

public class Profile implements Serializable {

    String Username;
    String Title;
    String Email;
    String Id;
    int Contactnum;
    int Eventsattended;
    int Saltvalue;
    String Hashedpassword;


    public Profile() {}

    public Profile(String id, String username, String title, String email,Integer contact, String password){
        Id = id;
        Username = username;
        Title = title;
        Contactnum = contact;
        Email = email;

        // Creating Salt Values
        Random rand = new Random();
        int lowerbound = 10000;
        int upperbound = 99999;
        int randomnum = rand.nextInt(upperbound);
        while (randomnum < lowerbound){
            randomnum = rand.nextInt(upperbound);
        }
        Saltvalue = randomnum;

        // Salting the Password
        String Saltpassword = Saltvalue + password;

        // Hashing the Salted password
        try
        {
            /* MessageDigest instance for MD5. */
            MessageDigest m = MessageDigest.getInstance("MD5");

            /* Add plain-text password bytes to digest using MD5 update() method. */
            m.update(Saltpassword.getBytes());

            /* Convert the hash value into bytes */
            byte[] bytes = m.digest();

            /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
            StringBuilder s = new StringBuilder();
            for (int i=0; i< bytes.length ;i++)
            {
                s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            /* Complete hashed password in hexadecimal format */
            Hashedpassword = s.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    // Hash the Password
    public static String HashPassword(int saltvalue, String pass){
        try
        {
            String Saltpassword = saltvalue + pass;
            /* MessageDigest instance for MD5. */
            MessageDigest m = MessageDigest.getInstance("MD5");

            /* Add plain-text password bytes to digest using MD5 update() method. */
            m.update(Saltpassword.getBytes());

            /* Convert the hash value into bytes */
            byte[] bytes = m.digest();

            /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
            StringBuilder s = new StringBuilder();
            for (int i=0; i< bytes.length ;i++)
            {
                s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            /* Complete hashed password in hexadecimal format */
            return s.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    // Check the password if it is equal
    public boolean CheckPassword(String hashedpassword, String Userhashedpass){
        if (hashedpassword == Userhashedpass){
            return true;
        }
        else {
            return false;
        }
    }









    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getContactnum() {
        return Contactnum;
    }

    public void setContactnum(int contactnum) {
        Contactnum = contactnum;
    }

    public int getEventsattended() {
        return Eventsattended;
    }

    public void setEventsattended(int eventsattended) {
        Eventsattended = eventsattended;
    }

    public int getSaltvalue() {
        return Saltvalue;
    }

    public void setSaltvalue(int saltvalue) {
        Saltvalue = saltvalue;
    }

    public String getHashedpassword() {
        return Hashedpassword;
    }

    public void setHashedpassword(String hashedpassword) {
        Hashedpassword = hashedpassword;
    }
}
