package org.saiku.web.export;

import org.saiku.web.rest.objects.resultset.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Patched version of org.saiku.web.export.PdfReport to remove dependency to iText
 *
 * @author akuehnel
 */
public class PdfReport {

    private static final Logger log = LoggerFactory.getLogger(PdfReport.class);

    public byte[] pdf(QueryResult qr, String svg) throws Exception {
        return null;
    }

}

