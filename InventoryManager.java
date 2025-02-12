import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryManager {
    private List<MenuItem> menuItems;

    public InventoryManager() {
        this.menuItems = new ArrayList<>();
    }

    public void addItem(MenuItem item) {
        menuItems.add(item);
    }

    public List<MenuItem> filterItems(String category) {
        return menuItems.stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<MenuItem> searchItems(String keyword) {
        return menuItems.stream()
                .filter(item -> item.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<MenuItem> sortItemsByPrice(boolean ascending) {
        return menuItems.stream()
                .sorted(ascending ? Comparator.comparingDouble(MenuItem::getPrice)
                        : Comparator.comparingDouble(MenuItem::getPrice).reversed())
                .collect(Collectors.toList());
    }

    public void printItems(List<MenuItem> items) {
        items.forEach(System.out::println);
    }
}
