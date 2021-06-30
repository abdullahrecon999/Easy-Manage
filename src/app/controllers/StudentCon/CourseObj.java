package app.controllers.StudentCon;

public class CourseObj {
    String c_name,crdt_hr, c_code, c_teacher, c_dept;

    public CourseObj(String c_name, String crdt_hr, String c_code, String c_teacher, String c_dept) {
        this.c_name = c_name;
        this.crdt_hr = crdt_hr;
        this.c_code = c_code;
        this.c_teacher = c_teacher;
        this.c_dept = c_dept;
    }

    public String getC_teacher() {
        return c_teacher;
    }

    public String getC_dept() {
        return c_dept;
    }

    public String getC_name() {
        return c_name;
    }

    public String getCrdt_hr() {
        return crdt_hr;
    }

    public String getC_code() {
        return c_code;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public void setCrdt_hr(String crdt_hr) {
        this.crdt_hr = crdt_hr;
    }

    public void setC_code(String c_code) {
        this.c_code = c_code;
    }

    public void setC_teacher(String c_teacher) {
        this.c_teacher = c_teacher;
    }

    public void setC_dept(String c_dept) {
        this.c_dept = c_dept;
    }
}
