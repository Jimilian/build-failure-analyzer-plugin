package com.sonyericsson.jenkins.plugins.bfa.model.dbf;

import hudson.Extension;
import hudson.model.Run;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.graph.FlowGraphWalker;
import org.jenkinsci.plugins.workflow.support.steps.build.BuildInfoAction;

/**
 * Get downstream builds for Build Flows.
 *
 * @author Alexander Akbashev
 */
@Extension
public class BuildFlowDBF extends DownstreamBuildFinder {
    private static final Logger logger = Logger.getLogger(BuildFlowDBF.class.getName());

    @Override
    public List<Run<?, ?>> getDownstreamBuilds(final Run build) {
        List<Run<?, ?>> runs = new ArrayList<Run<?, ?>>();

        if (build == null || !(build instanceof WorkflowRun)) {
            return EMPTY;
        }

        WorkflowRun workflowRun = (WorkflowRun)build;
        FlowGraphWalker walker = new FlowGraphWalker(workflowRun.getExecution());

        for (FlowNode step : walker) {
            BuildInfoAction buildInfoAction = step.getAction(BuildInfoAction.class);
            if (buildInfoAction != null) {
                runs.addAll(buildInfoAction.getChildBuilds());
            }
        }

        if (runs.isEmpty()) {
            logger.fine("No action was stored");
            return EMPTY;
        }

        return runs;
    }
}
