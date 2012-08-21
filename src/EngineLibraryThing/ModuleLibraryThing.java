/*
 */
package EngineLibraryThing;

import EngineLibraryThing.Ltml.Item;
import EngineLibraryThing.Ltml.Item.Commonknowledge.FieldList;
import EngineLibraryThing.Ltml.Item.Commonknowledge.FieldList.Field;
import EngineLibraryThing.Ltml.Item.Commonknowledge.FieldList.Field.VersionList.Version;
import isbnsniff.BookItem;
import isbnsniff.ConfigurationParser;
import isbnsniff.ConfigurationParserException;
import isbnsniff.IsbnModule;
import isbnsniff.IsbnModuleException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 * @author jousse_s
 */
public class ModuleLibraryThing extends IsbnModule {

    final static String MODULE_NAME = "LibraryThing";
    final private static String K_API_KEY = "api_key";
    final private static String[] K_LIST = {K_API_KEY};

    private String accessKey;
    private Unmarshaller unmarshaller = null;

    public ModuleLibraryThing() throws IsbnModuleException {
        moduleName = MODULE_NAME;
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class, Response.class);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage(), Level.SEVERE);
        }
    }

    public void setaccessKey(String value) {
        accessKey = value;
    }

    @Override
    protected void processQueryIsbn(BookItem book) throws IsbnModuleException {
        URL query = null;
        Response libraryThingXML = null;
        String path = "http://www.librarything.com/services/rest/1.1/?method=librarything.ck.getwork&isbn="
                + book.getIsbn().getIsbn13() + "&apikey=" + accessKey;
        try {
            query = new URL(path);
        } catch (MalformedURLException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_URL, ex.getMessage(), Level.WARNING);
        }
        try {
            libraryThingXML = (Response) unmarshaller.unmarshal(query);
        } catch (JAXBException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage(), Level.SEVERE);
        }
        processLibraryThingTree(libraryThingXML, book);
    }

    @Override
    protected void processQueryInitialize() {
    }

    @Override
    protected void processQueryTerminate() {
    }

    private void processLibraryThingTree(Response libraryThingXML, BookItem book) throws IsbnModuleException {
        if (libraryThingXML == null) {
            throw new IsbnModuleException(IsbnModuleException.ERR_UNEXPECTED_FORMAT, null, Level.INFO);
        }
        if (libraryThingXML.getLtml() == null) {
            String msg = null;
            if (libraryThingXML.getErr() != null) {
                msg = libraryThingXML.getErr().getValue();
            }
            throw new IsbnModuleException(IsbnModuleException.ERR_UNEXPECTED_FORMAT, msg, Level.INFO);
        }
        if (libraryThingXML.getLtml().getItem() != null) {
            Item bookItem = libraryThingXML.getLtml().getItem();
            if (bookItem.getAuthor() != null) {
                book.addAuthor(bookItem.getAuthor().getValue());
            }
            processCommonKnowledge(bookItem, book);
        }
    }

    private void processCommonKnowledge(Item bookItem, BookItem book) {
        if (bookItem.getCommonknowledge() != null) {
            FieldList fieldList = bookItem.getCommonknowledge().getFieldList();
            book.setTitle(getCommonKnowledgeFact(fieldList, "canonicaltitle"));
            book.setSynopsis(getCommonKnowledgeFact(fieldList, "description"));
            //PublicationDate
            String publicationDate =
                    getCommonKnowledgeFact(fieldList, "originalpublicationdate");
            if (publicationDate != null)
            {
                Pattern p = Pattern.compile("[0-9]{4}");
                Matcher m = p.matcher(publicationDate);
                if (m.find()) {
                    try {
                        book.setPublicationDate(new SimpleDateFormat("yyyy").parse(m.group()));
                    } catch (ParseException ex) {
                    }
                }
            }
        }
        //bookItem.getCommonknowledge().getFieldList().getField().get(0).getVersionList().getVersion().getFactList().getFact()
    }

    private String getCommonKnowledgeFact(FieldList fieldList, String fieldName) {
        List<String> factList = getCommonKnowledgeFactList(fieldList, fieldName);
        if (factList != null) {
            if (factList.size() > 0) {
                return factList.get(0);
            }
        }
        return null;
    }

    private List<String> getCommonKnowledgeFactList(FieldList fieldList, String fieldName) {
        if (fieldList == null) {
            return null;
        }
        for (Field field : fieldList.getField()) {
            if (field.getName().equals(fieldName)) {
                for (Version v : field.getVersionList().getVersion()) {
                    if (v.getLang().equals("eng")) {
                        return v.getFactList().getFact();
                    }
                }
                return field.getVersionList().getVersion().get(0).getFactList().getFact();
            }
        }
        return null;
    }

    @Override
    protected void setConfigurationSpecific(SubnodeConfiguration sObj)
            throws ConfigurationParserException {
        Map<String, String> valueList
                = ConfigurationParser.getSpecificModuleValues(sObj, K_LIST);
        accessKey = valueList.get(K_API_KEY);
    }
}
