package shactor.utils;

import cs.qse.common.structure.NS;
import cs.qse.common.structure.PS;
import cs.qse.common.structure.ShaclOrListItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class PruningUtil implements Serializable {
    public PruningUtil() {}
    
    public HashMap<String, String>  getStatsBySupport(List<NS> nodeShapes) {
        HashMap<String, String> statsMap = new HashMap<String, String>();
        int countNs = 0;
        int countPs = 0;
        int literalCount = 0;
        int nonLiteralCount = 0;
        
        for (NS ns : nodeShapes) {
            countPs += ns.getCountPscWithSupportPruneFlag() + ns.getCountPsWithSupportPruneFlag();
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
        
        statsMap.put("COUNT_NS", String.valueOf(countNs));
        statsMap.put("COUNT_PS", String.valueOf(countPs));
        statsMap.put("COUNT_LC", String.valueOf(literalCount));
        statsMap.put("COUNT_CC", String.valueOf(nonLiteralCount));
        
        return statsMap;
    }
    
    public HashMap<String, String>  getStatsByConfidence(List<NS> nodeShapes) {
        HashMap<String, String> statsMap = new HashMap<>();
        int countNs = 0;
        int countPs = 0;
        
        int literalCount = 0;
        int nonLiteralCount = 0;
        
        for (NS ns : nodeShapes) {
            countPs += ns.getCountPscWithConfidencePruneFlag() + ns.getCountPsWithConfidencePruneFlag();
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
        
        statsMap.put("COUNT_NS", String.valueOf(countNs));
        statsMap.put("COUNT_PS", String.valueOf(countPs));
        statsMap.put("COUNT_LC", String.valueOf(literalCount));
        statsMap.put("COUNT_CC", String.valueOf(nonLiteralCount));
        return statsMap;
    }
    
    public HashMap<String, String>  getStatsByBoth(List<NS> nodeShapes) {
        HashMap<String, String> statsMap = new HashMap<>();
        int countNs = 0;
        int countPs = 0;
        
        int literalCount = 0;
        int nonLiteralCount = 0;
        
        for (NS ns : nodeShapes) {
            countPs += ns.getCountPscWithPruneFlag() + ns.getCountPsWithPruneFlag();
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
        
        statsMap.put("COUNT_NS", String.valueOf(countNs));
        statsMap.put("COUNT_PS", String.valueOf(countPs));
        statsMap.put("COUNT_LC", String.valueOf(literalCount));
        statsMap.put("COUNT_CC", String.valueOf(nonLiteralCount));
        return statsMap;
    }
    
    public HashMap<String, String>  getDefaultStats(List<NS> nodeShapes) {
        HashMap<String, String> statsMap = new HashMap<>();
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
        
        statsMap.put("COUNT_NS", String.valueOf(countNs));
        statsMap.put("COUNT_PS", String.valueOf(countPs));
        statsMap.put("COUNT_LC", String.valueOf(literalCount));
        statsMap.put("COUNT_CC", String.valueOf(nonLiteralCount));
        
        return statsMap;
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