package com.cube9.gmarket.Account.ModelClass;

public class AllAddressPojo {
    String address_id,fname,lname,telephone,street_name,city,zip,state,company,country,fax,is_billing,is_shipping,street1,street2,checked;
    String intent_from,state_id,country_code;

    public AllAddressPojo(String address_id, String fname, String lname, String telephone, String street_name, String city, String zip, String state, String company, String country, String fax, String is_billing, String is_shipping, String intent_from,String street1,String street2,String checked,String state_id,String country_code) {

        this.address_id = address_id;
        this.fname = fname;
        this.lname = lname;
        this.telephone = telephone;
        this.street_name = street_name;
        this.city = city;
        this.zip = zip;
        this.state = state;
        this.company = company;
        this.country = country;
        this.fax = fax;
        this.is_billing = is_billing;
        this.is_shipping = is_shipping;
        this.intent_from=intent_from;
        this.street1=street1;
        this.street2=street2;
        this.checked=checked;
        this.state_id=state_id;
        this.country_code=country_code;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getIntent_from() {
        return intent_from;
    }

    public void setIntent_from(String intent_from) {
        this.intent_from = intent_from;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getStreet_name() {
        return street_name;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getIs_billing() {
        return is_billing;
    }

    public void setIs_billing(String is_billing) {
        this.is_billing = is_billing;
    }

    public String getIs_shipping() {
        return is_shipping;
    }

    public void setIs_shipping(String is_shipping) {
        this.is_shipping = is_shipping;
    }
}
