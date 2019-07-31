/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cimt.telefonica.karaftool;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bhashemi
 */
public class KarafToolModel {

    private String artifactId;
    private String groupId;
    private List<String> versions = new ArrayList<>();

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getArtifactId() {
        return this.artifactId;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getVersions() {
        return this.versions;
    }

    public void setVersions(String versions) {
        this.versions.add(versions);
    }
}
