package shactor.utils;

import cs.qse.common.ShapesExtractor;
import cs.qse.common.structure.NS;
import cs.qse.common.structure.PS;
import cs.qse.common.structure.ShaclOrListItem;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PruningUtil implements Serializable {
    public PruningUtil() {
    }

    HashMap<String, String> statsBySupport = new HashMap<>();
    HashMap<String, String> statsByConfidence = new HashMap<>();
    ;
    HashMap<String, String> statsByBoth = new HashMap<>();
    ;
    HashMap<String, String> statsDefault = new HashMap<>();
    ;

    public HashMap<String, String> getStatsBySupport() {
        return statsBySupport;
    }

    public HashMap<String, String> getStatsByConfidence() {
        return statsByConfidence;
    }

    public HashMap<String, String> getStatsByBoth() {
        return statsByBoth;
    }

    public HashMap<String, String> getStatsDefault() {
        return statsDefault;
    }

    public void computeStats(ShapesExtractor shapesExtractor, Integer support, Double confidence) {
        shapesExtractor.getDefaultRepoConnection().close();
        Repository db = shapesExtractor.getDefaultShapesDb();
        try (RepositoryConnection conn = shapesExtractor.getDefaultShapesDb().getConnection()) { // Open a connection to the database


            //Default Shapes Stats
            String countNs = executeQuery(conn, buildQueryToGetTotalNumberOfNodeShape());
            String countPs = executeQuery(conn, buildQueryToGetTotalNumberOfPropertyShapes());
            String countLiteralPSc = executeQuery(conn, buildQueryToGetTotalNumberOfLiteralPSc());
            String countNonLiteralPSc = executeQuery(conn, buildQueryToGetTotalNumberOfNonLiteralPSc());

            statsDefault.put("COUNT_NS", countNs);
            statsDefault.put("COUNT_PS", countPs);
            statsDefault.put("COUNT_LC", countLiteralPSc);
            statsDefault.put("COUNT_CC", countNonLiteralPSc);

            //Shapes Analysis by Support
            String countNsBySupp = executeQuery(conn, buildQueryToGetStatsBySupportForNodeShape(support));
            String countPsSupp = executeQuery(conn, buildQueryToGetPropertyShapesStatsBySupport(support));
            statsBySupport.put("COUNT_NS", countNsBySupp);
            statsBySupport.put("COUNT_PS", countPsSupp);

            //Shapes Analysis by Confidence
            String countPsConf = executeQuery(conn, buildQueryToGetPropertyShapesStatsByConfidence(confidence));
            statsByConfidence.put("COUNT_PS", countPsConf);
            //Shapes Analysis by Support Confidence
            String countPsSuppConf = executeQuery(conn, buildQueryToGetPropertyShapesStatsBySupportAndConfidence(support, confidence));

            statsByBoth.put("COUNT_PS", String.valueOf(countPsSuppConf));

        } finally {
            db.shutDown();
        }
    }

    private static String executeQuery(RepositoryConnection conn, String query) {
        String countValue = "";
        TupleQuery tupleQuery = conn.prepareTupleQuery(query);
        TupleQueryResult result = tupleQuery.evaluate();
        while (result.hasNext()) {
            BindingSet solution = result.next();
            countValue = solution.getBinding("count").getValue().stringValue();
            System.out.println("COUNT: " + solution.getBinding("count").getValue().stringValue());
        }
        return countValue;
    }

    // ************** Default Statistics ****************

    public String buildQueryToGetTotalNumberOfNodeShape() {
        return """
                   Select (COUNT(DISTINCT ?s) AS ?count) WHERE {\s
                    ?s a  <http://www.w3.org/ns/shacl#NodeShape> .
                   }
                """;
    }

    public String buildQueryToGetTotalNumberOfPropertyShapes() {
        return """
                   Select (COUNT(DISTINCT ?s) AS ?count) WHERE {\s
                    ?s a  <http://www.w3.org/ns/shacl#PropertyShape> .
                   }
                """;
    }

    public String buildQueryToGetTotalNumberOfLiteralPSc() {
        return """
                #Total Number of LITERAL Constraints
                PREFIX sh: <http://www.w3.org/ns/shacl#>
                SELECT (COUNT(?dataType) AS ?count) where {
                    ?s a sh:NodeShape .
                     ?s sh:property ?p .
                    ?p sh:NodeKind sh:Literal.
                    ?p sh:datatype ?dataType.
                }
                """;
    }

    public String buildQueryToGetTotalNumberOfNonLiteralPSc() {
        return """
                  #Total Number of SH:CLASS constraints
                    PREFIX sh: <http://www.w3.org/ns/shacl#>
                    SELECT (COUNT(?shClassConstraint) AS ?count) where {
                        ?s a sh:NodeShape .
                         ?s sh:property ?p .
                        ?p sh:class ?shClassConstraint.
                    }
                """;
    }


    // ************** Analysis by Support ****************
    public String buildQueryToGetStatsBySupportForNodeShape(Integer support) {
        String query = """
                   Select (COUNT(DISTINCT ?s) AS ?count) WHERE {\s
                    ?s a  <http://www.w3.org/ns/shacl#NodeShape> .
                   	?s <http://shaclshapes.org/support>  ?support .
                       FILTER(?support > SUPPORT )
                   }
                """;
        query = query.replace("SUPPORT", Integer.toString(support));
        return query;
    }

    public String buildQueryToGetPropertyShapesStatsBySupport(Integer support) {
        String query = """
                   Select (COUNT(DISTINCT ?s) AS ?count) WHERE {\s
                    ?s a  <http://www.w3.org/ns/shacl#PropertyShape> .
                   	?s <http://shaclshapes.org/support>  ?support .
                       FILTER(?support > SUPPORT )
                   }
                """;
        query = query.replace("SUPPORT", Integer.toString(support));
        return query;
    }


    // ************** Analysis by Confidence ****************
    public String buildQueryToGetPropertyShapesStatsByConfidence(Double confidence) {
        String query = """
                   Select (COUNT(DISTINCT ?s) AS ?count) WHERE {\s
                    ?s a  <http://www.w3.org/ns/shacl#PropertyShape> .
                       ?s <http://shaclshapes.org/confidence> ?confidence .
                       FILTER( ?confidence > CONFIDENCE)
                   }
                """;
        query = query.replace("CONFIDENCE", confidence.toString());
        return query;
    }


    // ************** Analysis by Both ****************
    public String buildQueryToGetPropertyShapesStatsBySupportAndConfidence(Integer support, Double confidence) {
        String query = """
                   Select (COUNT(DISTINCT ?s) AS ?count) WHERE {\s
                    ?s a  <http://www.w3.org/ns/shacl#PropertyShape> .
                   	?s <http://shaclshapes.org/support>  ?support .
                       ?s <http://shaclshapes.org/confidence> ?confidence .
                       FILTER(?support > SUPPORT && ?confidence > CONFIDENCE)
                   }
                """;
        query = query.replace("SUPPORT", Integer.toString(support));
        query = query.replace("CONFIDENCE", confidence.toString());
        return query;
    }


    public void getStatsBySupport(List<NS> nodeShapes) {
        statsBySupport = new HashMap<>();
        int countNs = 0;
        int countPs = 0;
        int literalCount = 0;
        int nonLiteralCount = 0;

        for (NS ns : nodeShapes) {
            countPs += ns.getCountPsWithSupportPruneFlag();
            if (ns.getPruneFlag()) {
                countNs++;
            }
        }
        int totalPs = 0;
        for (NS ns : nodeShapes) {
            totalPs += ns.getCountPropertyShapes();
        }

        for (NS ns : nodeShapes) {
            for (PS ps : ns.getPropertyShapes()) {
                if (ps.getNodeKind() != null) {
                    if (ps.getNodeKind().equals("Literal") && ps.getSupportPruneFlag()) {
                        literalCount++;
                    }
                    if (ps.getNodeKind().equals("IRI") && ps.getSupportPruneFlag()) {
                        nonLiteralCount++;
                    }
                }
            }
        }

        statsBySupport.put("COUNT_NS", String.valueOf(countNs));
        statsBySupport.put("COUNT_PS", String.valueOf(countPs));
        statsBySupport.put("COUNT_LC", String.valueOf(literalCount));
        statsBySupport.put("COUNT_CC", String.valueOf(nonLiteralCount));

    }

    public void getStatsByConfidence(List<NS> nodeShapes) {
        statsByConfidence = new HashMap<>();
        int countNs = 0;
        int countPs = 0;

        int literalCount = 0;
        int nonLiteralCount = 0;

        for (NS ns : nodeShapes) {
            countPs += ns.getCountPsWithConfidencePruneFlag();
            if (ns.getPruneFlag()) {
                countNs++;
            }
        }

        for (NS ns : nodeShapes) {
            for (PS ps : ns.getPropertyShapes()) {
                if (ps.getNodeKind() != null) {
                    if (ps.getNodeKind().equals("Literal") && ps.getConfidencePruneFlag()) {
                        literalCount++;
                    }
                    if (ps.getNodeKind().equals("IRI") && ps.getConfidencePruneFlag()) {
                        nonLiteralCount++;
                    }
                }
            }
        }

        statsByConfidence.put("COUNT_NS", String.valueOf(countNs));
        statsByConfidence.put("COUNT_PS", String.valueOf(countPs));
        statsByConfidence.put("COUNT_LC", String.valueOf(literalCount));
        statsByConfidence.put("COUNT_CC", String.valueOf(nonLiteralCount));
    }

    public void getStatsByBoth(List<NS> nodeShapes) {
        statsByBoth = new HashMap<>();
        int countNs = 0;
        int countPs = 0;

        int literalCount = 0;
        int nonLiteralCount = 0;

        for (NS ns : nodeShapes) {
            countPs += ns.getCountPsWithPruneFlag();
            if (ns.getPruneFlag()) {
                countNs++;
            }
        }

        for (NS ns : nodeShapes) {
            for (PS ps : ns.getPropertyShapes()) {
                if (ps.getNodeKind() != null) {
                    if (ps.getNodeKind().equals("Literal") && ps.getPruneFlag()) {
                        literalCount++;
                    }
                    if (ps.getNodeKind().equals("IRI") && ps.getPruneFlag()) {
                        nonLiteralCount++;
                    }
                }
            }
        }

        statsByBoth.put("COUNT_NS", String.valueOf(countNs));
        statsByBoth.put("COUNT_PS", String.valueOf(countPs));
        statsByBoth.put("COUNT_LC", String.valueOf(literalCount));
        statsByBoth.put("COUNT_CC", String.valueOf(nonLiteralCount));

    }

    public void getDefaultStats(List<NS> nodeShapes) {
        statsDefault = new HashMap<>();
        int countNs = nodeShapes.size();
        int countPs = 0;

        int literalCount = 0;
        int nonLiteralCount = 0;

        for (NS ns : nodeShapes) {
            countPs += ns.getCountPropertyShapes();
        }

        for (NS ns : nodeShapes) {
            for (PS ps : ns.getPropertyShapes()) {
                if (ps.getNodeKind() != null) {
                    if (ps.getNodeKind().equals("Literal")) {
                        literalCount++;
                    }
                    if (ps.getNodeKind().equals("IRI")) {
                        nonLiteralCount++;
                    }
                }
            }
        }

        statsDefault.put("COUNT_NS", String.valueOf(countNs));
        statsDefault.put("COUNT_PS", String.valueOf(countPs));
        statsDefault.put("COUNT_LC", String.valueOf(literalCount));
        statsDefault.put("COUNT_CC", String.valueOf(nonLiteralCount));

    }

    public void applyPruningFlags(List<NS> nodeShapes, Integer support, Double confidence) {
        for (NS currNS : nodeShapes) {
            List<PS> propertyShapes = currNS.getPropertyShapes();
            if (currNS.getSupport() < support) {
                currNS.setPruneFlag(true);
            }
            for (PS currPS : propertyShapes) {
                if (currPS.getSupport() != null && currPS.getConfidence() != null) {
                    currPS.getSupport();
                    currPS.getConfidence();
                    if (currPS.getSupport() < support && currPS.getConfidence() < confidence) {
                        //nodeShapesCopy.get(nsIndex).getPropertyShapes().remove(currPS);
                        currPS.setPruneFlag(true);
                    }
                    if (currPS.getSupport() < support) {
                        currPS.setSupportPruneFlag(true);
                    }
                    if (currPS.getConfidence() < confidence) {
                        currPS.setConfidencePruneFlag(true);
                    }
                }
                if (currPS.getShaclOrListItems() != null) {
                    List<ShaclOrListItem> orItems = currPS.getShaclOrListItems();
                    orItems.forEach(item -> {
                        if (item.getSupport() != null && item.getConfidence() != null) {
                            if (item.getSupport() < support && item.getConfidence() < confidence) {
                                item.setPruneFlag(true);
                            }
                            if (item.getSupport() < support) {
                                item.setSupportPruneFlag(true);
                            }
                            if (item.getConfidence() < confidence) {
                                item.setConfidencePruneFlag(true);
                            }
                        }
                    });
                    if (orItems.isEmpty()) {
                        System.out.println("WARNING: orItems is empty for " + currPS.getIri().toString());
                    }
                }
            }
        }
    }

}