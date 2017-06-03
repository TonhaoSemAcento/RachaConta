package br.com.tonhaosemacento.rachaconta.models;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by TonhaoSemAcento on 28/05/2017.
 */

public class ExpenseItem implements Serializable {
    private String guid;
    private String description;
    private double value;
    private String payer;

    public ExpenseItem( String description, double value, String payer){
        this.guid = UUID.randomUUID().toString();

        this.description = description;
        this.value = value;
        this.payer = payer;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }
}
