using System.Runtime.Serialization;
using System;

namespace Partner.Data {

  [DataContract(Namespace = "http://partner/external/payment/data/",
                Name = "PaymentRequest")]
  public class PaymentRequest
  {
    [DataMember]
    public string creditCard { get; set; }

    [DataMember]
    public double amount { get; set; }

    override public string ToString()
    {
      return "PaymentRequest[" + creditCard + ", " + amount + "]";
    }
  }

}
