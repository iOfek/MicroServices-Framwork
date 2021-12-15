package bgu.spl.mics.application.objects;

import javax.jws.WebParam.Mode;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private Model[] models;
   

    /**
     * Constructor for {@link Student}
     * @param name students name 
     * @param department student's department 
     * @param status {@link Degree} MSc||PhD
     * @param publications number of publications
     * @param papersRead number of papersRead
     */
    
    public Student(String name, String department, Degree status, int publications, int papersRead, Model[] models) {
        this.name = name;
        this.department = department;
        this.status = status;
        this.publications = publications;
        this.papersRead = papersRead;
        this.models= models;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Degree getStatus() {
        return status;
    }

    public void setStatus(Degree status) {
        this.status = status;
    }

    public int getPublications() {
        return publications;
    }

    public void addPublication() {
        this.publications +=1;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public void addPapersRead(int papersRead) {
        this.papersRead += papersRead;
    }
    public Model[] getModels() {
        return models;
    }

    public void setModels(Model[] models) {
        this.models = models;
    }

}
