import dto.QueryDefinition;
import dto.QueryParameter;
import exceptions.InvalidInputException;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

public class QueryUI {

    private static final String QUIT_INPUT = "q";
    private static final String INFO_INPUT = "i";
    private static final String HIDE_INSTRUCTION_INPUT = "h";
    private static final int QUERY_NUM_LOWER_BOUND = 1;

    private final List<QueryDefinition> queries;
    private final QueryRepository repository;
    private boolean instructionsVisible;

    public QueryUI(List<QueryDefinition> queries, QueryRepository repository){
        this.queries = queries;
        this.repository = repository;
        this.instructionsVisible = true;
    }

    /**
     * Runs the console UI.<br/>
     * The console UI basically have four main pages: main menu, query details, query execution, and result display:<br/>
     * <b>Main Menu</b>: contains the list of queries the user can choose from and some other operations.<br/>
     * <b>Query Details</b>: contains the detailed information of a query and operations available.<br/>
     * <b>Query Execution</b>: asks the user to input the parameters required.<br/>
     * <b>Result Display</b>: contains the result of the query.
     */
    public void runConsole(){
        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        String input;

        // print title & queries & instructions
        printInfo();

        while(!quit){
            // obtain input
            System.out.print("> ");
            input = scanner.nextLine().trim();

            // if print the query lists again
            if(INFO_INPUT.equals(input)){
                printInfo();
            }
            // if the user wants to make instructions invisible
            else if(HIDE_INSTRUCTION_INPUT.equals(input)){
                this.instructionsVisible = !this.instructionsVisible;
            }
            // if the user wants to quit
            else if(QUIT_INPUT.equals(input)){
                quit = true;
            }
            // empty input
            else if(input.isBlank()){
                // if the input is empty then we don't want to print a bunch of error messages or instructions on what to do
                continue;
            }
            else{

                // =================== MAIN MENU ===================

                // parse the input and validate it
                int queryNum = QUERY_NUM_LOWER_BOUND - 1;
                try{
                    queryNum = parseInputToInt(input, (queryId) -> {
                        // make sure the parsed query id is a positive number
                        if(queryId <= 0)
                            return false;

                        // make sure the parsed query id is an option
                        boolean valid = false;
                        for (QueryDefinition query : queries){
                            if (query.id() == queryId) {
                                valid = true;
                                break;
                            }
                        }
                        return valid;
                    }, "input must be a valid query number");
                }catch (InvalidInputException e){
                    System.out.println(e.getMessage());
                }

                // if the parsed query number lower than the lower bound, then we must encounter some issue when validating it
                // so if that happens, will skip these steps
                // in another word, if the code here executes, then the query number inputted must be a valid option
                if(queryNum >= QUERY_NUM_LOWER_BOUND){

                    // =================== QUERY DETAILS PAGE ===================

                    // get the query definition
                    QueryDefinition query = null;
                    for (int i = 0; i < queries.size() && query == null; i++) {
                        if(queries.get(i).id() == queryNum){
                            query = queries.get(i);
                        }
                    }

                    // print the query's detail information & instructions
                    printQueryDetails(query);

                    input = scanner.nextLine().trim();

                    // parse the operation chosen
                    // has coupling with the printQueryDetails function since we want to limit input based on what is printed (for now, can only be either 1 or 2)
                    // this isn't software engineering course, good enough for now
                    int operation = 0;
                    try{
                        operation = parseInputToInt(input, (operationNum) -> (operationNum == 1 || operationNum == 2), "");
                    }catch(InvalidInputException e){
                        System.out.println("You have been redirected to the main menu. To print the main menu again, enter " + INFO_INPUT + ".");
                    }

                    // if the operation is neither 1 nor 2, then it's the case where the user wants to go back to the main menu
                    if(operation == 1){

                        // =================== QUERY EXECUTION PAGE ===================

                        // ask user to enter parameters required
                        List<QueryParameter> parameters = new LinkedList<>();

                        // TODO: 让用户输入需要的参数，然后填进列表传给repository
                        System.out.println("就当执行了吧");

                    }else if(operation == 2){
                        // print sql
                        printSQL(query);
                    }

                }

            }
        }
    }

    /**
     * Prints the title of the application, available queries (with short names described), and instructions.
     */
    private void printInfo(){
        // 55 characters wide
        System.out.println();
        System.out.println("=======================================================");
        System.out.println("                    MOVIE ANALYTICS                    ");
        System.out.println("=======================================================");
        System.out.println();
        System.out.println("Available Queries:");
        System.out.println();
        for (QueryDefinition query : queries){
            System.out.println("[" + query.id() + "] " + query.name());
        }
        System.out.println();
        if(instructionsVisible){
            System.out.println("-------------------------------------------------------");
            System.out.println("Enter any query number to view details.");
            System.out.println("Enter " + INFO_INPUT + " to view queries' info again.");
            System.out.println("Enter " + HIDE_INSTRUCTION_INPUT + " to hide the instructions, entering it again will enable the display.");
            System.out.println("Enter " + QUIT_INPUT + " to quit.");
        }
    }

    /**
     * Prints the detailed information of the given query, and instructions.
     * @param query the query to be printed
     */
    private void printQueryDetails(QueryDefinition query){
        // 55 characters wide
        System.out.println();
        System.out.println("=======================================================");
        System.out.println("                     QUERY DETAILS                     ");
        System.out.println("=======================================================");
        System.out.println();
        System.out.println("Query: " + query.name());
        System.out.println();
        System.out.println("Description:\n" + query.description());
        System.out.println();
        System.out.println("-------------------------------------------------------");
        System.out.println("Options:");
        System.out.println("[1] Execute Query");
        System.out.println("[2] View SQL");
        if(instructionsVisible){
            System.out.println();
            System.out.println("Enter number of the operation you wish to perform.");
            System.out.println("Enter any other key(s) to return to the previous screen.");
            System.out.println("You will be redirected to the main menu after the selected operation is completed.");
        }
        System.out.print("> "); // there will not be any loops for this page, so print it here
    }

    private void printSQL(QueryDefinition query){
        System.out.println();
        System.out.println(query.sql());
        System.out.println();
        System.out.println("You have been redirected to the main menu. To print the main menu again, enter " + INFO_INPUT + ".");
    }

    /**
     * Parses the input to an integer, making sure that input is indeed an integer and satisfies the requirements.<br/>
     * @param input the input string
     * @param requires conditions that the input must meet
     * @return the input as an integer, if all validations pass
     * @throws InvalidInputException if input invalid, either is not a number or exceeds max value
     */
    private int parseInputToInt(String input, Predicate<Integer> requires, String message) throws InvalidInputException {
        // parse input
        int inputInt;
        try{
            inputInt = Integer.parseInt(input);
        }catch(NumberFormatException e){
            throw new InvalidInputException("Invalid input, input must be an integer, but is " + input, e);
        }

        // make sure the parsed integer satisfies the requirement
        boolean pass = requires.test(inputInt);
        if(!pass){
            throw new InvalidInputException("Invalid input, " + message + ", but is " + inputInt);
        }

        return inputInt;
    }

}