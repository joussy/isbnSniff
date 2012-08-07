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
import com.ECS.client.jax.Request;
import isbnsniff.BookItem;
import isbnsniff.IsbnModule;
import isbnsniff.IsbnNumber;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.Holder;

/**
 *
 * @author jousse_s
 */
public class ModuleAmazon extends IsbnModule {
    final static String MODULE_NAME = "AmazonDb";
    private String associateTag;
    private String awsAccessKey; // Generated on https://portal.aws.amazon.com/gp/aws/securityCredentials
    private String secretAccessKey; // Secret Access Key generated on https://portal.aws.amazon.com/gp/aws/securityCredentials
    private ItemLookup lookup;
    private ItemLookupRequest itemRequest;
    private AWSECommerceServicePortType port;
    public ModuleAmazon(String tag, String accessKey, String secretKey)
    {
        moduleName = MODULE_NAME;
        associateTag = tag;
        awsAccessKey = accessKey;
        secretAccessKey = secretKey;
        // Set the service:
        AWSECommerceService service = new AWSECommerceService();
        service.setHandlerResolver(new AwsHandlerResolver(secretAccessKey));
        //Set the service port:
        port = service.getAWSECommerceServicePortFR();
    }
    private void processItemList(List<Items> l)
    {
        for (Items itemList : l)
        {
            Request requestElement = itemList.getRequest();
            for (Item item : itemList.getItem())
            {
                List<IsbnNumber> amazonIsbnList = new ArrayList();
                for (String nb : item.getItemAttributes().getEISBN())
                    amazonIsbnList.add(new IsbnNumber(nb));
                amazonIsbnList.add(new IsbnNumber(item.getItemAttributes().getISBN()));
                BookItem book = null;
                for (IsbnNumber isbn : amazonIsbnList)
                {
                    for (BookItem bookItem : getBookItemList())
                    {
                        if (isbn.equals(bookItem.getIsbn()))
                        {
                            book = bookItem;
                            break;
                        }
                    }
                }
                //System.out.println("getISBN=" + item.getItemAttributes().getISBN());
                //System.out.println("getEAN=" + item.getItemAttributes().getEAN());
                //System.out.println("getEANList=" + item.getItemAttributes().getEANList());
                //System.out.println("getEISBN=" + item.getItemAttributes().getEISBN());
                //System.out.println("getSKU=" + item.getItemAttributes().getSKU());
                if (book != null)
                {
                    book.setNbPages(item.getItemAttributes().getNumberOfPages().intValue());
                }
            }
        }
    }
    @Override
    protected void processQueryIsbn(BookItem book) {
        itemRequest.getItemId().add(book.getIsbn().getIsbn13());
    }

    @Override
    protected void processQueryInitialize() {
        lookup = new ItemLookup();
        //Get the operation object:
        itemRequest = new ItemLookupRequest();
        //Fill in the request object:
        itemRequest.setIdType("ISBN");
        itemRequest.setSearchIndex("Books");
    }

    @Override
    protected void processQueryTerminate() {
        lookup.setAWSAccessKeyId(awsAccessKey);
        lookup.getRequest().add(itemRequest);
        lookup.setAssociateTag(associateTag);
        itemRequest.getResponseGroup().add("ItemAttributes,ItemIds");

        Holder<OperationRequest> operationrequest = new Holder<OperationRequest>();
        Holder<java.util.List<Items>> items = new Holder<java.util.List<Items>>();
        //itemLookup : MarketplaceDomain, AWSAccessKeyId, AssociateTag, XMLEscaping, Validate, Shared, Request, OperationRequest, Items
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
        processItemList(items.value);
    }
}
