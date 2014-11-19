/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.content.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.commons.collections.CollectionUtils;

import com.aditya.gmwp.aem.model.DisclaimerModel;
import com.aditya.gmwp.aem.services.content.LegalInfoService;
import com.aditya.gmwp.aem.services.core.AbstractService;
import com.aditya.gmwp.aem.services.core.JcrService;
import com.aditya.gmwp.aem.services.core.QueryService;
import com.aditya.gmwp.aem.utils.NodeUtil;
import com.aditya.gmwp.aem.utils.PageUtil;
import com.aditya.gmwp.aem.wrapper.GMResource;
import com.aditya.gmwp.aem.wrapper.LevelServiceWrapper.Constants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Component(name = "com.aditya.gmwp.aem.services.content.LegalInfoService", label = "GMWP LegalInfoService", description = "Service to retrieve all disclaimers from a page tree", enabled = true, metatype = false)
@Service(LegalInfoService.class)
@Property(name = "service.description", value = "LegalInfoService")
public class LegalInfoServiceImpl extends AbstractService<LegalInfoServiceImpl> implements LegalInfoService {

    /** The query service. */
    @Reference
    private final QueryService queryService = null;

    /** The jcr service. */
    @Reference
    private final JcrService jcrService = null;

    private static final String PUT_IN_PARENTHESES = "put_in_parentheses";

    /*
     * (non-Javadoc)
     * @see com.aditya.gmwp.aem.services.content.LegalInfoService#findAllPotentialDisclaimersForRessource(java.lang.String)
     */
    @Override
    public final List<DisclaimerModel> findAllPotentialDisclaimersForRessource(final String currentPath) {
        final List<DisclaimerModel> result = new ArrayList<DisclaimerModel>();

        // configurations
        final String renderingMode;
        final Counter currentIndex;

        // load configs from lslr
        final Node lslrDisclaimersNode = getDisclaimersNode(currentPath);
        if (lslrDisclaimersNode == null) {
            renderingMode = DisclaimerModel.PLAIN_RENDERING;
            currentIndex = new Counter("1");
        } else {
            renderingMode = determineRenderingMode(lslrDisclaimersNode);
            final String startReference = getStartReferenceFromIndex(lslrDisclaimersNode);
            currentIndex = new Counter(startReference);
        }

        // add disclaimers from current page
        final PageManager man = this.jcrService.getPageManager();
        final Page p = man.getContainingPage(currentPath);
        if (p != null && p.isValid()) {
            result.addAll(createDisclaimersForResource(p.getContentResource().getPath(), DisclaimerModel.PAGE_DISCLAIMER, currentIndex, renderingMode));
        }

        // add disclaimers from lslr
        result.addAll(findAllParentDisclaimersForResource(currentPath, currentIndex, renderingMode));

        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.gmwp.aem.services.content.LegalInfoService#isEnableLayers(java.lang.String)
     */
	@Override
	public boolean isEnableLayers(String path) {
		final Node disclaimerNode = getDisclaimersNode(path);
		return (disclaimerNode != null ? isEnableLayersOn(disclaimerNode) : false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.content.LegalInfoService#createDisclaimersByIdsForRessource(java.util.Set, com.aditya.gmwp.aem.wrapper.GMResource)
	 */
	@Override
	public List<DisclaimerModel> createDisclaimersByIdsForRessource(Set<String> disclaimerIds,
	                                                                GMResource currentRes) {
		final List<DisclaimerModel> result = new ArrayList<DisclaimerModel>();
        if (CollectionUtils.isNotEmpty(disclaimerIds)) {

            final Node lslrDiscNode = getDisclaimersNode(currentRes.getPath());
            final String startReference = getStartReferenceFromIndex(lslrDiscNode);
            final Counter pageIndex = new Counter(startReference);
            final String renderingMode = determineRenderingMode(lslrDiscNode);

            for (final String id : disclaimerIds) {
                final GMResource disclaimer = new GMResource(currentRes.getResourceResolver().resolve(id));
                if (disclaimer.isExisting()) {
                    final String label = disclaimer.getPropertyAsString("label");
                    final String text = disclaimer.getPropertyAsString("text");
                    result.add(new DisclaimerModel(label, text, DisclaimerModel.ABSTRACT_DISCLAIMER, id, pageIndex
                            .next(), renderingMode));
                }
            }
        }
        return result;
	}

    /**
     * Find all parent disclaimers for resource.
     * 
     * @param resourcePath
     *            the resource path
     * @param referenceCounter
     *            the current reference
     * @param renderingMode
     *            the rendering mode
     * @return the list
     */
    private List<DisclaimerModel> findAllParentDisclaimersForResource(final String resourcePath,
                                                                      final Counter referenceCounter,
                                                                      final String renderingMode) {
        final List<DisclaimerModel> result = new ArrayList<DisclaimerModel>();

        String bbcPath;
        try {
            final PageManager man = this.jcrService.getResourceResolver().adaptTo(PageManager.class);
            final Page p = man.getContainingPage(resourcePath);
            bbcPath = PageUtil.getPropertyFromPageIncludingAncestors(p, "baseballCardLink");
            if (StringUtils.isNotBlank(bbcPath)) {
                result.addAll(createDisclaimersForResource(bbcPath, DisclaimerModel.BODYSTYLE_DISCLAIMER, referenceCounter, renderingMode));
            }
        } catch (final Exception e) {
            getLog(this).error("Problems retrieving the baseballCardLink for the resource {}", resourcePath, e);
        }

        final GMResource lslr = this.queryService.findConfigurationPage(resourcePath, Constants.LSLR);
        if (lslr.isExisting()) {
            result.addAll(createDisclaimersForResource(lslr.getPath(), DisclaimerModel.GENERAL_DISCLAIMER, referenceCounter, renderingMode));
        }

        return result;
    }

    /**
     * Creates the disclaimers for resource.
     * 
     * @param sourcePath
     *            the source path
     * @param disclaimerType
     *            the disclaimer type
     * @param referenceCounter
     *            the page reference
     * @param renderingMode
     *            the rendering mode
     * @return the list
     */
    private List<DisclaimerModel> createDisclaimersForResource(final String sourcePath,
                                                               final String disclaimerType,
                                                               final Counter referenceCounter,
                                                               final String renderingMode) {
        final ArrayList<DisclaimerModel> result = new ArrayList<DisclaimerModel>();
        final GMResource resource = this.queryService.findByName("disclaimers", sourcePath);
        if (resource.isExisting()) {
            for (final GMResource child : resource.getChildren()) {
                final Node node = child.toNode();

                try {
                    if (node.hasProperty("label") && node.hasProperty("text")) {
                        final String label = node.getProperty("label").getValue().getString();
                        final String text = node.getProperty("text").getValue().getString();
                        result.add(new DisclaimerModel(label, text, disclaimerType, node.getPath(), referenceCounter
                                .getValue(), renderingMode));
                        referenceCounter.increase();
                    }
                } catch (final Exception e) {
                    getLog(this).error("Problems reading the properties of node {}", resource.getPath(), e);
                }
            }
        }
        return result;
    }

    /**
     * Determine rendering mode.
     * 
     * @param legalDisclaimersNode
     *            the legal disclaimers node
     * @return the string
     */
    private String determineRenderingMode(final Node legalDisclaimersNode) {
        final boolean parenthesesRendering = getPutInParentheses(legalDisclaimersNode);
        final boolean superscriptRendering = getSuperscriptReferences(legalDisclaimersNode);

        if (superscriptRendering && parenthesesRendering) {
            return DisclaimerModel.SUPERSCRIPT_AND_PARANTHESES_RENDERING;
        }

        if (parenthesesRendering) {
            return DisclaimerModel.PARENTHESES_RENDERING;
        }

        if (superscriptRendering) {
            return DisclaimerModel.SUPERSCRIPT_RENDERING;
        }

        return DisclaimerModel.PLAIN_RENDERING;
    }

    /**
     * Gets the put in parentheses.
     * 
     * @param legalDisclaimersNode
     *            the legal disclaimers node
     * @return the put in parentheses
     */
    private boolean getPutInParentheses(final Node legalDisclaimersNode) {
    	return BooleanUtils.toBoolean(NodeUtil.getPropertyAsString(legalDisclaimersNode, PUT_IN_PARENTHESES));
    }

    /**
     * Gets the value where to start the reference from, to determine the indexing count for the disclaimers.
     * 
     * @param legalDisclaimersNode
     *            the legal disclaimers node
     * @return the put in parentheses
     */
    private String getStartReferenceFromIndex(final Node legalDisclaimersNode) {
        String result = NodeUtil.getPropertyAsString(legalDisclaimersNode, "start_reference_from");
        if (StringUtils.isBlank(result)) {
            result = "1";
        }

        return result;
    }

    /**
     * Gets the superscript references.
     * 
     * @param legalDisclaimersNode
     *            the legal disclaimers node
     * @return the superscript references
     */
    private boolean getSuperscriptReferences(final Node legalDisclaimersNode) {
    	return BooleanUtils.toBoolean(NodeUtil.getPropertyAsString(legalDisclaimersNode, "superscript_references"));
    }

    /**
     * Gets the superscript references.
     * 
     * @param legalDisclaimersNode
     *            the legal disclaimers node
     * @return the superscript references
     */
    private boolean isEnableLayersOn(final Node legalDisclaimersNode) {
        return BooleanUtils.toBoolean(NodeUtil.getPropertyAsString(legalDisclaimersNode, "disclaimer_layers"));
    }

    /**
     * Gets the disclaimers node.
     * 
     * @param path
     *            the path
     * @return the disclaimers node
     */
    private Node getDisclaimersNode(final String path) {
        final String lslr = this.queryService.findConfigurationPage(path, Constants.LSLR).getPath();
        final GMResource res = this.queryService.findByName("cnt_legaldisclaimers_c1", lslr);
        if (res.isExisting()) {
            return res.toNode();
        } else {
            return null;
        }
    }
}
