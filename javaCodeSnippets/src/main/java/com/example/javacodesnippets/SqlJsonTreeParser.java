/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.javacodesnippets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bhashemi
 */
public class SqlJsonTreeParser {

    /**
     * @param args the command line arguments
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        try {
            SqlJsonTreeParser app = new SqlJsonTreeParser();
            
            InputStream stream = app.readJsonFile("/sqlJsonExample.json");
            
            ObjectMapper MAPPER = new ObjectMapper();
            JsonNode root = MAPPER.readTree(stream);
            JsonNode whereClauseTree = root.get("where");
            String result = app.buildQuery(whereClauseTree);
            System.out.println("result = " + result);

        } catch (IOException ex) {
            Logger.getLogger(SqlJsonTreeParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public InputStream readJsonFile(String path) {
        InputStream stream = SqlJsonTreeParser.class.getResourceAsStream(path);
        return stream;
    }

    public String buildQuery(JsonNode tree) {
        StringBuilder result = new StringBuilder();
        Iterator<Map.Entry<String, JsonNode>> fields = tree.fields();

        while (fields.hasNext()) {

            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode value = field.getValue();

            switch (field.getKey()) {
                case "AND":
                    if (value.isArray()) {
                        for (final JsonNode objNode : value) {
                            Iterator<Map.Entry<String, JsonNode>> objNodeFields = objNode.fields();
                            while (objNodeFields.hasNext()) {
                                Map.Entry<String, JsonNode> objNodeField = objNodeFields.next();
                                String op = objNodeField.getKey();
                                JsonNode objNodeValue = objNodeField.getValue();
                                if (!"AND".equals(op) && !"OR".equals(op)) {
                                    String column = objNodeValue.get("column").toString();
                                    String columnValue = objNodeValue.get("columnValue").toString();
                                    if (result.length() > 0) {
                                        result.append(" AND ");
                                    }
                                    result.append(column).append(op).append(columnValue);
                                } else {
                                    //return "(" +result + ") AND (" + buildQuery(objNode) + ")";
                                    result.append(" AND (").append(buildQuery(objNode)).append(")");

                                }
                            }

                        }
                    }
                    break;
                case "OR":
                    if (value.isArray()) {
                        for (final JsonNode objNode : value) {
                            Iterator<Map.Entry<String, JsonNode>> objNodeFields = objNode.fields();
                            while (objNodeFields.hasNext()) {
                                Map.Entry<String, JsonNode> objNodeField = objNodeFields.next();
                                String op = objNodeField.getKey();
                                JsonNode objNodeValue = objNodeField.getValue();
                                if (!"AND".equals(op) && !"OR".equals(op)) {
                                    String column = objNodeValue.get("column").toString();
                                    String columnValue = objNodeValue.get("columnValue").toString();
                                    if (result.length() > 0) {
                                        result.append(" OR ");
                                    }
                                    result.append(column).append(op).append(columnValue);
                                } else {
                                    // return "(" +result + ") OR (" + buildQuery(objNode) + ")";
                                    result.append(" OR (").append(buildQuery(objNode)).append(")");
                                }
                            }

                        }
                    }
                    break;
                default:
                    break;
            }

        }
        return result.toString();

    }

}
