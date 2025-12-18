using { sap.capire.bookshop as my } from '../db/schema';
service AdminService @(path:'/admin') {

  @Common.DraftRoot.NewAction: 'AdminService.createAuthorDraft'
  entity Authors as projection on my.Authors actions {
    action createAuthorDraft(in: many $self) returns Authors;
  };

  @Common.DraftRoot.NewAction: 'AdminService.createBookDraft'
  entity Books as projection on my.Books actions {
    action createBookDraft(in: many $self) returns Books;
  };

  entity Genres as projection on my.Genres;
}
