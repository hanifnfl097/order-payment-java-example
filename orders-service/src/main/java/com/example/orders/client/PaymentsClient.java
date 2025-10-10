
package com.example.orders.client;
import com.example.orders.dto.OrderWithPaymentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
@Component public class PaymentsClient {
  private final RestClient rest; private final String baseUrl;
  public PaymentsClient(RestClient r, @Value("${PAYMENTS_BASE_URL:http://localhost:8082}") String base){ this.rest=r; this.baseUrl=base; }
  public OrderWithPaymentDTO.Payment getPaymentByOrderId(Long orderId){
    var resp = rest.get().uri(baseUrl + "/api/payments/order/{id}", orderId).accept(MediaType.APPLICATION_JSON).retrieve().toEntity(String.class);
    if (resp.getStatusCode().is2xxSuccessful() && resp.getBody()!=null && !resp.getBody().isEmpty()){
      String body = resp.getBody();
      Long id = extractLong(body, "id"); Long oid = extractLong(body, "orderId"); Double amount = extractDouble(body, "amount");
      String status = extractString(body, "status"); String createdAt = extractString(body, "createdAt");
      return new OrderWithPaymentDTO.Payment(id, oid, amount, status, createdAt);
    }
    return null;
  }
  private Long extractLong(String json, String key){ try{ var m=json.replaceAll("[{}\\"]","").split(","); for(var kv:m){ var p=kv.split(":"); if(p.length==2 && p[0].trim().equals(key)) return Long.valueOf(p[1].trim()); } }catch(Exception ignored){} return null; }
  private Double extractDouble(String json, String key){ try{ var m=json.replaceAll("[{}\\"]","").split(","); for(var kv:m){ var p=kv.split(":"); if(p.length==2 && p[0].trim().equals(key)) return Double.valueOf(p[1].trim()); } }catch(Exception ignored){} return null; }
  private String extractString(String json, String key){ try{ int idx=json.indexOf("""+key+"""); if(idx>=0){ int colon=json.indexOf(":",idx); int start=json.indexOf(""",colon+1)+1; int end=json.indexOf(""",start); return json.substring(start,end);} }catch(Exception ignored){} return null; }
}
