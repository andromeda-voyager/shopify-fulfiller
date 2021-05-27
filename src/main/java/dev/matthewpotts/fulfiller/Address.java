package dev.matthewpotts.fulfiller;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("name")
    private String name;

    @SerializedName("address1")
    private String address1;

    @SerializedName("address2")
    private String address2;

    @SerializedName("company")
    private String company;

    @SerializedName("city")
    private String city;

    @SerializedName("zip")
    private String zip;

    @SerializedName("province")
    private String province;

    @SerializedName("country")
    private String country;


    public Address(String name){
        this.name = name;
        this.address1 = "123 Main St";
        this.city = "Town";
        this.zip = "052321";
        this.country = "United States";
    }
    String getName(){
        return name;
    }


    private boolean isValidAddressField(String str){
        return (str !=  null && !str.isEmpty());
    }

    /**
     *
     *
//     * @param  url  an absolute URL giving the base location of the image
//     * @param  name the location of the image, relative to the url argument
     * @return      the image at the specified URL
     * @see
     */
    public String toString(){
        StringBuilder address = new StringBuilder();

        address.append(name).append("\n");

        if(isValidAddressField(company)) {
            address.append(company).append("\n");
        }
        if(isValidAddressField(address1)) {
            address.append(address1).append("\n");
        }
        if(isValidAddressField(address2)) {
            address.append(address2).append("\n");
        }
        address.append(city).append(" ").append(province).append(" ").append(zip);

        if (!country.equals("United States")) {
            address.append("\n").append(country);
         }
        return address.toString();
    }


}
