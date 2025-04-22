package br.com.hbbucker.controller;

import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexName;
import br.com.hbbucker.shared.database.table.SchemaName;
import br.com.hbbucker.usecase.bloat.FindBloatedIndexesInput;
import br.com.hbbucker.usecase.bloat.FindBloatedIndexesUC;
import br.com.hbbucker.usecase.find.datasource.FindAllDataSourcesUC;
import br.com.hbbucker.usecase.staus.GetStatusIndexProcessUC;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Path("/index-maintenance/")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class IndexController {

    private final ProducerTemplate producerTemplate;
    private final FindBloatedIndexesUC findBloatedIndexes;
    private final FindAllDataSourcesUC findAllDataSources;
    private final GetStatusIndexProcessUC getStatusIndexProcess;

    @POST
    @Path("{dataSource}/index/recreate/all")
    public Response startMaintenance(
            final @PathParam("dataSource") DataSourceName dataSourceName) {

        CompletableFuture.runAsync(() -> producerTemplate.sendBodyAndHeaders(
                "direct:rebuild-indexes", (Object) null, createHeaders(dataSourceName, null, null)));
        return Response.accepted("Index maintenance pipeline started.").build();
    }

    private Map<String, Object> createHeaders(
            final DataSourceName dataSourceName,
            final SchemaName schemaName,
            final IndexName indexName) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("x-datasource-name", dataSourceName);
        if (schemaName != null) {
            headers.put("x-schema-name", schemaName);
        }
        if (indexName != null) {
            headers.put("x-index-name", indexName);
        }
        return headers;
    }

    @POST
    @Path("{dataSource}/index/recreate/{schemaName}/{indexName}")
    public Response startIndex(
            final @PathParam("dataSource") DataSourceName dataSourceName,
            final @PathParam("schemaName") SchemaName schemaName,
            final @PathParam("indexName") IndexName indexName) {

        Map<String, Object> headers = createHeaders(dataSourceName, schemaName, indexName);
        CompletableFuture.runAsync(() -> producerTemplate.sendBodyAndHeaders("direct:rebuild-index", (Object) null, headers));
        return Response.accepted("Index maintenance pipeline started.").build();
    }

    @GET
    @Path("{dataSource}/index/bloated")
    public Response getBloated(
            final @PathParam("dataSource") DataSourceName dataSourceName) {

        try {
            return Response.ok(findBloatedIndexes.execute(new FindBloatedIndexesInput(dataSourceName))).build();
        } catch (Exception e) {
            return Response.serverError().entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("datasources")
    public Response getDatasource() {
        return Response.ok(findAllDataSources.execute(null)).build();
    }

    @GET
    @Path("index/status")
    public Response getIndexesStatus() {
        return Response.ok(getStatusIndexProcess.execute(null)).build();
    }
}
