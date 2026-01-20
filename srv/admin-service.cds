using { sap.capire.bookshop as my } from '../db/schema';
using from './admin-constraints';

service AdminService @(path:'/admin') {

  entity Authors as projection on my.Authors;
  entity Books as projection on my.Books;
  entity Genres as projection on my.Genres;

}

annotate AdminService with @odata;
// Additionally serve via HCQL and REST
// missing in java: annotate AdminService with @hcql @rest;
