import dto.ParameterType;
import dto.QueryDefinition;
import dto.QueryParameter;
import dto.QueryResult;
import exceptions.InvalidInputException;

import java.util.*;
import java.util.function.Predicate;

public class QueryUI {

    private static final String QUIT_INPUT = "q";
    private static final String INFO_INPUT = "i";
    private static final String HIDE_INSTRUCTION_INPUT = "h";
    private static final int QUERY_NUM_LOWER_BOUND = 1;
    private static final String NULL_TEXT = "NULL";

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
    // TODO: 感觉可以把各个界面分开写在不同的方法中
    // TODO: 感觉可以把解析整数的逻辑抽象出来
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
                    // here's a warning of query could be null, however we checked that the queryNum must corresponds to a valid query
                    // so unless we want to change our code, I think we're fine
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
                        List<Object> parameters = new LinkedList<>();

                        for(QueryParameter parameter : query.parameters()){
                            printParameter(parameter);
                            boolean parameterValid = false;
                            while(!parameterValid){
                                System.out.print("> ");
                                input = scanner.nextLine().trim();
                                if(parameter.type() == ParameterType.INTEGER){
                                    try{
                                        parameters.addLast(Integer.parseInt(input));
                                        parameterValid = true;
                                    }catch(NumberFormatException e){
                                        System.out.println("The number you entered must be an integer");
                                    }
                                }else if(parameter.type() == ParameterType.STRING){
                                    // TODO: 可能会有允许空的参数？比如说根据某个条件查询的时候不输入就相当于是全量查询，算了先这么写吧
                                    parameterValid = !input.isBlank();
                                    if(parameterValid){
                                        parameters.addLast(input);
                                    }
                                }
                            }
                        }

                        // execute the query
                        System.out.println("Executing query...");
//                        QueryResult result = repository.executeQuery(query, parameters);
                        QueryResult result = new QueryResult(
                                List.of("a long long long long long long long long long long long long  column name", "b longlonglonglong longlonglonglonglonglonglong long  column name", "c long long long long long long long long long long long long  column name"),
                                List.of(
                                        List.of("1", 2, "3"),
                                        List.of("4", 5, "6")));

                        printResult(result);

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

    /**
     * Prints the sql statement of the specified query.
     * @param query the specified query, which the user wishes to print the sql
     */
    private void printSQL(QueryDefinition query){
        System.out.println();
        System.out.println(query.sql());
        System.out.println();
        System.out.println("You have been redirected to the main menu. To print the main menu again, enter " + INFO_INPUT + ".");
    }

    /**
     * Prints the instructions for asking the user to enter a required parameter.
     * @param parameter the required parameter
     */
    private void printParameter(QueryParameter parameter){
        String instruction = "Please enter a";
        switch(parameter.type()){
            case INTEGER: instruction += "n integer";break;
            case STRING: instruction += " string";break;
        }
        instruction += " for the " + parameter.name();
        System.out.println();
        System.out.print(instruction);
    }

    /**
     * Prints the result of a query.
     * @param result the query's result
     */
    private void printResult(QueryResult result){
        List<String> columns = result.columns();
        List<List<Object>> rows = result.rows();
        int[] widths = calculateColumnWidths(result); // the width limit for each column/cell

        System.out.println();
        printLineSeparations(widths);
        // print the column names
        printRow(columns, widths);
        printLineSeparations(widths);
        for (List<Object> row : rows){
            printRow(row, widths);
        }
        printLineSeparations(widths);
        System.out.println();;

        System.out.println("You are redirected to the main menu. To print the main menu again, enter " + INFO_INPUT + ".");
    }

    private void printLineSeparations(int[] widths){
        for (int width : widths) {
            System.out.print("+" + "-".repeat(width + 2));
        }
        System.out.println("+");
    }

    /**
     * Prints a single row, limiting the width of each cell.
     */
    private void printRow(List<?> row, int[] widths){
        List<List<String>> wrappedValues = new ArrayList<>();
        int maxLines = 1; // count the maximum number of lines needed to be printed in the current row

        // for each cell (in a row, goes from left most column to right most column), wrap the text
        // i.e. get the strings needed to be printed when considering printing them in a new line if too long.
        for (int i = 0; i < row.size(); i++) {
            // get the raw text, if null then use the text for null
            String rawText = (row.get(i) == null ? NULL_TEXT : row.get(i).toString());
            // wrap the raw text
            List<String> lines = wrapText(rawText, widths[i]);
            // save the wrapped text
            wrappedValues.addLast(lines);
            // update max lines count
            maxLines = Math.max(maxLines, lines.size());
        }

        // for each cell, print the content
        for(int lineIdx = 0; lineIdx < maxLines; lineIdx++){
            for (int columnIdx = 0; columnIdx < row.size(); columnIdx++){
                List<String> lines = wrappedValues.get(columnIdx);
                // print the current line, if there's nothing to print then print nothing
                String value = lineIdx < lines.size() ? lines.get(lineIdx) : "";
                // padding to the right to at most width limit
                System.out.printf("| %-" + widths[columnIdx] + "s ", value);
            }
            System.out.println("|");
        }

    }

    /**
     * Calculates the width each column should be. Assuming each line is maximum 80 characters.
     * @param result the result of the query
     * @return an array of integers representing each column's width
     */
    private int[] calculateColumnWidths(QueryResult result){

        List<String> columns = result.columns();
        List<List<Object>> rows = result.rows();
        int numColumns = columns.size();
        int[] widths = new int[numColumns];
        final int TOTAL_WIDTH = 100;
        final int MAX_WIDTH = TOTAL_WIDTH / numColumns;

        for (int i = 0; i < numColumns; i++) {
            widths[i] = columns.get(i).length();
        }

        for (List<Object> row : rows){
            for (int i = 0; i < numColumns; i++) {
                String value = row.get(i) == null ? NULL_TEXT : row.get(i).toString();
                widths[i] = Math.max(value.length(), widths[i]);
            }
        }

        for (int i = 0; i < numColumns; i++) {
            widths[i] = Math.min(widths[i], MAX_WIDTH);
        }

        return widths;
    }

    /**
     * Wraps the specified text so that it fits the width of the column given.<br/>
     * For the specified text and given width, the method separates the text into multiple lines if the text is longer than
     *      the width. The method will cut the lines at the spaces as close to the possible end as possible to avoid
     *      situations where the newline cuts between words. However, if there's just no spaces and the text is too long,
     *      the method would just cut at the maximum width.
     * @param text the specified text, or content in the cell
     * @param width the width of the column
     * @return a list of strings that each line should display
     */
    private List<String> wrapText(String text, int width){

        List<String> lines = new LinkedList<>();
        String value = (text == null ? NULL_TEXT : text);

        // I don't think our data would contain newline characters though, but just in case
        value = value.replace("\n", " ");

        // start and end position of current line, and the index of the last occurrence of the space
        int start = 0, end, spaceIdx;

        while(start < value.length()){
            // see if the rest of the text is able to be put into a single cell
            end = Math.min(start + width, value.length());

            // if not, then cut it into lines
            if(end < value.length()){
                // try to find the last occurrence of the space
                spaceIdx = value.lastIndexOf(' ', end);

                // if we found one, switch lines there
                // if we didn't find any spaces, then we can only cut at end
                if(spaceIdx > start){
                    end = spaceIdx;
                }
            }

            // save the line and move to the next line if any
            lines.addLast(value.substring(start, end).trim());
            start = end;
            while(start < value.length() && value.charAt(start) == ' '){
                start++;
            }
        }

        return lines;
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