package com.fireapps.whoisresponding;

import com.parse.ParseUser;

/**
 * Created by Austin on 3/31/2015.
 */
public class MemberObject extends ParseUser {

    public MemberObject(){}

    public String getName() {
        return getString("name");
    }

    public void setName(String value) {
        put("name", value);
    }

    public String getPosition() {
        return getString("position");
    }

    public void setPosition(String value) {
        put("position", value);
    }

    public String getDepartment() {
        return getString("department");
    }

    public void setDepartment(String value) {
        put("department", value);
    }


    public String getRespondingTo() {
        return getString("respondingTo");
    }

    public void setRespondingTo(String value) {
        put("respondingTo", value);
    }

    public String getPhoneNumber(){
        return getString("phone");
    }

    public void setPhoneNumber(String value){
        put("phone", value);
    }

    public String getLocation(){
        return getString("respondingFrom");
    }

    public void setLocation(String value){
        put("respondingFrom", value);
    }

}
