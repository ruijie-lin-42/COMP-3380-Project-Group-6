import dto.QueryDefinition;

import java.util.List;

public class Main{

    public static void main(String[] args){

        QueryLoader queryLoader = new QueryLoader();
        List<QueryDefinition> queries = queryLoader.loadQueries("resources/queries.xml");

        QueryRepository repository = new QueryRepository();

        QueryUI queryUI = new QueryUI(queries, repository);
        queryUI.runConsole();

    }

}