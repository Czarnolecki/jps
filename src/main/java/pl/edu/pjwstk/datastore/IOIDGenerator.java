package pl.edu.pjwstk.datastore;

import edu.pjwstk.jps.datastore.IOID;

public interface IOIDGenerator {
    IOID entryOid();
    IOID generate();
}
