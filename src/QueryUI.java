import java.util.Scanner;

public class QueryUI {

    private static String quitStatement = "q";

    public QueryUI(){}

    public void runConsole(){
        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        String input;
        while(!quit){
            System.out.println("enter query # or q to quit");
            input = scanner.nextLine();
            if(quitStatement.equals(input)){
                quit = true;
            }
        }
    }

}