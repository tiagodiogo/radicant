This is the response to the Radicant Coding Challenge requested on 17/02/2023

You may find the source code at:

- https://github.com/tiagodiogo/radicant

You may check out or download the source code to your machine via either:

- git clone https://github.com/tiagodiogo/radicant.git
- https://github.com/tiagodiogo/radicant/archive/refs/heads/master.zip

In order to speed up development a code generator was used.
A small frontend is included that gives tou (useful) access to a Swagger API where you can test the endpoints.
Please ignore the majority of the files, what really matters is at:

- /src/main/java/com/tiagodiogo/radicant
- /web.rest/PhoneBookResource
- /service/PhoneBookService
- /repository/PhoneBookDatabase
- /domain/PhoneBookRecord
- /domain/IDatabase

Implementation details

- I've created a small model (PhoneBookRecord) to represent the entities being stored in CSV format
- The database layer implements the provided Interface. Everything up works with the created domain
- The resource is fully documented and makes use of the proper http verbs and response codes
- The service acts as a bridge and mapper between the resource and the database
- A locking mechanism was used to handle concurrent operations
- A try with resources approach was used to ensure the file is closed at the end of the statement
- Logging is performed across the request with different levels

Given the allotted time some future improvements could be

- Use a CSV library like Apache Commons CSV to simplify the file handling
- Use streaming techniques if there are large file requirements
- Add integration tests that validate the HTTP codes and error cases
- Improve the locking mechanism to introduce fairness to the write access
- Move the file name/path declaration to the application properties

You may compile the source code into an executable jar and run it by executing:

- ./mvnw package -P prod,api-docs
- java -jar radicant-0.0.1-SNAPSHOT.jar

This will also run the included tests. Tests need access to /tmp for file creation, modification and deletion.

By navigating to http://localhost:8080/admin/docs you can access the Swagger API

- user: admin
- pass: admin

If you have any questions or something goes wrong please let me know.

Kind Regards,

Tiago
