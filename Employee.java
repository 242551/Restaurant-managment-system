public class Employee extends Person {
    private String position;

    public Employee(String name, String contact, String position) {
        super(name, contact);
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public void displayDetails() {
        System.out.println("Employee Name: " + getName());
        System.out.println("Contact: " + getContact());
        System.out.println("Position: " + position);
    }
}