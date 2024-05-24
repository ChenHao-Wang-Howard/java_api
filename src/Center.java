public class Center {
    private int id;
    private String centerName;
    private String url;
    private int districtCode;
    private String address;
    private String phone;

    public Center(int id, String centerName, String url, int districtCode, String address, String phone) {
        this.id = id;
        this.centerName = centerName;
        this.url = url;
        this.districtCode = districtCode;
        this.address = address;
        this.phone = phone;
    }
    
    public Center(String centerName, String url, int districtCode, String address, String phone) {
        this(-1, centerName, url, districtCode, address, phone);
    }

    public int getId() {
        return id;
    }

    public String getCenterName() {
        return centerName;
    }

    public String getUrl() {
        return url;
    }

    public int getDistrictCode() {
        return districtCode;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    
}
