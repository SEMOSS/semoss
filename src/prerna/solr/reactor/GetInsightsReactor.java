package prerna.solr.reactor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import prerna.auth.utils.AbstractSecurityUtils;
import prerna.auth.utils.SecurityUserInsightUtils;
import prerna.auth.utils.SecurityUserProjectUtils;
import prerna.engine.api.IRawSelectWrapper;
import prerna.query.querystruct.selectors.QueryColumnOrderBySelector;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.AbstractReactor;
import prerna.util.sql.AbstractSqlQueryUtil;

public class GetInsightsReactor extends AbstractReactor {
	
	private static List<String> META_KEYS_LIST = new Vector<String>();
	static {
		META_KEYS_LIST.add("description");
		META_KEYS_LIST.add("tag");
	}
	
	public GetInsightsReactor() {
		this.keysToGet = new String[] { ReactorKeysEnum.PROJECT.getKey(), ReactorKeysEnum.FILTER_WORD.getKey(),
				ReactorKeysEnum.LIMIT.getKey(), ReactorKeysEnum.OFFSET.getKey(), ReactorKeysEnum.TAGS.getKey(),
				ReactorKeysEnum.ONLY_FAVORITES.getKey(), ReactorKeysEnum.SORT.getKey()};
	}

	@Override
	public NounMetadata execute() {
		organizeKeys();
		GenRowStruct projectFilterGrs = this.store.getNoun(this.keysToGet[0]);
		List<NounMetadata> warningNouns = new Vector<>();
		// get list of engineIds if user has access
		List<String> projectFilters = null;
		if (projectFilterGrs != null && !projectFilterGrs.isEmpty()) {
			projectFilters = new Vector<String>();
			for (int i = 0; i < projectFilterGrs.size(); i++) {
				String pFilter = projectFilterGrs.get(i).toString();
				if (AbstractSecurityUtils.securityEnabled()) {
					pFilter = SecurityUserProjectUtils.testUserProjectIdForAlias(this.insight.getUser(), pFilter);
					if (SecurityUserProjectUtils.userCanViewProject(this.insight.getUser(), pFilter)) {
						projectFilters.add(pFilter);
					} else {
						// store warnings
						warningNouns.add(NounMetadata.getWarningNounMessage(pFilter + " does not exist or user does not have access to project."));
					}
				} else {
					projectFilters.add(pFilter);
				}
			}
		}
		String searchTerm = this.keyValue.get(this.keysToGet[1]);
		String limit = this.keyValue.get(this.keysToGet[2]);
		String offset = this.keyValue.get(this.keysToGet[3]);
		List<String> tagFilters = getTags();
		Boolean favoritesOnly = Boolean.parseBoolean(this.keyValue.get(this.keysToGet[5]));
		String sortCol = this.keyValue.get(this.keysToGet[6]);
		if(sortCol == null) {
			sortCol = "name";
		}
		// we only have 2 options for sorting
		// based on name and last modified date
		// these values are based on the projections in the query
		QueryColumnOrderBySelector sortBy = null;
		if(sortCol.equalsIgnoreCase("date")) {
			sortBy = new QueryColumnOrderBySelector("last_modified_on", "desc");
		} else {
			sortBy = new QueryColumnOrderBySelector("low_name");
		}
		// get results
		List<Map<String, Object>> results = null;
		// method handles if filters are null or not
		if (AbstractSecurityUtils.securityEnabled()) {
			results = SecurityUserInsightUtils.searchUserInsights(this.insight.getUser(), projectFilters, searchTerm, 
					tagFilters, favoritesOnly, sortBy, limit, offset);
		} else {
			results = SecurityUserInsightUtils.searchInsights(projectFilters, searchTerm, tagFilters, sortBy, limit, offset);
		}
		
		// this entire block is to add the additional metadata to the insights
		{
			// now i will aggregate each project id to its insight ids
			// and then i will query to get all the tags + descriptions
			int size = results.size();
			Map<String, List<String>> projectIdsToInsight = new HashMap<String, List<String>>();
			Map<String, Integer> index = new HashMap<String, Integer>(size);
			for(int i = 0; i < size; i++) {
				Map<String, Object> res = results.get(i);
				String projectId = (String) res.get("app_id");
				String insightId = (String) res.get("app_insight_id");
				
				// aggregate + store
				List<String> inIds = null;
				if(projectIdsToInsight.containsKey(projectId)) {
					inIds = projectIdsToInsight.get(projectId);
				} else {
					inIds = new Vector<String>();
					projectIdsToInsight.put(projectId, inIds);
				}
				
				inIds.add(insightId);
				
				// store so we can search by index
				index.put(projectId + insightId, new Integer(i));
				
				// i will put an empty description + tag placeholder
				res.put("description", "");
				res.put("tags", new Vector<String>());
			}
			
			// grab the wrapper for all the meta information for all the project + insights in one go
			if(!projectIdsToInsight.isEmpty()) {
				IRawSelectWrapper wrapper = SecurityUserInsightUtils.getInsightMetadataWrapper(projectIdsToInsight, META_KEYS_LIST);
				while(wrapper.hasNext()) {
					Object[] data = wrapper.next().getValues();
					String metaKey = (String) data[2];
					String value = null;
					if(data[3] instanceof java.sql.Clob) {
						value = AbstractSqlQueryUtil.flushClobToString((java.sql.Clob) data[3]);
					} else {
						value = (String) data[3];
					}
					if(value == null) {
						continue;
					}
					
					String unique = data[0] + "" + data[1];
					int indexToFind = index.get(unique);
					
					Map<String, Object> res = results.get(indexToFind);
					// right now only handling description + tags
					if(metaKey.equals("description")) {
						// we only have 1 description per insight
						// so just push
						res.put("description", value);
					} else if(metaKey.equals("tag")) {
						// multiple tags per insight
						List<String> tags = (List<String>) res.get("tags");
						// add to the list
						tags.add(value);
					}
				}
			}
		}
		
		NounMetadata retNoun = new NounMetadata(results, PixelDataType.CUSTOM_DATA_STRUCTURE, PixelOperationType.APP_INSIGHTS);
		// add warnings to results
		if(!warningNouns.isEmpty()) {
			for(NounMetadata warning: warningNouns) {
				retNoun.addAdditionalReturn(warning);
			}
		}
		return retNoun;
	}
	
	/**
	 * Get the tags to set for the insight
	 * @return
	 */
	private List<String> getTags() {
		List<String> tags = new Vector<String>();
		GenRowStruct grs = this.store.getNoun(ReactorKeysEnum.TAGS.getKey());
		if(grs != null && !grs.isEmpty()) {
			for(int i = 0; i < grs.size(); i++) {
				tags.add(grs.get(i).toString());
			}
		}
		
		return tags;
	}
	
	@Override
	protected String getDescriptionForKey(String key) {
		if(key.equals(ReactorKeysEnum.SORT.getKey())) {
			return "The sort is a string value containing either 'name' or 'date' for how to sort";
		}
		return super.getDescriptionForKey(key);
	}
}
