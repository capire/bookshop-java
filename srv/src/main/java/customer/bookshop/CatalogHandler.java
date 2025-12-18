package customer.bookshop;

import org.springframework.stereotype.Component;

import com.sap.cds.services.ServiceException;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnUpdate;

import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;

import cds.gen.catalogservice.Books;
import cds.gen.catalogservice.Books_;
import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.ListOfBooks;
import cds.gen.catalogservice.SubmitOrderContext;

import java.util.stream.Stream;

@Component
@ServiceName(CatalogService_.CDS_NAME)
public class CatalogHandler implements EventHandler {

  private final PersistenceService persistenceService;

  public CatalogHandler(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }

  @After(event = CqnService.EVENT_READ)
  public void AfterReadListOfBooks(Stream<ListOfBooks> books) {
    books.forEach(book -> {
      if(book.getStock()>111)
        book.setTitle(book.getTitle()+" -- 11% discount!");
    });
  }

  @On(event = SubmitOrderContext.CDS_NAME)
  public SubmitOrderContext.ReturnType submitOrder(SubmitOrderContext context) {
    Integer quantity = context.getQuantity();
    if(quantity<=0)
      throw new ServiceException(ErrorStatuses.BAD_REQUEST, "quantity has to be 1 or more").messageTarget("submitOrder");
    Integer booksLeft = updateBookQuantity(context.getBook(), context.getQuantity());
    SubmitOrderContext.ReturnType response = SubmitOrderContext.ReturnType.create();
    response.setStock(booksLeft);
    return response;
  }

  private Integer updateBookQuantity(Integer bookId, Integer quantity) {
    Result found = persistenceService.run(Select
      .from(Books_.CDS_NAME)
      .where(b -> b.get("ID").eq(bookId)));
    Books book = found.single(Books.class);
    Integer inStock = book.getStock();

    Integer booksLeft = inStock-quantity;
    if(booksLeft<0)
      throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Book("+bookId+") " + quantity + " exceeds stock").messageTarget("submitOrder");

    CqnUpdate update = Update.entity(Books_.CDS_NAME)
      .byId(book.getId())
      .set(Books.STOCK, CQL.get(Books.STOCK).minus(quantity));

    Result updateResult = persistenceService.run(update);
    updateResult.single(Books.class);
    return booksLeft;
  }
}
