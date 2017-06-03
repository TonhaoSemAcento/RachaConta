package br.com.tonhaosemacento.rachaconta.models;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by TonhaoSemAcento on 29/05/2017.
 */

public class Contacts  implements Serializable {
    private String guid;
    private String name;
    private String email;

    public Contacts(){

    }

    public  Contacts(String name, String email){
        this.guid = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}