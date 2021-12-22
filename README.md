# Isaiah Anason ORM
## Project Description
- This Project allows for automatically persisting annotated class models to PostgresSQL database

## How to use
#### Annotations
- How to use ORM
- Use Following Annotations on class fields
- @PrimaryKey - Must use one and only one.
- @NotNull - Fields that cannot have null value
- @Unique - Fields that must be unique for that Column of database
- @Column - Any other field you want stored in the database that doesn't
  contain constraints
#### ORM usage
- ORM.makeTable(Class)
- ORM.addRecord(Object)
- ORM.readRecordByID(Class,primaryKey)
- ORM.readAll(Class)
- ORM.updateRecord(Object)
- ORM.deleteRecordPrimaryKey(Class,primaryKey)
- ORM.dropTable(Class)

## Technologies Used
#### ORM Application
- Java 8
- JDBC
- PostgresSQL
- Apache maven
- AWS Technologies - CodePipeline, EC2, and RDS
#### Web Application 
https://github.com/211025-Enterprise/Isaiah_Anason-_Servlet_p1
- Java Servlet
- Jackson library (for JSON marshalling/unmarshalling)
- JUnit (TDD- Testing)
- Mockito (JUnit Mocking)
- Jacoco (code testing coverage)
## Contributors
- Isaiah Anason
