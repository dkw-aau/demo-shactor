package shactor.utils;

import javax.annotation.Nonnull;

public class Type {
    
    @Nonnull
    private String name;
    
    @Nonnull
    private Integer encodedKey;
    
    @Nonnull
    private Integer instanceCount;
    
    @Nonnull
    public Integer getInstanceCount() {
        return instanceCount;
    }
    
    public void setInstanceCount(@Nonnull Integer instanceCount) {
        this.instanceCount = instanceCount;
    }
    
    @Nonnull
    public String getName() {
        return name;
    }
    
    public void setName(@Nonnull String name) {
        this.name = name;
    }
    
    @Nonnull
    public Integer getEncodedKey() {
        return encodedKey;
    }
    
    public void setEncodedKey(@Nonnull Integer encodedKey) {
        this.encodedKey = encodedKey;
    }
}
