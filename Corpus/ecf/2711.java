package org.eclipse.ecf.tests.remoteservice.rest;

import java.io.NotSerializableException;
import java.util.Map;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.client.IRemoteCallParameter;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.IRemoteResponseDeserializer;
import org.eclipse.ecf.remoteservice.client.IRemoteServiceClientContainerAdapter;
import org.eclipse.ecf.remoteservice.client.RemoteCallParameter;
import org.eclipse.ecf.remoteservice.rest.RestCallFactory;
import org.eclipse.ecf.remoteservice.rest.RestCallableFactory;
import org.eclipse.ecf.remoteservice.rest.client.HttpPutRequestType;

@SuppressWarnings("unused")
public class RestPutServiceTest extends AbstractRestTestCase {

    private String username = System.getProperty("rest.test.username", "p126371rw");

    private String password = System.getProperty("rest.test.password", "demo");

    private String uri = System.getProperty("rest.test.uri", "http://phprestsql.sourceforge.net");

    private String resourcePath = System.getProperty("rest.test.resourcePath", "/tutorial/user/7");

    private String method = System.getProperty("rest.test.method", "putUser");

    private IContainer container;

    private IRemoteServiceRegistration registration;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Create container for service URI
        container = createRestContainer(uri);
        // Get adapter and set authentication info
        IRemoteServiceClientContainerAdapter adapter = (IRemoteServiceClientContainerAdapter) getRemoteServiceClientContainerAdapter(container);
        // Setup authentication
        adapter.setConnectContextForAuthentication(ConnectContextFactory.createUsernamePasswordConnectContext(username, password));
        // Setup response deserializer to do absolutely nothing (return null).  Note this is specific to this service.
        adapter.setResponseDeserializer(new IRemoteResponseDeserializer() {

            public Object deserializeResponse(String endpoint, IRemoteCall call, IRemoteCallable callable, Map responseHeaders, byte[] responseBody) throws NotSerializableException {
                return null;
            }
        });
        // Create callable and register
        IRemoteCallable callable = RestCallableFactory.createCallable(method, resourcePath, new IRemoteCallParameter[] { new RemoteCallParameter("body") }, new HttpPutRequestType(HttpPutRequestType.STRING_REQUEST_ENTITY, "application/xml", -1, "UTF-8"));
        // register callable
        registration = adapter.registerCallables(new IRemoteCallable[] { callable }, null);
    }

    public void testPutCallSync() throws Exception {
    //		IRemoteService restClientService = getRemoteServiceClientContainerAdapter(container).getRemoteService(registration.getReference());
    //		System.out.println("put uri="+uri+resourcePath);
    //		String body = createBody();
    //		System.out.println("body="+body);
    //		System.out.print("making remote method call="+method+"...");
    //		Object result = restClientService.callSync(RestCallFactory.createRestCall(method, new String[] { body }));
    //		System.out.println("received result="+result);
    }

    private String createBody() {
        return "firstname=Scott\nsurname=Example\nemail=slewis@example.org\ncompany_uid=1";
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        registration.unregister();
        container.disconnect();
    }
}
