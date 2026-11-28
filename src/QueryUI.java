import dto.QueryDefinition;
import dto.QueryParameter;
import dto.QueryResult;
import exceptions.ui.InvalidInputException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Predicate;

public class QueryUI {

    // the states for current screen showing
    private enum STATES{
        /// contains the list of queries the user can choose from and some other operations
        MAIN_MENU,

        /// contains the detailed information of a query and operations available
        QUERY_DETAILS,

        /// asks the user to input the parameters required
        PARAMETER_INPUT,

        /// executes the query selected
        EXECUTE_QUERY,

        /// contains the result of the query
        RESULT_DISPLAY,

        /// quit the application
        QUIT
    }

    // constants
    private static final String QUIT_INPUT = "q";
    private static final String INFO_INPUT = "i";
    private static final String HIDE_INSTRUCTION_INPUT = "h";
    private static final String NULL_TEXT = "NULL";
    private static final int SCREEN_WIDTH = 100;

    // dependency
    private final QueryRepository repository;

    // application state
    private STATES currentState;
    private boolean instructionsVisible;
    private final List<QueryDefinition> queries;

    // context
    private QueryDefinition selectedQuery;
    private List<Object> parametersPassed;
    private QueryResult queryResult;

    /**
     * Constructs the QueryUI instance.
     * @param queries all queries available, configure from an external file
     * @param repository the repository instance used for executing queries
     */
    public QueryUI(List<QueryDefinition> queries, QueryRepository repository){
        this.queries = queries;
        this.repository = repository;
        this.instructionsVisible = true;
        this.currentState = STATES.MAIN_MENU;
        this.clearContext(); // context should be cleared on initialization
    }

    /// Runs the console UI using the simple state machine.
    public void runConsole(){
        Scanner scanner = new Scanner(System.in);

        // print title & queries & instructions
        printInfo();

        // run the state machine
        while(currentState != STATES.QUIT){
            switch(currentState){
                case MAIN_MENU -> showMainMenu(scanner);
                case QUERY_DETAILS -> showQueryDetails(scanner);
                case PARAMETER_INPUT -> showParameterInput(scanner);
                case EXECUTE_QUERY -> showExecuteQuery();
                case RESULT_DISPLAY -> showResultDisplay();
            }
        }
    }

    // ==================================================== SCREENS ====================================================

    /**
     * Shows the screen for main menu, which prints information and instructions needed and asks user to input what to do.
     * @param scanner scanner to receive user input from console
     */
    private void showMainMenu(Scanner scanner){
        String input;

        // every time we come back to the main menu, the context should be cleared
        clearContext();

        while(currentState == STATES.MAIN_MENU){
            // obtain input
            System.out.print("> ");
            input = scanner.nextLine().trim();

            // if print the query lists again
            if(INFO_INPUT.equalsIgnoreCase(input)){
                printInfo();
            }
            // if the user wants to make instructions invisible
            else if(HIDE_INSTRUCTION_INPUT.equalsIgnoreCase(input)){
                instructionsVisible = !instructionsVisible;
            }
            // if the user wants to quit
            else if(QUIT_INPUT.equalsIgnoreCase(input)){
                currentState = STATES.QUIT;
            }
            // the remaining possibilities are inputting queryId or input invalid
            else if(!input.isBlank()){
                // check if the input is a queryId
                try{
                    // parse the input to integer
                    int selectedQueryId = parseInputToInt(input, (queryId) -> {
                        // make sure the parsed query id is a positive number
                        if(queryId <= 0)
                            return false;

                        // make sure the parsed query id is an option
                        boolean valid = false;
                        for (int i = 0; i < queries.size() && !valid; i++) {
                            QueryDefinition current = queries.get(i);
                            if (current.id() == queryId) {
                                valid = true;
                            }
                        }
                        return valid;
                    });

                    // get the query definition based on the query id selected
                    for (int i = 0; i < queries.size() && selectedQuery == null; i++) {
                        if(queries.get(i).id() == selectedQueryId){
                            selectedQuery = queries.get(i);
                        }
                    }

                    // if we can't find the corresponding query based on the selected query id
                    if(selectedQuery == null){
                        System.out.println("Oops! Something went wrong and we can't find the query based on the query number selected :(");
                    }
                    // if we can find the corresponding query, redirect to query details page
                    else{
                        currentState = STATES.QUERY_DETAILS;
                    }
                }
                // if anything is not correct, then it's an invalid input
                catch (InvalidInputException e){
                    System.out.println("Input invalid, must either be an integer for query number of a character for operation.");
                }
            }
            // if user input is blank, then don't do anything
        }
    }

    /**
     * Shows the screen for query details, which prints the details of the query and asks user to input what to do.
     * @param scanner scanner to receive user input from console
     */
    private void showQueryDetails(Scanner scanner){
        String input;

        // make sure that the selected query is not null
        if(selectedQuery == null){
            currentState = STATES.MAIN_MENU;
            System.out.println("No query is selected before execution while there should be.");
            printRedirectToMain();
            return; // just return here, I don't want to add an extra level of indentation to the remaining code
        }

        // print the query's detail information & instructions
        printQueryDetails(selectedQuery);

        int operation = 0;
        boolean valid = false;
        while(!valid){
            System.out.print("> ");
            input = scanner.nextLine().trim();

            // parse the operation chosen
            // has coupling with the printQueryDetails function since we want to limit input based on what is printed
            // 1 = execute query, 2 = print sql, 3 = back to main menu
            try{
                operation = parseInputToInt(input, (choice) -> (choice == 1 || choice == 2 || choice == 3));
                valid = true;
            }catch(InvalidInputException e){
                System.out.println("Invalid input. Please choose a valid operation number.");
            }
        }

        // jump to different pages based on the operations selected
        switch(operation){
            // execute query, so jump to parameter input page
            case 1 -> currentState = STATES.PARAMETER_INPUT;
            // print sql, to keep it simple stupid I just print the sql instead of making another screen
            case 2 -> {
                currentState = STATES.MAIN_MENU;
                printSQL(selectedQuery);
                printRedirectToMain();
            }
            case 3 -> {
                currentState = STATES.MAIN_MENU;
                printRedirectToMain();
            }
        }
    }

    /**
     * Shows the parameter input screen, which prints the information and instruction needed, and asks user to input the
     *      parameters required for the query.
     * @param scanner scanner to receive user input from console
     */
    private void showParameterInput(Scanner scanner){
        String input;

        // make sure that the selected query is not null
        if(selectedQuery == null){
            currentState = STATES.MAIN_MENU;
            System.out.println("No query is selected while there should be.");
            printRedirectToMain();
            return; // just return here, I don't want to add an extra level of indentation to the remaining code
        }

        // for each parameter declared for the query, ask the user to enter the parameter
        for(QueryParameter parameter : selectedQuery.parameters()){
            // print the parameter's info
            printParameter(parameter);

            // ask the user to input the current parameter until input valid
            boolean valid = false;
            while(!valid){
                System.out.print("> ");
                input = scanner.nextLine().trim();

                // if the current parameter is optional and is blank, pass null
                if(parameter.optional() && input.isBlank()){
                    parametersPassed.addLast(null);
                    valid = true;
                    System.out.println("No parameters provided; defaulting to null.");
                }else{
                    // if the input is not blank, then no matter if the current parameter is optional, try to parse and pass it
                    if (!input.isBlank()) {
                        switch(parameter.type()){
                            case INTEGER -> {
                                try{
                                    parametersPassed.addLast(Integer.parseInt(input));
                                    valid = true;
                                }catch(NumberFormatException e){
                                    System.out.println("The input must be an integer.");
                                }
                            }
                            case STRING -> {
                                parametersPassed.addLast(input);
                                valid = true;
                            }
                            case DOUBLE -> {
                                try{
                                    parametersPassed.addLast(Double.parseDouble(input));
                                    valid = true;
                                }catch (NumberFormatException e){
                                    System.out.println("The input must be a decimal number (double).");
                                }
                            }
                            case DATE -> {
                                try{
                                    parametersPassed.addLast(LocalDate.parse(input));
                                    valid = true;
                                }catch (DateTimeParseException e){
                                    System.out.println("The input must be a valid date in format of YYYY-MM-DD.");
                                }
                            }
                            case SORT_ORDER -> {
                                if("1".equals(input)){
                                    parametersPassed.addLast("ASC");
                                    valid = true;
                                }else if("2".equals(input)){
                                    parametersPassed.addLast("DESC");
                                    valid = true;
                                }else{
                                    System.out.println("You must select either [1] ASC or [2] DESC.");
                                }
                            }
                        }
                    }
                }
                // if the current parameter is not optional but is blank, it's an invalid input
            }
        }

        currentState = STATES.EXECUTE_QUERY;

    }

    /// Shows the execute query screen, which executes the query and gives the result.
    private void showExecuteQuery(){

        if(selectedQuery == null){
            currentState = STATES.MAIN_MENU;
            System.out.println("No query is selected before execution.");
            printRedirectToMain();
        }else if(parametersPassed == null || parametersPassed.size() != selectedQuery.parameters().size()){
            currentState = STATES.MAIN_MENU;
            System.out.println("Not all parameters are entered and passed.");
            printRedirectToMain();
        }else{
            System.out.println("Executing query...");
            queryResult = repository.executeQuery(selectedQuery, parametersPassed);

            currentState = STATES.RESULT_DISPLAY;
        }
    }

    /// Shows the result display screen, which prints the result of an executed query.
    private void showResultDisplay(){

        // make sure we have the result
        if(queryResult == null){
            currentState = STATES.MAIN_MENU;
            System.out.println("No query result is generated before printing.");
            printRedirectToMain();
        }

        // print the result
        printResult(queryResult);
        printRedirectToMain();

        currentState = STATES.MAIN_MENU;
    }

    // ======================================= PRINTING FOR INFO & INSTRUCTIONS =======================================

    /// Prints the title of the application, available queries (with short names described), and instructions.
    private void printInfo(){
        String title = "MOVIE ANALYTICS";
        int spaceLength = (SCREEN_WIDTH - title.length()) / 2;
        System.out.println();
        System.out.println("=".repeat(SCREEN_WIDTH));
        System.out.println(" ".repeat(spaceLength) + title + " ".repeat(spaceLength));
        System.out.println("=".repeat(SCREEN_WIDTH));
        System.out.println();
        System.out.println("Available Queries:");
        System.out.println();
        for (QueryDefinition query : queries){
            System.out.println("[" + query.id() + "] " + query.name());
        }
        System.out.println();
        if(instructionsVisible){
            System.out.println("-".repeat(SCREEN_WIDTH));
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
        List<String> descriptionLines = wrapText(query.description(), SCREEN_WIDTH);
        String title = "QUERY DETAILS";
        int spaceLength = (SCREEN_WIDTH - title.length()) / 2;
        System.out.println();
        System.out.println("=".repeat(SCREEN_WIDTH));
        System.out.println(" ".repeat(spaceLength) + title + " ".repeat(spaceLength ));
        System.out.println("=".repeat(SCREEN_WIDTH));
        System.out.println();
        System.out.println("Query: " + query.name());
        System.out.println();
        System.out.println("Description:");
        for (String line : descriptionLines){
            System.out.println(line);
        }
        System.out.println();
        System.out.println();
        System.out.println("-".repeat(100));
        System.out.println("Options:");
        System.out.println("[1] Execute Query");
        System.out.println("[2] View SQL");
        System.out.println("[3] Back to Main Menu");
        if(instructionsVisible){
            System.out.println();
            System.out.println("Enter number of the operation you wish to perform.");
        }
    }

    /**
     * Prints the sql statement of the specified query.
     * @param query the specified query, which the user wishes to print the sql
     */
    private void printSQL(QueryDefinition query){
        System.out.println();
        System.out.println(query.sql());
        System.out.println();
    }

    /**
     * Prints the instructions for asking the user to enter a required parameter.
     * @param parameter the required parameter
     */
    private void printParameter(QueryParameter parameter){
        String instruction = "Please enter a";
        String note = "";
        switch(parameter.type()){
            case INTEGER -> instruction += "n integer";
            case STRING -> instruction += " string";
            case DOUBLE -> instruction += " decimal number (double)";
            case DATE -> {
                instruction += " date";
                note = "Note that the format should be YYYY-MM-DD. e.g. 2000-01-01";
            }
            case SORT_ORDER -> {
                instruction += " sorting order";
                note = """
                        Please select one of the followings:
                        [1] ASC (ascending order)
                        [2] DESC (descending order)""";
            }
        }
        instruction += " for the " + parameter.name();
        System.out.println();
        System.out.print(parameter.optional() ? "OPTIONAL: " : "REQUIRED: ");
        System.out.println(instruction);
        if(!note.isEmpty()){
            System.out.println(note);
        }
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
        System.out.println();
    }

    /// Prints the separation line in the table.
    private void printLineSeparations(int[] widths){
        for (int width : widths) {
            System.out.print("+" + "-".repeat(width + 2));
        }
        System.out.println("+");
    }

    /// Prints a single row, limiting the width of each cell.
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

    /// Prints the message for being redirected to main menu if something went wrong, or we finished the operations.
    private void printRedirectToMain(){
        System.out.println("You are redirected to the main menu. To print the main menu again, enter " + INFO_INPUT + ".");
    }

    // ================================================ HELPER METHODS ================================================

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
        final int MAX_WIDTH = SCREEN_WIDTH / numColumns;

        // initialize the widths using the width of column names
        for (int i = 0; i < numColumns; i++) {
            widths[i] = columns.get(i).length();
        }

        // for each row, find the largest width required (lower than the max)
        for (List<Object> row : rows){
            for (int i = 0; i < numColumns; i++) {
                String value = row.get(i) == null ? NULL_TEXT : row.get(i).toString();
                widths[i] = Math.max(value.length(), widths[i]);
            }
        }

        // calculate the appropriate for each column
        for (int i = 0; i < numColumns; i++) {
            widths[i] = Math.min(widths[i], MAX_WIDTH);
        }

        return widths;
    }

    /**
     * Wraps the specified text so that it fits the width given.<br/>
     * For the specified text and given width, the method separates the text into multiple lines if the text is longer than
     *      the width. The method will cut the lines at the spaces as close to the possible end as possible to avoid
     *      situations where the newline cuts between words. However, if there's just no spaces and the text is too long,
     *      the method would just cut at the maximum width.
     * @param text the specified text
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
    private int parseInputToInt(String input, Predicate<Integer> requires) throws InvalidInputException {
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
            throw new InvalidInputException("Invalid input, input not meeting the requirements.");
        }

        return inputInt;
    }

    /**
     * Clears the application's context.<br/>
     * Make sure whenever this is called the old context is not used anymore.
     */
    private void clearContext(){
        this.selectedQuery = null;
        this.parametersPassed = new LinkedList<>();
        this.queryResult = null;
    }

}