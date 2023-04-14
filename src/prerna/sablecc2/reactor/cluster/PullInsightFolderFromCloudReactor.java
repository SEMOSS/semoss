package prerna.sablecc2.reactor.cluster;

import prerna.auth.utils.AbstractSecurityUtils;
import prerna.auth.utils.SecurityInsightUtils;
import prerna.cluster.util.ClusterUtil;
import prerna.project.api.IProject;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.AbstractReactor;
import prerna.util.AssetUtility;
import prerna.util.Utility;

public class PullInsightFolderFromCloudReactor extends AbstractReactor {
	
	public PullInsightFolderFromCloudReactor() {
		this.keysToGet = new String[]{ReactorKeysEnum.PROJECT.getKey(), ReactorKeysEnum.ID.getKey()};
	}

	@Override
	public NounMetadata execute() {
		organizeKeys();
		String projectId = this.keyValue.get(this.keysToGet[0]);
		String rdbmsId = this.keyValue.get(this.keysToGet[1]);
		if(projectId == null || (projectId=projectId.trim()).isEmpty()) {
			throw new IllegalArgumentException("Must input an project id");
		}
		if(rdbmsId == null || (rdbmsId=rdbmsId.trim()).isEmpty()) {
			throw new IllegalArgumentException("Must input an insight id");
		}
		
		if(AbstractSecurityUtils.securityEnabled()) {
			// make sure valid id for user
			if(!SecurityInsightUtils.userIsInsightOwner(this.insight.getUser(), projectId, rdbmsId)) {
				// you dont have access
				throw new IllegalArgumentException("Insight does not exist or user is not an owner to force pulling from cloud storage");
			}
		}
		
		IProject project = Utility.getProject(projectId);
		String projectFolderPath = AssetUtility.getProjectBaseFolder(project.getProjectName(), projectId).replace("\\", "/");
		ClusterUtil.reactorPullProjectFolder(project, projectFolderPath, rdbmsId);

		return new NounMetadata(true, PixelDataType.BOOLEAN);
	}

}