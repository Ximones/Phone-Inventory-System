import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Person {
    private static int peopleCount;

    protected String id;
    protected String name;
    protected String email;
    protected String birthDay;
    protected String phoneNum;

    public Person(){
        peopleCount = 0;
    }

    public Person(String id, String name, String email, String birthDay, String phoneNum) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthDay = birthDay;
        this.phoneNum = phoneNum;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public static int getPeopleCount() {
        return peopleCount;
    }

    public static void setPeopleCount(int peopleCount) {
        Person.peopleCount = peopleCount;
    }

    public Person[] createPersonArray(){
        Person[] people = new Person[20];
        try {
            BufferedReader reader = new BufferedReader(new FileReader("aux_files/person_txt/Person.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");

                if (data[0].equals("Staff")) {
                    people[peopleCount++] = new Staff(data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
                } else {
                    people[peopleCount++] = new Supplier(data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return people;
    }
}
