package Model;

public class CourseOffered {
      private String course_id;
      private Faculty faculty;
      private float cgpa_constraint;
      private int semester;
      private int academic_year;
      private int credit;

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public float getCgpa_constraint() {
        return cgpa_constraint;
    }

    public void setCgpa_constraint(float cgpa_constraint) {
        this.cgpa_constraint = cgpa_constraint;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(int academic_year) {
        this.academic_year = academic_year;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return "CourseOffered{" +
                "course_id='" + course_id + '\'' +
                ", faculty=" + faculty.getName() +
                ", cgpa_constraint=" + cgpa_constraint +
                ", semester=" + semester +
                ", academic_year=" + academic_year +
                '}';
    }
}
