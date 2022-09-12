# teckit
Student Support Ticket API homework

## Building and Running

Use Maven to build into a JAR, put into a web server. Running it from IntelliJ works.

#### Database

The service expects a MySQL instance at `localhost`, with credentials of "test/test".

## Notes

### User management

Users are managed by a barebones API that only allows addition of a new user.

The admin flag can only be set by directly manipulating the datastore.

### Security

There's no security to speak of. The API trusts whatever userid is passed with the request. 
In a real system, either the service gets an auth layer, 
or (better) there is a front-door service dealing with auth so that 
the services doing the actual processing exist in a secure environment, and are only 
ever fed a verified caller.

### Data Storage Layer

For the purposes of the exercise, the storage solution is a local MySQL DB accessed 
through Spring JPA + Hibernate. For a small-scale system, handling up to a dozen RPS on average
with no major spikes, this would even be sufficient.

Things to do to "productionize" the storage layer at small scale:
1. Move the DB to a dedicated place with some extra reliability, backups etc.
2. Currently, the relational integrity is very basic and manual. JPA has capabilities to address that. Implementation 
   needs a bit more than a passing acquaintance with JPA though (which I learned as I went).
3. Currently, the DAL has no caching that I am aware of, and the data objects can be duplicated in memory. 
   I think JPA + Hibernate has built-in caching capabilities, but that, again, needs more research.
4. For a number of reasons, it would make sense to extract an interface out of the existing DAL 
   class, and have more than one implementation thereof.
   
If a larger scale is needed, and/or the system expands beyond just three tables of data, the data access
layer would need to be reworked, as the "one class with all methods that we need" approach won't hold.

### Tests and Coverage

The main controllers were built test-first, and have 100% coverage.

The user controller has no tests, being just a helper API I built before realizing that I could 
just manually insert rows into MySQL.

The DAL has no tests, either, although it is relatively structured, and in hindsight, I could have
done it test-first, too.

Therefore, it makes total sense that when I ran the service for the first time, the only bugs 
that I found were in the DAL class, and the controllers "just worked".

### Extras

One of the potential future features is sending notifications about relevant changes. One such direction is 
explored in `CommentController`, where a `NotificationSender` is invoked to potentially send notifications to 
every user who participated in the ticket (creator and every commenter). The call itself is covered by a test, 
but the `NotificationSender` is currently just a stub, containing a single empty method. 

## Roadmap

If I had to set a roadmap for the next month or so, here's what it would contain:

1. Sort out CI/CD and cloud deployment. Use the free tier of AWS, it can go surprisingly far.
1. Sort out auth.
1. Abstract out the DAL interface, make a better architected implementation using the underlying cloud's capabilities. 
   E.g. move onto AWS RDS or DynamoDB.
1. Add attachments, stored in S3. Modify the API accordingly.