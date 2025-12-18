using { sap.capire.bookshop as my } from '../db/schema';

@requires: 'any'
@path:'/browse' @odata @rest @hcql
service CatalogService {

  /** For displaying lists of Books */
  @readonly entity ListOfBooks as projection on Books {
    *, genre.name as genre, currency.symbol as currency,
  } excluding { descr, genre, currency };

  /** For display in details pages */
  @readonly entity Books as projection on my.Books {
    *, author.name as author
  } excluding { createdBy, modifiedBy, author };

  @requires: 'authenticated-user'
  action submitOrder ( book: Books:ID, quantity: Integer ) returns { stock: Integer };

}
