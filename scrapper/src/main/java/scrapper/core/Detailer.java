package scrapper.core;

import org.jsoup.nodes.Document;

public interface Detailer {
    int scrapPagesNumber(Document page);
}
