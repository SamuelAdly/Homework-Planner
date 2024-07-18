import java.io.Serializable;
import java.util.Date;

class Homework implements Serializable {
    private String name;
    private Date dueDate;
    private long daysLeft;

    public Homework(String name, Date dueDate, long daysLeft) {
        this.name = name;
        this.dueDate = dueDate;
        this.daysLeft = daysLeft;
    }

    public String getName() {
        return name;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public long getDaysLeft() {
        return daysLeft;
    }
}