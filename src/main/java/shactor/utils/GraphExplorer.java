package shactor.utils;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import shactor.graphdb.GraphDBUtils;

import java.util.ArrayList;
import java.util.List;

public class GraphExplorer {
    private final GraphDBUtils graphDBUtils;
    
    public GraphExplorer(String url, String repo) {
        graphDBUtils = new GraphDBUtils(url, repo);
    }
    
    public List<Triple> runQuery(String query) {
        List<Triple> tripleList = new ArrayList<>();
        for (Statement row : graphDBUtils.runConstructQuery(query)) {
            tripleList.add(new Triple(row.getSubject().stringValue(), row.getPredicate().stringValue(), row.getObject().stringValue()));
        }
        return tripleList;
    }


    public List<BindingSet> runSelectQuery(String query) {
        return graphDBUtils.runSelectQuery(query);
    }
}
