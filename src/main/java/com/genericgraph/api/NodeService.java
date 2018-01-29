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
        
        Node firstFoundNode = findNode(firstNode);
        Node secondFoundNode = findNode(secondNode);

        org.neo4j.graphdb.Relationship graphRelationship = firstFoundNode.createRelationshipTo(secondFoundNode, RelationshipType.withName(relationship.name));

        if(relationship.properties != null) {
            relationship.properties.forEach( (String k, Object v) -> {
                graphRelationship.setProperty(k, v);;
            });
        }

        tx.success();
        return true;
    }

    private Node findNode(GenericNode node) {
        Entry nodeName = node.values.entrySet().iterator().next();
        return db.findNodes(Label.label(node.label.get(0)), nodeName.getKey().toString(), nodeName.getValue()).next();
    }
}