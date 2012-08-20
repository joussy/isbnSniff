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
import isbnsniff.IsbnModule;
import isbnsniff.IsbnModuleException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.commons.configuration.SubnodeConfiguration;

public class ModuleGoogleBooks extends IsbnModule {

    final static String MODULE_NAME = "GoogleBooks";
    private String accessKey = null;
    private Books books = null;

    public ModuleGoogleBooks() {
        moduleName = MODULE_NAME;
    }

    @Override
    protected void processQueryIsbn(BookItem book) throws IsbnModuleException {
        String query = "isbn:" + book.getIsbn().getIsbn13();
        List volumesList = null;
        try {
            volumesList = books.volumes().list(query);
        } catch (IOException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_WEBSERVICE, ex.getMessage());
        }
        Volumes volumes = null;
        try {
            volumes = volumesList.execute();
        } catch (IOException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_WEBSERVICE, ex.getMessage());
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
                for (String author : volume.getVolumeInfo().getAuthors()) {
                    book.addAuthor(author);
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

    @Override
    protected void processQueryInitialize() {
        JsonHttpRequestInitializer credential = new GoogleKeyInitializer(accessKey);
        // Set up Books client.

        JsonFactory jsonFactory = new JacksonFactory();
        books = new Books.Builder(new NetHttpTransport(), jsonFactory, null).setApplicationName("Google-BooksSample/1.0").setJsonHttpRequestInitializer(credential).build();
    }

    @Override
    protected void processQueryTerminate() {
    }

    @Override
    public void setConfigurationSpecific(SubnodeConfiguration sObj) {
        accessKey = sObj.getString("api_key", "undefined");
    }
}
