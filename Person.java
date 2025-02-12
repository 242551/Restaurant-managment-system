public abstract class Person {
    private String name;
    private String contact;

    public Person(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public abstract void displayDetails();
}