package Model;

import java.sql.Date;

public class Student {
    private String id;
    private String name;
    private Date dob;
    private String password;
    private String address;
    private String course;
    private String branch;
    private int current_cgpa;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public int getCurrent_cgpa() {
        return current_cgpa;
    }

    public void setCurrent_cgpa(int current_cgpa) {
        this.current_cgpa = current_cgpa;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dob=" + dob +
                ", address='" + address + '\'' +
                ", course='" + course + '\'' +
                ", branch='" + branch + '\'' +
                ", current_cgpa=" + current_cgpa +
                '}';
    }
}
