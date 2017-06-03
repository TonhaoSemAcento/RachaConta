package br.com.tonhaosemacento.rachaconta.models;

import java.util.HashMap;

import static android.R.attr.description;

/**
 * Created by TonhaoSemAcento on 27/05/2017.
 */

public class User {

    private String fullName;
    private String photo;
    private String email;
    private HashMap<String,Object> timestampJoined;

    public User() {
    }

    /**
     * Use this constructor to create new User.
     * Takes user name, email and timestampJoined as params
     *
     * @param timestampJoined
     */
    public User(String cFullName, String cPhoneNo, String cEmail, HashMap<String, Object> timestampJoined) {
        this.fullName = cFullName;
        this.photo = cPhoneNo;
        this.email = cEmail;
        this.timestampJoined = timestampJoined;
    }


    public String getFullName() {
        return fullName;
    }

    public String getPhoto() {
        return photo;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    @Override
    public String toString() {
        return "User{" +
                "fullName='"+fullName+ '\'' +
                ",email='"+email+'\'' +
                '}';
    }
}
