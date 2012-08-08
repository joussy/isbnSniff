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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.SubnodeConfiguration;

public class ModuleGoogleBooks extends IsbnModule {

    final static String MODULE_NAME = "GoogleBooks";
    private String accessKey = null;
    private Books books = null;

    public ModuleGoogleBooks() {
        moduleName = MODULE_NAME;
    }

    @Override
    protected void processQueryIsbn(BookItem book) {
        String query = "isbn:" + book.getIsbn().getIsbn13();
        List volumesList = null;
        try {
            volumesList = books.volumes().list(query);
        } catch (IOException ex) {
            Logger.getLogger(ModuleGoogleBooks.class.getName()).log(Level.SEVERE, null, ex);
        }
        Volumes volumes = null;
        try {
            volumes = volumesList.execute();
        } catch (IOException ex) {
            Logger.getLogger(ModuleGoogleBooks.class.getName()).log(Level.SEVERE, null, ex);
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
            /*
            System.out.println("Author: ");
            java.util.List<String> authors = volumeInfo.getAuthors();
            if (authors != null && !authors.isEmpty()) {
            System.out.print("Author(s): ");
            for (int i = 0; i < authors.size(); ++i) {
            System.out.print(authors.get(i));
            }
            }
             * 
             */
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
