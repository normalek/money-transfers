Money Transfers Application
===========
This Money Transfer application contains all the necessary `api` for
money transfer between accounts. User can open an `account` with an
initial deposit amount, an existing account holder can `deposit` or
`withdraw` amount which will instantly reflect on the account balance.
The apis provides the flexibility of money `transfer` between two
accounts. The `apis` are implemented with Test Driven Development and covered by
unit and integration test with concurrency.

How start the application
===========

- Clone the project from the repo
- Navigate to the source root folder `money-transfers`
- Execute `./mvnw clean package`. This will create the fat jar at `/target` folder.
- Navigate to `/target` folder and execute
`java -jar money-transfer-1.0.0.jar`

```
[Thread-1] INFO org.eclipse.jetty.util.log - Logging initialized @538ms to org.eclipse.jetty.util.log.Slf4jLog
[Thread-1] INFO spark.embeddedserver.jetty.EmbeddedJettyServer - == Spark has ignited ...
[Thread-1] INFO spark.embeddedserver.jetty.EmbeddedJettyServer - >> Listening on 0.0.0.0:4567
```

- App will start at default port `4567` and the apis can be accessed via
  `http://localhost:4567/<api path>`

API Details
===========

## Account ##

**Add a new user** - ```POST``` - ```http://localhost:4567/v1/account/add```
 
**Fetch all account details** - ```GET``` -
```http://localhost:4567/v1/account/all```

**Fetch details on a single account** - ```GET``` -
```http://localhost:4567/v1/account/100000```
 
**Deactivate account** - ```PUT``` -
```http://localhost:4567/v1/account/100000/deactivate``` 

**Activate account** - ```PUT``` -
```http://localhost:4567/v1/account/100000/activate```

**Make a deposit** - ```POST``` -
```http://localhost:4567/v1/account/deposit```
 
**Make a withdraw** - ```POST``` -
```http://localhost:4567/v1/account/withdraw```

## Transfer ##

**Make a transfer** - ```POST``` -
```http://localhost:4567/v1/transfer```
 
**Get details from an existing transfer** - ```GET``` -
```http://localhost:4567/v1/transfer/10000002```

Database Design
==============

H2 database with local persistence is being used for the application. The database
`ACCOUNT` and `TRANSFER` tables to keep the details
of customer accounts and their transfer history. The database also
stores an `unique transaction id` for every transfer for future
tracking. For auto generation of primary key `sequence` is being used in
both the tables. The database will be automatically created within the first app start 
and then the local H2-files will be used for further starts.
Table design can be found in transfers-scheme.sql file.

Error Handling
=============
- The data validation error code starts with 400
- The transaction error starts with 402 
- Other server run time exception is 500
 
Below are the two sample error scenario response.
 
```
{
    "errorCode": 400,
    "message": "Account must be active for further transactions",
    "type": "INVALID_ACCOUNT_STATUS"
}
```

```
{
    "errorCode": 404,
    "message": "Account doesn't exists",
    "type": "ACCOUNT_NOT_FOUND"
}
```

Testing
==========
- The unit testing code is available at
  ```test\java\com\revolut\transfers\unit``` folder
- The integration test is available at
  ```test\java\com\revolut\transfers\integration``` folder
- The integration test is implemented by using WireMock for mocking request/response.
- The multithreading tests use in-memory H2.
