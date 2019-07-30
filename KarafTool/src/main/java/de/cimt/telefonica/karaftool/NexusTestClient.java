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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 *
 * @author bhashemi
 */
public class NexusTestClient {

    private static final Logger log = Logger.getLogger(NexusTestClient.class.getSimpleName());

    private static String user = "admin";
    private static String password = "admin";
    private static String url = "http://dusss2syn:7070/nexus/content/repositories/talend_release";

    private WebResource servicePath = getService();

    public List getDataRecursive() throws JsonParseException, JsonMappingException, IOException {
        System.out.println(" ====================================== \n");
        ObjectMapper mapper = new ObjectMapper();
        List result = getDataRecursive1(servicePath); 
        System.out.println("result = " +result);
         return result;
    }

    public List<KarafToolModel> getDataRecursive(WebResource servicePath) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<KarafToolModel> jobs = new ArrayList<KarafToolModel>();
        Map map=new HashMap();  
        String nexusRepoTalend = servicePath.accept(MediaType.APPLICATION_JSON).get(String.class);
        // parse the html string
        Document page = Jsoup.parse(nexusRepoTalend);
        List versions = new ArrayList();
        boolean leaf=false;
        KarafToolModel ktm = new KarafToolModel();
        for (Element link : page.select("a")) {
           
            if (link.text().endsWith("/")) {
                WebResource servicePathJob = servicePath.path( link.text().replace("/", "") );
                jobs.addAll( getDataRecursive(servicePathJob));
                
                leaf=true;
            } else {
                continue;
            }

        }
        
        if(!leaf){
            JsonNode childNode = mapper.createObjectNode();
            String path=servicePath.toString().substring( url.length() );
            String[] parts=path.split("/");
            if(parts.length>=3){
            
                ktm.setVersions(parts[parts.length-1]);
                ktm.setArtifactId(parts[parts.length-2]);
                ktm.setGroupId(path.substring(0, path.lastIndexOf(parts[parts.length-2])-1).replace('/', '.'));
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
        JsonNode childNode = mapper.createObjectNode();
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
            String path=servicePath.toString().substring( url.length() );
            String[] parts=path.split("/");
            
            ktmObject.setArtifactId(parts[parts.length-1]);
            ktmObject.setGroupId(path.substring(1, path.lastIndexOf(parts[parts.length-1])-1).replace('/', '.'));
            
           
            mapper.writeValue(sw, ktmObject);
            System.out.println("value = " + sw);
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
            
            
            
        }
        jobs.add(sw);

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
            System.out.println("link = " + link);
            String linkHref = link.attr("href");
            String[] linkParts = linkHref.split("/");
            System.out.println("linkParts = " + linkParts[linkParts.length - 2]);
            String artifactId = linkParts[linkParts.length - 2];

            ((ObjectNode) childNode).put("artifactId", artifactId);

            ArrayNode versionsArray = mapper.valueToTree(versions);
            ((ObjectNode) childNode).put("versions", versionsArray);

        }
        if (childNode.has("artifactId")) {
            jobs.add(childNode);
        }

        return jobs;
    }

    public List getData() throws JsonParseException, JsonMappingException, IOException {
        WebResource service = getService();

//		log.info("Check that Nexus is running");
//		String nexusStatus = service.path("service").path("local").path("status").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class).toString();
//		log.info(nexusStatus + "\n");
//
//		log.info("GET Nexus Version");
//		String nexusVersion = service.path("service").path("local").path("status").accept(MediaType.APPLICATION_JSON).get(String.class).toString();
//		log.info(nexusVersion + "\n");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();
        List jobs = new ArrayList();
        log.info("nexusRepositories");
        // Go to starting path
        WebResource servicePath = service.path("content").path("repositories").path("talend_release").path("de").path("cimt").path("talend");

        String nexusRepoTalend = servicePath.accept(MediaType.APPLICATION_JSON).get(String.class);
        // parse the html string
        Document page = Jsoup.parse(nexusRepoTalend);

        // find links
        for (Element link : page.select("a")) {
            JsonNode childNode = mapper.createObjectNode();
            if (!link.text().endsWith("/")) {
                continue;
            }
            String linkHref = link.attr("href");
            String linkText = link.text().replace("/", "");
            ((ObjectNode) childNode).put("artifactId", linkText);

            WebResource servicePathJob = servicePath.path(linkText);

            String nexusRepoTalendJob = servicePathJob.accept(MediaType.APPLICATION_JSON).get(String.class);
            Document jobPage = Jsoup.parse(nexusRepoTalendJob);
            List versions = new ArrayList();
            for (Element jobLink : jobPage.select("a")) {
                if (jobLink.text().endsWith("/") && jobLink.text().matches("\\d+.\\d+.\\d+.*")) {
                    versions.add(jobLink.text().replace("/", ""));
                } else if (jobLink.text().endsWith("/") && !jobLink.text().contains(".")) {
                    // if needed go one more deeper
                }
            }

            ArrayNode versionsArray = mapper.valueToTree(versions);
            ((ObjectNode) childNode).put("versions", versionsArray);

            jobs.add(childNode);

        }
        // log.info(jobs + "\n");

        return jobs;
//		if( ! nexusVersion.contains( "nexus:metadata" ) ) {
//			log.warning("Please install the metadata plugin.");
//			return;
//		}
//		if( ! nexusVersion.contains( "Sonatype Nexus Professional" ) )  {
//			log.warning("Please install Sonatype Nexus Professional.");
//			return;
//		}
// 		log.info("Installation seems to be correct.\n");

//		String targetName = "NewTarget";
//		log.info("Create Repo Target with name: " +  targetName);
//		RepositoryTargetResourceResponse request = new RepositoryTargetResourceResponse();
//		RepositoryTargetResource data = new RepositoryTargetResource();
//		data.setContentClass("maven2");
//		data.setName(targetName);
//		data.setPatterns(Arrays.asList(".*"));
//		request.setData(data);
//		service.path("service").path("local").path("repo_targets").post(request);
//		
//		log.info("Get all repo targets");
//		String repoTargets = service.path("service").path("local").path("repo_targets")
//				.accept(MediaType.APPLICATION_JSON).get(String.class);
//		log.info( repoTargets );
//		if( ! repoTargets.contains( targetName ) )  {
//			log.warning("Repo Target was not created successfully");
//			return;
//		}
//		ObjectMapper mapper = new ObjectMapper();
//		RepositoryTargets targets = mapper.readValue(repoTargets, RepositoryTargets.class);
//		RepositoryTargetResource[] list = targets.getData();
//		for (RepositoryTargetResource res : list) {
//			if( res.getName().equalsIgnoreCase( targetName ) ) {
//				log.info("Delete Repo Target with ID " + res.getId() + " Name " + res.getName() );
//				service.path("service").path("local").path("repo_targets").path(res.getId()).delete();
//			}
//		}
//		log.info(service.path("service").path("local").path("search").path("m2").path("freeform")
//				.queryParam("p", "commitStage").queryParam("t", "matches").queryParam("v", "success")
//				.accept(MediaType.APPLICATION_JSON).get(String.class).toString());
//
//		String artifact = "urn:maven/artifact#de.mb:rest-test:0.0.1::jar";
//		log.info("Get metadata of artifact " + artifact + ". Only works with Nexus Pro & metadata plugin installed");
//		String encodedString = new String( Base64.encode(artifact.getBytes()));
//		String metaDataResult = service.path("service").path("local").path("index").path("custom_metadata").path("releases")
//				.path(encodedString).accept(MediaType.APPLICATION_JSON).get(String.class).toString();
//		log.info(metaDataResult);
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
