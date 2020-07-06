/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.cognitiveservices.language.luis.authoring.models;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The HierarchicalModelV2 model.
 */
public class HierarchicalModelV2 {
    /**
     * The name property.
     */
    @JsonProperty(value = "name")
    private String name;

    /**
     * The children property.
     */
    @JsonProperty(value = "children")
    private List<String> children;

    /**
     * The inherits property.
     */
    @JsonProperty(value = "inherits")
    private PrebuiltDomainObject inherits;

    /**
     * The roles property.
     */
    @JsonProperty(value = "roles")
    private List<String> roles;

    /**
     * Get the name value.
     *
     * @return the name value
     */
    public String name() {
        return this.name;
    }

    /**
     * Set the name value.
     *
     * @param name the name value to set
     * @return the HierarchicalModelV2 object itself.
     */
    public HierarchicalModelV2 withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the children value.
     *
     * @return the children value
     */
    public List<String> children() {
        return this.children;
    }

    /**
     * Set the children value.
     *
     * @param children the children value to set
     * @return the HierarchicalModelV2 object itself.
     */
    public HierarchicalModelV2 withChildren(List<String> children) {
        this.children = children;
        return this;
    }

    /**
     * Get the inherits value.
     *
     * @return the inherits value
     */
    public PrebuiltDomainObject inherits() {
        return this.inherits;
    }

    /**
     * Set the inherits value.
     *
     * @param inherits the inherits value to set
     * @return the HierarchicalModelV2 object itself.
     */
    public HierarchicalModelV2 withInherits(PrebuiltDomainObject inherits) {
        this.inherits = inherits;
        return this;
    }

    /**
     * Get the roles value.
     *
     * @return the roles value
     */
    public List<String> roles() {
        return this.roles;
    }

    /**
     * Set the roles value.
     *
     * @param roles the roles value to set
     * @return the HierarchicalModelV2 object itself.
     */
    public HierarchicalModelV2 withRoles(List<String> roles) {
        this.roles = roles;
        return this;
    }

}