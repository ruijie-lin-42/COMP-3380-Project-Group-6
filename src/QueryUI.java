import dto.QueryDefinition;

import java.util.List;
import java.util.Scanner;

public class QueryUI {

    private static String quitStatement = "q";

    private final List<QueryDefinition> queries;
    private final QueryRepository repository;

    public QueryUI(List<QueryDefinition> queries, QueryRepository repository){
        this.queries = queries;
        this.repository = repository;
    }

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