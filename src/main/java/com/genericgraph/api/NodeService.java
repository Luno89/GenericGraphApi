package com.genericgraph.api;

import java.util.Map.Entry;

import org.neo4j.graphdb.*;

class NodeService {
    protected GraphDatabaseService db;

    NodeService(GraphDatabaseService injectedDb) {
        this.db = injectedDb;
    }

    public boolean write(GenericNode gNode) {
        Transaction tx = db.beginTx();
        Node node = db.createNode();

        addLabels(node, gNode);
        addValues(node, gNode);

        tx.success();
        return true;
    }

    private void addLabels(Node node, GenericNode gNode) {
        if (gNode.label != null) {
            gNode.label.forEach(it -> {
                node.addLabel(Label.label(it));
            });
        }
    }

    private void addValues(Node node, GenericNode gNode) {
        if (gNode.values != null) {
            gNode.values.forEach((k,v) ->{
                node.setProperty(k, v);
            });
        }
    }

    public boolean writeRelationship(GenericNode firstNode, Relationship relationship, GenericNode secondNode) {
        Transaction tx = db.beginTx();
        Entry firstNodeName = firstNode.values.entrySet().iterator().next();
        Entry secondNodeName = secondNode.values.entrySet().iterator().next();

        Node firstFoundNode = db.findNodes(Label.label(firstNode.label.get(0)), firstNodeName.getKey().toString(), firstNodeName.getValue()).next();
        Node secondFoundNode = db.findNodes(Label.label(secondNode.label.get(0)), secondNodeName.getKey().toString(), secondNodeName.getValue()).next();
        
        firstFoundNode.createRelationshipTo(secondFoundNode, RelationshipType.withName(relationship.name));
        tx.success();
        return true;
    }
}