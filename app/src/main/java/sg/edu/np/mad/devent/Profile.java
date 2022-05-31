package sg.edu.np.mad.devent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Profile {
    String Username;
    String Title;
    int Id;
    int Contactnum;
    int Eventsattended;
    int Saltvalue;
    String Saltpassword;
    String Hashedpassword;


    public Profile() {}

    public Profile(String username, String title, Integer contact, String password, int id){
        Id = id;
        Username = username;
        Title = title;
        Contactnum = contact;
        Random rand = new Random();
        int lowerbound = 10000;
        int upperbound = 99999;
        int randomnum = rand.nextInt(upperbound);
        while (randomnum < lowerbound){
            randomnum = rand.nextInt(upperbound);
        }
        Saltvalue = randomnum;
        Saltpassword = Saltvalue + password;
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

        /* Display the unencrypted and encrypted passwords. */
        System.out.println("Plain-text password: " + password);
        System.out.println("Encrypted password using MD5: " + Hashedpassword);
    }

}
