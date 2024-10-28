package intellispaces.ixora.samples.http.echo;

import intellispace.ixora.jetty.JettyServerPorts;
import intellispace.ixora.okhttp.OkHttpPorts;
import intellispaces.common.base.collection.ArraysFunctions;
import intellispaces.ixora.cli.CliConfiguration;
import intellispaces.ixora.cli.MovableConsole;
import intellispaces.ixora.http.HttpMethods;
import intellispaces.ixora.http.HttpRequest;
import intellispaces.ixora.http.HttpRequests;
import intellispaces.ixora.http.HttpResponse;
import intellispaces.ixora.http.MovableInboundHttpPort;
import intellispaces.ixora.http.MovableOutboundHttpPort;
import intellispaces.ixora.internet.UrlToQueryParamValuesGuideImpl;
import intellispaces.jaquarius.annotation.Inject;
import intellispaces.jaquarius.annotation.Module;
import intellispaces.jaquarius.annotation.Projection;
import intellispaces.jaquarius.annotation.Startup;
import intellispaces.jaquarius.system.Modules;
import intellispaces.jaquarius.object.ObjectFunctions;

import java.nio.charset.StandardCharsets;

@Module({
    EchoPortExchangeGuideImpl.class,
    UrlToQueryParamValuesGuideImpl.class,
    CliConfiguration.class
})
public class EchoPortSample1 {

  @Projection
  public MovableInboundHttpPort inboundPort() {
    MovableInboundHttpPort underlyingPort = JettyServerPorts.get(
        8080, EchoPortExchangeChannel.class
    ).asInboundHttpPort();
    MovableEchoPort echoPort = EchoPorts.get(underlyingPort);
    underlyingPort.addProjection(EchoPortDomain.class, echoPort);
    return echoPort.asInboundHttpPort();
  }

  @Projection
  public MovableOutboundHttpPort outboundPort() {
    return OkHttpPorts.get().asOutboundHttpPort();
  }

  @Startup
  public void startup(@Inject MovableConsole console) {
    // Open inbound (server) port
    inboundPort().open();

    // Call inbound port
    HttpResponse response = null;
    try {
      HttpRequest request = HttpRequests.get(HttpMethods.get(), "http://localhost:8080/echo?msg=Hello!");
      response = outboundPort().exchange(request);

      byte[] responseBodyBytes = ArraysFunctions.toByteArray(response.bodyStream().readAll().nativeList());
      String responseMessage = new String(responseBodyBytes, StandardCharsets.UTF_8);
      console.println(responseMessage);
    } finally {
      ObjectFunctions.releaseSilently(response);
    }

    // Close inbound port
    inboundPort().close();
  }

  /**
   * In the main method, we load and run the IntelliSpaces framework module.
   */
  public static void main(String[] args) {
    Modules.get(EchoPortSample1.class, args).start();
  }
}