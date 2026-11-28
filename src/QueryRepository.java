import dto.DatabaseConfig;
import dto.QueryDefinition;
import dto.QueryParameter;
import dto.QueryResult;
import exceptions.repository.DataAccessException;
import exceptions.repository.IncorrectParameterTypeException;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Executes a query on the specified database.<br/>
 * Database configuration (i.e. url, username, password) required.
 */
public class QueryRepository{

    private final DatabaseConfig config;

    /**
     * Constructs the QueryRepository class.
     * @param databaseConfig necessary configurations for database connection and authentication
     */
    public QueryRepository(DatabaseConfig databaseConfig) {
        this.config = databaseConfig;
    }

    /**
     * Executes the specified query.
     * @param query the definition of the query to be executed
     * @param parameters the parameters passed in, if no parameters needed then should pass in empty list.
     * @return the result of the query
     * @throws DataAccessException when database connection couldn't be built or sql execution failed
     */
    public QueryResult executeQuery(QueryDefinition query, List<Object> parameters){

        QueryResult result;

        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query.sql())){

            // set the parameters
            for (int i = 0; i < query.parameters().size(); i++) {

                // obtain the parameters
                QueryParameter parameter = query.parameters().get(i);
                Object actualParameter = parameters.get(i);

                // Note that this may violate OCP (Open/Closed Principle, open to extension, closed for modification),
                // but in this project the types of parameters are relatively fixed, also the project is not large in scale,
                // so to keep it simple stupid I'm not solving the problem.
                // It's a database course, not a software engineering course. We are building a course project, not next Google.
                // I consider this to be good enough :)
                switch(parameter.type()){
                    case INTEGER -> preparedStatement.setInt(i, (Integer) actualParameter);
                    case STRING-> preparedStatement.setString(i, (String) actualParameter);
                    case DOUBLE -> preparedStatement.setDouble(i, (Double) actualParameter);
                    case DATE -> preparedStatement.setDate(i, Date.valueOf((LocalDate) actualParameter));
                    case SORT_ORDER -> {
                        String sortingOrder = (String) actualParameter;
                        if("ASC".equalsIgnoreCase(sortingOrder) || "DESC".equalsIgnoreCase(sortingOrder)){
                            preparedStatement.setString(i, sortingOrder);
                        }else{
                            throw new IncorrectParameterTypeException("Parameter type incorrect for parameter " + parameter.name() +
                                    ", expected being either ASC or DESC, but was " + actualParameter);
                        }
                    }
                    default -> throw new IncorrectParameterTypeException("Parameter type incorrect for parameter " + parameter.name() +
                            ", expected: " + parameter.type() + ", but was " + actualParameter.getClass().getSimpleName());
                }
            }

            // execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // extract column names
            List<String> columns = new LinkedList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            // in JDBC the index always starts from 1 instead of 0
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columns.addLast(metaData.getColumnLabel(i));
            }

            // extract rows for data
            List<List<Object>> rows = new LinkedList<>();
            while(resultSet.next()){
                List<Object> currentRow = new LinkedList<>();
                for(String columnLabel : columns){
                    currentRow.addLast(resultSet.getObject(columnLabel));
                }
                rows.addLast(currentRow);
            }

            result = new QueryResult(columns, rows);

        }catch(SQLException e){
            // thrown on connection build failure, statement preparation failure, and failing to get the expected values
            throw new DataAccessException(e);
        }

        return result;
    }

    /**
     * Retrieves the database connection based on the configuration.
     * @return the database connection
     * @throws SQLException on connection build failure
     */
    private Connection getConnection() throws SQLException {
        String url = config.url() + ";"
                + "database=cs3380;"
                + "encrypt=false;"
                + "trustServerCertificate=false;"
                + "loginTimeout=30;";
        // in case the special characters in username and password affects anything
        return DriverManager.getConnection(url, config.username(), config.password());
    }

}