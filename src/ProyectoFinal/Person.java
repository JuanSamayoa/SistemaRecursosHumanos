package ProyectoFinal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class Person {

    int id;
    String name;
    static PreparedStatement ps;
    static ResultSet rs;

    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract String toString();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
