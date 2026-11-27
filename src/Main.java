import dto.DatabaseConfig;
import dto.QueryDefinition;
import exceptions.main.InvalidCredentialException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Main{

    public static void main(String[] args){

        // get all queries
        QueryLoader queryLoader = new QueryLoader();
        List<QueryDefinition> queries = queryLoader.loadQueries("resources/queries.xml");

        // get the repository class for executing the queries
        QueryRepository repository = getRepository();

        // start the main user interface
        QueryUI queryUI = new QueryUI(queries, repository);
        queryUI.runConsole();

    }

    /**
     * get the repository instance, read necessary authentication & database connection configurations as well.
     * @return QueryRepository instance for executing the queries.
     */
    private static QueryRepository getRepository() {

        // read authentication & database connections configurations
        Properties properties = new Properties();
        try(FileInputStream configFile = new FileInputStream("config/auth.cfg")){
            properties.load(configFile);
        }catch(IOException e){
            throw new InvalidCredentialException(e);
        }
        DatabaseConfig databaseConfig = new DatabaseConfig(properties.getProperty("url"),
                properties.getProperty("username"), properties.getProperty("password"));

        return new QueryRepository(databaseConfig);
    }

}