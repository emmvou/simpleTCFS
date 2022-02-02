# TCF Third-part system (.Net)

  * Author: Sébastien Mosser 
  * Maintainer: Philippe Collet 
  * Reviewer: Anne-Marie Déry
  * Version: 03.2022

  
Third-part systems are implemented in this case study as a .Net service using the REST paradigm, implemented using the C# language.

To simplify the implementation and deployment, considering that this course is dedicated to software architecture and not to C# programming, we rely on a self-hosted server and the `Mono` implementation of the .Net framework. 

It gives us portability and a light-weighted way to deploy a third-part system. This is clearly not intended as is for production.

The provided service defines a _Payment_ service, one can use it to process credit card requests.

## Code architecture

The code is kept as simple as possible, and consists in only four files:

  * `BusinessObjects.cs`: The data structure to be used to support the payment service: `PaymentRequest`
  * `IPaymentService.cs`: the interface that models the resources exposed by the service, a `mailbox` to receive `PaymentRequest`s;  
  * `PaymentService.cs`: the concrete class that implement the previously described interface;
  * `Server.cs`: this main class starts an HTTP server and binds the implemented service to it.

## Mocked logic

The service will accept all payment requests in which the credit card number contains a magic key (896983), the ASCII code for "YES"...
    
## Running the service

To compile the service, you need to use a version of Mono that bundles the _Windows Communication Foundations_ (WCF) framework. Recent versions of mono include it natively. To compile all the C# source code with the WCF package available and create a `server.exe` binary, simply run the `compile.sh` command.
  
Then, one can start the `server.exe` using the mono runtime environment:

    $ mono server.exe
    Starting a WCF self-hosted .Net server... done!
    
    Listening to localhost:9090
    
    Hit Return to shutdown the server.  
    
    
    