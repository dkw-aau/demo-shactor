package shactor.utils;

import cs.qse.common.structure.NS;
import cs.qse.common.structure.PS;

public class QueryUtil {


    public static String buildQueryToExtractTypesOfPs(String type, String property) {
        String query = """
                SELECT DISTINCT ?types  WHERE {\s
                	?s a <CLASS> .\s
                	?s <PROPERTY> ?o .\s
                        ?s a ?types .
                }
                """;
        query = query.replace("CLASS", type);
        query = query.replace("PROPERTY", property);
        return query;
    }


    public static String buildQueryToComputeEntityCountForTypeOfPs(String type, String property) {
        String query = """
                SELECT (COUNT(DISTINCT ?s) AS ?entityCount)  WHERE { \s
                	?s a <CLASS> . \s
                	 ?s <PROPERTY> ?o . \s
                }
                """;
        query = query.replace("CLASS", type);
        query = query.replace("PROPERTY", property);
        return query;
    }
    //as triples
    public static String buildQueryToExtractEntitiesForTypeOfPs(String type, String property) {
        String query = """
                CONSTRUCT WHERE { \s
                	?s a <CLASS> . \s
                	?s <PROPERTY> ?o .
                  ?s ?p ?o .
                }
                """;
        query = query.replace("CLASS", type);
        query = query.replace("PROPERTY", property);
        return query;
    }


    // ********************* CONFORMANCE CHECKING QUERIES *********************

    // For NON-Literal Type PS
    public static String buildQueryToExtractObjectsHavingUndefinedShClass(String type, String property) {
        String query = """
                SELECT DISTINCT ?val WHERE { \s
                	?s a <CLASS> . \s
                	?s <PROPERTY> ?val .
                  FILTER NOT EXISTS {?val a ?objType. }
                }
                """;
        query = query.replace("CLASS", type);
        query = query.replace("PROPERTY", property);
        return query;
    }


    public static String buildQueryToExtractEntitiesHavingSpecificShClass(String type, String property, String shClassType) {
        String query = """
                SELECT DISTINCT ?val  WHERE { \s
                	?val a <CLASS> . \s
                	?val <PROPERTY> ?o .
                	?o a <OBJECT_TYPE> . \s
                }
                """;
        query = query.replace("CLASS", type);
        query = query.replace("PROPERTY", property);
        query = query.replace("OBJECT_TYPE", shClassType);
        System.out.println(query);
        return query;
    }

// FILTER NOT EXISTS {?s a ?objType. }
    public static String buildQueryToExtractEntitiesHavingUndefinedShClass(String type, String property) {
        String query = """
                SELECT DISTINCT ?val WHERE { \s
                	?val a <CLASS> . \s
                	?val <PROPERTY> ?s .
                }
                """;
        query = query.replace("CLASS", type);
        query = query.replace("PROPERTY", property);
        System.out.println(query);
        return query;
    }

    //TODO: For Literal Type PS


    // ********************* OTHER QUERIES *********************
    public static String extractSparqlQuery(NS ns, PS ps) {
        String constructQuery = """
                CONSTRUCT WHERE { \s
                \t ?s a <CLASS> . \s
                \t  ?s a ?types. \s
                \t ?s <PROPERTY> ?o . \s
                } \s
                """;

        constructQuery = constructQuery.replace("CLASS", ns.getTargetClass().toString());
        constructQuery = constructQuery.replace("PROPERTY", ps.getPath());

        return constructQuery;
    }

    public static String extractSparqlQueryWithFullEntitiesData(NS ns, PS ps) {
        String constructQuery = """
                CONSTRUCT WHERE { \s
                \t ?s a <CLASS> . \s
                \t ?s ?p ?o . \s
                } \s
                """;
        constructQuery = constructQuery.replace("CLASS", ns.getTargetClass().toString());
        //constructQuery = constructQuery.replace("PROPERTY", ps.getPath());

        return constructQuery;
    }
}
