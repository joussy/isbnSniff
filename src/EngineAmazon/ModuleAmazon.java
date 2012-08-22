/*
 */
package EngineAmazon;

import com.ECS.client.jax.AWSECommerceService;
import com.ECS.client.jax.AWSECommerceServicePortType;
import com.ECS.client.jax.Item;
import com.ECS.client.jax.ItemLookup;
import com.ECS.client.jax.ItemLookupRequest;
import com.ECS.client.jax.Items;
import com.ECS.client.jax.OperationRequest;
import isbnsniff.BookItem;
import isbnsniff.ConfigurationParser;
import isbnsniff.ConfigurationParserException;
import isbnsniff.IsbnFormatException;
import isbnsniff.IsbnModule;
import isbnsniff.IsbnModuleException;
import isbnsniff.IsbnNumber;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.ws.Holder;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 * @author jousse_s
 */
//@todo How many ISBNs at the same time ?
public class ModuleAmazon extends IsbnModule {

    final static String MODULE_NAME = "AmazonDb";
    private String associateTag;
    private String awsAccessKey; // Generated on https://portal.aws.amazon.com/gp/aws/securityCredentials
    private String secretAccessKey; // Secret Access Key generated on https://portal.aws.amazon.com/gp/aws/securityCredentials
    private ItemLookup lookup;
    private ItemLookupRequest itemRequest;
    private AWSECommerceServicePortType port;
    
    final private static int MAX_ITEMS_BY_QUERY = 3;
    final private static String K_ASSOCIATE_TAG = "api_associates_id";
    final private static String K_ACCESS_KEY = "api_access_key";
    final private static String K_SECRET_ACCESS_KEY = "api_secret_key";
    final private static String[] K_LIST = {K_ASSOCIATE_TAG, K_ACCESS_KEY, K_SECRET_ACCESS_KEY};

    /**
     * Amazon Engine implementation
     * Use Amazon Java dedicated webservice
     */
    public ModuleAmazon() {
        moduleName = MODULE_NAME;
    }

    private void processItemList(List<Items> l) {
        for (Items itemList : l) {
            for (Item item : itemList.getItem()) {
                List<IsbnNumber> amazonIsbnList = new ArrayList<IsbnNumber>();
                if (item.getItemAttributes() != null) {
                    for (String nb : item.getItemAttributes().getEISBN()) {
                        try {
                            amazonIsbnList.add(new IsbnNumber(nb));
                        } catch (IsbnFormatException ex) {}
                    }
                    try {
                        amazonIsbnList.add(new IsbnNumber(item.getItemAttributes().getISBN()));
                    } catch (IsbnFormatException ex) {}
                }
                BookItem book = null;
                for (IsbnNumber isbn : amazonIsbnList) {
                    for (BookItem bookItem : getBookItemList()) {
                        if (isbn.equals(bookItem.getIsbn())) {
                            book = bookItem;
                            break;
                        }
                    }
                }
                if (book != null) {
                    processItem(item, book);
                }
            }
        }
    }

    private void processItem(Item item, BookItem book) {
        if (item.getItemAttributes() != null) {
            book.setTitle(item.getItemAttributes().getTitle());
            if (item.getItemAttributes().getNumberOfPages() != null)
                book.setNbPages(item.getItemAttributes().getNumberOfPages().intValue());
            String publicationDateString = item.getItemAttributes().getPublicationDate();
            if (publicationDateString != null) {
                Date publicationDate = null;
                try {
                    publicationDate = new SimpleDateFormat("yyyy-MM-dd").parse(publicationDateString);
                } catch (ParseException ex) {
                    try {
                        publicationDate = new SimpleDateFormat("yyyy-MM").parse(publicationDateString);
                    } catch (ParseException ex2) {
                        try {
                            publicationDate = new SimpleDateFormat("yyyy").parse(publicationDateString);
                        } catch (ParseException ex1) {
                        }
                    }
               }
                book.setPublicationDate(publicationDate);
            }
            if (item.getItemAttributes().getCategory() != null) {
                for (String category : item.getItemAttributes().getCategory()) {
                    book.addCategory(category);
                }
            }
            if (item.getItemAttributes().getAuthor() != null) {
                for (String category : item.getItemAttributes().getAuthor()) {
                    book.addAuthor(category);
                }
            }
            book.setPublisher(item.getItemAttributes().getPublisher());
        }
    }

    /**
     * 
     * @param book
     */
    @Override
    protected void processQueryIsbn(BookItem book) {
        //itemRequest.getItemId().add(book.getIsbn().getIsbn13());
    }

    /**
     * instantiate Amazon webservice and set API keys
     * @throws IsbnModuleException
     */
    @Override
    protected void processQueryInitialize() throws IsbnModuleException {
        // Set the service:
        AWSECommerceService service = new AWSECommerceService();
        service.setHandlerResolver(new AwsHandlerResolver(secretAccessKey));
        //Set the service port:
        try {
        port = service.getAWSECommerceServicePortUK();
        } catch (Exception ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_WEBSERVICE, ex.getMessage(), Level.SEVERE);
        }
        lookup = new ItemLookup();
        lookup.setAWSAccessKeyId(awsAccessKey);
        lookup.setAssociateTag(associateTag);
        //Get the operation object:
        itemRequest = new ItemLookupRequest();
        //Fill in the request object:
        itemRequest.setIdType("ISBN");
        itemRequest.setSearchIndex("Books");
    }

    /**
     * Specify all the ISBNs and query the Amazon Server.
     * @throws IsbnModuleException
     */
    @Override
    protected void processQueryTerminate() throws IsbnModuleException {
        
        itemRequest.getResponseGroup().add("ItemAttributes,ItemIds");
        for (int i = 0; i < bookItemList.size();) {
            itemRequest.getItemId().clear();
            for (int j = 0; j < MAX_ITEMS_BY_QUERY && i < bookItemList.size(); j++, i++) {
                itemRequest.getItemId().add(bookItemList.get(i).getIsbn().getIsbn13());
                //System.out.println("DEBUG, I=" + i + " J=" + j + "ISBN=" + bookItemList.get(i).getIsbn().getIsbn13());
            }
            lookup.getRequest().clear();
            lookup.getRequest().add(itemRequest);
            
            Holder<OperationRequest> operationrequest = new Holder<OperationRequest>();
            Holder<java.util.List<Items>> items = new Holder<java.util.List<Items>>();
            //itemLookup : MarketplaceDomain, AWSAccessKeyId, AssociateTag, XMLEscaping, Validate, Shared, Request, OperationRequest, Items
            try
            {
                port.itemLookup(
                        lookup.getMarketplaceDomain(),
                        lookup.getAWSAccessKeyId(),
                        lookup.getAssociateTag(),
                        lookup.getXMLEscaping(),
                        lookup.getValidate(),
                        lookup.getShared(),
                        lookup.getRequest(),
                        operationrequest,
                        items);
            } catch (Exception ex) {
                throw new IsbnModuleException(IsbnModuleException.ERR_WEBSERVICE, ex.getMessage(), Level.SEVERE);
            }
            processItemList(items.value);
        }
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
        associateTag = valueList.get(K_ASSOCIATE_TAG);
        awsAccessKey = valueList.get(K_ACCESS_KEY);
        secretAccessKey = valueList.get(K_SECRET_ACCESS_KEY);
    }
}
