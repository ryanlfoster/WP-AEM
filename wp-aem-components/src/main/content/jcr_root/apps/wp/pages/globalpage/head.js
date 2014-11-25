/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2014 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
/*jslint node: true*/
/*global use*/
"use strict";

var global = this;

use(["/libs/wcm/foundation/components/utils/ResourceUtils.js"], function (ResourceUtils) {
    
    var CONST = {
        PROP_DESIGN_PATH: "cq:designPath",
        PROP_META_TAGS: "cq:tags",
        PROP_META_DESC: "metaDesc",
        PROP_CQ_TEMPLATE: "cq:template",
        PROP_SLING_RESOURCETYPE: "sling:resourceType"
    };
    
    var _getKeywords = function () {
        var keywords = "";
        if (global.currentPage) {
            var tags = global.currentPage.getTags();
            for (var tagIdx=0 ; tagIdx < tags.length ; tagIdx++) {
                keywords += tags[tagIdx].getTitle();
                keywords += (tagIdx <= tags.length - 1) ? "," : "";
            }
        }
        
        return keywords;
    };
    
    var designPathPromise = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
        return ResourceUtils.getInheritedPageProperty(pageResource, CONST.PROP_DESIGN_PATH)
            .then(function (designPath) {
                return designPath;
            });
    });

    var templateName = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
    	return ResourceUtils.getResource(pageResource.path + "/jcr:content")
	        .then(function (contentResource) {
	        	return contentResource.properties[CONST.PROP_CQ_TEMPLATE];
	        });
	});

    var templateResourceType = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
    	return ResourceUtils.getResource(pageResource.path + "/jcr:content")
	        .then(function (contentResource) {
	        	return contentResource.properties[CONST.PROP_SLING_RESOURCETYPE];
	        });
	});

    var faviconPathPromise = designPathPromise.then(function (designPath) {
        return designPath + "/favicon.ico";
    });

    var titlePromise = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
        return ResourceUtils.getResource(pageResource.path + "/jcr:content")
            .then(function (contentResource) {
            	var title = contentResource.properties["pageTitle"];
            	if (!title) {
                	title = contentResource.properties["jcr:title"];
	                if (!title) {
	                    title = pageResource.name;
	                }
            	}
                return title;
            });
    });

    var metatags = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
    	return ResourceUtils.getResource(pageResource.path + "/jcr:content").then(function (contentResource) {
            var abc = contentResource.properties[CONST.PROP_META_TAGS].join();
            console.log(abc);
            return abc;
        });
    });

    var metadesc = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
    	return ResourceUtils.getResource(pageResource.path + "/jcr:content").then(function (contentResource) {
            return contentResource.properties[CONST.PROP_META_DESC];
        });
    });

    var pagePathPromise = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
        return pageResource.path + ".html";
    });
    
    var keywords = _getKeywords();

    var ogTitle = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
    	return ResourceUtils.getResource(pageResource.path + "/jcr:content").then(function (contentResource) {
            return contentResource.properties["og_title"];
        });
    });

    var ogDescription = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
    	return ResourceUtils.getResource(pageResource.path + "/jcr:content").then(function (contentResource) {
            return contentResource.properties["og_description"];
        });
    });

    var ogType = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
    	return ResourceUtils.getResource(pageResource.path + "/jcr:content").then(function (contentResource) {
            return contentResource.properties["og_type"];
        });
    });

    return {
        keywords: metatags,
        description: metadesc,
        faviconPath: faviconPathPromise,
        designPath: designPathPromise,
        pagePath: pagePathPromise,
        template: templateName,
        templateResourceType: templateResourceType,
        title: titlePromise,
        og_title: ogTitle,
        og_description: ogDescription,
        og_type: ogType
    };
});