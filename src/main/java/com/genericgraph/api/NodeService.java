package com.genericgraph.api;

import org.neo4j.graphdb.*;

class NodeService {
    protected GraphDatabaseService db;

    NodeService(GraphDatabaseService injectedDb) {
        this.db = injectedDb;
    }

    public boolean write(GenericNode gNode) {
        Transaction tx = db.beginTx();
        Node node = db.createNode();
        node.addLabel(DynamicLabel.label(gNode.label.get(0)));
        tx.success();
        return true;
    }
}