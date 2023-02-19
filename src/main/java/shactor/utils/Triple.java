package shactor.utils;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

public class Triple {
    String subject;
    String predicate;
    String object;

    IRI subjectIRI;
    IRI predicateIRI;
    IRI objectIRI;
    
    public Triple(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public Triple(IRI subject, IRI predicate, IRI object) {
        this.subjectIRI = subject;
        this.predicateIRI = predicate;
        this.objectIRI = object;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "subject='" + subject + '\'' +
                ", predicate='" + predicate + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getPredicate() {
        return predicate;
    }
    
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }
    
    public String getObject() {
        return object;
    }
    
    public void setObject(String object) {
        this.object = object;
    }
}
