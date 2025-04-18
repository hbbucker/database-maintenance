package br.com.hbbucker.controller;

import br.com.hbbucker.shared.database.DataBaseType;
import br.com.hbbucker.usecase.bloat.FindBloatedIndexesInput;
import br.com.hbbucker.usecase.bloat.FindBloatedIndexesUC;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

import java.util.concurrent.CompletableFuture;

@Path("/index-maintenance")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class IndexController {

    private final ProducerTemplate producerTemplate;
    private final FindBloatedIndexesUC findBloatedIndexes;

    @GET
    @Path("/start/all/{databaseType}")
    public void startMaintenance(@Suspended AsyncResponse asyncResponse, @PathParam("databaseType") DataBaseType dataBaseType) {
        CompletableFuture.runAsync(() -> {
            FindBloatedIndexesInput input = new FindBloatedIndexesInput(dataBaseType);
            producerTemplate.sendBody("direct:rebuild-indexes", input);
        });

        asyncResponse.resume(Response.accepted("Index maintenance pipeline started.").build());
    }

    @GET
    @Path("/bloated/{databaseType}")
    public Response getBloated(@PathParam("databaseType") DataBaseType dataBaseType) {
        try {
            return Response.ok(findBloatedIndexes.execute(new FindBloatedIndexesInput(dataBaseType))).build();
        } catch (Exception e) {
            return Response.serverError().entity("Error: " + e.getMessage()).build();
        }
    }
}