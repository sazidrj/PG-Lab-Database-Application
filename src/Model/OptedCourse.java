package Model;

public class OptedCourse {
    private String course_id;
    private String student_id;
    private String faculty_id;
    private float current_score;
    private String course_name;
    private int credit_obtained;
    private int sem;
    private int year;

    public String getCourse_name() {
        return course_name;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getFaculty_id() {
        return faculty_id;
    }

    public void setFaculty_id(String faculty_id) {
        this.faculty_id = faculty_id;
    }

    public float getCurrent_score() {
        return current_score;
    }

    public void setCurrent_score(float current_score) {
        this.current_score = current_score;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public int getCredit_obtained() {
        return credit_obtained;
    }

    public void setCredit_obtained(int credit_obtained) {
        this.credit_obtained = credit_obtained;
    }

    public int getSem() {
        return sem;
    }

    public void setSem(int sem) {
        this.sem = sem;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
