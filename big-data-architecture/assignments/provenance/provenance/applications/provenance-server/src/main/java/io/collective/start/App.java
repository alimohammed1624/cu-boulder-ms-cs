package io.collective.start;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.collective.articles.ArticleDataGateway;
import io.collective.articles.ArticleRecord;
import io.collective.articles.ArticlesController;
import io.collective.endpoints.EndpointTask;
import io.collective.endpoints.EndpointWorkFinder;
import io.collective.endpoints.EndpointWorker;
import io.collective.endpoints.EndpointDataGateway;
import io.collective.workflow.WorkScheduler;
import io.collective.restsupport.BasicApp;
import io.collective.restsupport.RestTemplate;
import io.collective.restsupport.NoopController;
import org.eclipse.jetty.server.handler.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class App extends BasicApp {
    private static ArticleDataGateway articleDataGateway = new ArticleDataGateway(List.of(
            new ArticleRecord(10101, "Programming Languages InfoQ Trends Report - October 2019 4", true),
            new ArticleRecord(10106,
                    "Ryan Kitchens on Learning from Incidents at Netflix, the Role of SRE, and Sociotechnical Systems",
                    true)));

    @Override
    public void start() {
        super.start();

        { // todo - start the endpoint worker
            EndpointDataGateway gateway = new EndpointDataGateway();
            RestTemplate template = new RestTemplate();
            EndpointWorkFinder finder = new EndpointWorkFinder(gateway);
            EndpointWorker worker = new EndpointWorker(template, articleDataGateway);
            List<EndpointWorker> workers = new ArrayList<>();
            workers.add(worker);
            WorkScheduler<EndpointTask> scheduler = new WorkScheduler<>(finder, workers, 300);
            scheduler.start();
        }
    }

    public App(int port) {
        super(port);
    }

    @NotNull
    @Override
    protected HandlerList handlerList() {
        HandlerList list = new HandlerList();
        list.addHandler(new ArticlesController(new ObjectMapper(), articleDataGateway));
        list.addHandler(new NoopController());
        return list;
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        String port = System.getenv("PORT") != null ? System.getenv("PORT") : "8881";
        App app = new App(Integer.parseInt(port));
        app.start();
    }
}
