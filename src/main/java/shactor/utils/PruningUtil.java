package shactor.utils;

import cs.qse.common.structure.NS;
import cs.qse.common.structure.PS;
import cs.qse.common.structure.ShaclOrListItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class PruningUtil implements Serializable {
    public PruningUtil() {}
    
    HashMap<String, String> statsBySupport;
    HashMap<String, String> statsByConfidence;
    HashMap<String, String> statsByBoth;
    HashMap<String, String> statsDefault;
    
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
    
    public void   getStatsByConfidence(List<NS> nodeShapes) {
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
    
    public void  getStatsByBoth(List<NS> nodeShapes) {
        statsByBoth = new HashMap<>();
        int countNs = 0;
        int countPs = 0;
        
        int literalCount = 0;
        int nonLiteralCount = 0;
        
        for (NS ns : nodeShapes) {
            countPs +=  ns.getCountPsWithPruneFlag();
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
    
    public void  getDefaultStats(List<NS> nodeShapes) {
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
                    if (ps.getNodeKind().equals("Literal") ) {
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
                    if (currPS.getSupport() < support) {currPS.setSupportPruneFlag(true);}
                    if (currPS.getConfidence() < confidence) {currPS.setConfidencePruneFlag(true);}
                }
                
                if (currPS.getShaclOrListItems() != null) {
                    List<ShaclOrListItem> orItems = currPS.getShaclOrListItems();
                    orItems.forEach(item -> {
                        if (item.getSupport() != null && item.getConfidence() != null) {
                            if (item.getSupport() < support && item.getConfidence() < confidence) {
                                item.setPruneFlag(true);
                            }
                            if (item.getSupport() < support) {item.setSupportPruneFlag(true);}
                            if (item.getConfidence() < confidence) {item.setConfidencePruneFlag(true);}
                        }
                    });
                }
            }
        }
    }
    
}