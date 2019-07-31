/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cimt.telefonica.karaftool;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

// import org.codehaus.jackson.JsonParseException;
// import org.codehaus.jackson.map.JsonMappingException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 *
 * @author bhashemi
 */
public class NexusClient {

    private static final Logger log = Logger.getLogger(NexusClient.class.getSimpleName());

    private static String user = "admin";
    private static String password = "admin";
    private static String url = "http://dusss2syn:7070/nexus/content/repositories/talend_release";

    private WebResource servicePath = getService();

    public List getDataRecursive() throws JsonParseException, JsonMappingException, IOException {
        log.info("Start collecting data ...");
        long start = System.currentTimeMillis();
        List result = getDataRecursive2(servicePath);
        log.log(Level.INFO, "Data collection is Compeleted! Execution time : {0} ms", System.currentTimeMillis() - start);
        return result;
    }

    public List<KarafToolModel> getDataRecursive(WebResource servicePath) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<KarafToolModel> jobs = new ArrayList<KarafToolModel>();
        HashMap<String, HashMap<String, Object>> result = new HashMap<String, HashMap<String, Object>>();
        String nexusRepoTalend = servicePath.accept(MediaType.APPLICATION_JSON).get(String.class);
        // parse the html string
        Document page = Jsoup.parse(nexusRepoTalend);
        List versions = new ArrayList();
        boolean leaf = false;
        KarafToolModel ktm = new KarafToolModel();
        for (Element link : page.select("a")) {

            if (link.text().endsWith("/")) {
                WebResource servicePathJob = servicePath.path(link.text().replace("/", ""));
                jobs.addAll(getDataRecursive(servicePathJob));

                leaf = true;
            } else {
                continue;
            }

        }

        if (!leaf) {
            JsonNode childNode = mapper.createObjectNode();
            String path = servicePath.toString().substring(url.length());
            String[] parts = path.split("/");
            if (parts.length >= 3) {

                ktm.setVersions(parts[parts.length - 1]);
                ktm.setArtifactId(parts[parts.length - 2]);
                ktm.setGroupId(path.substring(0, path.lastIndexOf(parts[parts.length - 2]) - 1).replace('/', '.'));
                StringWriter sw = new StringWriter();
                mapper.writeValue(sw, ktm);
                System.out.println("value = " + sw);

                jobs.add(ktm);
            }

            //            if (versions.isEmpty()) {
//                continue;
//            }
//            System.out.println("link = " + link);
//            String linkHref = link.attr("href");
//            String[] linkParts = linkHref.split("/");
//            System.out.println("linkParts = " + linkParts[linkParts.length - 2]);
//            String artifactId = linkParts[linkParts.length - 2];
//
//            ((ObjectNode) childNode).put("artifactId", artifactId);
//
//            ArrayNode versionsArray = mapper.valueToTree(versions);
//            ((ObjectNode) childNode).put("versions", versionsArray);
//           jobs.add( servicePath.getURI().relativize( URI.create(url) ) )
        }

        return jobs;
    }

    public List getDataRecursive2(WebResource servicePath) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        List jobs = new ArrayList();
        String nexusRepoTalend = servicePath.accept(MediaType.APPLICATION_JSON).get(String.class);
        // parse the html string
        Document page = Jsoup.parse(nexusRepoTalend);
        List versions = new ArrayList();

        KarafToolModel ktmObject = new KarafToolModel();
        StringWriter sw = new StringWriter();
        for (Element link : page.select("a")) {

            if (link.text().endsWith("/") && link.text().matches("\\d+.\\d+.\\d+.*")) {
                versions.add(link.text().replace("/", ""));
                ktmObject.setVersions(link.text().replace("/", ""));

            } else if (link.text().endsWith("/") && !link.text().contains(".")) {
                String linkText = link.text().replace("/", "");
                WebResource servicePathJob = servicePath.path(linkText);
                jobs.addAll(getDataRecursive2(servicePathJob));
            } else {
                continue;
            }

            if (versions.isEmpty()) {
                continue;
            }
            sw = new StringWriter();
            String path = servicePath.toString().substring(url.length());
            String[] parts = path.split("/");

            ktmObject.setArtifactId(parts[parts.length - 1]);
            ktmObject.setGroupId(path.substring(1, path.lastIndexOf(parts[parts.length - 1]) - 1).replace('/', '.'));

            mapper.writeValue(sw, ktmObject);
        }
        if (sw.toString().length() > 0) {
            jobs.add(sw);
        }

        return jobs;
    }

    public List getDataRecursive1(WebResource servicePath) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        List jobs = new ArrayList();
        String nexusRepoTalend = servicePath.accept(MediaType.APPLICATION_JSON).get(String.class);
        // parse the html string
        Document page = Jsoup.parse(nexusRepoTalend);
        List versions = new ArrayList();
        JsonNode childNode = mapper.createObjectNode();
        for (Element link : page.select("a")) {

            if (link.text().endsWith("/") && link.text().matches("\\d+.\\d+.\\d+.*")) {
                versions.add(link.text().replace("/", ""));

            } else if (link.text().endsWith("/") && !link.text().contains(".")) {
                String linkText = link.text().replace("/", "");
                WebResource servicePathJob = servicePath.path(linkText);
                jobs.addAll(getDataRecursive1(servicePathJob));
            } else {
                continue;
            }

            if (versions.isEmpty()) {
                continue;
            }

            String path = servicePath.toString().substring(url.length());
            String[] parts = path.split("/");

            ((ObjectNode) childNode).put("artifactId", parts[parts.length - 1]);
            ((ObjectNode) childNode).put("groupId", path.substring(1, path.lastIndexOf(parts[parts.length - 1]) - 1).replace('/', '.'));
            ((ObjectNode) childNode).put("versions", mapper.valueToTree(versions));

        }
        if (childNode.has("artifactId")) {
            jobs.add(childNode);
        }

        return jobs;
    }
    
    private WebResource getService() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        // client.addFilter(new HTTPBasicAuthFilter(user, password)); 
        return client.resource(getBaseURI());
    }

    private URI getBaseURI() {
        return UriBuilder.fromUri(url).build();
    }
}
