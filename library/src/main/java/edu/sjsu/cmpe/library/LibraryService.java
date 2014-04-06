package edu.sjsu.cmpe.library;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import edu.sjsu.cmpe.library.api.resources.BookResource;
import edu.sjsu.cmpe.library.api.resources.RootResource;
import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.message.broker.pubsub.Listener;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.ui.resources.HomeResource;

public class LibraryService extends Service<LibraryServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    public static void main(String[] args) throws Exception {
	new LibraryService().run(args);
    }

    @Override
    public void initialize(Bootstrap<LibraryServiceConfiguration> bootstrap) {
	bootstrap.setName("library-service");
	bootstrap.addBundle(new ViewBundle());
	bootstrap.addBundle(new AssetsBundle());
    }

    @Override
    public void run(LibraryServiceConfiguration configuration,
	    Environment environment) throws Exception {
	// This is how you pull the configurations from library_x_config.yml
	String queueName = configuration.getStompQueueName();
	String topicName = configuration.getStompTopicName();
	BookRepositoryInterface bookRepository = new BookRepository();
	backGroundThread(configuration, bookRepository);
	// TODO: Apollo STOMP Broker URL and login

	String apolloUser = configuration.getApolloUser();
	String apolloPassword = configuration.getApolloPassword();
	String apolloHost = configuration.getApolloHost();
	String apolloPort = configuration.getApolloPort();
	log.debug("{} - Queue name is {}. Topic name is {}. User name is {}. "
			+ "Password is {}. Host is {}. Port is {}.",
			configuration.getLibraryName(), queueName,
			topicName, apolloUser, apolloPassword, apolloHost, apolloPort);
	
	/** Root API */
	environment.addResource(RootResource.class);
	/** Books APIs */
	environment.addResource(new BookResource(bookRepository, configuration));

	/** UI Resources */
	environment.addResource(new HomeResource(bookRepository));
    }
    
    public void backGroundThread(final LibraryServiceConfiguration configuration, 
    		 final BookRepositoryInterface bookRepository)
    {
    	int numThreads = 1;
	    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
 
	    Runnable backgroundTask = new Runnable() {
 
    	    @Override
    	    public void run() {
    	    	Listener listener = new Listener(configuration, bookRepository);
    	    	try {
    	    		System.out.println("****#####*****#####");
					listener.listenFromTopic();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		System.out.println("Hello World");
    	    }
	    };
	    System.out.println("About to submit the background task");
	    try {
	    	executor.execute(backgroundTask);
	    	System.out.println("Submitted the background task");
	    }
	    catch(Exception e) {
	    	executor.shutdown();
	    	System.out.println("Finished the background task");
	    	e.printStackTrace();
	    }
    }
}
