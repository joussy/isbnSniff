/*
 */

package EngineLibraryThing;


import EngineLibraryThing.Ltml.Item;
import EngineLibraryThing.Ltml.Item.Commonknowledge.FieldList;
import EngineLibraryThing.Ltml.Item.Commonknowledge.FieldList.Field;
import EngineLibraryThing.Ltml.Item.Commonknowledge.FieldList.Field.VersionList.Version;
import isbnsniff.BookItem;
import isbnsniff.IsbnModule;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.List;


/**
 *
 * @author jousse_s
 */

public class ModuleLibraryThing extends IsbnModule {
    final static String MODULE_NAME = "LibraryThing";
    private String accessKey;
    private Unmarshaller unmarshaller = null;

    public ModuleLibraryThing(String key)
    {
        moduleName = MODULE_NAME;
        accessKey = key;
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class, Ltml.class, Response.class);
            unmarshaller = jc.createUnmarshaller();
        }
        catch (JAXBException ex) {
            Logger.getLogger(ModuleLibraryThing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setaccessKey(String value)
    {
        accessKey = value;
    }
    @Override
    protected void processQueryIsbn(BookItem book) {
        URL query = null;
        Response libraryThingXML = null;
        String path = "http://www.librarything.com/services/rest/1.1/?method=librarything.ck.getwork&isbn="
                + book.getIsbn().getIsbn13() + "&apikey=" + accessKey;
        try {
            query = new URL(path);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ModuleLibraryThing.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            libraryThingXML = (Response)unmarshaller.unmarshal(query);
        } catch (JAXBException ex) {
            Logger.getLogger(ModuleLibraryThing.class.getName()).log(Level.SEVERE, null, ex);
        }
        processLibraryThingTree(libraryThingXML, book);
    }
    @Override
    protected void processQueryInitialize() {
    }

    @Override
    protected void processQueryTerminate() {
    }

    private void processLibraryThingTree(Response libraryThingXML, BookItem book) {
        if (libraryThingXML != null)
            if (libraryThingXML.getLtml() != null)
                if (libraryThingXML.getLtml().getItem() != null)
                {
                    Item bookItem = libraryThingXML.getLtml().getItem();
                    processCommonKnowledge(bookItem, book);
                }
    }

    private void processCommonKnowledge(Item bookItem, BookItem book) {
        if (bookItem.getCommonknowledge() != null)
        {
            FieldList fieldList = bookItem.getCommonknowledge().getFieldList();
            book.setTitle(getCommonKnowledgeFact(fieldList, "canonicaltitle"));
        }
            //bookItem.getCommonknowledge().getFieldList().getField().get(0).getVersionList().getVersion().getFactList().getFact()
    }
    private String getCommonKnowledgeFact(FieldList fieldList, String fieldName)
    {
        List<String> factList = getCommonKnowledgeFactList(fieldList, fieldName);
        if (factList != null)
        {
            if (factList.size() > 0)
                return factList.get(0);
        }
        return null;
    }
    private List<String> getCommonKnowledgeFactList(FieldList fieldList, String fieldName)
    {
        if (fieldList == null)
            return null;
        for (Field field : fieldList.getField())
        {
            if (field.getName().equals(fieldName))
            {
                for (Version v : field.getVersionList().getVersion())
                {
                    if (v.getLang().equals("eng"))
                        return v.getFactList().getFact();
                }
                return field.getVersionList().getVersion().get(0).getFactList().getFact();
            }
        }
        return null;
    }
}
