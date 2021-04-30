package sample;

import java.util.ArrayList;
import java.util.Arrays;

public class PassengerQueue {

    private Passenger[] queueArray = new Passenger[10];
    private ArrayList<Passenger> boarded = new ArrayList<>();
    private int first=0;
    private int last=0;
    private int maxStayInQueue;
    private int maxLength;

    public void add (Passenger next){ //Setter
        if (isFull()){
            System.out.println("Train queue is full");
        }else{
            System.out.println("Added To Queue");  //  testing
            queueArray[last] = next;
            last++;
            maxLength++;
            String[] tempSeatNums = new String[10];
        }
    }

    public void board (){
        Passenger current = queueArray[first];
        if (isEmpty()) {
            System.out.println("Train queue is empty. Nothing to board");
        }else{
            queueArray[first]=null;
            for (int numOne = 0; numOne < 9; numOne++) {  // Using classical bubble sort
                if (queueArray[numOne+1]!=null) {
                    queueArray[numOne] = queueArray[numOne + 1];
                    queueArray[numOne + 1]=null;
                }
            }
            first++;
            maxLength--;
        }
        boarded.add(current);
    }

    public Passenger remove(){  //Setter
        Passenger current = queueArray[first];
        if (isEmpty()) {
            System.out.println("Train queue is empty");
        }else{
            for (int numOne = 1; numOne < 9; numOne++) {  // Using classical bubble sort
                if (queueArray[numOne+1]!=null) {
                    queueArray[numOne] = queueArray[numOne + 1];
                    queueArray[numOne + 1]=null;
                }
            }
            first++;
            maxLength--;
        }
        return current;
    }

    public String accessName(int index){
        return queueArray[index].getFullName();
    }

    public boolean delete(String name){
        for (int traverse=0;traverse<10;traverse++){
            if (queueArray[traverse]!=null) {
                if (queueArray[traverse].getName().equals(name) || queueArray[traverse].getSeat().equals(name)) {
                    System.out.println(Arrays.toString(queueArray));
                    queueArray[traverse].setAdded(false);
                    for (int numOne = traverse; numOne < 9; numOne++) {  // Using classical bubble sort
                        if (queueArray[numOne+1]!=null) {
                            queueArray[numOne] = queueArray[numOne + 1];
                            queueArray[numOne + 1]=null;
                        }
                    }
                    System.out.println(Arrays.toString(queueArray));
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEmpty() {  //Getter
        return last == first;
    }

    public boolean isFull() {  //Getter
        return maxLength == 10;
    }

    public void display() {  //Getter
        if (isEmpty()) {
            System.out.println("Empty Train Queue.");
        }else{
            System.out.println("Items:  ");
            for (int i=0;i<maxLength;i++){
                System.out.println(queueArray[i].getName());
            }
        }
    }

    public int getLength() {  //Getter
        return maxLength;
    }

    public int getMaxStay() {  //Getter
        return maxStayInQueue;
    }

    public Object getQueue() {
        return queueArray;
    }

    public void setQueue(Passenger[] temp) {
        queueArray = temp;
        int count=0;
        for (Passenger passenger : queueArray) {
            if (passenger != null) {
                count++;
            }
        }
        maxLength=count;
        last=maxLength-1;
    }

    public int setTime(int time){
        maxStayInQueue=maxStayInQueue+time;
        queueArray[last-1].setSecondsInQueue(maxStayInQueue);
        return maxStayInQueue;
    }

    public int getSeconds(){
        System.out.println( queueArray[first].getSeconds());
        return queueArray[first].getSeconds();
    }

    public int getShortestStay(){return queueArray[0].getSeconds();}

    public int getLongestStay(){return queueArray[last-1].getSeconds();}

    public int getAverage(){
        if (isEmpty()){
            return 0;
        }else{
            return maxStayInQueue/maxLength;
        }
    }

    public void removeSecond(){

    }

}