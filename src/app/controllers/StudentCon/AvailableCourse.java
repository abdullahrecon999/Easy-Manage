package app.controllers.StudentCon;

public class AvailableCourse{
    String c_name,c_code, crd_hr;

    public AvailableCourse(String c_name, String c_code, String crd_hr) {
        this.c_name = c_name;
        this.c_code = c_code;
        this.crd_hr = crd_hr;
    }

    public String getC_name() {
        return c_name;
    }

    public String getC_code() {
        return c_code;
    }

    public String getCrd_hr() {
        return crd_hr;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public void setC_code(String c_code) {
        this.c_code = c_code;
    }

    public void setCrd_hr(String crd_hr) {
        this.crd_hr = crd_hr;
    }
}
