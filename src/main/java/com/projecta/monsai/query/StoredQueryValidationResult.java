package com.projecta.monsai.query;

/**
 * Validation result of a specific stored query
 *
 * @author akuehnel
 */
public class StoredQueryValidationResult {

    public String path;
    public String mdx;
    public String message;

    public StoredQueryValidationResult( String path, String mdx, String message ) {
        super();
        this.path = path;
        this.mdx = mdx;
        this.message = message;
    }

    public String getPath() {
        return path;
    }
    public void setPath( String path ) {
        this.path = path;
    }
    public String getMdx() {
        return mdx;
    }
    public void setMdx( String mdx ) {
        this.mdx = mdx;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage( String message ) {
        this.message = message;
    }
}
