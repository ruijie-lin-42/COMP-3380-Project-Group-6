import dto.QueryDefinition;

import java.util.List;

public class Main{

    public static void main(String[] args){

        QueryLoader queryLoader = new QueryLoader();
        List<QueryDefinition> definitions = queryLoader.loadQueries("resources/queries.xml");

        QueryUI queryUI = new QueryUI();
        queryUI.runConsole();

    }

}