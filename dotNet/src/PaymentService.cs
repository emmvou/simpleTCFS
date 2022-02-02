using System;
using System.Net;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Collections.Generic;
using System.Linq;
using Partner.Data;

namespace Partner.Service {

  // The service is stateful, as it is only a Proof of Concept.
  // Services should be stateless, this is for demonstration purpose only.
  [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single)]
  public class PaymentService : IPaymentService
  {
    private const string magicKey = "896983"; // ASCII code for "YES"

    private int counter;

    public int ReceiveRequest(PaymentRequest request)
    {
      Console.WriteLine("ReceiveRequest: " + request);
      if (request.creditCard.Contains(magicKey)) {
        counter++;
        Console.WriteLine("OK for payment, sending back: " + counter);
        return counter;
      } else {
        Console.WriteLine("*NOK* for payment, sending back: 0");
        return 0;
      }
    }
  }
}
