using System;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Collections.Generic;

using Partner.Data;

namespace Partner.Service {

  [ServiceContract]
  public interface IPaymentService
  {
    [OperationContract]
    [WebInvoke( Method = "POST", UriTemplate = "mailbox",
                RequestFormat = WebMessageFormat.Json,
                ResponseFormat = WebMessageFormat.Json)]
    int ReceiveRequest(PaymentRequest request);

}

}
