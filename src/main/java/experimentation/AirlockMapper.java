package experimentation;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ranger.plugin.model.RangerServiceResource;
import org.apache.ranger.tagsync.source.atlas.AtlasResourceMapper;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyResource;
import org.apache.ranger.tagsync.source.atlasrest.RangerAtlasEntity;

import java.util.HashMap;
import java.util.Map;

public class AirlockMapper extends AtlasResourceMapper {

    private static final Log LOG = LogFactory.getLog(AirlockMapper.class);

    private static final String RANGER_SERVICE_NAME = "testservice"; // S3 Service name
    private static final String ATLAS_ENTITY_PSEUDO_DIR = "aws_s3_pseudo_dir";
    private static final String RANGER_ENTITY_BUCKET = "path";
    private static final String ENTITY_ATTRIBUTE_QUALIFIED_NAME = "qualifiedName";

    public static final String[] SUPPORTED_ENTITY_TYPES = {ATLAS_ENTITY_PSEUDO_DIR};

    public AirlockMapper() {
        super("s3", SUPPORTED_ENTITY_TYPES);
        LOG.debug("Starting AirlockAtlasMapper...");
    }

    @Override
    public RangerServiceResource buildResource(final RangerAtlasEntity entity) throws Exception {
        LOG.debug("running buildResource...");

        String qualifiedName =  (String)entity.getAttributes().get(AtlasResourceMapper.ENTITY_ATTRIBUTE_QUALIFIED_NAME);

        if (StringUtils.isEmpty(qualifiedName)) {
            throw new Exception("attribute '" +  ENTITY_ATTRIBUTE_QUALIFIED_NAME + "' not found in entity");
        }

        String resourceStr = getResourceNameFromQualifiedName(qualifiedName);
        if (StringUtils.isEmpty(resourceStr)) {
            throwExceptionWithMessage("resource not found in attribute '" +  ENTITY_ATTRIBUTE_QUALIFIED_NAME + "': " + qualifiedName);
        }

        String   entityType  = entity.getTypeName();
        String   entityGuid  = entity.getGuid();
        String[] resources   = resourceStr.split(QUALIFIED_NAME_DELIMITER);
        String   pseudoDir   = resources.length > 0 ? resources[0] : null;
        // align with name in ranger policy path
        String   formattedPseudoDir = "/" + pseudoDir.replace(pseudoDir.substring(pseudoDir.length()-1), "");

        LOG.debug("extracted objects " + entityType + " " + entityGuid + " " + pseudoDir);

        Map<String, RangerPolicyResource> elements = new HashMap<String, RangerPolicyResource>();

        if (StringUtils.equals(entityType, ATLAS_ENTITY_PSEUDO_DIR)) {
            if (StringUtils.isNotEmpty(entityGuid)) {
                elements.put(RANGER_ENTITY_BUCKET, new RangerPolicyResource(formattedPseudoDir));
            }
        } else {
            throwExceptionWithMessage("unrecognized entity-type: " + entityType);
        }

        if(elements.isEmpty()) {
            throwExceptionWithMessage("invalid qualifiedName for entity-type '" + entityType + "': " + qualifiedName);
        }

        RangerServiceResource ret = new RangerServiceResource(entityGuid, RANGER_SERVICE_NAME, elements);
        return ret;
    }

}
