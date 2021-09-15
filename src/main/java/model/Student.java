package model;

import exceptions.DataException;

public class Student {
    private int registration;
    private String studanteName;
    private int absences;
    private int grade_one;
    private int grade_two;
    private int grade_three;
    private String situation;
    private int grade_for_final_approval;

    public Student(int registration, String studanteName, int absences, int grade_one, int grade_two, int grade_three) {
        
        try {
            this.setRegistration(registration);
            this.setStudanteName(studanteName);
            this.setAbsences(absences);
            this.setGrade_one(grade_one);
            this.setGrade_two(grade_two);
            this.setGradle_three(grade_three);
            this.setGrade_for_final_approval(0);
        } catch (Exception dataException) {
            System.out.printf("{message:'the data entered for creating the user is invalid',data:{registration:%d,studanteName:%s,absences:%d,grade_one:%d,grade_two:%d,grade_three:%f,grade_for_final_approval:%s}}",
                registration,
                studanteName,
                absences,
                grade_one,
                grade_two,
                grade_three
            );
        }      
    }

    // Set Methods
    public void setRegistration(int registration) throws DataException {
        if (registration > 0) {
            this.registration = registration;
        } else {
            throw new DataException();
        }
    }

    public void setStudanteName(String studanteName) throws DataException {
        if (studanteName.replace(" ", "") != "" && studanteName != null) {
            this.studanteName = studanteName;
        } else {
            throw new DataException();
        }
    }

    public void setAbsences(int absences) throws DataException {
        if (absences >= 0) {
            this.absences = absences;
        } else {
            throw new DataException();
        }
    }

    public void setGrade_one(int grade_one) throws DataException {
        if (grade_one >= 0) {
            this.grade_one = grade_one;
        } else {
            throw new DataException();
        }
    }

    public void setGrade_two(int grade_two) throws DataException {
        if (grade_two >= 0) {
            this.grade_two = grade_two;
        } else {
            throw new DataException();
        }
    }

    public void setGradle_three(int grade_three) throws DataException {
        if (grade_three >= 0) {
            this.grade_three = grade_three;
        } else {
            throw new DataException();
        }
    }

    public void setSituation(String situation) throws DataException {
        if (situation.replace(" ", "") != "" && situation != null) {
            this.situation = situation;
        } else {
            throw new DataException();
        }
    }

    public void setGrade_for_final_approval(int grade_for_final_approval) throws DataException {
        if (grade_for_final_approval >= 0) {
            this.grade_for_final_approval = grade_for_final_approval;
        } else {
            throw new DataException();
        }
    }

    // Get Methods
    public int getRegistration() {
        return this.registration;
    }

    public String getStudanteName() {
        return this.studanteName;

    }

    public int getAbsences() {
        return this.absences;
    }

    public int getGrade_one() {
        return this.grade_one;
    }

    public int getGrade_two() {
        return this.grade_two;
    }

    public int getGradle_three() {
        return this.grade_three;

    }

    public String getSituation() {
        return this.situation;

    }

    public int getGrade_for_final_approval() {
        return this.grade_for_final_approval;
    }

}
