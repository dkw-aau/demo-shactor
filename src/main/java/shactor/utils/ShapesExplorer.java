package shactor.utils;

import cs.qse.common.Utility;
import cs.utils.FilesUtil;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.List;

public class ShapesExplorer {
    public RepositoryConnection conn;
    
    //For testing of constraints annotation by reading SHACL shapes from a file
    public ShapesExplorer() {
        String fileAddress = "";
        //this.conn = Utility.readFileAsRdf4JModel(fileAddress);
    }
    
    public ShapesExplorer(RepositoryConnection conn) {
        System.out.println("Invoked:: ShapesExplorer");
        this.conn = conn;
    }
    
    
    
    public void getNodeShapesAndIterativelyProcessPropShapes() {
        TupleQuery query = conn.prepareTupleQuery(FilesUtil.readShaclQuery("node_shapes_wd_ps_count"));
        try (TupleQueryResult result = query.evaluate()) {
            while (result.hasNext()) {
                BindingSet solution = result.next();
                //targetClass
                System.out.println(solution.getValue("nodeShape").stringValue() + " " + solution.getValue("countPs"));
           
                //binding : nodeShape
                getPropShapesWithDirectShClassAttribute(solution.getValue("nodeShape").stringValue());
                //getPropShapesWithEncapsulatedShClassAttribute(solution.getValue("nodeShape").stringValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Property Shapes having direct sh:class constraint
     */
    private void getPropShapesWithDirectShClassAttribute(String nodeShape) {
        TupleQuery query = conn.prepareTupleQuery(FilesUtil.readShaclQuery("ps_of_ns_direct_sh_class").replace("NODE_SHAPE", nodeShape));
        try (TupleQueryResult result = query.evaluate()) {
            while (result.hasNext()) {
                //?propertyShape ?path ?class ?support ?confidence
                BindingSet solution = result.next();
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Property Shapes having sh:class constraint encapsulated in sh:or RDF list
     */
    private void getPropShapesWithEncapsulatedShClassAttribute(String nodeShape) {
        TupleQuery queryA = conn.prepareTupleQuery(FilesUtil.readShaclQuery("ps_of_ns_indirect_sh_class").replace("NODE_SHAPE", nodeShape));
        try (TupleQueryResult resultA = queryA.evaluate()) {
            while (resultA.hasNext()) {
                BindingSet bindingsA = resultA.next(); //bindings : ?propertyShape
                TupleQuery queryB = conn.prepareTupleQuery(FilesUtil.readShaclQuery("sh_class_indirect_ps")
                        .replace("PROPERTY_SHAPE", bindingsA.getValue("propertyShape").stringValue())
                        .replace("NODE_SHAPE", nodeShape));
                try (TupleQueryResult resultB = queryB.evaluate()) {
                    while (resultB.hasNext()) {
                        BindingSet bindingsB = resultB.next(); //bindings : ?propertyShape ?path ?class
                        //insertShNodeConstraint(bindingsB);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private boolean isNodeShape(String shaclClassValue) {
        String query = FilesUtil.readShaclQuery("ns_existence").replace("SHACL_CLASS", shaclClassValue);
        return conn.prepareBooleanQuery(query).evaluate();
    }
    
    
    private String getNodeShape(String shaclClassValue) {
        String nodeShapeIRI = "";
        TupleQuery query = conn.prepareTupleQuery(FilesUtil.readShaclQuery("ns").replace("SHACL_CLASS", shaclClassValue));
        
        try (TupleQueryResult resultB = query.evaluate()) {
            while (resultB.hasNext()) {
                BindingSet solution = resultB.next(); //bindings : ?nodeShape
                nodeShapeIRI = solution.getValue("nodeShape").stringValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nodeShapeIRI;
    }
}
