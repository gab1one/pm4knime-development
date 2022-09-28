package org.pm4knime.node.conformance.table.fitness;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * This is an example implementation of the node factory of the
 * "ConformanceChecker" node.
 *
 * @author 
 */
public class FitnessCheckerNodeFactory 
        extends NodeFactory<FitnessCheckerNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public FitnessCheckerNodeModel createNodeModel() {
		// Create and return a new node model.
        return new FitnessCheckerNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
		// The number of views the node should have, in this cases there is none.
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<FitnessCheckerNodeModel> createNodeView(final int viewIndex,
            final FitnessCheckerNodeModel nodeModel) {
		return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
		// Indication whether the node has a dialog or not.
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
		// This example node has a dialog, hence we create and return it here. Also see "hasDialog()".
        return null;
    }

}

