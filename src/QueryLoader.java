import dto.ParameterType;
import dto.QueryDefinition;
import dto.QueryParameter;
import exceptions.QueriesNotFoundException;
import exceptions.QueriesParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the queries from the specified xml file.<br/>
 * Not creating an interface since for this project, we are only considering store the queries' information using xml file.<br/>
 * So to keep it simple stupid I'm not creating an interface for this class.
 */
public class QueryLoader{

    /**
     * Loads all queries specified in the target file
     * @param filePath the path of the specified file that contains all the queries
     * @return a list of QueryDefinitions that contains all the information needed to execute the query
     * @throws QueriesNotFoundException when specified file containing all queries is not found
     * @throws QueriesParseException when failed to parse any of the queries
     */
    public List<QueryDefinition> loadQueries(String filePath){
        List<QueryDefinition> result = new ArrayList<>();
        try{
            // get the target xml file's document object for later parsing
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));

            // read all queries
            NodeList queries = document.getElementsByTagName("query");
            // parse the queries obtained
            for (int i = 0; i < queries.getLength(); i++) {
                // current query
                // type conversion since item() must be returning Element, which is a subclass of Node
                Element query = (Element) queries.item(i);

                // parse its information
                int id = Integer.parseInt(query.getAttribute("id"));
                String name = getChildText(query, "name");
                String description = getChildText(query, "description");
                String sql = getChildText(query, "sql");
                List<QueryParameter> parametersList = new ArrayList<>();

                // obtain the current query's parameters list
                NodeList parameters = query.getElementsByTagName("parameter");
                // parse the current query's parameters
                for (int j = 0; j < parameters.getLength(); j++) {
                    Element parameter = (Element) parameters.item(j);

                    // parse the current parameter's information
                    String parameterName = parameter.getAttribute("name");
                    ParameterType parameterType = ParameterType.valueOf(parameter.getAttribute("type"));
                    String optionalStr = parameter.getAttribute("optional");
                    boolean optional = false;
                    if("true".equals(optionalStr)){
                        optional = true;
                    }else if(!"false".equals(optionalStr)){
                        throw new QueriesParseException("Unexpected value for optional, expected either true or false, but is " + optionalStr, QueriesParseException.Field.PARSE);
                    }

                    parametersList.addLast(new QueryParameter(parameterName, parameterType, optional));
                }

                // add to result list
                result.addLast(new QueryDefinition(id, name, description, parametersList, sql));
            }
        }catch (IOException e){
            // thrown when the specified file doesn't exist
            throw new QueriesNotFoundException(e);
        }catch (ParserConfigurationException e){
            // thrown when the parser doesn't support the current configuration
            // should not be thrown since there are no specific configurations
            throw new QueriesParseException(e, QueriesParseException.Field.CONFIGURATION);
        }catch (SAXException e) {
            // thrown when xml file's format isn't correct
            throw new QueriesParseException("Specified xml file contains unexpected format", e, QueriesParseException.Field.PARSE);
        }catch (NumberFormatException e){
            // thrown when the types of data contained is not expected
            throw new QueriesParseException("Specified xml file contains unexpected data type", e, QueriesParseException.Field.PARSE);
        }catch(IllegalArgumentException e){
            // thrown when the ParameterType.valueOf() couldn't recognize the given string
            throw new QueriesParseException("Specified xml file contains unexpected parameter type for a query", e, QueriesParseException.Field.PARSE);
        }

        return result;
    }

    /**
     * Retrieves the text content of a child tag of the parent tag in a xml file
     * @param parent the parent tag
     * @param childTagName the name of the child tag
     * @return the child tag's text content
     */
    private static String getChildText(Element parent, String childTagName){
        NodeList children = parent.getElementsByTagName(childTagName);
        return children.getLength() > 0 ? children.item(0).getTextContent() : null;
    }

}