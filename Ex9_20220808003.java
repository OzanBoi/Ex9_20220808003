import java.util.Arrays;

public class Ex9_20220808003 {
    public static void main(String[] args) {
        Paper paper=new Paper("test");
        Paper paper2=new Paper("test2");
        Matroschka<Paper> matrosch=new Matroschka<Paper>(paper);
        System.out.println(matrosch);
        System.out.println();
    }
}

interface Sellable{
String getName();
double getPrice();
}

interface Package<T>{
        T extract();
        boolean pack(T item);
        boolean isEmpty();
        double getPriority();
    }

interface Wrappable extends Sellable{
}

interface Common<T>{
    boolean isEmpty();
    T peek();
    int size();
}

interface Stack<T> extends Common<T>{
    boolean push(T item);
    T pop();
}

interface Node<T> {
    int DEFAULT_CAPACITY = 2;
    void setNext(T item);
    T getNext();
    double getPriority();
}

interface PriorityQueue<T> extends Common<T>{
    int FLEET_CAPACITY = 2;
    boolean enqueue(T item);
    T dequeue();
}

abstract class Product implements Sellable{
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
    public String toString() {
        return getClass().getSimpleName() + " (" + name + ", " + price + ")";
    }
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
}

class Mirror extends Product{
    private int width;
    private int height;
    public Mirror(int width, int height) {
        super("mirror", 2);
        this.width = width;
        this.height = height;
    }
    public int getArea() {
        return width * height;
    }
    public <T> T reflect(T item) {
        System.out.println("Reflecting item: " + item);
        return item;
    }
}

class Paper extends Product implements Wrappable{
    private String note;
    public Paper(String note){
        super("A4",3);
        this.note = note;
    }
    public String getNote(){
        return note;
    }
    public void setNote(String note){
        this.note = note;
    }
}

class Matroschka<T extends Wrappable> extends Product implements Wrappable,Package<T>{
private T item;

    public Matroschka(T item) {
        super("Doll", item.getPrice()+5);
        this.item = item;
    }
    public String toString() {
        return super.toString() + "{"  + item + "}";
    }

    public T extract() {
        T content = item;
        if(isEmpty()) {
           return null;
        }
        else{
            item = null;
            return content;
        }
    }

    public boolean pack(T item) {
        if(isEmpty()){
            this.item=item;
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isEmpty() {
        return item == null;
    }

    @Override
    public double getPriority() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

class Box<T extends Sellable> implements Package<T>{
    private T item;
    private boolean seal;
    private int distanceToAdress;

    public Box() {
        this.item = null;
        this.seal = false;
        this.distanceToAdress = distanceToAdress;
    }

    public Box(T item, int distanceToAdress) {
        this.item = item;
        this.seal = true;
        this.distanceToAdress = distanceToAdress;
    }

    public T extract() {
        this.seal = false;
        T content = item;
        if(isEmpty()) {
            return null;
        }
        else{
            item = null;
            return content;
        }
    }

    public boolean pack(T item) {
        if(isEmpty()){
            this.item=item;
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isEmpty() {
        return item == null;
    }

    public String toString() {
        return getClass().getSimpleName() + " {" + item + "}Seal: " + seal;
    }

    @Override
    public double getPriority() {
        return distanceToAdress/item.getPrice();
    }
    }

class Container implements Stack<Box>, Node<Container>, Comparable<Container>{
    private Box<?>[] boxes;
    private int top;
    private int size;
    private double priority;
    private Container next;
    public Container(){
        boxes=new Box[DEFAULT_CAPACITY];
        this.top = -1;
        this.next = null;
        this.priority = 0.0;
    }

    public String toString() {
        return "Container with priority: " + priority;
    }

    public boolean isEmpty() {
        return size==0;
    }

    public Box peek() {
        return boxes[top];
    }

    public int size() {
        return size;
    }

    public boolean push(Box item) {
        int capacity=DEFAULT_CAPACITY;
        if(capacity>size) {
            top += 1;
            size+=1;
            boxes[top] = item;
            return true;
        }else{
            return false;
        }
    }

    public Box<?> pop() {
        Box<?> temp=boxes[top];
        boxes[top]=null;
        top-=1;
        size-=1;
        return temp;
    }

    public void setNext(Container item) {
        next = item;
    }

    public Container getNext() {
        return next;
    }

    public double getPriority() {
        return priority;
    }

    public int compareTo(Container o) {
        if(priority<o.priority){
            return 1;
        }
        else if(priority>o.priority){
            return -1;
        }
        else{
            return 0;
        }
    }
}

class CargoFleet implements PriorityQueue<Container> {
    private Container head;
    private int size;

    public CargoFleet() {
        this.head = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Container peek() {
        return head;
    }

    public int size() {
        return size;
    }

    public boolean enqueue(Container item) {
        if (item == null) {
            return false;
        }
        if (head == null) {
            head = item;
        } else {
            Container current = head;
            Container previous = null;
            while (current != null && item.compareTo(current) >= 0) {
                previous = current;
                current = current.getNext();
            }
            if (previous == null) {
                item.setNext(head);
                head = item;
            } else {
                previous.setNext(item);
                item.setNext(current);
            }
        }
        size++;
        return true;
    }

    public Container dequeue() {
        Container content = head;
        head = head.getNext();
        size--;
        return content;
    }
}

 class CargoCompany {
        private Container stack;
        private CargoFleet queue;

        public CargoCompany() {
            stack = new Container();
            queue = new CargoFleet();
        }

        public <T extends Box<?>> void add(T box) {
            if (stack.push(box)) {
            } else {
                if (queue.enqueue(stack)) {
                    stack = new Container();
                    add(box);
                } else {
                    ship(queue);
                }
            }
        }

        private void ship(CargoFleet fleet) {
            while (!fleet.isEmpty()) {
                empty(fleet.dequeue());
            }
        }

        private void empty(Container container) {
            while (!container.isEmpty()) {
                Box<?> popped = container.pop();
                System.out.println(deliver(popped));
            }
        }

        private <T extends Box<?>> Sellable deliver(T box) {
            return box.extract();
        }
    }