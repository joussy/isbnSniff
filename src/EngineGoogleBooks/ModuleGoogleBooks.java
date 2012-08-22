package EngineGoogleBooks;

import com.google.api.client.googleapis.services.GoogleKeyInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.Books.Volumes.List;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;
import isbnsniff.BookItem;
import isbnsniff.ConfigurationParser;
import isbnsniff.ConfigurationParserException;
import isbnsniff.IsbnModule;
import isbnsniff.IsbnModuleException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 * Implementation of Google Books Search Engine, using the dedicated Java API
 * @author jousse_s
 */
public class ModuleGoogleBooks extends IsbnModule {

    final static String MODULE_NAME = "GoogleBooks";
    final private static String K_API_KEY = "api_key";
    final private static String[] K_LIST = {K_API_KEY};

    private String accessKey = null;
    private Books books = null;

    /**
     * 
     */
    public ModuleGoogleBooks() {
        moduleName = MODULE_NAME;
    }

    /**
     * For each BookItem, perform a request an retreive the results
     * @param book
     * @throws IsbnModuleException
     */
    @Override
    protected void processQueryIsbn(BookItem book) throws IsbnModuleException {
        String query = "isbn:" + book.getIsbn().getIsbn13();
        List volumesList = null;
        try {
            volumesList = books.volumes().list(query);
        } catch (IOException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_WEBSERVICE, ex.getMessage(), Level.WARNING);
        }
        Volumes volumes = null;
        try {
            volumes = volumesList.execute();
        } catch (IOException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_WEBSERVICE, ex.getMessage(), Level.WARNING);
        }
        if (volumes.getTotalItems() == 0 || volumes.getItems() == null) {
            return;
        } else {
            processVolumes(volumes, book);
        }
    }

    private void processVolumes(Volumes volumes, BookItem book) {
        for (Volume volume : volumes.getItems()) {
            book.setTitle(volume.getVolumeInfo().getTitle());
            book.setNbPages(volume.getVolumeInfo().getPageCount());
            if (volume.getVolumeInfo() != null) {
                if (volume.getVolumeInfo().getAuthors() != null) {
                    for (String author : volume.getVolumeInfo().getAuthors()) {
                        book.addAuthor(author);
                    }
                }
                if (volume.getVolumeInfo().getCategories() != null) {
                    for (String category : volume.getVolumeInfo().getCategories()) {
                        book.addCategory(category);
                    }
                }
                book.setSynopsis(volume.getVolumeInfo().getDescription());
                String publicationDate = volume.getVolumeInfo().getPublishedDate();
                if (publicationDate != null) {
                    try {
                        book.setPublicationDate(new SimpleDateFormat("yyyy-MM-dd").parse(publicationDate));
                    } catch (ParseException ex) {
                    }
                }
            }
        }
    }

    /**
     * Initialize Google Books API with specified API Key
     */
    @Override
    protected void processQueryInitialize() {
        JsonHttpRequestInitializer credential = new GoogleKeyInitializer(accessKey);
        // Set up Books client.

        JsonFactory jsonFactory = new JacksonFactory();
        books = new Books.Builder(new NetHttpTransport(), jsonFactory, null).setApplicationName("Google-BooksSample/1.0").setJsonHttpRequestInitializer(credential).build();
    }

    /**
     * 
     */
    @Override
    protected void processQueryTerminate() {
    }

    /**
     * 
     * @param sObj
     * @throws ConfigurationParserException
     */
    @Override
    protected void setConfigurationSpecific(SubnodeConfiguration sObj)
            throws ConfigurationParserException {
        Map<String, String> valueList
                = ConfigurationParser.getSpecificModuleValues(sObj, K_LIST);
        accessKey = valueList.get(K_API_KEY);
    }
}
